package rosegold.gumtuneclient.modules.player;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.util.BlockPos;
import net.minecraft.util.Vec3;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import rosegold.gumtuneclient.GumTuneClient;
import rosegold.gumtuneclient.utils.*;
import rosegold.gumtuneclient.utils.pathfinding.AStarCustomPathfinder;
import rosegold.gumtuneclient.utils.pathfinding.PathFinder;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.stream.Collectors;

public class PathFinding {
    private static int stuckTicks = 0;
    private static BlockPos oldPos;
    private static BlockPos curPos;
    public static boolean walk = false;
    public static ArrayList<BlockPos> temp = new ArrayList<>();
    public static ConcurrentLinkedQueue<Vec3> points = new ConcurrentLinkedQueue<>();
    public static ConcurrentHashMap<BlockPos, Integer> renderHubs = new ConcurrentHashMap<>();

    @SubscribeEvent
    public void onTick(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.START) return;
        if(!walk) return;
        if (PathFinder.hasPath()) {
            if (++stuckTicks >= 20) {
                curPos = GumTuneClient.mc.thePlayer.getPosition();
                if (oldPos != null && VectorUtils.getHorizontalDistance(new Vec3(curPos), new Vec3(oldPos)) <= 0.1) {
                    initWalk();
                    PathFinder.path.clear();
                    new Thread(() -> PathFinder.setup(new BlockPos(VectorUtils.floorVec(GumTuneClient.mc.thePlayer.getPositionVector())), PathFinder.goal, 0.0, 2000)).start();
                    return;
                }
                oldPos = curPos;
                stuckTicks = 0;
            }
            Vec3 nextPos = goodPoints(PathFinder.path);
            PathFinder.path.removeIf(vec -> new BlockPos(vec).getY() == GumTuneClient.mc.thePlayer.getPosition().getY() && PathFinder.path.indexOf(vec) < PathFinder.path.indexOf(nextPos));
            Vec3 first = PathFinder.getCurrent().addVector(0.5, 0.0, 0.5);
            RotationUtils.Rotation needed = RotationUtils.getRotation(first);
            needed.setPitch(GumTuneClient.mc.thePlayer.rotationPitch);
            if (VectorUtils.getHorizontalDistance(GumTuneClient.mc.thePlayer.getPositionVector(), first) < 0.7) {
                if(GumTuneClient.mc.thePlayer.getPositionVector().distanceTo(first) > 2) {
                    if (RotationUtils.done && needed.getYaw() < 135.0f) {
                        RotationUtils.smoothLook(needed, 150L);
                    }
                    Vec3 lastTick = new Vec3(GumTuneClient.mc.thePlayer.lastTickPosX, GumTuneClient.mc.thePlayer.lastTickPosY, GumTuneClient.mc.thePlayer.lastTickPosZ);
                    Vec3 diffy = GumTuneClient.mc.thePlayer.getPositionVector().subtract(lastTick);
                    diffy = diffy.addVector(diffy.xCoord * 4.0, 0.0, diffy.zCoord * 4.0);
                    Vec3 nextTick = GumTuneClient.mc.thePlayer.getPositionVector().add(diffy);
                    stopMovement();
                    GumTuneClient.mc.thePlayer.setSprinting(false);
                    ArrayList<KeyBinding> neededPresses = VectorUtils.getNeededKeyPresses(GumTuneClient.mc.thePlayer.getPositionVector(), first);
                    if (Math.abs(nextTick.distanceTo(first) - GumTuneClient.mc.thePlayer.getPositionVector().distanceTo(first)) <= 0.05 || nextTick.distanceTo(first) <= GumTuneClient.mc.thePlayer.getPositionVector().distanceTo(first)) {
                        neededPresses.forEach(v -> KeyBinding.setKeyBindState(v.getKeyCode(), true));
                    }
                    if (Math.abs(GumTuneClient.mc.thePlayer.posY - first.yCoord) > 0.5) {
                        KeyBinding.setKeyBindState(GumTuneClient.mc.gameSettings.keyBindJump.getKeyCode(), GumTuneClient.mc.thePlayer.posY < first.yCoord);
                    }
                    else {
                        KeyBinding.setKeyBindState(GumTuneClient.mc.gameSettings.keyBindJump.getKeyCode(), false);
                    }
                } else {
                    RotationUtils.reset();
                    if (!PathFinder.goNext()) {
                        stopMovement();
                    }
                }
            } else {
                if (RotationUtils.done) {
                    RotationUtils.smoothLook(needed, 150L);
                }
                stopMovement();
                KeyBinding.setKeyBindState(GumTuneClient.mc.gameSettings.keyBindForward.getKeyCode(), true);
                GumTuneClient.mc.thePlayer.setSprinting(true);
                if (Math.abs(GumTuneClient.mc.thePlayer.posY - first.yCoord) > 0.5) {
                    KeyBinding.setKeyBindState(GumTuneClient.mc.gameSettings.keyBindJump.getKeyCode(), GumTuneClient.mc.thePlayer.posY < first.yCoord);
                }
                else {
                    KeyBinding.setKeyBindState(GumTuneClient.mc.gameSettings.keyBindJump.getKeyCode(), false);
                }
            }
        }
    }

    @SubscribeEvent
    public void onRender(RenderWorldLastEvent event) {
        if (PathFinder.path != null && !PathFinder.path.isEmpty()) {
            Vec3 last = PathFinder.path.get(PathFinder.path.size() - 1);
            RenderUtils.renderEspBox(new BlockPos(last), event.partialTicks, ColorUtils.getChroma(3000.0f, (int)(last.xCoord + last.yCoord + last.zCoord)));
            RenderUtils.drawLines(PathFinder.path, 1, event.partialTicks);
        }
//        for(BlockPos blockPos : temp) {
//            RenderUtils.renderEspBox(blockPos, event.partialTicks, Color.WHITE.getRGB());
//        }
//        for(Vec3 blockPos : points) {
//            RenderUtils.renderSmallBox(blockPos, Color.RED.getRGB());
//        }
        renderHubs.forEach((blockPos, integer) -> {
            RenderUtils.renderEspBox(blockPos, event.partialTicks, Color.CYAN.getRGB());
        });
    }

    private static Vec3 goodPoints(ArrayList<Vec3> path) {
        ArrayList<Vec3> reversed = new ArrayList<>(path);
        Collections.reverse(reversed);
        for(Vec3 vec : reversed.stream().filter(vec -> new BlockPos(vec).getY() == GumTuneClient.mc.thePlayer.getPosition().getY()).collect(Collectors.toList())) {
            if(isGood(vec)) {
                return vec;
            }
        }
        return null;
    }


    private static boolean isGood(Vec3 point) {
        if(point == null) return false;
        Vec3 topPoint = point.add(new Vec3(0, 2, 0));

        Vec3 topPos = GumTuneClient.mc.thePlayer.getPositionVector().addVector(0, 1, 0);
        Vec3 botPos = GumTuneClient.mc.thePlayer.getPositionVector();
        Vec3 underPos = GumTuneClient.mc.thePlayer.getPositionVector().addVector(0, -1, 0);

        Vec3 directionTop = RotationUtils.getLook(topPoint);
        directionTop = VectorUtils.scaleVec(directionTop, 0.5f);
        for (int i = 0; i < Math.round(topPoint.distanceTo(GumTuneClient.mc.thePlayer.getPositionEyes(1))) * 2; i++) {
            IBlockState topBlockState = GumTuneClient.mc.theWorld.getBlockState(new BlockPos(topPos));
            if(topBlockState.getBlock().getCollisionBoundingBox(
                    GumTuneClient.mc.theWorld,
                    new BlockPos(topPos),
                    topBlockState
            ) != null) return false;

            IBlockState botBlockState = GumTuneClient.mc.theWorld.getBlockState(new BlockPos(botPos));
            if(botBlockState.getBlock().getCollisionBoundingBox(
                    GumTuneClient.mc.theWorld,
                    new BlockPos(botPos),
                    botBlockState
            ) != null) return false;

            IBlockState underBlockState = GumTuneClient.mc.theWorld.getBlockState(new BlockPos(underPos));
            if(underBlockState.getBlock().getCollisionBoundingBox(
                    GumTuneClient.mc.theWorld,
                    new BlockPos(underPos),
                    underBlockState
            ) != null) return false;

            underPos = underPos.add(directionTop);
        }
        return true;
    }

    public static void initTeleport() {
        walk = false;
        stuckTicks = 0;
        oldPos = null;
        curPos = null;
    }

    public static void initWalk() {
        walk = true;
        stuckTicks = 0;
        oldPos = null;
        curPos = null;
    }

    private void stopMovement() {
        KeyBinding.setKeyBindState(GumTuneClient.mc.gameSettings.keyBindLeft.getKeyCode(), false);
        KeyBinding.setKeyBindState(GumTuneClient.mc.gameSettings.keyBindRight.getKeyCode(), false);
        KeyBinding.setKeyBindState(GumTuneClient.mc.gameSettings.keyBindForward.getKeyCode(), false);
        KeyBinding.setKeyBindState(GumTuneClient.mc.gameSettings.keyBindBack.getKeyCode(), false);
        KeyBinding.setKeyBindState(GumTuneClient.mc.gameSettings.keyBindJump.getKeyCode(), false);
    }

}
