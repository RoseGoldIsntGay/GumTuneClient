package rosegold.gumtuneclient.config.pages;

import cc.polyfrost.oneconfig.config.annotations.Info;
import cc.polyfrost.oneconfig.config.annotations.Switch;
import cc.polyfrost.oneconfig.config.data.InfoType;

public class WorldScannerFilter {

    @Info(
            text = "Crystal Hollows Scanners",
            type = InfoType.INFO,
            size = 2
    )
    public static boolean ignored;
    @Switch(
            name = "Nucleus Crystals"
    )
    public static boolean worldScannerCHCrystals = false;

    @Switch(
            name = "Nucleus Crystals Beacon"
    )
    public static boolean worldScannerCHCrystalsBeacon = false;

    @Switch(
            name = "Mob Killing Spots"
    )
    public static boolean worldScannerCHMobSpots = false;

    @Switch(
            name = "Mob Killing Spots Beacon"
    )
    public static boolean worldScannerCHMobSpotsBeacon = false;

    @Switch(
            name = "Fairy Grottos"
    )
    public static boolean worldScannerCHFairyGrottos = false;

    @Switch(
            name = "Fairy Grottos Beacon"
    )
    public static boolean worldScannerCHFairyGrottosBeacon = false;

    @Switch(
            name = "Worm Fishing Spots"
    )
    public static boolean worldScannerCHWormFishing = false;

    @Switch(
            name = "Worm Fishing Spots Beacon"
    )
    public static boolean worldScannerCHWormFishingBeacon = false;

    @Switch(
            name = "Golden Dragon Nest"
    )
    public static boolean worldScannerCHGoldenDragonNest = false;

    @Switch(
            name = "Golden Dragon Nest Beacon"
    )
    public static boolean worldScannerCHGoldenDragonNestBeacon = false;

    @Info(
            text = "Other Scanners",
            type = InfoType.INFO,
            size = 2
    )
    public static boolean ignored2;
}
