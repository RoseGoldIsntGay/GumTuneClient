package rosegold.gumtuneclient.config;

import cc.polyfrost.oneconfig.config.Config;
import cc.polyfrost.oneconfig.config.annotations.*;
import cc.polyfrost.oneconfig.config.core.OneColor;
import cc.polyfrost.oneconfig.config.core.OneKeyBind;
import cc.polyfrost.oneconfig.config.data.InfoType;
import cc.polyfrost.oneconfig.config.data.Mod;
import cc.polyfrost.oneconfig.config.data.ModType;
import cc.polyfrost.oneconfig.config.data.PageLocation;
import cc.polyfrost.oneconfig.libs.universal.UKeyboard;
import rosegold.gumtuneclient.GumTuneClient;
import rosegold.gumtuneclient.config.pages.*;
import rosegold.gumtuneclient.huds.SlayerHud;
import rosegold.gumtuneclient.modules.player.AutoSell;

public class GumTuneClientConfig extends Config {

    // Categories
    private transient static final String MACRO = "Macros";
    private transient static final String WORLD = "World"; //todo better name
    private transient static final String RENDER = "Render";
    private transient static final String CONFIG = "Config";
    private transient static final String MINING = "Mining";
    private transient static final String QOL = "QoL";

    private transient static final String AVOID_BREAKING_CROPS = "Avoid Breaking Crops";
    private transient static final String ESPS = "ESPs";
    private transient static final String ESP_SETTINGS = "ESP Settings";
    private transient static final String FARMING = "Farming";
    private transient static final String DEV = "Dev";
    private transient static final String SLAYER = "Slayer";
    private transient static final String PLAYER = "Player";

    // Modules
    private transient static final String CROP_PLACER = "Crop Placer";
    private transient static final String HARP_MACRO = "Harp Macro";
    private transient static final String POWDER_CHEST_SOLVER = "Powder Chest Solver";
    private transient static final String NUKER = "Nuker";
    private transient static final String TRACKERS = "Trackers";
    private transient static final String MOB_MACRO = "Mob Macro";
    private transient static final String SERVER_SIDE_ROTATIONS = "Server Side Rotations";
    private transient static final String ROTATION_CONFIG = "Rotations Config";
    private transient static final String CAMERA = "Camera";
    private transient static final String WORLD_SCANNER = "World Scanner";
    private transient static final String METAL_DETECTOR_SOLVER = "Metal Detector Solver";
    private transient static final String MOBX_DRILL = "Mobx Drill";
    private transient static final String AUTO_SELL = "Auto Sell";
    private transient static final String BLOCK_HITBOXES = "Block Hitboxes";
    private transient static final String FAIRY_SOUL_AURA = "Fairy Soul Aura";
    private transient static final String AUTO_MADDOX = "Auto Maddox";
    private transient static final String PACKET_LOGGER = "Packet Logger";
    private transient static final String PREVENT_RENDERING_CROPS = "Prevent Rendering Crops";
    private transient static final String REVEAL_HIDDEN_MOBS = "Reveal Hidden Mobs";
    private transient static final String SKILL_TRACKER = "Skill Tracker";
    private transient static final String COPY_NBT_DATA = "Copy NBT Data";
    private transient static final String HIGHLIGHT_SLAYER_BOSS = "Highlight Slayer Boss";
    private transient static final String VISITOR_HELPERS = "Visitor Helpers";

    @Switch(
            name = "Enabled",
            category = WORLD,
            subcategory = CROP_PLACER,
            size = 2
    )
    public static boolean cropPlacer = false;

    @Slider(
            name = "Blocks Per Second",
            category = WORLD,
            subcategory = CROP_PLACER,
            min = 10, max = 80,
            step = 10
    )
    public static int cropPlacerSpeed = 20;

    @Dropdown(
            name = "Crop Type",
            category = WORLD,
            subcategory = CROP_PLACER,
            options = {"Sugar Cane", "Cactus", "Cocoa Beans"}
    )
    public static int cropPlacerCropType = 0;

    @Dropdown(
            name = "Finding Algorithm",
            category = WORLD,
            subcategory = CROP_PLACER,
            options = {"Closest first", "Furthest first"}
    )
    public static int cropPlacerFindingAlgorithm = 0;

    @Switch(
            name = "World Scanner",
            category = WORLD,
            subcategory = WORLD_SCANNER,
            size = 2
    )
    public static boolean worldScanner = false;

