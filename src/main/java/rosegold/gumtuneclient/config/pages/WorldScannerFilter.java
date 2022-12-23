package rosegold.gumtuneclient.config.pages;

import cc.polyfrost.oneconfig.config.annotations.Info;
import cc.polyfrost.oneconfig.config.annotations.Switch;
import cc.polyfrost.oneconfig.config.data.InfoType;

public class WorldScannerFilter {

    @Info(
            text = "Crystal Hollows Scanners",
            type = InfoType.INFO
    )
    public static boolean ignored;
    @Switch(
            name = "Nucleus Crystals",
            description = ""
    )
    public static boolean worldScannerCHCrystals = false;

    @Switch(
            name = "Mob Killing Spots",
            description = ""
    )
    public static boolean worldScannerCHMobSpots = false;

    @Switch(
            name = "Fairy Grottos",
            description = ""
    )
    public static boolean worldScannerCHFairyGrottos = false;

    @Info(
            text = "Other Scanners",
            type = InfoType.INFO
    )
    public static boolean ignored2;

    @Switch(
            name = "Foo",
            description = ""
    )
    public static boolean worldScannerFoo = false;

    @Switch(
            name = "Bar",
            description = ""
    )
    public static boolean worldScannerBar = false;
}
