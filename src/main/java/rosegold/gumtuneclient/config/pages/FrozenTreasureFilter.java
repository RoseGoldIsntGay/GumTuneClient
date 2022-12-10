package rosegold.gumtuneclient.config.pages;

import cc.polyfrost.oneconfig.config.annotations.Switch;

public class FrozenTreasureFilter {
    @Switch(
            name = "Packed Ice"
    )
    public static boolean frozenTreasurePackedIce = true;

    @Switch(
            name = "Enchanted Ice"
    )
    public static boolean frozenTreasureEnchantedIce = true;

    @Switch(
            name = "Enchanted Packed Ice"
    )
    public static boolean frozenTreasureEnchantedPackedIce = true;

    @Switch(
            name = "Ice Bait"
    )
    public static boolean frozenTreasureIceBait = true;

    @Switch(
            name = "Glowy Chum Bait"
    )
    public static boolean frozenTreasureGlowyChumBait = true;

    @Switch(
            name = "Glacial Fragment"
    )
    public static boolean frozenTreasureGlacialFragment = true;

    @Switch(
            name = "White Gift"
    )
    public static boolean frozenTreasureWhiteGift = true;

    @Switch(
            name = "Green Gift"
    )
    public static boolean frozenTreasureGreenGift = true;

    @Switch(
            name = "Red Gift"
    )
    public static boolean frozenTreasureRedGift = true;

    @Switch(
            name = "Glacial Talisman"
    )
    public static boolean frozenTreasureGlacialTalisman = true;
}