    @Dropdown(
            name = "Scanner Mode",
            category = WORLD,
            subcategory = WORLD_SCANNER,
            options = {"When chunk is loaded", "Timer", "Both"}
    )
    public static int worldScannerScanMode = 0;

    @Slider(
            name = "World Scan Timer",
            description = "Seconds per scan",
            category = WORLD,
            subcategory = WORLD_SCANNER,
            min = 1, max = 20,
            step = 1
    )
    public static int worldScannerScanFrequency = 10;

    @Page(
            name = "Scanner Filter",
            category = WORLD,
            subcategory = WORLD_SCANNER,
            location = PageLocation.BOTTOM
    )
    public WorldScannerFilter worldScannerFilter = new WorldScannerFilter();

    @Switch(
            name = "Send Coords In Chat",
            category = WORLD,
            subcategory = WORLD_SCANNER
    )
    public static boolean worldScannerSendCoordsInChat = false;

    @Switch(
            name = "Add Waypoint To Skytils Map",
            category = WORLD,
            subcategory = WORLD_SCANNER
    )
    public static boolean worldScannerAddWaypointToSkytilsMap = false;

    @Dropdown(
            name = "Chat Messages Mode",
            category = WORLD,
            subcategory = WORLD_SCANNER,
            options = {"Name + Coords", "Name + Slightly Randomized Coords", "Different Names + Slightly Randomize Coords"},
            size = 2
    )
    public static int worldScannerChatMode = 0;

    @Switch(
            name = "Block Hitbox Modifier",
            category = WORLD,
            subcategory = BLOCK_HITBOXES,
            size = 2
    )
    public static boolean blockHitboxesModifier = false;

    @Switch(
            name = "Enabled",
            category = MACRO,
            subcategory = HARP_MACRO,
            size = 2
    )
    public static boolean harpMacro = false;

    @Slider(
            name = "Click delay (ms): lower = faster song",
            description = "Change this slider based on song speed and ping",
            category = MACRO,
            subcategory = HARP_MACRO,
            min = 0, max = 1000
    )
    public static int harpMacroDelay = 10;

    @Info(
            text = "Remember to toggle both Nuker and it's keybind!",
            type = InfoType.INFO,
            category = MINING,
            subcategory = NUKER
    )
    public static boolean nukerReminderIgnored;

    @Switch(
            name = "Enabled",
            category = MINING,
            subcategory = NUKER,
            size = 2
    )
    public static boolean nuker = false;

    @KeyBind(
            name = "Keybind",
            category = MINING,
            subcategory = NUKER,
            size = 2
    )
    public static OneKeyBind nukerKeyBind = new OneKeyBind(UKeyboard.KEY_NONE);

    @Page(
            name = "Block Filters",
            description = "Hover over an option for more details",
            category = MINING,
            subcategory = NUKER,
            location = PageLocation.TOP
    )
    public NukerBlockFilter nukerBlockFilter = new NukerBlockFilter();

    @Dropdown(
            name = "Rotation Type",
            category = MINING,
            subcategory = NUKER,
            options = {"None", "Server Side", "Smooth Server Side"}
    )
    public static int nukerRotationType = 0;

    @Slider(
            name = "Rotation Speed",
            description = "Rotation time in ms",
            category = MINING,
            subcategory = NUKER,
            min = 50, max = 500
    )
    public static int nukerRotationSpeed = 250;

    @Switch(
            name = "Mine ALL Blocks In Front",
            description = "Mine all blocks in the way of the player",
            category = MINING,
            subcategory = NUKER,
            size = 2
    )
    public static boolean mineBlocksInFront = false;

    @Slider(
            name = "Speed",
            description = "Blocks per second",
            category = MINING,
            subcategory = NUKER,
            min = 0, max = 80
    )
    public static int nukerSpeed = 20;

    @Slider(
            name = "Range",
            description = "Range in blocks",
            category = MINING,
            subcategory = NUKER,
            min = 1, max = 5,
            step = 1
    )
    public static int nukerRange = 5;

    @Slider(
            name = "Height",
            description = "Blocks above your head",
            category = MINING,
            subcategory = NUKER,
            min = 0, max = 5
    )
    public static int nukerHeight = 0;

    @Slider(
            name = "Depth",
            description = "Blocks below your head",
            category = MINING,
            subcategory = NUKER,
            min = 0, max = 4
    )
    public static int nukerDepth = 1;

