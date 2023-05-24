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
import rosegold.gumtuneclient.hud.SlayerHud;
import rosegold.gumtuneclient.hud.TrackersHud;

public class GumTuneClientConfig extends Config {

    // Categories
    private transient static final String MACRO = "Macros";
    private transient static final String WORLD = "World"; //todo better name
    private transient static final String RENDER = "Render";
    private transient static final String CONFIG = "Config";
    private transient static final String MINING = "Mining";
    private transient static final String QOL = "QoL";
    private transient static final String COMBAT = "Combat";

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
    private transient static final String GEMSTONE_MACRO = "Gemstone Macro";
    private transient static final String ALCHEMY_HELPER = "Alchemy Helper";
    private transient static final String HYPIXEL_API_KEY = "Hypixel API Key";
    private transient static final String AUTO_CRAFT = "Auto Craft";
    private transient static final String RIFT = "Rift";
    private transient static final String POWDER_CHEST_TRACKER = "Powder Chest Tracker";

    @Switch(
            name = "Enabled",
            category = FARMING,
            subcategory = CROP_PLACER,
            size = 2
    )
    public static boolean cropPlacer = false;

    @Slider(
            name = "Blocks Per Second",
            category = FARMING,
            subcategory = CROP_PLACER,
            min = 10, max = 80,
            step = 10
    )
    public static int cropPlacerSpeed = 20;

    @Dropdown(
            name = "Crop Type",
            category = FARMING,
            subcategory = CROP_PLACER,
            options = {"Sugar Cane", "Cactus", "Cocoa Beans", "Potato", "Carrot", "Wheat", "Pumpkin", "Melon", "Nether Wart", "Mushroom"}
    )
    public static int cropPlacerCropType = 0;

    @Dropdown(
            name = "Finding Algorithm",
            category = FARMING,
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
            name = "Toggle Keybind",
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

    @Page(
            name = "Nuker Switches",
            description = "Toggles for specific nuker configurations",
            category = MINING,
            subcategory = NUKER,
            location = PageLocation.BOTTOM
    )
    public NukerBooleanOptions nukerBooleanOptions = new NukerBooleanOptions();

    @Page(
            name = "Nuker Sliders",
            description = "Sliders for specific nuker configurations",
            category = MINING,
            subcategory = NUKER,
            location = PageLocation.BOTTOM
    )
    public NukerSliderOptions nukerSliderOptions = new NukerSliderOptions();

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

    @Dropdown(
            name = "Shape",
            category = MINING,
            subcategory = NUKER,
            options = {"Sphere", "Facing Axis", "Axis Tunnels", "Triggerbot", "Route Nuker"}
    )
    public static int nukerShape = 0;

    @Dropdown(
            name = "Algorithm",
            category = MINING,
            subcategory = NUKER,
            options = {"Closest Block (Classic)", "Smallest Rotation"}
    )
    public static int nukerAlgorithm = 0;

    @Switch(
            name = "Powder Chest Solver",
            category = MINING,
            subcategory = POWDER_CHEST_SOLVER,
            size = 2
    )
    public static boolean powderChestSolver = false;

    @Switch(
            name = "Legit Mode",
            category = MINING,
            subcategory = POWDER_CHEST_SOLVER
    )
    public static boolean powderChestSolverLegitMode = false;

    @Switch(
            name = "Smooth Rotations",
            category = MINING,
            subcategory = POWDER_CHEST_SOLVER
    )
    public static boolean powderChestSolverSmoothRotations = false;

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
            name = "AOTV Gemstone Macro",
            category = MACRO,
            subcategory = GEMSTONE_MACRO,
            description = "AOTVs around in a set path, mines gemstones around you",
            size = 2
    )
    public static boolean aotvGemstoneMacro = false;

