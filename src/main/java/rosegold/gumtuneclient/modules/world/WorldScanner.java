package rosegold.gumtuneclient.modules.world;

import kotlin.Triple;
import net.minecraft.block.*;
import net.minecraft.block.properties.PropertyEnum;
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
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import rosegold.gumtuneclient.GumTuneClient;
import rosegold.gumtuneclient.config.GumTuneClientConfig;
import rosegold.gumtuneclient.config.pages.WorldScannerFilter;
import rosegold.gumtuneclient.events.ChunkLoadEvent;
import rosegold.gumtuneclient.utils.StringUtils;
import rosegold.gumtuneclient.utils.*;
import rosegold.gumtuneclient.utils.objects.worldscanner.Structure;
import rosegold.gumtuneclient.utils.objects.worldscanner.StructureType;

import java.awt.*;
import java.util.List;
import java.util.*;
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
        private final HashSet<Integer> chunkCache;
        private final String serverName;

        public World(String serverName) {
            this.crystalWaypoints = new ConcurrentHashMap<>();
            this.mobSpotWaypoints = new ConcurrentHashMap<>();
            this.fairyGrottosWaypoints = new ConcurrentHashMap<>();
            this.wormFishingWaypoints = new ConcurrentHashMap<>();
            this.dragonNestWaypoints = new ConcurrentHashMap<>();
            this.chunkCache = new HashSet<>();
            this.serverName = serverName;
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

        public void cacheChunk(Chunk chunk) {
            this.chunkCache.add(chunk.xPosition * 65536 + chunk.zPosition);
        }

        private boolean isChunkCached(Chunk chunk) {
            return this.chunkCache.contains(chunk.xPosition * 65536 + chunk.zPosition);
        }
    }

    private static final Pattern patternControlCode = Pattern.compile("\\u00A7([0-9a-fk-or])", Pattern.CASE_INSENSITIVE);
    public static final HashMap<String, World> worlds = new HashMap<>();
    private static int cooldown = 100;
    private static boolean initialScan = false;
    private static final HashMap<String, String[]> alternativeNames = new HashMap<String, String[]>() {{
        put("§6King", new String[]{"§6King", "§6Goblin King"});
        put("§6Queen", new String[]{"§6Queen", "§6Goblin Queen", "§6Queen's Den", "§6Goblin Queen's Den"});
        put("§2Divan", new String[]{"§2Divan", "§2Mines of Divan", "§2Mines"});
        put("§5Temple", new String[]{"§5Temple", "§5Jungle Temple"});
        put("§bCity", new String[]{"§bCity", "§bPrecursor City"});
        put("§6Bal", new String[]{"§6Bal", "§6Khazad-dûm", "§6Khazad-dum"});
    }};
    private static final HashMap<String, String> internalSkytilsNames = new HashMap<String, String>() {{
        put("§6King", "internal_king");
        put("§6Queen", "internal_den");
        put("§2Divan", "internal_mines");
        put("§5Temple", "internal_temple");
        put("§bCity", "internal_city");
        put("§6Bal", "internal_bal");
    }};
    private static long unloadedTimestamp = 0;

    @SubscribeEvent
    public void onChunkLoad(ChunkLoadEvent event) {
        if (!GumTuneClientConfig.worldScanner || GumTuneClient.mc.theWorld == null || GumTuneClient.mc.thePlayer == null)
            return;
        if (cooldown != 0) return;
        World currentWorld = worlds.get(LocationUtils.serverName);
        if (currentWorld == null) return;
        if (!currentWorld.isChunkCached(event.getChunk())) {
            handleChunkLoad(event.getChunk(), currentWorld);
            currentWorld.cacheChunk(event.getChunk());
        }
    }

    @SubscribeEvent
    @SuppressWarnings("unchecked")
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
            if (initialScan) return;
            World currentWorld = worlds.get(LocationUtils.serverName);
            if (currentWorld == null) return;
            initialScan = true;
            Object object = ReflectionUtils.field(GumTuneClient.mc.theWorld.getChunkProvider(), "field_73237_c");
            if (object instanceof List) {
                ModUtils.sendMessage("Running initial full-scan");
                for (Chunk chunk : (List<Chunk>) object) {
                    currentWorld.cacheChunk(chunk);
                    handleChunkLoad(chunk, currentWorld);
                }
            }
        }
    }

    @SubscribeEvent
    public void onWorldUnload(WorldEvent.Unload event) {
        if (System.currentTimeMillis() - unloadedTimestamp > 2000) {
            cooldown = 80;
            initialScan = false;
            unloadedTimestamp = System.currentTimeMillis();
        }
    }

    @SubscribeEvent
    public void onRender(RenderWorldLastEvent event) {
        if (!GumTuneClientConfig.worldScanner || GumTuneClient.mc.theWorld == null || GumTuneClient.mc.thePlayer == null)
            return;
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

    @SuppressWarnings("unchecked")
    private static boolean scanStructure(Chunk chunk, Structure structure, int x, int y, int z) {
        if (!structure.getQuarter().testPredicate(new BlockPos(chunk.xPosition * 16 + x, y, chunk.zPosition * 16 + z))) {
            return false;
        }

        for (int structureY = 0; structureY < structure.getStates().size(); structureY++) {
            Triple<Block, PropertyEnum, Comparable> triple = structure.getStates().get(structureY);

            if (triple.getFirst() != null && !triple.getFirst().equals(chunk.getBlock(x, y + structureY, z))) {
                return false;
            }

            if (triple.getSecond() != null && triple.getThird() != null && getBlockState(chunk, x, y + structureY, z).getValue(triple.getSecond()) != triple.getThird()) {
                return false;
            }
        }

        return true;
    }

    public static void handleChunkLoad(Chunk chunk, World currentWorld) {
        for (int x = 0; x < 16; x++) {
            for (int y = 0; y < 170; y++) {
                for (int z = 0; z < 16; z++) {
                    for (Structure structure : Structure.values()) {
                        if (LocationUtils.currentIsland == structure.getIsland()) {
                            if (structure.getStructureType().equals(StructureType.CH_CRYSTALS) && WorldScannerFilter.worldScannerCHCrystals) {
                                if (!currentWorld.getCrystalWaypoints().containsKey(structure.getName())) {
                                    if (structure != Structure.BAL || y < 80) {
                                        if (scanStructure(chunk, structure, x, y, z)) {
                                            sendCoordinatesMessage(structure.getName(), chunk.xPosition * 16 + x + structure.getXOffset(), y + structure.getYOffset(), chunk.zPosition * 16 + z + structure.getZOffset());
                                            addToSkytilsMap(structure.getName(), chunk.xPosition * 16 + x + structure.getXOffset(), y + structure.getYOffset(), chunk.zPosition * 16 + z + structure.getZOffset());
                                            currentWorld.updateCrystalWaypoints(structure.getName(), new BlockPos(chunk.xPosition * 16 + x + structure.getXOffset(), y + structure.getYOffset(), chunk.zPosition * 16 + z + structure.getZOffset()));
                                            return;
                                        }
                                    }
                                }
                            }

                            if (structure.getStructureType().equals(StructureType.CH_MOB_SPOTS) && WorldScannerFilter.worldScannerCHMobSpots) {
                                if (scanStructure(chunk, structure, x, y, z)) {
                                    currentWorld.updateMobSpotWaypoints(structure.getName(), new BlockPos(chunk.xPosition * 16 + x + structure.getXOffset(), y + structure.getYOffset(), chunk.zPosition * 16 + z + structure.getZOffset()));
                                    return;
                                }
                            }

                            if (structure.getStructureType().equals(StructureType.FAIRY_GROTTO)) {
                                if (WorldScannerFilter.worldScannerCHFairyGrottos) {
                                    if (scanStructure(chunk, structure, x, y, z)) {
                                        currentWorld.updateFairyGrottos(new BlockPos(chunk.xPosition * 16 + x, y, chunk.zPosition * 16 + z));
                                        return;
                                    }
                                } else if (WorldScannerFilter.worldScannerCHMagmaFieldsFairyGrottos && y < 64) {
                                    if (scanStructure(chunk, structure, x, y, z)) {
                                        currentWorld.updateFairyGrottos(new BlockPos(chunk.xPosition * 16 + x, y, chunk.zPosition * 16 + z));
                                        return;
                                    }
                                }
                            }

                            if (structure.getStructureType().equals(StructureType.WORM_FISHING) && WorldScannerFilter.worldScannerCHWormFishing) {
                                if ((chunk.xPosition * 16 + x >= 564 && chunk.zPosition * 16 + z >= 513) || (chunk.xPosition * 16 + x >= 513 && chunk.zPosition * 16 + z >= 564)) {
                                    if (y > 63 &&
                                            (chunk.getBlock(x, y, z) == Blocks.lava || chunk.getBlock(x, y, z) == Blocks.flowing_lava) &&
                                            (chunk.getBlock(x, y + 1, z) != Blocks.lava && chunk.getBlock(x, y + 1, z) != Blocks.flowing_lava)) {
                                        currentWorld.updateWormFishing(new BlockPos(chunk.xPosition * 16 + x, y, chunk.zPosition * 16 + z));
                                        return;
                                    }
                                }
                            }

                            if (structure.getStructureType().equals(StructureType.GOLDEN_DRAGON) && WorldScannerFilter.worldScannerCHGoldenDragonNest) {
                                if (scanStructure(chunk, structure, x, y, z)) {
                                    currentWorld.updateDragonNest(new BlockPos(chunk.xPosition * 16 + x + structure.getXOffset(), y + structure.getYOffset(), chunk.zPosition * 16 + z + structure.getZOffset()));
                                    return;
                                }
                            }
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
