package rosegold.gumtuneclient.modules.player;

import com.google.common.collect.Lists;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.util.BlockPos;
import net.minecraft.util.Vec3;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.lwjgl.opengl.GL11;
import rosegold.gumtuneclient.GumTuneClient;
import rosegold.gumtuneclient.utils.*;
import rosegold.gumtuneclient.utils.pathfinding.AStarCustomPathfinder;
import rosegold.gumtuneclient.utils.pathfinding.PathFinder;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

public class PathFinding {
    private static int stuckTicks = 0;
    private static BlockPos oldPos;
    private static BlockPos curPos;
    public static boolean walk = false;
    public static ArrayList<BlockPos> temp = new ArrayList<>();
    public static CopyOnWriteArrayList<Vec3> points = Lists.newCopyOnWriteArrayList();
    public static CopyOnWriteArrayList<BlockPos> renderHubs = Lists.newCopyOnWriteArrayList();

    @SubscribeEvent
    public void onRender(RenderWorldLastEvent event) {
        if (PathFinder.path != null && !PathFinder.path.isEmpty()) {
            Vec3 last = PathFinder.path.get(PathFinder.path.size() - 1);
            RenderUtils.renderEspBox(new BlockPos(last), event.partialTicks, ColorUtils.getChroma(3000.0f, (int)(last.xCoord + last.yCoord + last.zCoord)));
            RenderUtils.drawLines(PathFinder.path, 1, event.partialTicks);

            GlStateManager.blendFunc(770, 771);
            GlStateManager.enableBlend();
            GlStateManager.disableTexture2D();
            GlStateManager.disableDepth();
            GlStateManager.disableAlpha();
            GlStateManager.depthMask(false);
            GlStateManager.disableLighting();

            GL11.glTranslated(-GumTuneClient.mc.getRenderManager().viewerPosX, -GumTuneClient.mc.getRenderManager().viewerPosY, -GumTuneClient.mc.getRenderManager().viewerPosZ);
            GlStateManager.color(1, 0, 0, 0.5f);
            for (List<Vec3> vectors : Lists.partition(PathFinder.path, 512)) {
                RenderUtils.renderEspVectors(vectors);
            }

            GL11.glTranslated(GumTuneClient.mc.getRenderManager().viewerPosX, GumTuneClient.mc.getRenderManager().viewerPosY, GumTuneClient.mc.getRenderManager().viewerPosZ);

            GlStateManager.enableAlpha();
            GlStateManager.enableTexture2D();
            GlStateManager.enableDepth();
            GlStateManager.depthMask(true);
            GlStateManager.disableBlend();
            GlStateManager.enableLighting();
        }
//        for(BlockPos blockPos : temp) {
//            RenderUtils.renderEspBox(blockPos, event.partialTicks, Color.WHITE.getRGB());
//        }
//        for(Vec3 blockPos : points) {
//            RenderUtils.renderSmallBox(blockPos, Color.RED.getRGB());
//        }
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
