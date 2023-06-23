package rosegold.gumtuneclient.modules.mining;

import cc.polyfrost.oneconfig.utils.Multithreading;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.util.Vec3;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import rosegold.gumtuneclient.GumTuneClient;
import rosegold.gumtuneclient.config.GumTuneClientConfig;
import rosegold.gumtuneclient.modules.player.PathFinding;
import rosegold.gumtuneclient.utils.ModUtils;
import rosegold.gumtuneclient.utils.RenderUtils;
import rosegold.gumtuneclient.utils.StringUtils;
import rosegold.gumtuneclient.utils.VectorUtils;
import rosegold.gumtuneclient.utils.pathfinding.PathFinder;

import java.awt.*;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Objects;

public class MetalDetectorSolver {
    private static BlockPos anchor;
    private static final HashSet<BlockPos> relativeChestCoords = new HashSet<>();
    private static final HashSet<BlockPos> absoluteChestCoords = new HashSet<>();
    private static BlockPos ignoreBlockPos;
    private static final HashSet<BlockPos> predictedChestLocations = new HashSet<>();
    private static Vec3 lastPos;
    private enum Walking {
        STOP,
        WAITING,
        IDLE
    }

    private static Walking walking = Walking.IDLE;
    private static long lastScan = 0;
    private static boolean lobbyInitialized = false;

    public MetalDetectorSolver() {
        JsonParser jsonParser = new JsonParser();
        Object obj = jsonParser.parse(new BufferedReader(new InputStreamReader(Objects.requireNonNull(getClass().getClassLoader().getResourceAsStream("assets/" + GumTuneClient.MODID + "/metal_detector_coords.json")))));
        JsonArray jsonArray = (JsonArray) obj;
        for (JsonElement jsonElement : jsonArray) {
            relativeChestCoords.add(new BlockPos(
                    jsonElement.getAsJsonArray().get(0).getAsInt(),
                    jsonElement.getAsJsonArray().get(1).getAsInt(),
                    jsonElement.getAsJsonArray().get(2).getAsInt()
            ));
        }
    }

    @SubscribeEvent
    public void onActionBar(ClientChatReceivedEvent event) {
        if (!GumTuneClientConfig.metalDetectorSolver || GumTuneClient.mc.theWorld == null || GumTuneClient.mc.thePlayer == null) return;
        if (event.type != 2) return;
        String text = StringUtils.removeFormatting(event.message.getUnformattedText());
        if (text.contains("TREASURE")) {
            if (!lobbyInitialized && System.currentTimeMillis() - lastScan > 3000) {
                lastScan = System.currentTimeMillis();
                Multithreading.runAsync(MetalDetectorSolver::scanChunks);

                if (anchor != null) {
                    absoluteChestCoords.clear();
                    for (BlockPos blockPos : relativeChestCoords) {
                        BlockPos absolutePosition = new BlockPos(anchor.getX() - blockPos.getX(), anchor.getY() - blockPos.getY() + 1, anchor.getZ() - blockPos.getZ());
                        absoluteChestCoords.add(absolutePosition);
                    }

                    lobbyInitialized = true;
                } else {
                    return;
                }


            }
            EntityPlayerSP player = GumTuneClient.mc.thePlayer;

            if (lastPos == null || player.posX != lastPos.xCoord || player.posY != lastPos.yCoord || player.posZ != lastPos.zCoord) {
                lastPos = player.getPositionVector();
                return;
            }

            double treasureDistance = Double.parseDouble(text
                    .split("TREASURE: ")[1].split("m")[0].replaceAll("(?!\\.)\\D", ""));
            for (BlockPos blockPos : absoluteChestCoords) {
                double dist = Math.sqrt(
                        Math.pow(player.posX - blockPos.getX(), 2) +
                        Math.pow(player.posY - blockPos.getY(), 2) +
                        Math.pow(player.posZ - blockPos.getZ(), 2)
                );

                if (Math.round(dist * 10D) / 10D == treasureDistance) {
                    if (blockPos.add(0, -1, 0).equals(ignoreBlockPos)) {
                        ignoreBlockPos = null;
                        return;
                    }

                    if (!predictedChestLocations.contains(blockPos.add(0, -1, 0))) {
                        GumTuneClient.mc.thePlayer.playSound("random.orb", 1, 0.5F);
                    }

                    predictedChestLocations.clear();
                    predictedChestLocations.add(blockPos.add(0, -1, 0));
                }

            }

            /*if (predictedChestLocations.size() > 1) {
                BlockPos playerPos = player.getPosition();
                for (BlockPos blockPos : BlockPos.getAllInBox(playerPos.add(-1, 0, -1), playerPos.add(1, 0, 1))) {
                    if (getBlockState(blockPos).getBlock() == Blocks.air &&
                            getBlockState(blockPos.add(0, 1, 0)).getBlock() == Blocks.air) {
                        VectorUtils.getNeededKeyPresses(GumTuneClient.mc.thePlayer.getPositionVector(), new Vec3(blockPos)).forEach(keyBinding -> {
                            KeyBinding.setKeyBindState(keyBinding.getKeyCode(), true);
                            walking = Walking.WAITING;
                        });
                        break;
                    }
                }
            } else if (predictedChestLocations.size() == 1 && !PathFinder.hasPath() && !PathFinder.calculating) {
                ModUtils.sendMessage("calculate path to " + predictedChestLocations.iterator().next());
                Multithreading.runAsync(() -> {
                    PathFinding.initTeleport();
                    PathFinder.setup(new BlockPos(VectorUtils.floorVec(GumTuneClient.mc.thePlayer.getPositionVector().addVector(0, -1, 0))), predictedChestLocations.iterator().next(), 3, 400);
                });
            }*/
        }
    }