    @Dropdown(
            name = "Shape",
            category = MINING,
            subcategory = NUKER,
            options = {"Sphere", "Facing Axis", "Axis Tunnels", "Triggerbot"}
    )
    public static int nukerShape = 0;

    @Dropdown(
            name = "Algorithm",
            category = MINING,
            subcategory = NUKER,
            options = {"Closest Block (Classic)", "Smallest Rotation"}
    )
    public static int nukerAlgorithm = 0;

    @Slider(
            name = "Field of View",
            description = "Change fov of sphere shape nuker",
            category = MINING,
            subcategory = NUKER,
            min = 0, max = 361, // bruh moment
            step = 20
    )
    public static int nukerFieldOfView = 180;

    @Switch(
            name = "Preview",
            description = "Show which blocks are going to be mined",
            category = MINING,
            subcategory = NUKER,
            size = 2
    )
    public static boolean nukerPreview = false;

    @Slider(
            name = "Sideways Offset",
            description = "For facing axis mode (positive - offset to the right)",
            category = MINING,
            subcategory = NUKER,
            min = -4, max = 4,
            step = 1
    )
    public static int nukerSidewaysOffset = 0;

    @Slider(
            name = "Forwards-Backwards Offset",
            description = "For facing axis mode (positive - forwards)",
            category = MINING,
            subcategory = NUKER,
            min = -4, max = 4,
            step = 1
    )
    public static int nukerForwardsOffset = 0;

    @Slider(
            name = "Pingless reset cutoff",
            description = "Mess with this slider and see if it makes nuker faster",
            category = MINING,
            subcategory = NUKER,
            min = 0f, max = 20,
            step = 1
    )
    public static int nukerPinglessCutoff = 10;

    @Switch(
            name = "Powder Chest Solver",
            category = MINING,
            subcategory = POWDER_CHEST_SOLVER,
            size = 2
    )
    public static boolean powderChestSolver = false;
    @Switch(
            name = "Only when looking at chests?",
            category = MINING,
            subcategory = POWDER_CHEST_SOLVER,
            size = 2
    )
    public static boolean powderChestOnLook = true;


    @Slider(
            name = "Rotation time (ms)",
            description = "How many milliseconds to complete a rotation",
            category = MINING,
            subcategory = POWDER_CHEST_SOLVER,
            min = 0, max = 500
    )
    public static int powderChestRotationTime = 200;

    @Dropdown(
            name = "Pause nuker when solving",
            category = MINING,
            subcategory = POWDER_CHEST_SOLVER,
            options = {"Don't", "Pause Nuker", "Pause Nuker Rotations"}
    )
    public static int powderChestPauseNukerMode = 0;

    @Switch(
            name = "Metal Detector Solver",
            category = MINING,
            subcategory = METAL_DETECTOR_SOLVER,
            size = 2
    )
    public static boolean metalDetectorSolver = false;

    @Switch(
            name = "Show All Spots",
            description = "Show all possible spots for divan treasures",
            category = MINING,
            subcategory = METAL_DETECTOR_SOLVER,
            size = 2
    )
    public static boolean metalDetectorSolverShowAllSpots = false;

    @Switch(
            name = "Tracer",
            description = "Draw a tracer to solved treasure",
            category = MINING,
            subcategory = METAL_DETECTOR_SOLVER,
            size = 2
    )
    public static boolean metalDetectorSolverTracer = false;

    @Switch(
            name = "Cancel Client Item Update Packets",
            category = MINING,
            subcategory = MOBX_DRILL,
            description = "Originally by mobx, stops the client canceling your mining progress",
            size = 2
    )
    public static boolean cancelClientItemUpdates = false;

    @Switch(
            name = "Avoid Breaking Stems",
            category = FARMING,
            subcategory = AVOID_BREAKING_CROPS,
            description = "Prevents the client from breaking pumpkin and melon stems"
    )
    public static boolean avoidBreakingStems = false;

    @Switch(
            name = "Avoid Breaking Bottom Sugar Cane",
            category = FARMING,
            subcategory = AVOID_BREAKING_CROPS,
            description = "Prevents the client from breaking bottom sugar cane blocks"
    )
    public static boolean avoidBreakingBottomSugarCane = false;

    @Switch(
            name = "Avoid Breaking Non Fully-Grown Crops",
            category = FARMING,
            subcategory = AVOID_BREAKING_CROPS,
            description = "Prevents the client from breaking crops that are still growing"
    )
    public static boolean avoidBreakingChildCrops = false;

