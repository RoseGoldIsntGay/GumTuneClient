package rosegold.gumtuneaddons.config;

import cc.polyfrost.oneconfig.config.Config;
import cc.polyfrost.oneconfig.config.annotations.Slider;
import cc.polyfrost.oneconfig.config.annotations.Switch;
import cc.polyfrost.oneconfig.config.data.Mod;
import cc.polyfrost.oneconfig.config.data.ModType;
import rosegold.gumtuneaddons.GumTuneAddons;

public class GumTuneAddonsConfig extends Config {

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
            min = 10f, max = 80f,
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
            min = 0f, max = 100f,
            step = 1
    )
    public static int harpMacroDelay = 10;

    public GumTuneAddonsConfig() {
        super(new Mod(GumTuneAddons.NAME, ModType.SKYBLOCK, "https://i.imgur.com/chsDDyx.png"), GumTuneAddons.MODID + ".json");
        initialize();
    }
}

