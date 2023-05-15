package rosegold.gumtuneclient.config.pages;

import cc.polyfrost.oneconfig.config.annotations.Switch;

public class GemstoneTypeFilter {
    @Switch(
            name = "Amethyst"
    )
    public static boolean amethyst = true;

    @Switch(
            name = "Sapphire"
    )
    public static boolean sapphire = true;

    @Switch(
            name = "Amber"
    )
    public static boolean amber = true;

    @Switch(
            name = "Jade"
    )
    public static boolean jade = true;

    @Switch(
            name = "Jasper"
    )
    public static boolean jasper = true;

    @Switch(
            name = "Topaz"
    )
    public static boolean topaz = true;

    @Switch(
            name = "Ruby"
    )
    public static boolean ruby = true;
}
