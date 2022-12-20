package rosegold.gumtuneclient.modules.world;

import cc.polyfrost.oneconfig.utils.Multithreading;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.storage.ExtendedBlockStorage;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.event.world.ChunkEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import rosegold.gumtuneclient.config.GumTuneClientConfig;
import rosegold.gumtuneclient.config.pages.WorldScannerFilter;
import rosegold.gumtuneclient.modules.render.HighlightBlock;
import rosegold.gumtuneclient.utils.RenderUtils;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class WorldScanner {

    public static final HashMap<BlockPos, String> crystals = new HashMap<>();

    @SubscribeEvent
    public void onChunkLoad(ChunkEvent.Load event) {
        if (!GumTuneClientConfig.worldScanner) return;
        Multithreading.runAsync(() -> {
            Chunk chunk = event.getChunk();
            for (int x = 0; x < 16; x++) {
                for (int y = 0; y < 256; y++) {
                    for (int z = 0; z < 16; z++) {
                        if (WorldScannerFilter.worldScannerCrystals) {
                            // queen
                            if (chunk.getBlock(x, y, z) == Blocks.stone &&
                                    chunk.getBlock(x, y + 1, z) == Blocks.log2 &&
                                    chunk.getBlock(x, y + 2, z) == Blocks.log2 &&
                                    chunk.getBlock(x, y + 3, z) == Blocks.log2 &&
                                    chunk.getBlock(x, y + 4, z) == Blocks.log2 &&
                                    chunk.getBlock(x, y + 5, z) == Blocks.cauldron) {
                                crystals.put(new BlockPos(chunk.xPosition * 16 + x, y + 5, chunk.zPosition * 16 + z), "§6Queen");
                            }
                            // divan
                            if (chunk.getBlock(x, y, z) == Blocks.quartz_block && // pillar
                                    chunk.getBlock(x, y + 1, z) == Blocks.quartz_stairs &&
                                    chunk.getBlock(x, y + 2, z) == Blocks.stone_brick_stairs &&
                                    chunk.getBlock(x, y + 3, z) == Blocks.stonebrick) { // chiseled
                                crystals.put(new BlockPos(chunk.xPosition * 16 + x, y + 5, chunk.zPosition * 16 + z), "§2Divan");
                            }
                            // temple
                            if (chunk.getBlock(x, y, z) == Blocks.bedrock &&
                                    chunk.getBlock(x, y + 1, z) == Blocks.clay &&
                                    chunk.getBlock(x, y + 2, z) == Blocks.clay &&
                                    chunk.getBlock(x, y + 3, z) == Blocks.stained_hardened_clay && // color lime
                                    chunk.getBlock(x, y + 4, z) == Blocks.wool && // color green
                                    chunk.getBlock(x, y + 5, z) == Blocks.leaves) { // oak leaves
                                crystals.put(new BlockPos(chunk.xPosition * 16 + x, y + 5, chunk.zPosition * 16 + z), "§5Temple");
                            }
                            // city
                            if (chunk.getBlock(x, y, z) == Blocks.cobblestone &&
                                    chunk.getBlock(x, y + 1, z) == Blocks.cobblestone &&
                                    chunk.getBlock(x, y + 2, z) == Blocks.cobblestone &&
                                    chunk.getBlock(x, y + 3, z) == Blocks.cobblestone &&
                                    chunk.getBlock(x, y + 4, z) == Blocks.stone_stairs &&
                                    chunk.getBlock(x, y + 5, z) == Blocks.stone && // smooth andesite
                                    chunk.getBlock(x, y + 6, z) == Blocks.stone && // smooth andesite
                                    chunk.getBlock(x, y + 7, z) == Blocks.dark_oak_stairs) {
                                crystals.put(new BlockPos(chunk.xPosition * 16 + x, y + 5, chunk.zPosition * 16 + z), "§bCity");
                            }
                            // king
                            if (chunk.getBlock(x, y, z) == Blocks.wool && // color red
                                    chunk.getBlock(x, y + 1, z) == Blocks.dark_oak_stairs &&
                                    chunk.getBlock(x, y + 2, z) == Blocks.dark_oak_stairs &&
                                    chunk.getBlock(x, y + 3, z) == Blocks.dark_oak_stairs) {
                                crystals.put(new BlockPos(chunk.xPosition * 16 + x, y + 5, chunk.zPosition * 16 + z), "§6King");
                            }
                        }
                    }
                }
            }
        });
    }

    @SubscribeEvent
    public void onChunkLoad(ChunkEvent.Unload event) {
        if (!GumTuneClientConfig.worldScanner) return;
        Multithreading.runAsync(() -> {
            Chunk chunk = event.getChunk();
            for (int x = 0; x < 16; x++) {
                for (int y = 0; y < 256; y++) {
                    for (int z = 0; z < 16; z++) {
                        crystals.remove(new BlockPos(chunk.xPosition * 16 + x, y + 5, chunk.zPosition * 16 + z));
                    }
                }
            }
        });
    }

    @SubscribeEvent
    public void onRender(RenderWorldLastEvent event) {
        if (!GumTuneClientConfig.worldScanner) return;
        for (Map.Entry<BlockPos, String> entry : crystals.entrySet()) {
            BlockPos blockPos = entry.getKey();
            RenderUtils.renderEspBox(blockPos, event.partialTicks, Color.WHITE.getRGB());
            RenderUtils.renderWaypointText(entry.getValue(), blockPos.getX() + 0.5, blockPos.getY() + 0.5, blockPos.getZ() + 0.5, event.partialTicks);
        }
    }
}