    @Dropdown(
            name = "Avoid Breaking Mode",
            category = FARMING,
            subcategory = AVOID_BREAKING_CROPS,
            description = "Select how the mod should act when avoiding breaking crops",
            options = {"Don't break", "Break only on the client"},
            size = 2
    )
    public static int avoidBreakingMode = 0;

    @Switch(
            name = "Prevent Rendering Crops",
            category = FARMING,
            subcategory = PREVENT_RENDERING_CROPS,
            description = "Prevents the client from rendering crops"
    )
    public static boolean preventRenderingCrops = false;

    @Switch(
            name = "ESPs",
            category = RENDER,
            subcategory = ESPS,
            description = "Required for all of the ESPs below",
            size = 4
    )
    public static boolean ESPs = false;

    @Switch(
            name = "Custom Block ESP",
            category = RENDER,
            subcategory = ESPS,
            description = "use /gtc esp",
            size = 2
    )
    public static boolean customBlockESP = false;

    @Switch(
            name = "Force Recheck",
            category = RENDER,
            subcategory = ESPS,
            description = "force recheck all blocks when /gtc esp is executed",
            size = 2
    )
    public static boolean customESPForceRecheck = false;

    @Color(
            name = "ESP Color",
            category = RENDER
    )
    public static OneColor espColor = new OneColor(26, 35, 143);

    @Switch(
            name = "Arachne's Keeper ESP",
            category = RENDER,
            subcategory = ESPS
    )
    public static boolean arachneKeeperESP = false;

    @Switch(
            name = "Frozen Treasure ESP",
            category = RENDER,
            subcategory = ESPS
    )
    public static boolean frozenTreasureESP = false;

    @Switch(
            name = "Fairy Soul ESP",
            category = RENDER,
            subcategory = ESPS
    )
    public static boolean fairySoulESP = false;

    @Page(
            name = "Frozen Treasure Filters",
            description = "Filter out treasures for the ESP",
            category = RENDER,
            subcategory = ESPS,
            location = PageLocation.BOTTOM
    )
    public FrozenTreasureFilter frozenTreasureFilter = new FrozenTreasureFilter();

    @Switch(
            name = "Reveal Hidden Mobs",
            category = RENDER,
            subcategory = REVEAL_HIDDEN_MOBS,
            size = 4
    )
    public static boolean revealHiddenMobs = false;

    @Page(
            name = "Reveal Hidden Mobs Filters",
            description = "Filter out mobs to be revealed",
            category = RENDER,
            subcategory = REVEAL_HIDDEN_MOBS,
            location = PageLocation.BOTTOM
    )
    public RevealHiddenMobsFilter revealHiddenMobsFilter = new RevealHiddenMobsFilter();

    @Switch(
            name = "Phase Camera Through Blocks",
            category = RENDER,
            subcategory = CAMERA
    )
    public static boolean phaseCameraThroughBlocks = false;

    @Switch(
            name = "Mob Macro",
            category = MACRO,
            subcategory = MOB_MACRO,
            size = 2
    )
    public static boolean mobMacro = false;

    @KeyBind(
            name = "Keybind",
            category = MACRO,
            subcategory = MOB_MACRO,
            size = 2
    )
    public static OneKeyBind mobMacroKeyBind = new OneKeyBind(UKeyboard.KEY_NONE);

    @Slider(
            name = "Delay in Ticks",
            category = MACRO,
            subcategory = MOB_MACRO,
            min = 1, max = 40
    )
    public static int mobMacroDelay = 5;

    @Switch(
            name = "Walk",
            category = MACRO,
            subcategory = MOB_MACRO
    )
    public static boolean mobMacroWalk = false;

    @Switch(
            name = "Jump",
            category = MACRO,
            subcategory = MOB_MACRO
    )
    public static boolean mobMacroJump = false;

    @Switch(
            name = "Smart Sprint",
            category = MACRO,
            subcategory = MOB_MACRO
    )
    public static boolean mobMacroSmartSprint = false;

    @Switch(
            name = "Entity Lock",
            category = MACRO,
            subcategory = MOB_MACRO,
            description = "Once targeted an entity, will not change to a different entity until it is defeated or becomes impossible to defeat"
    )
    public static boolean mobMacroEntityLock = false;

