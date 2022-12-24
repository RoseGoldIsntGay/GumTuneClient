package rosegold.gumtuneclient.modules.world;

import cc.polyfrost.oneconfig.utils.Multithreading;
import com.google.common.collect.Lists;
import net.minecraft.block.BlockColored;
import net.minecraft.block.BlockPlanks;
import net.minecraft.block.BlockStone;
import net.minecraft.block.BlockStoneSlab;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.multiplayer.ChunkProviderClient;
import net.minecraft.crash.CrashReport;
import net.minecraft.init.Blocks;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ReportedException;
import net.minecraft.util.Vec3;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.storage.ExtendedBlockStorage;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.event.world.ChunkEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import rosegold.gumtuneclient.GumTuneClient;
import rosegold.gumtuneclient.config.GumTuneClientConfig;
import rosegold.gumtuneclient.config.pages.WorldScannerFilter;
import rosegold.gumtuneclient.utils.LocationUtils;
import rosegold.gumtuneclient.utils.ReflectionUtils;
import rosegold.gumtuneclient.utils.RenderUtils;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WorldScanner {

    public static class World {
        private final HashMap<String, BlockPos> waypoints;
        private final ArrayList<BlockPos> fairyGrottosWaypoints;
        private final ArrayList<BlockPos> wormFishingWaypoints;
        private final String worldId;

        public World(String worldId) {
            this.worldId = worldId;
            this.waypoints = new HashMap<>();
            this.fairyGrottosWaypoints = new ArrayList<>();
            this.wormFishingWaypoints = new ArrayList<>();
        }

        public HashMap<String, BlockPos> getWaypoints() {
            return waypoints;
        }

        public void updateWaypoints(String name, BlockPos blockPos) {
            this.waypoints.put(name, blockPos);
        }

        public void updateFairyGrottos(BlockPos blockPos) {
            this.fairyGrottosWaypoints.add(blockPos);
        }

        public ArrayList<BlockPos> getFairyGrottos() {
            return this.fairyGrottosWaypoints;
        }

        public void updateWormFishing(BlockPos blockPos) {
            this.wormFishingWaypoints.add(blockPos);
        }

        public ArrayList<BlockPos> getWormFishing() {
            return this.wormFishingWaypoints;
        }

        public String getWorldId() {
            return this.worldId;
        }
    }

    public static final HashMap<String, World> worlds = new HashMap<>();
    private static int cooldown = 100;
    private static long lastScan = 0;

    @SubscribeEvent
    public void onChunkLoad(ChunkEvent.Load event) {
        if (!GumTuneClientConfig.worldScanner || GumTuneClient.mc.theWorld == null || GumTuneClient.mc.thePlayer == null) return;
        if (GumTuneClientConfig.worldScannerScanMode == 1) return;
        if (cooldown != 0) return;
        World currentWorld = worlds.get(LocationUtils.serverName);
        if (currentWorld == null) return;
        Multithreading.runAsync(() -> handleChunkLoad(event.getChunk(), currentWorld));
    }

    @SubscribeEvent
    public void onTick(TickEvent.ClientTickEvent event) {
        if (!GumTuneClientConfig.worldScanner || GumTuneClient.mc.theWorld == null || GumTuneClient.mc.thePlayer == null) return;
        if (event.phase == TickEvent.Phase.START) return;
        if (cooldown > 0) {
            cooldown--;
        }
        if (cooldown == 1 && !worlds.containsKey(LocationUtils.serverName)) {
            worlds.put(LocationUtils.serverName, new World(LocationUtils.serverName));
        }
        if (cooldown == 0) {
            if (GumTuneClientConfig.worldScannerScanMode == 0) return;
            World currentWorld = worlds.get(LocationUtils.serverName);
            if (currentWorld == null) return;
            if (System.currentTimeMillis() - lastScan > GumTuneClientConfig.worldScannerScanFrequency * 1000L) {
                lastScan = System.currentTimeMillis();
                Object object = ReflectionUtils.field((ChunkProviderClient) GumTuneClient.mc.theWorld.getChunkProvider(), "chunkListing");
                if (object != null && object.getClass() == Lists.newArrayList().getClass()) {
                    for (Chunk chunk : (List<Chunk>) object) {
                        Multithreading.runAsync(() -> handleChunkLoad(chunk, currentWorld));
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public void onWorldUnload(WorldEvent.Unload event) {
        cooldown = 100;
    }

    @SubscribeEvent
    public void onRender(RenderWorldLastEvent event) {
        if (!GumTuneClientConfig.worldScanner || GumTuneClient.mc.theWorld == null || GumTuneClient.mc.thePlayer == null) return;
        if (cooldown != 0) return;
        if (!LocationUtils.onSkyblock) return;
        World currentWorld = worlds.get(LocationUtils.serverName);
        if (currentWorld == null) return;
        for (Map.Entry<String, BlockPos> entry : currentWorld.getWaypoints().entrySet()) {
            BlockPos blockPos = entry.getValue();
            if (GumTuneClientConfig.espHighlight) RenderUtils.renderEspBox(blockPos, event.partialTicks, Color.WHITE.getRGB());
            if (GumTuneClientConfig.espWaypointText) RenderUtils.renderWaypointText(entry.getKey(), blockPos.getX() + 0.5, blockPos.getY() + 0.5, blockPos.getZ() + 0.5, event.partialTicks);
            if (GumTuneClientConfig.espBeacon) RenderUtils.renderBeacon(new Vec3(blockPos.getX() + 0.5, blockPos.getY() + 0.5, blockPos.getZ() + 0.5), Color.PINK, event.partialTicks);
        }
        for (BlockPos blockPos : currentWorld.getFairyGrottos()) {
            if (GumTuneClientConfig.espHighlight) RenderUtils.renderEspBox(blockPos, event.partialTicks, Color.PINK.getRGB());
            if (GumTuneClientConfig.espWaypointText) RenderUtils.renderWaypointText("§dFairy Grotto", blockPos.getX() + 0.5, blockPos.getY() + 0.5, blockPos.getZ() + 0.5, event.partialTicks);
            if (GumTuneClientConfig.espBeacon) RenderUtils.renderBeacon(new Vec3(blockPos.getX() + 0.5, blockPos.getY() + 0.5, blockPos.getZ() + 0.5), Color.PINK, event.partialTicks);
        }
        for (BlockPos blockPos : currentWorld.getWormFishing()) {
            if (GumTuneClientConfig.espHighlight) RenderUtils.renderEspBox(blockPos, event.partialTicks, Color.ORANGE.getRGB());
            if (GumTuneClientConfig.espWaypointText) RenderUtils.renderWaypointText("§6Worm Fishing", blockPos.getX() + 0.5, blockPos.getY() + 0.5, blockPos.getZ() + 0.5, event.partialTicks);
            if (GumTuneClientConfig.espBeacon) RenderUtils.renderBeacon(new Vec3(blockPos.getX() + 0.5, blockPos.getY() + 0.5, blockPos.getZ() + 0.5), Color.PINK, event.partialTicks);
        }
    }

    public static void handleChunkLoad(Chunk chunk, World currentWorld) {
        for (int x = 0; x < 16; x++) {
            for (int y = 0; y < 256; y++) {
                for (int z = 0; z < 16; z++) {
                    if (WorldScannerFilter.worldScannerCHCrystals && LocationUtils.currentIsland == LocationUtils.Island.CRYSTAL_HOLLOWS) {
                        // queen
                        if (chunk.getBlock(x, y, z) == Blocks.stone &&
                                chunk.getBlock(x, y + 1, z) == Blocks.log2 &&
                                chunk.getBlock(x, y + 2, z) == Blocks.log2 &&
                                chunk.getBlock(x, y + 3, z) == Blocks.log2 &&
                                chunk.getBlock(x, y + 4, z) == Blocks.log2 &&
                                chunk.getBlock(x, y + 5, z) == Blocks.cauldron) {
                            currentWorld.updateWaypoints("§6Queen", new BlockPos(chunk.xPosition * 16 + x, y + 5, chunk.zPosition * 16 + z));
                            return;
                        }
                        // divan
                        if (chunk.getBlock(x, y, z) == Blocks.quartz_block && // pillar
                                chunk.getBlock(x, y + 1, z) == Blocks.quartz_stairs &&
                                chunk.getBlock(x, y + 2, z) == Blocks.stone_brick_stairs &&
                                chunk.getBlock(x, y + 3, z) == Blocks.stonebrick) { // chiseled
                            currentWorld.updateWaypoints("§2Divan", new BlockPos(chunk.xPosition * 16 + x, y + 5, chunk.zPosition * 16 + z));
                            return;
                        }
                        // temple
                        if (chunk.getBlock(x, y, z) == Blocks.bedrock &&
                                chunk.getBlock(x, y + 1, z) == Blocks.clay &&
                                chunk.getBlock(x, y + 2, z) == Blocks.clay &&
                                chunk.getBlock(x, y + 3, z) == Blocks.stained_hardened_clay && // color lime
                                chunk.getBlock(x, y + 4, z) == Blocks.wool && // color green
                                chunk.getBlock(x, y + 5, z) == Blocks.leaves &&
                                chunk.getBlock(x, y + 6, z) == Blocks.leaves) { // oak leaves
                            currentWorld.updateWaypoints("§5Temple Crystal", new BlockPos(chunk.xPosition * 16 + x + 9, y + 2, chunk.zPosition * 16 + z));
                            currentWorld.updateWaypoints("§5Temple Door Guardian", new BlockPos(chunk.xPosition * 16 + x - 45, y + 47, chunk.zPosition * 16 + z - 18));
                            return;
                        }
                        // city
                        if (chunk.getBlock(x, y, z) == Blocks.cobblestone &&
                                chunk.getBlock(x, y + 1, z) == Blocks.cobblestone &&
                                chunk.getBlock(x, y + 2, z) == Blocks.cobblestone &&
                                chunk.getBlock(x, y + 3, z) == Blocks.cobblestone &&
                                chunk.getBlock(x, y + 4, z) == Blocks.stone_stairs &&
                                chunk.getBlock(x, y + 5, z) == Blocks.stone && getBlockState(chunk, x, y + 5, z).getValue(BlockStone.VARIANT) == BlockStone.EnumType.ANDESITE_SMOOTH  &&  // smooth andesite
                                chunk.getBlock(x, y + 6, z) == Blocks.stone && getBlockState(chunk, x, y + 5, z).getValue(BlockStone.VARIANT) == BlockStone.EnumType.ANDESITE_SMOOTH  &&  // smooth andesite
                                chunk.getBlock(x, y + 7, z) == Blocks.dark_oak_stairs) {
                            currentWorld.updateWaypoints("§bCity", new BlockPos(chunk.xPosition * 16 + x + 24, y, chunk.zPosition * 16 + z - 17));
                            return;
                        }
                        // king
                        if (chunk.getBlock(x, y, z) == Blocks.wool && // color red
                                chunk.getBlock(x, y + 1, z) == Blocks.dark_oak_stairs &&
                                chunk.getBlock(x, y + 2, z) == Blocks.dark_oak_stairs &&
                                chunk.getBlock(x, y + 3, z) == Blocks.dark_oak_stairs) {
                            currentWorld.updateWaypoints("§6King", new BlockPos(chunk.xPosition * 16 + x + 1, y - 1, chunk.zPosition * 16 + z + 2));
                            return;
                        }
                    }

                    if (WorldScannerFilter.worldScannerCHMobSpots && LocationUtils.currentIsland == LocationUtils.Island.CRYSTAL_HOLLOWS) {
                        // goblin hall
                        if (chunk.getBlock(x, y, z) == Blocks.planks && getBlockState(chunk, x, y, z).getValue(BlockPlanks.VARIANT) == BlockPlanks.EnumType.SPRUCE && // spruce
                                chunk.getBlock(x, y + 2, z) == Blocks.spruce_stairs &&
                                chunk.getBlock(x, y + 3, z) == Blocks.spruce_stairs &&
                                chunk.getBlock(x, y + 6, z) == Blocks.spruce_stairs &&
                                chunk.getBlock(x, y + 7, z) == Blocks.spruce_stairs &&
                                chunk.getBlock(x, y + 10, z) == Blocks.spruce_stairs &&
                                chunk.getBlock(x, y + 11, z) == Blocks.spruce_stairs &&
                                chunk.getBlock(x, y + 13, z) == Blocks.planks && getBlockState(chunk, x, y + 13, z).getValue(BlockPlanks.VARIANT) == BlockPlanks.EnumType.SPRUCE) { // spruce
                            currentWorld.updateWaypoints("§6Goblin Hall", new BlockPos(chunk.xPosition * 16 + x, y + 7, chunk.zPosition * 16 + z));
                            return;
                        }
                        // grunt bridge
                        if (chunk.getBlock(x, y, z) == Blocks.stone_brick_stairs &&
                                chunk.getBlock(x, y + 5, z) == Blocks.stonebrick &&
                                chunk.getBlock(x, y + 6, z) == Blocks.stonebrick &&
                                chunk.getBlock(x, y + 8, z) == Blocks.stone_slab && // stone brick slab
                                chunk.getBlock(x, y + 9, z) == Blocks.stonebrick &&
                                chunk.getBlock(x, y + 13, z) == Blocks.stonebrick &&
                                chunk.getBlock(x, y + 14, z) == Blocks.stone_slab) { // stone brick slab
                            currentWorld.updateWaypoints("§bGrunt Bridge", new BlockPos(chunk.xPosition * 16 + x, y, chunk.zPosition * 16 + z));
                            return;
                        }
                    }

                    if (WorldScannerFilter.worldScannerCHFairyGrottos && LocationUtils.currentIsland == LocationUtils.Island.CRYSTAL_HOLLOWS) {
                        if (chunk.getBlock(x, y, z) == Blocks.stained_glass && getBlockState(chunk, x, y, z).getValue(BlockColored.COLOR) == EnumDyeColor.MAGENTA) {
                            currentWorld.updateFairyGrottos(new BlockPos(chunk.xPosition * 16 + x, y, chunk.zPosition * 16 + z));
                            return;
                        }
                    }

                    if (WorldScannerFilter.worldScannerCHWormFishing && LocationUtils.currentIsland == LocationUtils.Island.CRYSTAL_HOLLOWS) {
                        if (chunk.xPosition * 16 + x >= 564 && chunk.zPosition + z >= 513 || chunk.xPosition * 16 + x >= 513 && chunk.zPosition + z >= 564) {
                            if (y > 63 && (chunk.getBlock(x, y, z) == Blocks.lava || chunk.getBlock(x, y, z) == Blocks.flowing_lava)) {
                                currentWorld.updateWormFishing(new BlockPos(chunk.xPosition * 16 + x, y, chunk.zPosition * 16 + z));
                            }
                        }
                    }
                }
            }
        }
    }

    private static IBlockState getBlockState(Chunk chunk, int x, int y, int z) {
        ExtendedBlockStorage extendedblockstorage;
        IBlockState iBlockState = Blocks.air.getDefaultState();
        if (y >= 0 && y >> 4 < chunk.getBlockStorageArray().length && (extendedblockstorage = chunk.getBlockStorageArray()[y >> 4]) != null) {
            try {
                iBlockState = extendedblockstorage.get(x, y & 0xF, z);
            } catch (Throwable throwable) {
                CrashReport crashreport = CrashReport.makeCrashReport(throwable, "Getting block");
                throw new ReportedException(crashreport);
            }
        }
        return iBlockState;
    }
}
