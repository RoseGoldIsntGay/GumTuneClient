package rosegold.gumtuneclient.config;

import cc.polyfrost.oneconfig.config.Config;
import cc.polyfrost.oneconfig.config.annotations.*;
import cc.polyfrost.oneconfig.config.core.OneColor;
import cc.polyfrost.oneconfig.config.core.OneKeyBind;
import cc.polyfrost.oneconfig.config.data.Mod;
import cc.polyfrost.oneconfig.config.data.ModType;
import cc.polyfrost.oneconfig.config.data.PageLocation;
import cc.polyfrost.oneconfig.libs.universal.UKeyboard;
import net.minecraft.util.ResourceLocation;
import rosegold.gumtuneclient.GumTuneClient;
import rosegold.gumtuneclient.config.pages.FrozenTreasureFilter;
import rosegold.gumtuneclient.config.pages.NukerBlockFilter;
import rosegold.gumtuneclient.config.pages.MobMacroFilter;
import rosegold.gumtuneclient.config.pages.WorldScannerFilter;

public class GumTuneClientConfig extends Config {

    // Categories

    private transient static final String MACRO = "Macros";
    private transient static final String WORLD = "World"; //todo better name
    private transient static final String RENDER = "Render";
    private transient static final String CONFIG = "Config";
    private transient static final String MINING = "Mining";
    private transient static final String QOL = "QoL";
    private transient static final String ESPS = "ESPs";
    private transient static final String ESP_SETTINGS = "ESP Settings";

    // Modules
    private transient static final String SUGAR_CANE_PLACER = "Sugar Cane Placer";
    private transient static final String HARP_MACRO = "Harp Macro";
    private transient static final String POWDER_CHEST_SOLVER = "Powder Chest Solver";
    private transient static final String NUKER = "Nuker";
    private transient static final String TRACKERS = "Trackers";
    private transient static final String MOB_MACRO = "Mob Macro";
    private transient static final String SERVER_SIDE_ROTATIONS = "Server Side Rotations";
    private transient static final String ROTATION_CONFIG = "Rotations Config";

    private transient static final String CAMERA = "Camera";
    private transient static final String WORLD_SCANNER = "World Scanner";

    @Switch(
            name = "Enabled",
            category = WORLD,
            subcategory = SUGAR_CANE_PLACER,
            size = 2
    )
    public static boolean sugarCanePlacer = false;

    @Slider(
            name = "Blocks Per Second",
            category = WORLD,
            subcategory = SUGAR_CANE_PLACER,
            min = 10, max = 80,
            step = 10
    )
    public static int sugarCanePlacerSpeed = 20;

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
            description = "",
            category = WORLD,
            subcategory = WORLD_SCANNER,
            location = PageLocation.BOTTOM
    )
    public WorldScannerFilter worldScannerFilter = new WorldScannerFilter();

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

    @Switch(
            name = "Server Side Rotation",
            description = "Rotate to mined blocks",
            category = MINING,
            subcategory = NUKER,
            size = 2
    )
    public static boolean serverSideNukerRotations = false;

    @Switch(
            name = "Mine Blocks In Front",
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
            min = 0, max = 80,
            step = 10
    )
    public static int nukerSpeed = 20;

    @Slider(
            name = "Height",
            description = "Blocks above your head",
            category = MINING,
            subcategory = NUKER,
            min = 0, max = 4,
            step = 1
    )
    public static int nukerHeight = 0;

    @Slider(
            name = "Depth",
            description = "Blocks below your head",
            category = MINING,
            subcategory = NUKER,
            min = 0f, max = 4,
            step = 1
    )
    public static int nukerDepth = 1;

    @Dropdown(
            name = "Shape",
            category = MINING,
            subcategory = NUKER,
            options = {"Sphere", "Facing Axis", "Axis Tunnels"}
    )
    public static int nukerShape = 0;

    @Dropdown(
            name = "Algorithm",
            category = MINING,
            subcategory = NUKER,
            options = {"Closest Block (Classic)", "Smallest Rotation (NEW!)"}
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

    @Dropdown(
            name = "Pause nuker when solving",
            category = MINING,
            subcategory = POWDER_CHEST_SOLVER,
            options = {"Don't", "Pause Nuker", "Pause Nuker Rotations"}
    )
    public static int powderChestPauseNukerMode = 0;

    @Switch(
            name = "ESPs",
            category = RENDER,
            subcategory = ESPS,
            description = "Required for all of the ESPs below",
            size = 4
    )
    public static boolean customESP = false;

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

    @Page(
            name = "Frozen Treasure Filters",
            description = "Filter out treasures for the ESP",
            category = RENDER,
            subcategory = ESPS,
            location = PageLocation.BOTTOM
    )
    public FrozenTreasureFilter frozenTreasureFilter = new FrozenTreasureFilter();

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
            min = 4, max = 40,
            step = 1
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
            options = {"Precursor Eye", "Frozen Scythe (and similar items)", "Shortbows", "Hyperion"}
    )
    public static int mobMacroAttackType = 0;

    @Switch(
            name = "Smooth Server Side Rotation",
            category = CONFIG,
            subcategory = ROTATION_CONFIG,
            size = 2,
            description = "Smooth rotate when possible"
    )
    public static boolean smoothServerSideRotations = false;

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

    public GumTuneClientConfig() {
        super(new Mod(GumTuneClient.NAME, ModType.SKYBLOCK, "/assets/" + GumTuneClient.MODID + "/gtc_small.png"), GumTuneClient.MODID + ".json");
        registerKeyBind(nukerKeyBind, () -> {});
        registerKeyBind(mobMacroKeyBind, () -> {});
        addDependency("commissionTracker", "trackers");
        initialize();
    }
}

