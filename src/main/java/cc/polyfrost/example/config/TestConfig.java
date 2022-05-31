package cc.polyfrost.example.config;

import cc.polyfrost.example.hud.TestHud;
import cc.polyfrost.oneconfig.config.annotations.Option;
import cc.polyfrost.oneconfig.config.data.Mod;
import cc.polyfrost.oneconfig.config.data.ModType;
import cc.polyfrost.oneconfig.config.data.OptionType;
import cc.polyfrost.oneconfig.config.interfaces.Config;

public class TestConfig extends Config {

    @Option(
            name = "Very cool HUD",
            subcategory = "Test",
            type = OptionType.HUD,
            size = 2
    )
    public static TestHud testHud = new TestHud(true, 500, 500);

    @Option(
            name = "Test",
            subcategory = "Test",
            type = OptionType.SWITCH,
            size = 2
    )
    public static boolean test = true;

    public TestConfig() {
        super(new Mod("Example Mod", ModType.UTIL_QOL), "./config/example_mod.json");
    }
}