    @Page(
            name = "Mobs Filters",
            description = "Select mobs to kill with macro",
            category = MACRO,
            subcategory = MOB_MACRO,
            location = PageLocation.TOP
    )
    public MobMacroFilter mobMacroFilter = new MobMacroFilter();

    @Dropdown(
            name = "Rotation Type",
            category = MACRO,
            subcategory = MOB_MACRO,
            options = {"Instant", "Server Side", "Smooth"}
    )
    public static int mobMacroRotation = 0;

    @Dropdown(
            name = "Attack Type",
            category = MACRO,
            subcategory = MOB_MACRO,
            options = {"Precursor Eye", "Frozen Scythe (and similar items)", "Left Click"}
    )
    public static int mobMacroAttackType = 0;

    @Switch(
            name = "Auto Sell",
            category = PLAYER,
            subcategory = AUTO_SELL,
            size = 2,
            description = "Main toggle"
    )
    public static boolean autoSell = false;

    @Slider(
            name = "Auto Sell At Inventory Filled %",
            category = PLAYER,
            subcategory = AUTO_SELL,
            min = 0, max = 100
    )
    public static int autoSellInventoryFullness = 100;

    @KeyBind(
            name = "Execute Auto Sell",
            category = PLAYER,
            subcategory = AUTO_SELL,
            size = 2
    )
    public static OneKeyBind executeAutoSellKeybind = new OneKeyBind(UKeyboard.KEY_NONE);

    @Dropdown(
            name = "Auto Sell Mode",
            category = PLAYER,
            subcategory = AUTO_SELL,
            options = {"Bazaar Only", "Trades Only", "Bazaar Then Trades"}
    )
    public static int autoSellMode = 0;

    @Text(
            name = "Item Filter",
            placeholder = "SkyBlock Item IDs, separated by a - \", \"",
            category = PLAYER,
            subcategory = AUTO_SELL,
            multiline = true
    )
    public static String autoSellItemFilter = "";

    @Slider(
            name = "Sell Delay",
            category = PLAYER,
            subcategory = AUTO_SELL,
            min = 0, max = 1000
    )
    public static int autoSellClickDelay = 200;

    @KeyBind(
            name = "Add Item To Filter Keybind",
            category = PLAYER,
            subcategory = AUTO_SELL,
            size = 2
    )
    public static OneKeyBind addItemToAutoSellFilter = new OneKeyBind(UKeyboard.KEY_NONE);

    @Info(
            text = "Passive Mode will disable opening the bazaar / trades menu, instead it will sell in trades menu when you open it manually",
            category = PLAYER,
            subcategory = AUTO_SELL,
            type = InfoType.INFO,
            size = 2
    )
    public static boolean passiveModeInfoIgnored;

    @Switch(
            name = "Passive Mode",
            category = PLAYER,
            subcategory = AUTO_SELL
    )
    public static boolean autoSellPassiveMode = false;

    @Switch(
            name = "Passive Mode Close Trades",
            category = PLAYER,
            subcategory = AUTO_SELL
    )
    public static boolean autoSellPassiveModeCloseTrades = false;

    @Switch(
            name = "Fairy Soul Aura",
            category = PLAYER,
            subcategory = FAIRY_SOUL_AURA,
            size = 2
    )
    public static boolean fairySoulAura = false;

    @Switch(
            name = "Auto Maddox Batphone",
            category = SLAYER,
            subcategory = AUTO_MADDOX,
            size = 2
    )
    public static boolean autoMaddoxBatphone = false;

    @Dropdown(
            name = "Boss Type",
            category = SLAYER,
            subcategory = AUTO_MADDOX,
            options = {"Revenant", "Broodfather", "Sven", "Voidgloom", "Inferno"}
    )
    public static int autoMaddoxBossType = 0;

    @Dropdown(
            name = "Boss Level",
            category = SLAYER,
            subcategory = AUTO_MADDOX,
            options = {"1", "2", "3", "4", "5"}
    )
    public static int autoMaddoxBossLevel = 0;

    @Switch(
            name = "Always show server rotations",
            category = CONFIG,
            subcategory = SERVER_SIDE_ROTATIONS
    )
    public static boolean alwaysShowServerRotations = false;

    @Switch(
            name = "Show Waypoint Text",
            category = CONFIG,
            subcategory = ESP_SETTINGS,
            size = 2
    )
    public static boolean espWaypointText = true;

    @Switch(
            name = "Show Highlight",
            category = CONFIG,
            subcategory = ESP_SETTINGS,
            size = 2
    )
    public static boolean espHighlight = true;

