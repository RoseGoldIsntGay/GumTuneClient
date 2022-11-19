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

    @Switch(
            name = "Sugar Cane Placer",
            subcategory = "Sugar Cane Placer",
            size = 2
    )
    public static boolean sugarCanePlacer = false;

    @Slider(
            name = "You slide me right round baby right round",
            min = 0f, max = 80f,        // min and max values for the slider
            // if you like, you can use step to set a step value for the slider,
            // giving it little steps that the slider snaps to.
            step = 10
    )
    public static int sugarCanePlacerSpeed = 20;

    public GumTuneAddonsConfig() {
        super(new Mod(GumTuneAddons.NAME, ModType.SKYBLOCK, "https://i.imgur.com/cLtXE48.png"), GumTuneAddons.MODID + ".json");
        initialize();
    }
}