    @SubscribeEvent
    public void onTick(TickEvent.ClientTickEvent event) {
        if (!GumTuneClientConfig.metalDetectorSolver || GumTuneClient.mc.theWorld == null || GumTuneClient.mc.thePlayer == null) return;
        if (event.phase != TickEvent.Phase.END) return;
        if (walking == Walking.WAITING) {
            walking = Walking.STOP;
            return;
        }
        if (walking == Walking.STOP) {
            KeyBinding.setKeyBindState(GumTuneClient.mc.gameSettings.keyBindLeft.getKeyCode(), false);
            KeyBinding.setKeyBindState(GumTuneClient.mc.gameSettings.keyBindRight.getKeyCode(), false);
            KeyBinding.setKeyBindState(GumTuneClient.mc.gameSettings.keyBindForward.getKeyCode(), false);
            KeyBinding.setKeyBindState(GumTuneClient.mc.gameSettings.keyBindBack.getKeyCode(), false);
            KeyBinding.setKeyBindState(GumTuneClient.mc.gameSettings.keyBindJump.getKeyCode(), false);
            walking = Walking.IDLE;
        }
    }

    @SubscribeEvent
    public void onChat(ClientChatReceivedEvent event) {
        if (!GumTuneClientConfig.metalDetectorSolver || GumTuneClient.mc.theWorld == null || GumTuneClient.mc.thePlayer == null) return;
        if (event.type != 0) return;
        String text = StringUtils.removeFormatting(event.message.getUnformattedText());
        if (text.startsWith("You found") && text.endsWith("Metal Detector!")) {
            if (predictedChestLocations.iterator().hasNext()) {
                ignoreBlockPos = predictedChestLocations.iterator().next();
            }
            predictedChestLocations.clear();
            PathFinder.reset();
        }
    }

    @SubscribeEvent
    public void onRender(RenderWorldLastEvent event) {
        if (!GumTuneClientConfig.metalDetectorSolver || GumTuneClient.mc.theWorld == null || GumTuneClient.mc.thePlayer == null)
            return;
        if (GumTuneClientConfig.metalDetectorSolverShowAllSpots) {
            if (anchor != null) {
                RenderUtils.renderEspBox(anchor, event.partialTicks, Color.WHITE.getRGB());
                if (absoluteChestCoords.size() == relativeChestCoords.size()) {
                    absoluteChestCoords.forEach(blockPos -> RenderUtils.renderEspBox(blockPos, event.partialTicks, Color.RED.getRGB()));
                }
            }
        }
        predictedChestLocations.forEach(blockPos -> {
            RenderUtils.renderEspBox(blockPos, event.partialTicks, Color.GREEN.getRGB());
            RenderUtils.renderWaypointText("Treasure", blockPos, event.partialTicks);
            RenderUtils.renderBeacon(blockPos, Color.GREEN, event.partialTicks);
            if (GumTuneClientConfig.metalDetectorSolverTracer) {
                RenderUtils.renderTracer(blockPos, Color.GREEN, event.partialTicks);
            }
        });
    }


    @SubscribeEvent
    public void onWorldUnload(WorldEvent.Unload event) {
        anchor = null;
        ignoreBlockPos = null;
        lastScan = 0;
        lastPos = null;
        predictedChestLocations.clear();
    }

    private static void scanChunks() {
        int playerX = (int) GumTuneClient.mc.thePlayer.posX;
        int playerY = (int) GumTuneClient.mc.thePlayer.posY;
        int playerZ = (int) GumTuneClient.mc.thePlayer.posZ;

        for (int x = playerX - 50; x < playerX + 50; x++) {
            for (int y = playerY + 35; y > playerY; y--) {
                for (int z = playerZ - 50; z < playerZ + 50; z++) {
                    if (getBlockState(new BlockPos(x, y, z)).getBlock() == Blocks.quartz_stairs &&
                        getBlockState(new BlockPos(x, y + 13, z)).getBlock() == Blocks.barrier) {
                        anchor = verifyAnchor(x, y + 13, z);
                        return;
                    }
                }
            }
        }
    }

    private static BlockPos verifyAnchor(int posX, int posY, int posZ) {
        boolean loop = true;

        if (getBlockState(new BlockPos(posX, posY, posZ)).getBlock() != Blocks.barrier) {
            return new BlockPos(posX, posY, posZ);
        }
        while (loop) {
            loop = false;
            if (getBlockState(new BlockPos(posX + 1, posY, posZ)).getBlock() == Blocks.barrier) {
                posX++;
                loop = true;
            }
            if (getBlockState(new BlockPos(posX, posY - 1, posZ)).getBlock() == Blocks.barrier) {
                posY--;
                loop = true;
            }
            if (getBlockState(new BlockPos(posX, posY, posZ + 1)).getBlock() == Blocks.barrier) {
                posZ++;
                loop = true;
            }
        }
        return new BlockPos(posX, posY, posZ);
    }

    private static IBlockState getBlockState(BlockPos blockPos) {
        return GumTuneClient.mc.theWorld.getBlockState(blockPos);
    }
}
