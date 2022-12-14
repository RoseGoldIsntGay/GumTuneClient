package rosegold.gumtuneclient.config.pages;

import cc.polyfrost.oneconfig.config.annotations.Switch;

public class MobMacroFilter {
    @Switch(
            name = "Zombies"
    )
    public static boolean zombies = false;

    @Switch(
            name = "Spiders"
    )
    public static boolean spiders = false;

    @Switch(
            name = "Wolves"
    )
    public static boolean wolves = false;

    @Switch(
            name = "Endermen"
    )
    public static boolean endermen = false;
}
