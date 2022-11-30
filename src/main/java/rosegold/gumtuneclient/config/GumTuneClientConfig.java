package rosegold.gumtuneclient.config;

import cc.polyfrost.oneconfig.config.Config;
import cc.polyfrost.oneconfig.config.annotations.*;
import cc.polyfrost.oneconfig.config.core.OneKeyBind;
import cc.polyfrost.oneconfig.config.data.Mod;
import cc.polyfrost.oneconfig.config.data.ModType;
import cc.polyfrost.oneconfig.config.data.PageLocation;
import cc.polyfrost.oneconfig.libs.universal.UKeyboard;
import rosegold.gumtuneclient.GumTuneClient;
import rosegold.gumtuneclient.config.pages.NukerBlockFilter;

public class GumTuneClientConfig extends Config {

    // Categories

    private transient static final String MACRO = "Macros";
    private transient static final String WORLD = "World"; //todo better name
    private transient static final String MINING = "Mining";
    private transient static final String QOL = "QoL";

    // Modules
    private transient static final String SUGAR_CANE_PLACER = "Sugar Cane Placer";
    private transient static final String HARP_MACRO = "Harp Macro";
    private transient static final String POWDER_CHEST_SOLVER = "Powder Chest Solver";
    private transient static final String NUKER = "Nuker";
    private transient static final String TRACKERS = "Trackers";

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
            name = "Enabled",
            category = MACRO,
            subcategory = HARP_MACRO,
            size = 2
    )
    public static boolean harpMacro = false;

    @Slider(
            name = "Delay between clicks",
            description = "Indicated in ticks, more lag = less delay",
            category = MACRO,
            subcategory = HARP_MACRO,
            min = 0, max = 100,
            step = 1
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
            description = "Pick out which blocks nuker will mine",
            category = MINING,
            subcategory = NUKER,
            location = PageLocation.TOP
    )
    public NukerBlockFilter nukerBlockFilter = new NukerBlockFilter();

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
            min = 0f, max = 4f,
            step = 1
    )
    public static int nukerDepth = 1;

    @Dropdown(
            name = "Shape",
            category = MINING,
            subcategory = NUKER,
            options = {"Sphere", "Facing Axis"}
    )
    public static int nukerShape = 0;

    @Slider(
            name = "Pingless reset cutoff",
            description = "Mess with this slider and see if it makes nuker faster",
            category = MINING,
            subcategory = NUKER,
            min = 0f, max = 4f,
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

    public GumTuneClientConfig() {
        super(new Mod(GumTuneClient.NAME, ModType.SKYBLOCK, "https://i.imgur.com/chsDDyx.png"), GumTuneClient.MODID + ".json");
        registerKeyBind(nukerKeyBind, () -> {});
        addDependency("commissionTracker", "trackers");
        initialize();
    }
}

