package rosegold.gumtuneclient.config.pages;

import cc.polyfrost.oneconfig.config.annotations.Switch;

public class NukerBlockFilter {

    @Switch(
            name = "Hardstone",
            description = "Stone in Crystal Hollows"
    )
    public static boolean nukerBlockFilterHardstone = false;

    @Switch(
            name = "Gemstones",
            description = "Glass in Crystal Hollows"
    )
    public static boolean nukerBlockFilterGemstones = false;

    @Switch(
            name = "Mithril",
            description = "Mithril blocks in Dwarven Mines and Crystal Hollows"
    )
    public static boolean nukerBlockFilterMithril = false;

    @Switch(
            name = "Titanium",
            description = "Smooth Diorite in the Dwarven Mines"
    )
    public static boolean nukerBlockFilterTitanium = false;

    @Switch(
            name = "Crimson Isle Excavatables",
            description = "Mycelium and red sand in Crimson Isle"
    )
    public static boolean nukerBlockFilterExcavatables = false;

    @Switch(
            name = "Gold",
            description = "Gold Blocks"
    )
    public static boolean nukerBlockFilterGold = false;

    @Switch(
            name = "Stone",
            description = "All stone & cobblestone"
    )
    public static boolean nukerBlockFilterStone = false;

    @Switch(
            name = "Ores",
            description = "All ores"
    )
    public static boolean nukerBlockFilterOres = false;

    @Switch(
            name = "Obsidian",
            description = "Obsidian"
    )
    public static boolean nukerBlockFilterObsidian = false;

    @Switch(
            name = "Crops",
            description = "All crops"
    )
    public static boolean nukerBlockFilterCrops = false;

    @Switch(
            name = "Foliage",
            description = "Grass and leaves"
    )
    public static boolean nukerBlockFilterFoliage = false;

    @Switch(
            name = "Wood",
            description = "All logs"
    )
    public static boolean nukerBlockFilterWood = false;

    @Switch(
            name = "Sand and Gravel",
            description = "Sand and red sand"
    )
    public static boolean nukerBlockFilterSand = false;

    @Switch(
            name = "Dirt",
            description = "Dirt, farmland and grass"
    )
    public static boolean nukerBlockFilterDirt = false;

    @Switch(
            name = "Glowstone",
            description = "Glowstone"
    )
    public static boolean nukerBlockFilterGlowstone = false;

    @Switch(
            name = "Netherrack",
            description = "Netherrack"
    )
    public static boolean nukerBlockFilterNetherrack = false;

    @Switch(
            name = "Ice",
            description = "Ice"
    )
    public static boolean nukerBlockFilterIce = false;

    @Switch(
            name = "Frozen Treasure",
            description = "Frozen Treasure"
    )
    public static boolean nukerBlockFilterFrozenTreasure = false;
}