    @Page(
            name = "Gemstone Macro Filters",
            description = "Filter out gemstone types",
            category = MACRO,
            subcategory = GEMSTONE_MACRO,
            location = PageLocation.BOTTOM
    )
    public GemstoneTypeFilter gemstoneTypeFilter = new GemstoneTypeFilter();

    @Page(
            name = "Gemstone Macro Routes",
            description = "Create and edit AOTV routes",
            category = MACRO,
            subcategory = GEMSTONE_MACRO,
            location = PageLocation.BOTTOM
    )
    public GemstoneMacroAOTVRoutes gemstoneMacroAOTVRoutes = new GemstoneMacroAOTVRoutes();

    @Slider(
            name = "Rotation Speed",
            description = "Rotation time in ms",
            category = MACRO,
            subcategory = GEMSTONE_MACRO,
            min = 50, max = 500
    )
    public static int aotvGemstoneMacroRotationSpeed = 250;

    @Switch(
            name = "Reset State on Toggle",
            category = MACRO,
            subcategory = GEMSTONE_MACRO,
            description = "Resets gemstone macro state when toggled"
    )
    public static boolean aotvGemstoneMacroResetStateOnToggle = false;

    @Dropdown(
            name = "Mining Mode",
            category = MACRO,
            subcategory = GEMSTONE_MACRO,
            options = {"Legit", "Nuker", "Armadillo"}
    )
    public static int aotvGemstoneMacroMiningMode = 0;

    @KeyBind(
            name = "Toggle Keybind",
            category = MACRO,
            subcategory = GEMSTONE_MACRO,
            size = 2
    )
    public static OneKeyBind gemstoneMacroToggleKeyBind = new OneKeyBind(UKeyboard.KEY_NONE);

    @KeyBind(
            name = "Add Block To AOTV Path",
            category = MACRO,
            subcategory = GEMSTONE_MACRO,
            size = 2
    )
    public static OneKeyBind gemstoneMacroAddToPathKeyBind = new OneKeyBind(UKeyboard.KEY_NONE);

    @Switch(
            name = "Show Blocks Blocking Path",
            category = MACRO,
            subcategory = GEMSTONE_MACRO,
            description = "Highlight blocks that might prevent AOTV from working"
    )
    public static boolean aotvGemstoneShowBlocksBlockingPath = false;

    @Switch(
            name = "Mine Panes",
            category = MACRO,
            subcategory = GEMSTONE_MACRO,
            description = "Mine gemstone panes along with full blocks"
    )
    public static boolean aotvGemstoneMinePanes = false;

