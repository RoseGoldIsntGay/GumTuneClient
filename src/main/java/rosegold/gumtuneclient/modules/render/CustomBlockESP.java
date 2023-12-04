package rosegold.gumtuneclient.modules.render;

import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.crash.CrashReport;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ReportedException;
import net.minecraft.util.Vec3;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.storage.ExtendedBlockStorage;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.event.world.ChunkEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.opengl.GL11;
import rosegold.gumtuneclient.GumTuneClient;
import rosegold.gumtuneclient.config.GumTuneClientConfig;
import rosegold.gumtuneclient.events.BlockChangeEvent;
import rosegold.gumtuneclient.events.ChunkLoadEvent;
import rosegold.gumtuneclient.modules.mining.PowderChestSolver;
import rosegold.gumtuneclient.utils.ModUtils;
import rosegold.gumtuneclient.utils.ReflectionUtils;
import rosegold.gumtuneclient.utils.RenderUtils;
import rosegold.gumtuneclient.utils.objects.ColorBlockState;

import java.awt.*;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class CustomBlockESP {
    private static final ConcurrentHashMap<Color, Set<BlockPos>> highlightedBlocks = new ConcurrentHashMap<>();
    private static final HashMap<Block, ColorBlockState> blockEsp = new HashMap<>();
    private static long unloadedTimestamp = 0;

    @SubscribeEvent
    public void onBlockChange(BlockChangeEvent event) {
        if (!GumTuneClientConfig.customBlockESP) return;
        if (blockEsp.isEmpty()) return;

        BlockPos blockPos = event.pos;
        IBlockState blockState = event.update;

        if (blockEsp.containsKey(blockState.getBlock()) && (blockEsp.get(blockState.getBlock()).blockState == Blocks.air.getDefaultState() || blockEsp.get(blockState.getBlock()).blockState.equals(blockState))) {
            highlightedBlocks.get(blockEsp.get(blockState.getBlock()).color).add(blockPos);
        } else {
            highlightedBlocks.values().forEach(blockPosHashSet -> blockPosHashSet.remove(blockPos));
        }
    }

    @SubscribeEvent
    public void onChunkLoad(ChunkLoadEvent event) {
        if (!GumTuneClientConfig.customBlockESP) return;

        handleChunkLoad(event.getChunk());
    }

    @SubscribeEvent
    public void onChunkLoad(ChunkEvent.Unload event) {
        if (!GumTuneClientConfig.customBlockESP) return;

        highlightedBlocks.forEach((color, blockPosSet) -> {
            HashSet<BlockPos> toRemove = new HashSet<>();

            blockPosSet.forEach(blockPos -> {
                if (blockPos.getX() >= event.getChunk().xPosition * 16 && blockPos.getX() < event.getChunk().xPosition * 16 + 16) {
                    if (blockPos.getZ() >= event.getChunk().zPosition * 16 && blockPos.getZ() < event.getChunk().zPosition * 16 + 16) {
                        toRemove.add(blockPos);
                    }
                }
            });

            highlightedBlocks.get(color).removeAll(toRemove);
        });
    }

    @SubscribeEvent
    public void onWorldUnload(WorldEvent.Unload event) {
        if (System.currentTimeMillis() - unloadedTimestamp > 2000) {
            for (Set<BlockPos> value : highlightedBlocks.values()) {
                value.clear();
            }
            unloadedTimestamp = System.currentTimeMillis();
        }
    }

    @SubscribeEvent
    public void onRenderWorld(RenderWorldLastEvent event) {
        if (!GumTuneClientConfig.customBlockESP) return;
        if (highlightedBlocks.isEmpty()) return;

        GlStateManager.blendFunc(770, 771);
        GlStateManager.enableBlend();
        GlStateManager.disableTexture2D();
        GlStateManager.disableDepth();
        GlStateManager.disableAlpha();
        GlStateManager.depthMask(false);
        GlStateManager.disableLighting();

        highlightedBlocks.forEach((color, blockPosHashSet) -> {
            ArrayList<Vec3> blockPositions = new ArrayList<>();

            Iterator<BlockPos> iterator = blockPosHashSet.iterator();

            while (iterator.hasNext()) {
                BlockPos blockPos = iterator.next();

                if ((PowderChestSolver.closestChest == null || !PowderChestSolver.closestChest.equals(blockPos)) &&
                        !PowderChestSolver.solved.contains(blockPos) &&
                        (GumTuneClientConfig.customBlockESPRange == 0 || GumTuneClient.mc.thePlayer.getDistanceSq(blockPos.getX(), blockPos.getY(), blockPos.getZ()) < Math.pow(GumTuneClientConfig.customBlockESPRange, 2))) {
                    blockPositions.add(new Vec3(blockPos));
                }
            }

            GL11.glTranslated(-GumTuneClient.mc.getRenderManager().viewerPosX, -GumTuneClient.mc.getRenderManager().viewerPosY, -GumTuneClient.mc.getRenderManager().viewerPosZ);
            GlStateManager.color(color.getRed() / 256f, color.getGreen() / 256f, color.getBlue() / 256f, 0.5f);
            for (List<Vec3> blocks : Lists.partition(blockPositions, 512)) {
                RenderUtils.renderEspVectors(blocks);
            }

            GL11.glTranslated(GumTuneClient.mc.getRenderManager().viewerPosX, GumTuneClient.mc.getRenderManager().viewerPosY, GumTuneClient.mc.getRenderManager().viewerPosZ);
        });

        GlStateManager.enableAlpha();
        GlStateManager.enableTexture2D();
        GlStateManager.enableDepth();
        GlStateManager.depthMask(true);
        GlStateManager.disableBlend();
        GlStateManager.enableLighting();
    }

    public static void handleChunkLoad(Chunk chunk) {
        if (blockEsp.isEmpty()) return;

        for (int x = 0; x < 16; x++) {
            for (int y = 0; y < 256; y++) {
                for (int z = 0; z < 16; z++) {
                    BlockPos blockPos = new BlockPos(chunk.xPosition * 16 + x, y, chunk.zPosition * 16 + z);
                    IBlockState blockState = getBlockState(chunk, x, y, z);
                    Block block = blockState.getBlock();

                    if (blockEsp.containsKey(block) && (blockEsp.get(block).blockState == Blocks.air.getDefaultState() || blockEsp.get(block).blockState.equals(blockState))) {
                        highlightedBlocks.get(blockEsp.get(block).color).add(blockPos);
                    }
                }
            }
        }
    }

    public static void addBlock(IBlockState blockState, Color color, boolean wildcard) {
        if (blockState == null) return;
        if (!highlightedBlocks.containsKey(color)) {
            highlightedBlocks.put(color, new HashSet<>());
        }
        blockEsp.put(blockState.getBlock(), new ColorBlockState(wildcard ? Blocks.air.getDefaultState() : blockState, color));

        for (Set<BlockPos> value : highlightedBlocks.values()) {
            value.clear();
        }
        reload();

        saveConfig();
    }

    public static void removeBlock(IBlockState blockState, boolean wildcard) {
        if (blockState == null) return;
        if (wildcard) {
            blockEsp.remove(blockState.getBlock());
        } else {
            Block toRemove = null;

            for (Map.Entry<Block, ColorBlockState> entry : blockEsp.entrySet()) {
                if (entry.getValue().blockState.equals(blockState)) {
                    toRemove = entry.getKey();
                }
            }

            if (toRemove != null) {
                blockEsp.remove(toRemove);
            }
        }

        for (Set<BlockPos> value : highlightedBlocks.values()) {
            value.clear();
        }
        reload();

        saveConfig();
    }

    public static void reset() {
        blockEsp.clear();

        for (Set<BlockPos> value : highlightedBlocks.values()) {
            value.clear();
        }
        reload();

        saveConfig();
    }

    public static void list() {
        for (Map.Entry<Block, ColorBlockState> entry : blockEsp.entrySet()) {
            ModUtils.sendMessage((entry.getValue().blockState == Blocks.air.getDefaultState() ? entry.getKey() : entry.getValue().blockState) + ": r=" + entry.getValue().color.getRed() + " g=" + entry.getValue().color.getBlue() + " b=" + entry.getValue().color.getBlue());
        }
    }

    public static void dump() {
        Path path = Paths.get("./config/" + GumTuneClient.MODID + "/customBlockESPDump.json");
        ModUtils.sendMessage("Dumping highlightedBlocks to file " + path);

        try {
            Files.write(path, new Gson().toJson(highlightedBlocks).getBytes(StandardCharsets.UTF_8));
        } catch (IOException ioException) {
            ioException.printStackTrace();
            ModUtils.sendMessage("Failed dumping highlightedBlocks!");
        }
    }

    @SuppressWarnings("unchecked")
    public static void reload() {
        for (Set<BlockPos> value : highlightedBlocks.values()) {
            value.clear();
        }

        Object object = ReflectionUtils.field(GumTuneClient.mc.theWorld.getChunkProvider(), "field_73237_c");
        if (object instanceof List) {
            for (Chunk chunk : (List<Chunk>) object) {
                handleChunkLoad(chunk);
            }
        }
    }

    private static IBlockState getBlockState(Chunk chunk, int x, int y, int z) {
        ExtendedBlockStorage extendedblockstorage;
        if (y >= 0 && y / 16 < chunk.getBlockStorageArray().length && (extendedblockstorage = chunk.getBlockStorageArray()[y / 16]) != null) {
            try {
                return extendedblockstorage.get(x, y % 16, z);
            } catch (Throwable throwable) {
                CrashReport crashreport = CrashReport.makeCrashReport(throwable, "Getting block");
                throw new ReportedException(crashreport);
            }
        }

        return Blocks.air.getDefaultState();
    }

    public static void loadConfig() {
        try {
            File file = new File(Paths.get("./config/" + GumTuneClient.MODID + "/customBlockESP.json").toUri());
            if (file.exists()) {
                JsonObject jsonObject = (JsonObject) new JsonParser().parse(new FileReader(file));

                for (Map.Entry<String, JsonElement> entry: jsonObject.entrySet()) {
                    JsonObject colorBlockState = (JsonObject) entry.getValue();
                    Block block = Block.getBlockFromName(entry.getKey());
                    Color color = new Color(colorBlockState.get("color").getAsInt());
                    IBlockState iBlockState = Block.getBlockFromName(colorBlockState.get("block").getAsString()).getStateFromMeta(colorBlockState.get("meta").getAsInt());

                    if (!highlightedBlocks.containsKey(color)) {
                        highlightedBlocks.put(color, new HashSet<>());
                    }

                    blockEsp.put(block, new ColorBlockState(iBlockState, color));
                }
            }
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
    }

    public static void saveConfig() {
        try {
            StringBuilder stringBuilder = new StringBuilder().append("{");
            for (Map.Entry<Block, ColorBlockState> entry : blockEsp.entrySet()) {
                stringBuilder.append("\"").append(entry.getKey().getRegistryName());
                stringBuilder.append("\":{");
                stringBuilder.append("\"color\":").append(entry.getValue().color.getRGB()).append(",");
                stringBuilder.append("\"block\":\"").append(entry.getValue().blockState.getBlock().getRegistryName()).append("\",");
                stringBuilder.append("\"meta\":").append(entry.getValue().blockState.getBlock().getMetaFromState(entry.getValue().blockState));
                stringBuilder.append("},");
            }
            if (stringBuilder.charAt(stringBuilder.length() - 1) == ',') {
                stringBuilder.deleteCharAt(stringBuilder.length() - 1);
            }
            stringBuilder.append("}");

            Files.write(Paths.get("./config/" + GumTuneClient.MODID + "/customBlockESP.json"), stringBuilder.toString().getBytes(StandardCharsets.UTF_8));
        } catch (IOException ioException) {
            ioException.printStackTrace();
            ModUtils.sendMessage("Failed saving Custom Block ESP filter!");
        }
    }
}
