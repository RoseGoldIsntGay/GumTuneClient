package rosegold.gumtuneclient.config;

import cc.polyfrost.oneconfig.config.Config;
import cc.polyfrost.oneconfig.config.annotations.Dropdown;
import cc.polyfrost.oneconfig.config.annotations.KeyBind;
import cc.polyfrost.oneconfig.config.annotations.Slider;
import cc.polyfrost.oneconfig.config.annotations.Switch;
import cc.polyfrost.oneconfig.config.core.OneKeyBind;
import cc.polyfrost.oneconfig.config.data.Mod;
import cc.polyfrost.oneconfig.config.data.ModType;
import cc.polyfrost.oneconfig.libs.universal.UKeyboard;
import net.minecraft.client.settings.KeyBinding;
import org.lwjgl.input.Keyboard;
import rosegold.gumtuneclient.GumTuneClient;

public class GumTuneClientConfig extends Config {

    /*@HUD(
            name = "Very cool HUD",
            subcategory = "Test"
    )
    public static TestHud testHud = new TestHud();*/

    // Categories

    private transient static final String MACRO = "Macros";
    private transient static final String WORLD = "World"; //todo better name

    // Modules
    private transient static final String SUGAR_CANE_PLACER = "Sugar Cane Placer";
    private transient static final String HARP_MACRO = "Harp Macro";
    private transient static final String NUKER = "Nuker";

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
            category = WORLD,
            subcategory = NUKER,
            size = 2
    )
    public static boolean nuker = false;

    @KeyBind(
            name = "Keybind",
            category = WORLD,
            subcategory = NUKER,
            size = 2
    )
    public static OneKeyBind nukerKeyBind = new OneKeyBind(UKeyboard.KEY_NONE);

    @Slider(
            name = "Speed",
            description = "Blocks per second",
            category = WORLD,
            subcategory = NUKER,
            min = 0, max = 80,
            step = 10
    )
    public static int nukerSpeed = 20;

    @Dropdown(
            name = "Shape",
            category = WORLD,
            subcategory = NUKER,
            options = {"Sphere", "Facing Axis"}
    )
    public static int nukerShape = 0;

    @Slider(
            name = "Height",
            description = "Blocks above your head",
            category = WORLD,
            subcategory = NUKER,
            min = 0, max = 4,
            step = 1
    )
    public static int nukerHeight = 0;

    @Slider(
            name = "Depth",
            description = "Blocks below your head",
            category = WORLD,
            subcategory = NUKER,
            min = 0f, max = 4f,
            step = 1
    )
    public static int nukerDepth = 1;

    public GumTuneClientConfig() {
        super(new Mod(GumTuneClient.NAME, ModType.SKYBLOCK, "https://i.imgur.com/chsDDyx.png"), GumTuneClient.MODID + ".json");
        registerKeyBind(nukerKeyBind, () -> {});
        initialize();
    }
}