    @Slider(
            name = "Waypoint Render Distance",
            description = "Set a maximum amount of waypoints to render at a time, to reduce lag",
            category = MACRO,
            subcategory = GEMSTONE_MACRO,
            min = 0, max = 200
    )
    public static int aotvGemstoneMacroWaypointRenderDistance = 0;

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
            size = 2
    )
    public static boolean ESPs = false;

    @Switch(
            name = "Custom Block ESP",
            category = RENDER,
            subcategory = ESPS,
            description = "use /gtc esp"
    )
    public static boolean customBlockESP = false;

    @Slider(
            name = "Custom Block ESP Range (0 = infinite)",
            description = "Might cause a bit of lag tbh idk",
            category = RENDER,
            subcategory = ESPS,
            min = 0, max = 60
    )
    public static int customBlockESPRange = 0;

    @Switch(
            name = "Force Recheck",
            category = RENDER,
            subcategory = ESPS,
            description = "force recheck all blocks when /gtc esp is executed"
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

    @Switch(
            name = "Crystal Hollows Mob ESP",
            category = RENDER,
            subcategory = ESPS
    )
    public static boolean crystalHollowsMobESP = false;

    @Page(
            name = "Rift ESPs",
            description = "Special ESPs dedicated to the rift",
            category = RENDER,
            subcategory = ESPS,
            location = PageLocation.BOTTOM
    )
    public RiftESPs riftESPs = new RiftESPs();

    @Info(
            text = "Configuration Options For ESP",
            category = RENDER,
            subcategory = ESPS,
            type = InfoType.INFO,
            size = 2
    )
    public static boolean configurationOptionsForESPIgnored;

    @Switch(
            name = "Entity - Render Waypoint",
            category = RENDER,
            subcategory = ESPS
    )
    public static boolean entityRenderWaypoint = false;

    @Switch(
            name = "Custom Block ESP - Render Tracer",
            category = RENDER,
            subcategory = ESPS
    )
    public static boolean customBlockESPRenderTracer = false;

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

    @Info(
            text = "Remember to toggle both Mob Macro and it's keybind!",
            type = InfoType.INFO,
            category = MACRO,
            subcategory = MOB_MACRO
    )
    public static boolean mobMacroReminderIgnored;

    @Switch(
            name = "Mob Macro",
            category = MACRO,
            subcategory = MOB_MACRO,
            size = 2
    )
    public static boolean mobMacro = false;

    @KeyBind(
            name = "Toggle Keybind",
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
            name = "Auto Craft",
            category = PLAYER,
            subcategory = AUTO_CRAFT,
            size = 2,
            description = "Main toggle"
    )
    public static boolean autoCraft = false;

    @Slider(
            name = "Auto Craft At Inventory Filled %",
            category = PLAYER,
            subcategory = AUTO_CRAFT,
            min = 0, max = 100
    )
    public static int autoCraftInventoryFullness = 100;

    @Text(
            name = "Item Filter",
            placeholder = "SkyBlock Item IDs, separated by a - \", \"",
            category = PLAYER,
            subcategory = AUTO_CRAFT,
            multiline = true
    )
    public static String autoCraftItemFilter = "";

    @Slider(
            name = "Craft Delay",
            category = PLAYER,
            subcategory = AUTO_CRAFT,
            min = 0, max = 1000
    )
    public static int autoCraftClickDelay = 200;

    @KeyBind(
            name = "Add Item To Filter Keybind",
            category = PLAYER,
            subcategory = AUTO_CRAFT,
            size = 2
    )
    public static OneKeyBind addItemToAutoCraftFilter = new OneKeyBind(UKeyboard.KEY_NONE);


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
            category = RENDER,
            subcategory = SERVER_SIDE_ROTATIONS
    )
    public static boolean alwaysShowServerRotations = false;

    @HypixelKey
    @Text(
            name = "Hypixel API Key",
            placeholder = "Run /api new",
            category = CONFIG,
            subcategory = HYPIXEL_API_KEY
    )
    public static String hypixelApiKey = "";

    @Switch(
            name = "Client Packet Logger",
            category = DEV,
            subcategory = PACKET_LOGGER,
            size = 2
    )
    public static boolean clientPacketLogger = false;

    @Dropdown(
            name = "Client Packet Type 1",
            category = DEV,
            subcategory = PACKET_LOGGER,
            options = {
                    "None",
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
            }
    )
    public static int packetLoggerClientType1 = 0;

    @Dropdown(
            name = "Client Packet Type 2",
            category = DEV,
            subcategory = PACKET_LOGGER,
            options = {
                    "None",
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
    public static int packetLoggerClientType2 = 0;

    @Switch(
            name = "Server Packet Logger",
            category = DEV,
            subcategory = PACKET_LOGGER,
            size = 2
    )
    public static boolean serverPacketLogger = false;

    @Dropdown(
            name = "Server Packet Type 1",
            category = DEV,
            subcategory = PACKET_LOGGER,
            options = {
                    "None",
                    "S0APacketUseBed",
                    "S0BPacketAnimation",
                    "S0CPacketSpawnPlayer",
                    "S0DPacketCollectItem",
                    "S0EPacketSpawnObject",
                    "S0FPacketSpawnMob",
                    "S00PacketKeepAlive",
                    "S1BPacketEntityAttach",
                    "S1CPacketEntityMetadata",
                    "S1DPacketEntityEffect",
                    "S1EPacketRemoveEntityEffect",
            }
    )
    public static int packetLoggerServerType1 = 0;

    @Dropdown(
            name = "Server Packet Type 2",
            category = DEV,
            subcategory = PACKET_LOGGER,
            options = {
                    "None",
                    "S1FPacketSetExperience",
                    "S01PacketJoinGame",
                    "S2APacketParticles",
                    "S2BPacketChangeGameState",
                    "S2CPacketSpawnGlobalEntity",
                    "S2DPacketOpenWindow",
                    "S2EPacketCloseWindow",
                    "S2FPacketSetSlot",
                    "S02PacketChat",
                    "S3APacketTabComplete",
                    "S3BPacketScoreboardObjective",
                    "S3CPacketUpdateScore",
            }
    )
    public static int packetLoggerServerType2 = 0;

    @Dropdown(
            name = "Server Packet Type 3",
            category = DEV,
            subcategory = PACKET_LOGGER,
            options = {
                    "None",
                    "S3DPacketDisplayScoreboard",
                    "S3EPacketTeams",
                    "S3FPacketCustomPayload",
                    "S03PacketTimeUpdate",
                    "S04PacketEntityEquipment",
                    "S05PacketSpawnPosition",
                    "S06PacketUpdateHealth",
                    "S07PacketRespawn",
                    "S08PacketPlayerPosLook",
                    "S09PacketHeldItemChange",
                    "S10PacketSpawnPainting",
                    "S11PacketSpawnExperienceOrb",
            }
    )
    public static int packetLoggerServerType3 = 0;

    @Dropdown(
            name = "Server Packet Type 4",
            category = DEV,
            subcategory = PACKET_LOGGER,
            options = {
                    "None",
                    "S12PacketEntityVelocity",
                    "S13PacketDestroyEntities",
                    "S14PacketEntity",
                    "S18PacketEntityTeleport",
                    "S19PacketEntityHeadLook",
                    "S19PacketEntityStatus",
                    "S20PacketEntityProperties",
                    "S21PacketChunkData",
                    "S22PacketMultiBlockChange",
                    "S23PacketBlockChange",
                    "S24PacketBlockAction",
                    "S25PacketBlockBreakAnim",
            }
    )
    public static int packetLoggerServerType4 = 0;

    @Dropdown(
            name = "Server Packet Type 5",
            category = DEV,
            subcategory = PACKET_LOGGER,
            options = {
                    "None",
                    "S26PacketMapChunkBulk",
                    "S27PacketExplosion",
                    "S28PacketEffect",
                    "S29PacketSoundEffect",
                    "S30PacketWindowItems",
                    "S31PacketWindowProperty",
                    "S32PacketConfirmTransaction",
                    "S33PacketUpdateSign",
                    "S34PacketMaps",
                    "S35PacketUpdateTileEntity",
                    "S36PacketSignEditorOpen",
                    "S37PacketStatistics",
            }
    )
    public static int packetLoggerServerType5 = 0;

    @Dropdown(
            name = "Server Packet Type 6",
            category = DEV,
            subcategory = PACKET_LOGGER,
            options = {
                    "None",
                    "S38PacketPlayerListItem",
                    "S39PacketPlayerAbilities",
                    "S40PacketDisconnect",
                    "S41PacketServerDifficulty",
                    "S42PacketCombatEvent",
                    "S43PacketCamera",
                    "S44PacketWorldBorder",
                    "S45PacketTitle",
                    "S46PacketSetCompressionLevel",
                    "S47PacketPlayerListHeaderFooter",
                    "S48PacketResourcePackSend",
                    "S49PacketUpdateEntityNBT"
            }
    )
    public static int packetLoggerServerType6 = 0;

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

    @Switch(
            name = "Visitor Quick Buy Debug",
            category = FARMING,
            subcategory = VISITOR_HELPERS
    )
    public static boolean visitorQuickBuyDebug = false;

    @Switch(
            name = "Agaricus Cap Helper",
            category = FARMING,
            subcategory = RIFT
    )
    public static boolean agaricusCapHelper = false;

    @Switch(
            name = "Alchemy Helper",
            category = PLAYER,
            subcategory = ALCHEMY_HELPER,
            size = 2
    )
    public static boolean alchemyHelper = false;

    @Switch(
            name = "Auto Nether Wart",
            category = PLAYER,
            subcategory = ALCHEMY_HELPER
    )
    public static boolean alchemyHelperAutoNetherWart = false;

    @Switch(
            name = "Auto Extract Potions",
            category = PLAYER,
            subcategory = ALCHEMY_HELPER
    )
    public static boolean alchemyHelperAutoExtractPotions = false;

    @Switch(
            name = "Auto Main Ingredient",
            category = PLAYER,
            subcategory = ALCHEMY_HELPER
    )
    public static boolean alchemyHelperAutoMainIngredient = false;

    @Text(
            name = "Main Ingredient Skyblock ID",
            placeholder = "You can use NEU middle click to find the skyblock id of an item",
            category = PLAYER,
            subcategory = ALCHEMY_HELPER
    )
    public static String alchemyHelperMainIngredientId = "";

    @Switch(
            name = "Auto Glowstone",
            category = PLAYER,
            subcategory = ALCHEMY_HELPER
    )
    public static boolean alchemyHelperAutoGlowstone = false;

    @Text(
            name = "Base Potion Level",
            placeholder = "Potion level (in roman numerals) before glowstone is added",
            category = PLAYER,
            subcategory = ALCHEMY_HELPER
    )
    public static String alchemyHelperBasePotionLevel = "";

    @Slider(
            name = "Action Delay",
            category = PLAYER,
            subcategory = ALCHEMY_HELPER,
            min = 100, max = 500
    )
    public static int alchemyHelperActionDelay = 300;

    @Switch(
            name = "Close Brewer",
            category = PLAYER,
            subcategory = ALCHEMY_HELPER
    )
    public static boolean alchemyHelperCloseBrewer = false;

    @Switch(
            name = "Mirrorverse Helpers",
            category = PLAYER,
            subcategory = RIFT
    )
    public static boolean mirrorverseHelpers = false;

    @Switch(
            name = "Anti Shy",
            category = COMBAT,
            subcategory = RIFT,
            description = "Look away from Shys when they become angry"
    )
    public static boolean antiShy = false;

    @Switch(
            name = "Trackers",
            category = TRACKERS,
            size = 2
    )
    public static boolean trackers = false;

    @HUD(
            name = "Trackers Hud",
            category = TRACKERS
    )
    public TrackersHud trackersHud = new TrackersHud();

    @Switch(
            name = "Powder Chest Tracker",
            category = TRACKERS,
            subcategory = POWDER_CHEST_TRACKER
    )
    public static boolean powderChestTracker = false;


    public GumTuneClientConfig() {
        super(new Mod(GumTuneClient.NAME, ModType.SKYBLOCK, "/assets/" + GumTuneClient.MODID + "/gtc_small.png", 84, 84), GumTuneClient.MODID + ".json");
        registerKeyBind(nukerKeyBind, () -> {});
        registerKeyBind(mobMacroKeyBind, () -> {});
        registerKeyBind(copyNBTDataKeyBind, () -> {});
        registerKeyBind(addItemToAutoSellFilter, () -> {});
        registerKeyBind(addItemToAutoCraftFilter, () -> {});
        registerKeyBind(executeAutoSellKeybind, () -> {});
        registerKeyBind(gemstoneMacroToggleKeyBind, () -> {});
        registerKeyBind(gemstoneMacroAddToPathKeyBind, () -> {});
        initialize();
    }
}