    @Switch(
            name = "Show Beacon",
            category = CONFIG,
            subcategory = ESP_SETTINGS,
            size = 2
    )
    public static boolean espBeacon = false;

    @Switch(
            name = "Packet Logger",
            category = DEV,
            subcategory = PACKET_LOGGER,
            size = 2
    )
    public static boolean packetLogger = false;

    @Dropdown(
            name = "Packet Type",
            category = DEV,
            subcategory = PACKET_LOGGER,
            options = {
                    "C0APacketAnimation",
                    "C0BPacketEntityAction",
                    "C0CPacketInput",
                    "C0DPacketCloseWindow",
                    "C0EPacketClickWindow",
                    "C0FPacketConfirmTransaction",
                    "C00PacketKeepAlive",
                    "C01PacketChatMessage",
                    "C02PacketUseEntity",
                    "C03PacketPlayer",
                    "C07PacketPlayerDigging",
                    "C08PacketPlayerBlockPlacement",
                    "C09PacketHeldItemChange",
                    "C10PacketCreativeInventoryAction",
                    "C11PacketEnchantItem",
                    "C12PacketUpdateSign",
                    "C13PacketPlayerAbilities",
                    "C14PacketTabComplete",
                    "C15PacketClientSettings",
                    "C16PacketClientStatus",
                    "C17PacketCustomPayload",
                    "C18PacketSpectate",
                    "C19PacketResourcePackStatus"
            }
    )
    public static int packetLoggerType = 0;

    @Switch(
            name = "Copy NBT Data",
            category = DEV,
            subcategory = COPY_NBT_DATA,
            size = 2
    )
    public static boolean copyNBTData = false;

    @KeyBind(
            name = "Keybind",
            category = DEV,
            subcategory = COPY_NBT_DATA,
            size = 2
    )
    public static OneKeyBind copyNBTDataKeyBind = new OneKeyBind(UKeyboard.KEY_NONE);

    @Switch(
            name = "Highlight Slayer Boss",
            category = SLAYER,
            subcategory = HIGHLIGHT_SLAYER_BOSS,
            size = 2
    )
    public static boolean highlightSlayerBoss = false;

    @HUD(
            name = "Slayer Status Hud",
            category = SLAYER,
            subcategory = HIGHLIGHT_SLAYER_BOSS
    )
    public SlayerHud slayerHud = new SlayerHud();

    @Switch(
            name = "Visitor Queue Full Chat Message",
            category = FARMING,
            subcategory = VISITOR_HELPERS
    )
    public static boolean visitorQueueFullChatMessage = false;

    @Dropdown(
            name = "Message Chat Type",
            category = FARMING,
            subcategory = VISITOR_HELPERS,
            options = {"Coop Chat", "All Chat", "Party Chat", "Guild Chat"}
    )
    public static int visitorQueueFullChatType = 0;

    @Switch(
            name = "Visitor Quick Buy",
            category = FARMING,
            subcategory = VISITOR_HELPERS,
            size = 1
    )
    public static boolean visitorQuickBuy = false;

    @Dropdown(
            name = "Visitor Quick Buy Mode",
            category = FARMING,
            subcategory = VISITOR_HELPERS,
            options = {"When Shift Clicking Accept Offer", "When Opening Visitor GUI"}
    )
    public static int visitorQuickBuyMode = 0;

//    @Switch(
//            name = "Skill Tracker",
//            category = TRACKERS,
//            subcategory = SKILL_TRACKER,
//            size = 2
//    )
//    public static boolean skillTracker = false;
//
//    @Dropdown(
//            name = "Skill Type",
//            category = TRACKERS,
//            subcategory = SKILL_TRACKER,
//            options = {"Combat", "Mining", }
//    )


    public GumTuneClientConfig() {
        super(new Mod(GumTuneClient.NAME, ModType.SKYBLOCK, "/assets/" + GumTuneClient.MODID + "/gtc_small.png", 84, 84), GumTuneClient.MODID + ".json");
        registerKeyBind(nukerKeyBind, () -> {});
        registerKeyBind(mobMacroKeyBind, () -> {});
        registerKeyBind(copyNBTDataKeyBind, () -> {});
        registerKeyBind(addItemToAutoSellFilter, () -> {});
        registerKeyBind(executeAutoSellKeybind, () -> {
            AutoSell.autoSell();
        });
        initialize();
    }
}

