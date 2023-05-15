package rosegold.gumtuneclient.modules.world;

import cc.polyfrost.oneconfig.utils.Multithreading;
import net.minecraft.block.BlockColored;
import net.minecraft.block.BlockPlanks;
import net.minecraft.block.BlockSlab;
import net.minecraft.block.BlockStone;
import net.minecraft.block.state.IBlockState;
import net.minecraft.crash.CrashReport;
import net.minecraft.event.ClickEvent;
import net.minecraft.event.HoverEvent;
import net.minecraft.init.Blocks;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.util.*;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.storage.ExtendedBlockStorage;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.event.world.ChunkEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import rosegold.gumtuneclient.GumTuneClient;
import rosegold.gumtuneclient.config.GumTuneClientConfig;
import rosegold.gumtuneclient.config.pages.WorldScannerFilter;
import rosegold.gumtuneclient.utils.*;
import rosegold.gumtuneclient.utils.StringUtils;

import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WorldScanner {

    public static class World {
        private final ConcurrentHashMap<String, BlockPos> crystalWaypoints;
        private final ConcurrentHashMap<String, BlockPos> mobSpotWaypoints;
        private final ConcurrentHashMap<BlockPos, Integer> fairyGrottosWaypoints;
        private final ConcurrentHashMap<BlockPos, Integer> wormFishingWaypoints;
        private final ConcurrentHashMap<BlockPos, Integer> dragonNestWaypoints;
        public World(String serverName) {
            this.crystalWaypoints = new ConcurrentHashMap<>();
            this.mobSpotWaypoints = new ConcurrentHashMap<>();
            this.fairyGrottosWaypoints = new ConcurrentHashMap<>();
            this.wormFishingWaypoints = new ConcurrentHashMap<>();
            this.dragonNestWaypoints = new ConcurrentHashMap<>();
        }

        public void updateCrystalWaypoints(String name, BlockPos blockPos) {
            this.crystalWaypoints.put(name, blockPos);
        }

        public ConcurrentHashMap<String, BlockPos> getCrystalWaypoints() {
            return crystalWaypoints;
        }

        public void updateMobSpotWaypoints(String name, BlockPos blockPos) {
                 this.mobSpotWaypoints.put(name, blockPos);
        }

        public ConcurrentHashMap<String, BlockPos> getMobSpotWaypoints() {
            return mobSpotWaypoints;
        }

        public void updateFairyGrottos(BlockPos blockPos) {
            this.fairyGrottosWaypoints.put(blockPos, 0);
        }

        public ConcurrentHashMap<BlockPos, Integer> getFairyGrottos() {
            return this.fairyGrottosWaypoints;
        }

        public void updateWormFishing(BlockPos blockPos) {
            this.wormFishingWaypoints.put(blockPos, 0);
        }

        public ConcurrentHashMap<BlockPos, Integer> getWormFishing() {
            return this.wormFishingWaypoints;
        }

        public void updateDragonNest(BlockPos blockPos) {
            this.dragonNestWaypoints.put(blockPos, 0);
        }

        public ConcurrentHashMap<BlockPos, Integer> getDragonNest() {
            return this.dragonNestWaypoints;
        }
    }

    private static final Pattern patternControlCode = Pattern.compile("\\u00A7([0-9a-fk-or])", Pattern.CASE_INSENSITIVE);
    public static final HashMap<String, World> worlds = new HashMap<>();
    private static int cooldown = 100;
    private static boolean initialScan = false;
    private static long lastScan = 0;
    public static boolean checkMobs = GumTuneClientConfig.worldScannerScanFrequency == 17;
    private static final HashMap<String, String[]> alternativeNames = new HashMap<String, String[]>() {{
        put("§6King", new String[] { "§6King", "§6Goblin King" });
        put("§6Queen", new String[] { "§6Queen", "§6Goblin Queen", "§6Queen's Den", "§6Goblin Queen's Den" });
        put("§2Divan", new String[] { "§2Divan", "§2Mines of Divan", "§2Mines" });
        put("§5Temple", new String[] { "§5Temple", "§5Jungle Temple" });
        put("§bCity", new String[] { "§bCity", "§bPrecursor City"});
        put("§6Bal", new String[] { "§6Bal", "§6Khazad-dûm", "§6Khazad-dum" });
    }};
    private static final HashMap<String, String> internalSkytilsNames = new HashMap<String, String>() {{
        put("§6King", "internal_king");
        put("§6Queen", "internal_den");
        put("§2Divan", "internal_mines");
        put("§5Temple", "internal_temple");
        put("§bCity", "internal_city");
        put("§6Bal", "internal_bal");
    }};

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
    public void onTick(TickEvent.ClientTickEvent event) throws IllegalAccessException {
        if (GumTuneClient.mc.theWorld == null || GumTuneClient.mc.thePlayer == null) return;
        if (!GumTuneClientConfig.worldScanner) return;
        if (event.phase == TickEvent.Phase.START) return;
        if (cooldown > 0) {
            cooldown--;
        }
        if (cooldown == 1 && !worlds.containsKey(LocationUtils.serverName)) {
            worlds.put(LocationUtils.serverName, new World(LocationUtils.serverName));
        }
        if (cooldown == 0) {
            if (GumTuneClientConfig.worldScannerScanMode == 0 && initialScan) return;
            World currentWorld = worlds.get(LocationUtils.serverName);
            if (currentWorld == null) return;
            if (System.currentTimeMillis() - lastScan > GumTuneClientConfig.worldScannerScanFrequency * 1000L) {
                initialScan = true;
                lastScan = System.currentTimeMillis();
                Object object = ReflectionUtils.field(GumTuneClient.mc.theWorld.getChunkProvider(), "field_73237_c");
                if (object instanceof List) {
                    for (Chunk chunk : (List<Chunk>) object) {
                        handleChunkLoad(chunk, currentWorld);
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public void onWorldUnload(WorldEvent.Unload event) {
        checkMobs = GumTuneClientConfig.worldScannerScanFrequency == 14;
        cooldown = 100;
        initialScan = false;
    }

    @SubscribeEvent
    public void onRender(RenderWorldLastEvent event) {
        if (!GumTuneClientConfig.worldScanner || GumTuneClient.mc.theWorld == null || GumTuneClient.mc.thePlayer == null) return;
        if (cooldown != 0) return;
        if (!LocationUtils.onSkyblock) return;
        World currentWorld = worlds.get(LocationUtils.serverName);
        if (currentWorld == null) return;
        if (WorldScannerFilter.worldScannerCHCrystals) {
            for (Map.Entry<String, BlockPos> entry : currentWorld.getCrystalWaypoints().entrySet()) {
                BlockPos blockPos = entry.getValue();
                RenderUtils.renderEspBox(blockPos, event.partialTicks, colorCodeToColor(entry.getKey()).getRGB());
                RenderUtils.renderWaypointText(entry.getKey(), blockPos.getX() + 0.5, blockPos.getY() + 0.5, blockPos.getZ() + 0.5, event.partialTicks);
                if (WorldScannerFilter.worldScannerCHCrystalsBeacon)
                    RenderUtils.renderBeacon(blockPos, colorCodeToColor(entry.getKey()), event.partialTicks);
            }
        }
        if (WorldScannerFilter.worldScannerCHMobSpots) {
            for (Map.Entry<String, BlockPos> entry : currentWorld.getMobSpotWaypoints().entrySet()) {
                BlockPos blockPos = entry.getValue();
                RenderUtils.renderEspBox(blockPos, event.partialTicks, Color.RED.getRGB());
                RenderUtils.renderWaypointText(entry.getKey(), blockPos.getX() + 0.5, blockPos.getY() + 0.5, blockPos.getZ() + 0.5, event.partialTicks);
                if (WorldScannerFilter.worldScannerCHMobSpotsBeacon)
                    RenderUtils.renderBeacon(blockPos, Color.RED, event.partialTicks);
            }
        }
        if (WorldScannerFilter.worldScannerCHFairyGrottos) {
            for (BlockPos blockPos : currentWorld.getFairyGrottos().keySet()) {
                RenderUtils.renderEspBox(blockPos, event.partialTicks, Color.PINK.getRGB());
                RenderUtils.renderWaypointText("§dFairy Grotto", blockPos.getX() + 0.5, blockPos.getY() + 0.5, blockPos.getZ() + 0.5, event.partialTicks);
                if (WorldScannerFilter.worldScannerCHFairyGrottosBeacon)
                    RenderUtils.renderBeacon(blockPos, Color.PINK, event.partialTicks);
            }
        }
        if (WorldScannerFilter.worldScannerCHWormFishing) {
            for (BlockPos blockPos : currentWorld.getWormFishing().keySet()) {
                RenderUtils.renderEspBox(blockPos, event.partialTicks, Color.ORANGE.getRGB());
                RenderUtils.renderWaypointText("§6Worm Fishing", blockPos.getX() + 0.5, blockPos.getY() + 0.5, blockPos.getZ() + 0.5, event.partialTicks);
                if (WorldScannerFilter.worldScannerCHWormFishingBeacon)
                    RenderUtils.renderBeacon(blockPos, Color.ORANGE, event.partialTicks);
            }
        }
        if (WorldScannerFilter.worldScannerCHGoldenDragonNest) {
            for (BlockPos blockPos : currentWorld.getDragonNest().keySet()) {
                RenderUtils.renderEspBox(blockPos, event.partialTicks, Color.ORANGE.getRGB());
                RenderUtils.renderWaypointText("§6Dragon Nest", blockPos.getX() + 0.5, blockPos.getY() + 0.5, blockPos.getZ() + 0.5, event.partialTicks);
                if (WorldScannerFilter.worldScannerCHGoldenDragonNestBeacon)
                    RenderUtils.renderBeacon(blockPos, Color.ORANGE, event.partialTicks);
            }
        }
    }

    public static void handleChunkLoad(Chunk chunk, World currentWorld) {
        for (int x = 0; x < 16; x++) {
            for (int y = 0; y < 200; y++) {
                for (int z = 0; z < 16; z++) {
                    if (WorldScannerFilter.worldScannerCHCrystals && LocationUtils.currentIsland == LocationUtils.Island.CRYSTAL_HOLLOWS) {
                        // queen
                        if (!currentWorld.getCrystalWaypoints().containsKey("§6Queen") && chunk.getBlock(x, y, z) == Blocks.stone &&
                                chunk.getBlock(x, y + 1, z) == Blocks.log2 &&
                                chunk.getBlock(x, y + 2, z) == Blocks.log2 &&
                                chunk.getBlock(x, y + 3, z) == Blocks.log2 &&
                                chunk.getBlock(x, y + 4, z) == Blocks.log2 &&
                                chunk.getBlock(x, y + 5, z) == Blocks.cauldron) {
                            sendCoordinatesMessage("§6Queen", chunk.xPosition * 16 + x, y + 5, chunk.zPosition * 16 + z);
                            addToSkytilsMap("§6Queen", chunk.xPosition * 16 + x, y + 5, chunk.zPosition * 16 + z);
                            currentWorld.updateCrystalWaypoints("§6Queen", new BlockPos(chunk.xPosition * 16 + x, y + 5, chunk.zPosition * 16 + z));
                            return;
                        }
                        // divan
                        if (!currentWorld.getCrystalWaypoints().containsKey("§2Divan") && chunk.getBlock(x, y, z) == Blocks.quartz_block && // pillar
                                chunk.getBlock(x, y + 1, z) == Blocks.quartz_stairs &&
                                chunk.getBlock(x, y + 2, z) == Blocks.stone_brick_stairs &&
                                chunk.getBlock(x, y + 3, z) == Blocks.stonebrick) { // chiseled
                            sendCoordinatesMessage("§2Divan", chunk.xPosition * 16 + x, y + 5, chunk.zPosition * 16 + z);
                            addToSkytilsMap("§2Divan", chunk.xPosition * 16 + x, y + 5, chunk.zPosition * 16 + z);
                            currentWorld.updateCrystalWaypoints("§2Divan", new BlockPos(chunk.xPosition * 16 + x, y + 5, chunk.zPosition * 16 + z));
                            return;
                        }
                        // temple
                        if (!currentWorld.getCrystalWaypoints().containsKey("§5Temple Crystal") && chunk.getBlock(x, y, z) == Blocks.bedrock &&
                                chunk.getBlock(x, y + 1, z) == Blocks.clay &&
                                chunk.getBlock(x, y + 2, z) == Blocks.clay &&
                                chunk.getBlock(x, y + 3, z) == Blocks.stained_hardened_clay && // color lime
                                chunk.getBlock(x, y + 4, z) == Blocks.wool && // color green
                                chunk.getBlock(x, y + 5, z) == Blocks.leaves &&
                                chunk.getBlock(x, y + 6, z) == Blocks.leaves) { // oak leaves
                            sendCoordinatesMessage("§5Temple", chunk.xPosition * 16 + x - 45, y + 47, chunk.zPosition * 16 + z - 18);
                            addToSkytilsMap("§5Temple", chunk.xPosition * 16 + x - 45, y + 47, chunk.zPosition * 16 + z - 18);
                            currentWorld.updateCrystalWaypoints("§5Temple Crystal", new BlockPos(chunk.xPosition * 16 + x + 9, y + 2, chunk.zPosition * 16 + z));
                            currentWorld.updateCrystalWaypoints("§5Temple Door Guardian", new BlockPos(chunk.xPosition * 16 + x - 45, y + 47, chunk.zPosition * 16 + z - 18));
                            return;
                        }
                        // city
                        if (!currentWorld.getCrystalWaypoints().containsKey("§bCity") && chunk.getBlock(x, y, z) == Blocks.cobblestone &&
                                chunk.getBlock(x, y + 1, z) == Blocks.cobblestone &&
                                chunk.getBlock(x, y + 2, z) == Blocks.cobblestone &&
                                chunk.getBlock(x, y + 3, z) == Blocks.cobblestone &&
                                chunk.getBlock(x, y + 4, z) == Blocks.stone_stairs &&
                                chunk.getBlock(x, y + 5, z) == Blocks.stone && getBlockState(chunk, x, y + 5, z).getValue(BlockStone.VARIANT) == BlockStone.EnumType.ANDESITE_SMOOTH  &&  // smooth andesite
                                chunk.getBlock(x, y + 6, z) == Blocks.stone && getBlockState(chunk, x, y + 5, z).getValue(BlockStone.VARIANT) == BlockStone.EnumType.ANDESITE_SMOOTH  &&  // smooth andesite
                                chunk.getBlock(x, y + 7, z) == Blocks.dark_oak_stairs) {
                            sendCoordinatesMessage("§bCity", chunk.xPosition * 16 + x + 24, y, chunk.zPosition * 16 + z - 17);
                            addToSkytilsMap("§bCity", chunk.xPosition * 16 + x + 24, y, chunk.zPosition * 16 + z - 17);
                            currentWorld.updateCrystalWaypoints("§bCity", new BlockPos(chunk.xPosition * 16 + x + 24, y, chunk.zPosition * 16 + z - 17));
                            return;
                        }
                        // king
                        if (!currentWorld.getCrystalWaypoints().containsKey("§6King") && chunk.getBlock(x, y, z) == Blocks.wool && // color red
                                chunk.getBlock(x, y + 1, z) == Blocks.dark_oak_stairs &&
                                chunk.getBlock(x, y + 2, z) == Blocks.dark_oak_stairs &&
                                chunk.getBlock(x, y + 3, z) == Blocks.dark_oak_stairs) {
                            sendCoordinatesMessage("§6King", chunk.xPosition * 16 + x + 1, y - 1, chunk.zPosition * 16 + z + 2);
                            addToSkytilsMap("§6King", chunk.xPosition * 16 + x + 1, y - 1, chunk.zPosition * 16 + z + 2);
                            currentWorld.updateCrystalWaypoints("§6King", new BlockPos(chunk.xPosition * 16 + x + 1, y - 1, chunk.zPosition * 16 + z + 2));
                            return;
                        }
                        // balls
                        if (!currentWorld.getCrystalWaypoints().containsKey("§6Bal") && y < 80 && chunk.getBlock(x, y, z) == Blocks.lava &&
                                chunk.getBlock(x, y + 1, z) == Blocks.barrier &&
                                chunk.getBlock(x, y + 2, z) == Blocks.barrier &&
                                chunk.getBlock(x, y + 3, z) == Blocks.barrier &&
                                chunk.getBlock(x, y + 4, z) == Blocks.barrier &&
                                chunk.getBlock(x, y + 5, z) == Blocks.barrier &&
                                chunk.getBlock(x, y + 6, z) == Blocks.barrier &&
                                chunk.getBlock(x, y + 7, z) == Blocks.barrier &&
                                chunk.getBlock(x, y + 8, z) == Blocks.barrier &&
                                chunk.getBlock(x, y + 9, z) == Blocks.barrier &&
                                chunk.getBlock(x, y + 10, z) == Blocks.barrier) {
                            sendCoordinatesMessage("§6Bal", chunk.xPosition * 16 + x + 1, y - 1, chunk.zPosition * 16 + z + 2);
                            addToSkytilsMap("§6Bal", chunk.xPosition * 16 + x + 1, y - 1, chunk.zPosition * 16 + z + 2);
                            currentWorld.updateCrystalWaypoints("§6Bal", new BlockPos(chunk.xPosition * 16 + x + 1, y - 1, chunk.zPosition * 16 + z + 2));
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
                            currentWorld.updateMobSpotWaypoints("§6Goblin Hall", new BlockPos(chunk.xPosition * 16 + x, y + 7, chunk.zPosition * 16 + z));
                            return;
                        }
                        // goblin ring
                        if (chunk.getBlock(x, y, z) == Blocks.oak_fence && // spruce
                                chunk.getBlock(x, y + 1, z) == Blocks.skull &&
                                chunk.getBlock(x, y + 6, z) == Blocks.wooden_slab && getBlockState(chunk, x, y + 6, z).getValue(BlockSlab.HALF) == BlockSlab.EnumBlockHalf.TOP &&
                                chunk.getBlock(x, y + 7, z) == Blocks.wooden_slab && getBlockState(chunk, x, y + 7, z).getValue(BlockSlab.HALF) == BlockSlab.EnumBlockHalf.BOTTOM &&
                                chunk.getBlock(x, y + 11, z) == Blocks.planks && getBlockState(chunk, x, y + 11, z).getValue(BlockPlanks.VARIANT) == BlockPlanks.EnumType.SPRUCE) { // spruce
                            currentWorld.updateMobSpotWaypoints("§6Goblin Ring", new BlockPos(chunk.xPosition * 16 + x, y + 11, chunk.zPosition * 16 + z));
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
                            currentWorld.updateMobSpotWaypoints("§bGrunt Bridge", new BlockPos(chunk.xPosition * 16 + x, y - 1, chunk.zPosition * 16 + z - 45));
                            return;
                        }
                        // trapped slime spiral
                        if (chunk.getBlock(x, y, z) == Blocks.stone_slab && getBlockState(chunk, x, y, z).getValue(BlockSlab.HALF) == BlockSlab.EnumBlockHalf.BOTTOM &&
                                chunk.getBlock(x, y + 1, z) == Blocks.stone_slab && getBlockState(chunk, x, y + 1, z).getValue(BlockSlab.HALF) == BlockSlab.EnumBlockHalf.TOP &&
                                chunk.getBlock(x, y + 2, z) == Blocks.netherrack &&
                                chunk.getBlock(x, y + 4, z) == Blocks.stone_brick_stairs &&
                                chunk.getBlock(x, y + 5, z) == Blocks.stone_slab && getBlockState(chunk, x, y + 5, z).getValue(BlockSlab.HALF) == BlockSlab.EnumBlockHalf.BOTTOM &&
                                chunk.getBlock(x, y + 7, z) == Blocks.stone_slab && getBlockState(chunk, x, y + 7, z).getValue(BlockSlab.HALF) == BlockSlab.EnumBlockHalf.TOP) {
                            currentWorld.updateMobSpotWaypoints("§aTrapped Slime Spiral", new BlockPos(chunk.xPosition * 16 + x, y, chunk.zPosition * 16 + z));
                            return;
                        }
                        // goblin diggy-hole
                        if (chunk.getBlock(x, y, z) == Blocks.stonebrick &&
                                chunk.getBlock(x, y + 1, z) == Blocks.stonebrick &&
                                chunk.getBlock(x, y + 2, z) == Blocks.stonebrick &&
                                chunk.getBlock(x, y + 3, z) == Blocks.stonebrick &&
                                chunk.getBlock(x, y + 24, z) == Blocks.stonebrick &&
                                chunk.getBlock(x, y + 25, z) == Blocks.stonebrick &&
                                chunk.getBlock(x, y + 26, z) == Blocks.fire &&
                                chunk.getBlock(x, y + 27, z) == Blocks.stonebrick && checkMobs) {
                            currentWorld.updateMobSpotWaypoints("§6Goblin Diggy Hole", new BlockPos(chunk.xPosition * 16 + x + 23, y + 11, chunk.zPosition * 16 + z + 17));
                            return;
                        }
                        // goblin campsite
                        if (chunk.getBlock(x, y, z) == Blocks.stone_slab && getBlockState(chunk, x, y, z).getValue(BlockSlab.HALF) == BlockSlab.EnumBlockHalf.BOTTOM &&
                                chunk.getBlock(x, y + 1, z) != Blocks.stone_slab &&
                                chunk.getBlock(x, y + 14, z) == Blocks.stone_slab && getBlockState(chunk, x, y + 14, z).getValue(BlockSlab.HALF) == BlockSlab.EnumBlockHalf.TOP &&
                                chunk.getBlock(x, y + 15, z) == Blocks.double_stone_slab &&
                                chunk.getBlock(x, y + 17, z) == Blocks.stone_slab && getBlockState(chunk, x, y + 17, z).getValue(BlockSlab.HALF) == BlockSlab.EnumBlockHalf.TOP &&
                                chunk.getBlock(x, y + 18, z) == Blocks.stonebrick && checkMobs) {
                            currentWorld.updateMobSpotWaypoints("§6Goblin Campsite", new BlockPos(chunk.xPosition * 16 + x, y - 3, chunk.zPosition * 16 + z + 34));
                            return;
                        }
                        // grunt rails 1
                        if (chunk.getBlock(x, y, z) == Blocks.planks && getBlockState(chunk, x, y, z).getValue(BlockPlanks.VARIANT) == BlockPlanks.EnumType.SPRUCE &&
                                chunk.getBlock(x, y + 1, z) == Blocks.air &&
                                chunk.getBlock(x, y + 2, z) == Blocks.wall_sign &&
                                chunk.getBlock(x, y + 3, z) == Blocks.air &&
                                chunk.getBlock(x, y + 4, z) == Blocks.air &&
                                chunk.getBlock(x, y + 5, z) == Blocks.air &&
                                chunk.getBlock(x, y + 6, z) == Blocks.air &&
                                chunk.getBlock(x, y + 7, z) == Blocks.planks && getBlockState(chunk, x, y + 7, z).getValue(BlockPlanks.VARIANT) == BlockPlanks.EnumType.SPRUCE &&
                                chunk.getBlock(x, y + 8, z) == Blocks.tnt) {
                            currentWorld.updateMobSpotWaypoints("§bGrunt Rails 1", new BlockPos(chunk.xPosition * 16 + x, y, chunk.zPosition * 16 + z));
                            return;
                        }
                        // grunt hero statue
                        if (chunk.getBlock(x, y, z) == Blocks.stone_slab && getBlockState(chunk, x, y, z).getValue(BlockSlab.HALF) == BlockSlab.EnumBlockHalf.TOP &&
                                chunk.getBlock(x, y + 1, z) == Blocks.stone && getBlockState(chunk, x, y + 1, z).getValue(BlockStone.VARIANT) == BlockStone.EnumType.DIORITE &&
                                chunk.getBlock(x, y + 2, z) == Blocks.stone && getBlockState(chunk, x, y + 2, z).getValue(BlockStone.VARIANT) == BlockStone.EnumType.DIORITE &&
                                chunk.getBlock(x, y + 3, z) == Blocks.stone && getBlockState(chunk, x, y + 3, z).getValue(BlockStone.VARIANT) == BlockStone.EnumType.DIORITE &&
                                chunk.getBlock(x, y + 4, z) == Blocks.stone && getBlockState(chunk, x, y + 4, z).getValue(BlockStone.VARIANT) == BlockStone.EnumType.DIORITE &&
                                chunk.getBlock(x, y + 5, z) == Blocks.cobblestone &&
                                chunk.getBlock(x, y + 6, z) == Blocks.stone && getBlockState(chunk, x, y + 6, z).getValue(BlockStone.VARIANT) == BlockStone.EnumType.ANDESITE_SMOOTH &&
                                chunk.getBlock(x, y + 7, z) == Blocks.cobblestone &&
                                chunk.getBlock(x, y + 8, z) == Blocks.stone_stairs) {
                            currentWorld.updateMobSpotWaypoints("§bGrunt Hero Statue", new BlockPos(chunk.xPosition * 16 + x, y, chunk.zPosition * 16 + z));
                            return;
                        }
                        // small grunt bridge
                        if (chunk.getBlock(x, y, z) == Blocks.air &&
                                chunk.getBlock(x, y + 1, z) == Blocks.spruce_stairs &&
                                chunk.getBlock(x, y + 2, z) == Blocks.spruce_stairs &&
                                chunk.getBlock(x, y + 3, z) == Blocks.air &&
                                chunk.getBlock(x, y + 4, z) == Blocks.spruce_stairs &&
                                chunk.getBlock(x, y + 5, z) == Blocks.log && getBlockState(chunk, x, y + 5, z).getValue(BlockPlanks.VARIANT) == BlockPlanks.EnumType.OAK &&
                                chunk.getBlock(x, y + 6, z) == Blocks.oak_fence &&
                                chunk.getBlock(x, y + 7, z) == Blocks.torch) {
                            currentWorld.updateMobSpotWaypoints("§bGrunt Small Bridge", new BlockPos(chunk.xPosition * 16 + x, y, chunk.zPosition * 16 + z));
                            return;
                        }
                        // key guardian spiral
                        if (chunk.getBlock(x, y, z) == Blocks.jungle_stairs &&
                                chunk.getBlock(x, y + 1, z) == Blocks.planks &&
                                chunk.getBlock(x, y + 2, z) == Blocks.glowstone &&
                                chunk.getBlock(x, y + 3, z) == Blocks.carpet &&
                                chunk.getBlock(x, y + 4, z) == Blocks.air &&
                                chunk.getBlock(x, y + 5, z) == Blocks.wooden_slab &&
                                chunk.getBlock(x, y + 6, z) == Blocks.air &&
                                chunk.getBlock(x, y + 7, z) == Blocks.jungle_stairs &&
                                chunk.getBlock(x, y + 8, z) == Blocks.stone &&
                                chunk.getBlock(x, y + 9, z) == Blocks.stone &&
                                chunk.getBlock(x, y + 10, z) == Blocks.stone) {
                            currentWorld.updateMobSpotWaypoints("§aKey Guardian Spiral", new BlockPos(chunk.xPosition * 16 + x, y, chunk.zPosition * 16 + z));
                            return;
                        }
                        // sludge waterfalls
                        if (chunk.getBlock(x, y, z) == Blocks.stone &&
                                chunk.getBlock(x, y + 1, z) == Blocks.dirt &&
                                chunk.getBlock(x, y + 2, z) == Blocks.stone && getBlockState(chunk, x, y + 2, z).getValue(BlockStone.VARIANT) == BlockStone.EnumType.GRANITE_SMOOTH &&
                                chunk.getBlock(x, y + 3, z) == Blocks.jungle_stairs &&
                                chunk.getBlock(x, y + 4, z) == Blocks.air &&
                                chunk.getBlock(x, y + 5, z) == Blocks.air &&
                                chunk.getBlock(x, y + 6, z) == Blocks.air &&
                                chunk.getBlock(x, y + 7, z) == Blocks.air &&
                                chunk.getBlock(x, y + 8, z) == Blocks.air) {
                            currentWorld.updateMobSpotWaypoints("§aSludge Waterfalls", new BlockPos(chunk.xPosition * 16 + x, y, chunk.zPosition * 16 + z));
                            return;
                        }
                        // sludge Bridges
                        if (chunk.getBlock(x, y, z) == Blocks.planks && getBlockState(chunk, x, y, z).getValue(BlockPlanks.VARIANT) == BlockPlanks.EnumType.JUNGLE &&
                                chunk.getBlock(x, y + 1, z) == Blocks.planks && getBlockState(chunk, x, y + 1, z).getValue(BlockPlanks.VARIANT) == BlockPlanks.EnumType.JUNGLE &&
                                chunk.getBlock(x, y + 2, z) == Blocks.planks && getBlockState(chunk, x, y + 2, z).getValue(BlockPlanks.VARIANT) == BlockPlanks.EnumType.JUNGLE &&
                                chunk.getBlock(x, y + 3, z) == Blocks.jungle_stairs &&
                                chunk.getBlock(x, y + 4, z) == Blocks.planks && getBlockState(chunk, x, y + 4, z).getValue(BlockPlanks.VARIANT) == BlockPlanks.EnumType.JUNGLE &&
                                chunk.getBlock(x, y + 5, z) == Blocks.planks && getBlockState(chunk, x, y + 5, z).getValue(BlockPlanks.VARIANT) == BlockPlanks.EnumType.JUNGLE &&
                                chunk.getBlock(x, y + 6, z) == Blocks.planks && getBlockState(chunk, x, y + 6, z).getValue(BlockPlanks.VARIANT) == BlockPlanks.EnumType.JUNGLE &&
                                chunk.getBlock(x, y + 7, z) == Blocks.jungle_stairs &&
                                chunk.getBlock(x, y + 8, z) == Blocks.planks && getBlockState(chunk, x, y + 8, z).getValue(BlockPlanks.VARIANT) == BlockPlanks.EnumType.JUNGLE &&
                                chunk.getBlock(x, y + 9, z) == Blocks.stone && getBlockState(chunk, x, y + 9, z).getValue(BlockStone.VARIANT) == BlockStone.EnumType.GRANITE &&
                                chunk.getBlock(x, y + 10, z) == Blocks.stone && getBlockState(chunk, x, y + 10, z).getValue(BlockStone.VARIANT) == BlockStone.EnumType.GRANITE) {
                            currentWorld.updateMobSpotWaypoints("§aSludge Bridges", new BlockPos(chunk.xPosition * 16 + x, y, chunk.zPosition * 16 + z));
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
                        if ((chunk.xPosition * 16 + x >= 564 && chunk.zPosition * 16 + z >= 513) || (chunk.xPosition * 16 + x >= 513 && chunk.zPosition * 16 + z >= 564)) {
                            if (y > 63 &&
                                    (chunk.getBlock(x, y, z) == Blocks.lava || chunk.getBlock(x, y, z) == Blocks.flowing_lava) &&
                                    (chunk.getBlock(x, y + 1, z) != Blocks.lava && chunk.getBlock(x, y + 1, z) != Blocks.flowing_lava)) {
                                currentWorld.updateWormFishing(new BlockPos(chunk.xPosition * 16 + x, y, chunk.zPosition * 16 + z));
                                return;
                            }
                        }
                    }

                    if (WorldScannerFilter.worldScannerCHGoldenDragonNest && LocationUtils.currentIsland == LocationUtils.Island.CRYSTAL_HOLLOWS) {
                        if (chunk.getBlock(x, y, z) == Blocks.stone &&
                                chunk.getBlock(x, y + 1, z) == Blocks.stained_hardened_clay && getBlockState(chunk, x, y + 1, z).getValue(BlockColored.COLOR) == EnumDyeColor.RED &&
                                chunk.getBlock(x, y + 2, z) == Blocks.stained_hardened_clay && getBlockState(chunk, x, y + 1, z).getValue(BlockColored.COLOR) == EnumDyeColor.RED &&
                                chunk.getBlock(x, y + 3, z) == Blocks.stained_hardened_clay && getBlockState(chunk, x, y + 1, z).getValue(BlockColored.COLOR) == EnumDyeColor.RED &&
                                chunk.getBlock(x, y + 4, z) == Blocks.skull &&
                                chunk.getBlock(x, y + 5, z) == Blocks.wool && getBlockState(chunk, x, y + 1, z).getValue(BlockColored.COLOR) == EnumDyeColor.RED) {
                            currentWorld.updateDragonNest(new BlockPos(chunk.xPosition * 16 + x, y - 3, chunk.zPosition * 16 + z + 34));
                            return;
                        }
                    }
                }
            }
        }
    }

    private static Color colorCodeToColor(String text) {
        Matcher matcher = patternControlCode.matcher(text);
        if (matcher.find()) {
            String code = matcher.group(1);
            switch (code) {
                case "4":
                    return new Color(170, 0, 0);
                case "c":
                    return new Color(255, 85, 85);
                case "6":
                    return new Color(255, 170, 0);
                case "e":
                    return new Color(255, 255, 85);
                case "2":
                    return new Color(0, 170, 0);
                case "a":
                    return new Color(85, 255, 85);
                case "b":
                    return new Color(85, 255, 255);
                case "3":
                    return new Color(0, 170, 170);
                case "1":
                    return new Color(0, 0, 170);
                case "9":
                    return new Color(85, 85, 255);
                case "d":
                    return new Color(255, 85, 255);
                case "5":
                    return new Color(170, 0, 170);
                case "f":
                    return new Color(255, 255, 255);
                case "7":
                    return new Color(170, 170, 170);
                case "8":
                    return new Color(85, 85, 85);
                case "0":
                    return new Color(0, 0, 0);
            }
        }
        return Color.WHITE;
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

    private static void sendCoordinatesMessage(String name, int x, int y, int z) {
        if (!GumTuneClientConfig.worldScannerSendCoordsInChat) return;
        if (GumTuneClientConfig.worldScannerChatMode != 0) {
            Random random = new Random();
            x = x + random.nextInt(41) - 20;
            y = y + random.nextInt(41) - 20;
            z = z + random.nextInt(41) - 20;

            if (GumTuneClientConfig.worldScannerChatMode == 2) {
                name = alternativeNames.get(name)[random.nextInt(alternativeNames.get(name).length)];
            }
        }
        ChatStyle style = new ChatStyle();
        style.setChatClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/gtc setclipboard " + StringUtils.removeFormatting(name).replace(" ", ";") + ";" + x + ";" + y + ";" + z));
        style.setChatHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ChatComponentText(EnumChatFormatting.YELLOW + "/gtc copy " + StringUtils.removeFormatting(name) + " " + x + " " + y + " " + z)));

        ChatComponentText coordsMessage = new ChatComponentText("§7[§d" + GumTuneClient.NAME + "§7] §f" + name + " §f(" + x + ", " + y + ", " + z + ")");
        coordsMessage.setChatStyle(style);

        GumTuneClient.mc.thePlayer.addChatMessage(coordsMessage);
    }

    private static void addToSkytilsMap(String name, int x, int y, int z) {
        if (!GumTuneClientConfig.worldScannerAddWaypointToSkytilsMap) return;
        ClientCommandHandler.instance.executeCommand(GumTuneClient.mc.thePlayer, "/sthw set " + x + " " + y + " " + z + " " + internalSkytilsNames.get(name));
    }

}
