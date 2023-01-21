package rosegold.gumtuneclient.modules.mining;

import cc.polyfrost.oneconfig.utils.Multithreading;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import rosegold.gumtuneclient.GumTuneClient;
import rosegold.gumtuneclient.config.GumTuneClientConfig;
import rosegold.gumtuneclient.utils.ModUtils;
import rosegold.gumtuneclient.utils.ReflectionUtils;
import rosegold.gumtuneclient.utils.RenderUtils;

import java.awt.*;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class MetalDetectorSolver {
    private static BlockPos anchor;
    private static ArrayList<BlockPos> relativeChestCoords = new ArrayList<>();
    private static boolean error = false;
    private static long lastScan = 0;

    public MetalDetectorSolver() {
        JsonParser jsonParser = new JsonParser();
        Object obj = jsonParser.parse(new BufferedReader(new InputStreamReader(getClass().getClassLoader().getResourceAsStream("assets/" + GumTuneClient.MODID + "/metal_detector_coords.json"))));
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
    public void onPack(ClientChatReceivedEvent event) {
        if (!GumTuneClientConfig.metalDetectorSolver || GumTuneClient.mc.theWorld == null || GumTuneClient.mc.thePlayer == null) return;
        if (event.type != 2) return;
        String text = removeFormatting(event.message.getUnformattedText());
        if (text.contains("TREASURE")) {
            if (System.currentTimeMillis() - lastScan > 15000) {
                lastScan = System.currentTimeMillis();
                scanChunks();
            }
            EntityPlayerSP player = GumTuneClient.mc.thePlayer;
            if (anchor == null) return;
            for (BlockPos blockPos : relativeChestCoords) {
                BlockPos absolutePosition = new BlockPos(anchor.getX() - blockPos.getX(), anchor.getY() - blockPos.getY(), anchor.getZ() - blockPos.getZ());
                double dist = Math.sqrt(
                        Math.pow(player.posX - absolutePosition.getX() + 0.5, 2) +
                                Math.pow(player.posY - absolutePosition.getY() + 0.5, 2) +
                                Math.pow(player.posZ - absolutePosition.getX() + 0.5, 2)
                );

                System.out.println(absolutePosition + " " + dist);
            }
        }
    }

    @SubscribeEvent
    public void onRender(RenderWorldLastEvent event) {
        if (!GumTuneClientConfig.metalDetectorSolver || GumTuneClient.mc.theWorld == null || GumTuneClient.mc.thePlayer == null)
            return;
        if (anchor != null) {
            RenderUtils.renderEspBox(anchor, event.partialTicks, Color.WHITE.getRGB());
            if (relativeChestCoords != null) {
                for (BlockPos blockPos : relativeChestCoords) {
                    RenderUtils.renderEspBox(anchor.subtract(blockPos), event.partialTicks, new Color(165, 42, 42).getRGB());
                }
            }
        }
    }


    @SubscribeEvent
    public void onWorldUnload(WorldEvent.Unload event) {
        anchor = null;
    }

    private static String removeFormatting(String text) {
        return text.replaceAll("§[0-9a-fk-or]", "");
    }

    private static void scanChunks() {
        Object object = ReflectionUtils.field(GumTuneClient.mc.theWorld.getChunkProvider(), "field_73237_c");
        if (object != null) {
            for (Chunk chunk : (ArrayList<Chunk>) object) {
                Multithreading.runAsync(() -> handleChunkLoad(chunk));
            }
        }
    }

    private static void handleChunkLoad(Chunk chunk) {
        for (int x = 0; x < 16; x++) {
            for (int y = 0; y < 256; y++) {
                for (int z = 0; z < 16; z++) {
                    if (chunk.getBlock(x, y, z) == Blocks.quartz_stairs &&
                            chunk.getBlock(x, y + 13, z) == Blocks.barrier) {
                        ModUtils.sendMessage("found anchor");
                        anchor = verifyAnchor(new BlockPos(chunk.xPosition * 16 + x, y, chunk.zPosition * 16 + z));
                        return;
                    }
                }
            }
        }
    }

    private static BlockPos verifyAnchor(BlockPos suspected) {
        boolean loop = true;
        int posX = suspected.getX();
        int posY = suspected.getY();
        int posZ = suspected.getZ();

        while (loop) {
            loop = false;
            if (GumTuneClient.mc.theWorld.getBlockState(new BlockPos(posX + 1, posY, posZ)).getBlock() == Blocks.barrier) {
                posX++;
                loop = true;
            }
            if (GumTuneClient.mc.theWorld.getBlockState(new BlockPos(posX, posY - 1, posZ)).getBlock() == Blocks.barrier) {
                posY++;
                loop = true;
            }
            if (GumTuneClient.mc.theWorld.getBlockState(new BlockPos(posX, posY, posZ + 1)).getBlock() == Blocks.barrier) {
                posZ++;
                loop = true;
            }
        }
        return new BlockPos(posX, posY, posZ);
    }
}
