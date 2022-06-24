package cc.polyfrost.example.config;

import cc.polyfrost.example.hud.TestHud;
import cc.polyfrost.oneconfig.config.Config;
import cc.polyfrost.oneconfig.config.annotations.HUD;
import cc.polyfrost.oneconfig.config.annotations.Switch;
import cc.polyfrost.oneconfig.config.data.Mod;
import cc.polyfrost.oneconfig.config.data.ModType;

public class TestConfig extends Config {

    @HUD(
            name = "Very cool HUD",
            subcategory = "Test"
    )
    public static TestHud testHud = new TestHud(true, 500, 500);

    @Switch(
            name = "Test",
            subcategory = "Test",
            size = 2
    )
    public static boolean test = true;

    public TestConfig() {
        super(new Mod("Example Mod", ModType.UTIL_QOL), "config/example_mod.json");
    }
}

