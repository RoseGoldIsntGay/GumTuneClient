package rosegold.gumtuneclient.config.pages;

import cc.polyfrost.oneconfig.config.annotations.Switch;

public class WorldScannerFilter {
    @Switch(
            name = "Crystal Hollows Crystals",
            description = ""
    )
    public static boolean worldScannerCrystals = false;
}
