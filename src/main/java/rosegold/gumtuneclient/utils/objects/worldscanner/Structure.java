package rosegold.gumtuneclient.utils.objects.worldscanner;

import kotlin.Triple;
import net.minecraft.block.*;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.init.Blocks;
import net.minecraft.item.EnumDyeColor;
import rosegold.gumtuneclient.utils.LocationUtils;

import java.util.ArrayList;

public enum Structure {
    QUEEN(new ArrayList<Triple<Block, PropertyEnum, Comparable>>() {{
        add(new Triple<>(Blocks.stone, null, null));
        add(new Triple<>(Blocks.log2, null, null));
        add(new Triple<>(Blocks.log2, null, null));
        add(new Triple<>(Blocks.log2, null, null));
        add(new Triple<>(Blocks.log2, null, null));
        add(new Triple<>(Blocks.cauldron, null, null));
    }}, StructureType.CH_CRYSTALS, LocationUtils.Island.CRYSTAL_HOLLOWS, CrystalHollowsQuarter.GOBLIN_HOLDOUT, "§6Queen", 0, 5, 0),
    DIVAN(new ArrayList<Triple<Block, PropertyEnum, Comparable>>() {{
        add(new Triple<>(Blocks.quartz_block, null, null));
        add(new Triple<>(Blocks.quartz_stairs, null, null));
        add(new Triple<>(Blocks.stone_brick_stairs, null, null));
        add(new Triple<>(Blocks.stonebrick, null, null));
    }}, StructureType.CH_CRYSTALS, LocationUtils.Island.CRYSTAL_HOLLOWS, CrystalHollowsQuarter.MITHRIL_DEPOSITS, "§2Divan", 0, 5, 0),
    CITY(new ArrayList<Triple<Block, PropertyEnum, Comparable>>() {{
        add(new Triple<>(Blocks.cobblestone, null, null));
        add(new Triple<>(Blocks.cobblestone, null, null));
        add(new Triple<>(Blocks.cobblestone, null, null));
        add(new Triple<>(Blocks.cobblestone, null, null));
        add(new Triple<>(Blocks.stone_stairs, null, null));
        add(new Triple<>(Blocks.stone, BlockStone.VARIANT, BlockStone.EnumType.ANDESITE_SMOOTH));
        add(new Triple<>(Blocks.stone, BlockStone.VARIANT, BlockStone.EnumType.ANDESITE_SMOOTH));
        add(new Triple<>(Blocks.dark_oak_stairs, null, null));
    }}, StructureType.CH_CRYSTALS, LocationUtils.Island.CRYSTAL_HOLLOWS, CrystalHollowsQuarter.PRECURSOR_REMNANTS, "§bCity", 24, 0, -17),
    TEMPLE(new ArrayList<Triple<Block, PropertyEnum, Comparable>>() {{
        add(new Triple<>(Blocks.bedrock, null, null));
        add(new Triple<>(Blocks.clay, null, null));
        add(new Triple<>(Blocks.clay, null, null));
        add(new Triple<>(Blocks.stained_hardened_clay, null, null));
        add(new Triple<>(Blocks.wool, null, null));
        add(new Triple<>(Blocks.leaves, null, null));
        add(new Triple<>(Blocks.leaves, null, null));
    }}, StructureType.CH_CRYSTALS, LocationUtils.Island.CRYSTAL_HOLLOWS, CrystalHollowsQuarter.JUNGLE, "§5Temple", -45, 47, -18),
    KING(new ArrayList<Triple<Block, PropertyEnum, Comparable>>() {{
        add(new Triple<>(Blocks.wool, null, null));
        add(new Triple<>(Blocks.dark_oak_stairs, null, null));
        add(new Triple<>(Blocks.dark_oak_stairs, null, null));
        add(new Triple<>(Blocks.dark_oak_stairs, null, null));
    }}, StructureType.CH_CRYSTALS, LocationUtils.Island.CRYSTAL_HOLLOWS, CrystalHollowsQuarter.GOBLIN_HOLDOUT, "§6King", 1, -1, 2),
    BAL(new ArrayList<Triple<Block, PropertyEnum, Comparable>>() {{
        add(new Triple<>(Blocks.lava, null, null));
        add(new Triple<>(Blocks.barrier, null, null));
        add(new Triple<>(Blocks.barrier, null, null));
        add(new Triple<>(Blocks.barrier, null, null));
        add(new Triple<>(Blocks.barrier, null, null));
        add(new Triple<>(Blocks.barrier, null, null));
        add(new Triple<>(Blocks.barrier, null, null));
        add(new Triple<>(Blocks.barrier, null, null));
        add(new Triple<>(Blocks.barrier, null, null));
        add(new Triple<>(Blocks.barrier, null, null));
        add(new Triple<>(Blocks.barrier, null, null));
    }}, StructureType.CH_CRYSTALS, LocationUtils.Island.CRYSTAL_HOLLOWS, CrystalHollowsQuarter.MAGMA_FIELDS, "§6Bal", 0, 1, 0),

    FAIRY_GROTTO(new ArrayList<Triple<Block, PropertyEnum, Comparable>>() {{
        add(new Triple<>(Blocks.stained_glass, BlockColored.COLOR, EnumDyeColor.MAGENTA));
    }}, StructureType.FAIRY_GROTTO, LocationUtils.Island.CRYSTAL_HOLLOWS, CrystalHollowsQuarter.ANY, "", 0, 0, 0),

    GOBLIN_HALL(new ArrayList<Triple<Block, PropertyEnum, Comparable>>() {{
        add(new Triple<>(Blocks.planks, BlockPlanks.VARIANT, BlockPlanks.EnumType.SPRUCE)); // 0
        add(new Triple<>(null, null, null)); // 1
        add(new Triple<>(Blocks.spruce_stairs, null, null)); // 2
        add(new Triple<>(Blocks.spruce_stairs, null, null)); // 3
        add(new Triple<>(null, null, null)); // 4
        add(new Triple<>(null, null, null)); // 5
        add(new Triple<>(Blocks.spruce_stairs, null, null)); // 6
        add(new Triple<>(Blocks.spruce_stairs, null, null)); // 7
        add(new Triple<>(null, null, null)); // 8
        add(new Triple<>(null, null, null)); // 9
        add(new Triple<>(Blocks.spruce_stairs, null, null)); // 10
        add(new Triple<>(Blocks.spruce_stairs, null, null)); // 11
        add(new Triple<>(null, null, null)); // 12
        add(new Triple<>(Blocks.planks, BlockPlanks.VARIANT, BlockPlanks.EnumType.SPRUCE)); // 13
    }}, StructureType.CH_MOB_SPOTS, LocationUtils.Island.CRYSTAL_HOLLOWS, CrystalHollowsQuarter.GOBLIN_HOLDOUT, "§6Goblin Hall", 0, 7, 0),
    GOBLIN_RING(new ArrayList<Triple<Block, PropertyEnum, Comparable>>() {{
        add(new Triple<>(Blocks.oak_fence, null, null)); // 0
        add(new Triple<>(Blocks.skull, null, null)); // 1
        add(new Triple<>(null, null, null)); // 2
        add(new Triple<>(null, null, null)); // 3
        add(new Triple<>(null, null, null)); // 4
        add(new Triple<>(null, null, null)); // 5
        add(new Triple<>(Blocks.wooden_slab, BlockSlab.HALF, BlockSlab.EnumBlockHalf.TOP)); // 6
        add(new Triple<>(Blocks.wooden_slab, BlockSlab.HALF, BlockSlab.EnumBlockHalf.BOTTOM)); // 7
        add(new Triple<>(null, null, null)); // 8
        add(new Triple<>(null, null, null)); // 9
        add(new Triple<>(null, null, null)); // 10
        add(new Triple<>(Blocks.planks, BlockPlanks.VARIANT, BlockPlanks.EnumType.SPRUCE)); // 11
    }}, StructureType.CH_MOB_SPOTS, LocationUtils.Island.CRYSTAL_HOLLOWS, CrystalHollowsQuarter.GOBLIN_HOLDOUT, "§6Goblin Ring", 0, 11, 0),
    GRUNT_BRIDGE(new ArrayList<Triple<Block, PropertyEnum, Comparable>>() {{
        add(new Triple<>(Blocks.stone_brick_stairs, null, null)); // 0
        add(new Triple<>(null, null, null)); // 1
        add(new Triple<>(null, null, null)); // 2
        add(new Triple<>(null, null, null)); // 3
        add(new Triple<>(null, null, null)); // 4
        add(new Triple<>(Blocks.stonebrick, null, null)); // 5
        add(new Triple<>(Blocks.stonebrick, null, null)); // 6
        add(new Triple<>(null, null, null)); // 7
        add(new Triple<>(Blocks.stone_slab, null, null)); // 8
        add(new Triple<>(Blocks.stonebrick, null, null)); // 9
        add(new Triple<>(null, null, null)); // 10
        add(new Triple<>(null, null, null)); // 11
        add(new Triple<>(null, null, null)); // 12
        add(new Triple<>(Blocks.stonebrick, null, null)); // 13
        add(new Triple<>(Blocks.stone_slab, null, null)); // 14
    }}, StructureType.CH_MOB_SPOTS, LocationUtils.Island.CRYSTAL_HOLLOWS, CrystalHollowsQuarter.MITHRIL_DEPOSITS, "§bGrunt Bridge", 0, -1, -45),
    CORLEONE_DOCK(new ArrayList<Triple<Block, PropertyEnum, Comparable>>() {{
        add(new Triple<>(Blocks.stonebrick, null, null)); // 0
        add(new Triple<>(Blocks.stonebrick, null, null)); // 1
        add(new Triple<>(Blocks.stonebrick, null, null)); // 2
        add(new Triple<>(Blocks.stonebrick, null, null)); // 3
        add(new Triple<>(null, null, null)); // 4
        add(new Triple<>(null, null, null)); // 5
        add(new Triple<>(null, null, null)); // 6
        add(new Triple<>(null, null, null)); // 7
        add(new Triple<>(null, null, null)); // 8
        add(new Triple<>(null, null, null)); // 9
        add(new Triple<>(null, null, null)); // 10
        add(new Triple<>(null, null, null)); // 11
        add(new Triple<>(null, null, null)); // 12
        add(new Triple<>(null, null, null)); // 13
        add(new Triple<>(null, null, null)); // 14
        add(new Triple<>(null, null, null)); // 15
        add(new Triple<>(null, null, null)); // 16
        add(new Triple<>(null, null, null)); // 17
        add(new Triple<>(null, null, null)); // 18
        add(new Triple<>(null, null, null)); // 19
        add(new Triple<>(null, null, null)); // 20
        add(new Triple<>(null, null, null)); // 21
        add(new Triple<>(null, null, null)); // 22
        add(new Triple<>(null, null, null)); // 23
        add(new Triple<>(Blocks.stonebrick, null, null)); // 24
        add(new Triple<>(Blocks.stonebrick, null, null)); // 25
        add(new Triple<>(Blocks.fire, null, null)); // 26
        add(new Triple<>(Blocks.stonebrick, null, null)); // 27
    }},StructureType.CH_MOB_SPOTS,LocationUtils.Island.CRYSTAL_HOLLOWS, CrystalHollowsQuarter.MITHRIL_DEPOSITS, "§bCorleone Dock",23,11,17),
    CORLEONE_HOLE(new ArrayList<Triple<Block, PropertyEnum, Comparable>>() {{
        add(new Triple<>(Blocks.stone_slab, BlockSlab.HALF, BlockSlab.EnumBlockHalf.BOTTOM)); // 0
        add(new Triple<>(null, null, null)); // 1
        add(new Triple<>(null, null, null)); // 2
        add(new Triple<>(null, null, null)); // 3
        add(new Triple<>(null, null, null)); // 4
        add(new Triple<>(null, null, null)); // 5
        add(new Triple<>(null, null, null)); // 6
        add(new Triple<>(null, null, null)); // 7
        add(new Triple<>(null, null, null)); // 8
        add(new Triple<>(null, null, null)); // 9
        add(new Triple<>(null, null, null)); // 10
        add(new Triple<>(null, null, null)); // 11
        add(new Triple<>(null, null, null)); // 12
        add(new Triple<>(null, null, null)); // 13
        add(new Triple<>(Blocks.stone_slab, BlockSlab.HALF, BlockSlab.EnumBlockHalf.TOP)); // 14
        add(new Triple<>(Blocks.double_stone_slab, null, null)); // 15
        add(new Triple<>(null, null, null)); // 16
        add(new Triple<>(Blocks.stone_slab, BlockSlab.HALF, BlockSlab.EnumBlockHalf.TOP)); // 17
        add(new Triple<>(Blocks.stonebrick, null, null)); // 18
    }},StructureType.CH_MOB_SPOTS,LocationUtils.Island.CRYSTAL_HOLLOWS, CrystalHollowsQuarter.MITHRIL_DEPOSITS, "§bCorleone Hole",0,-3,34),
    GRUNT_RAILS_1(new ArrayList<Triple<Block, PropertyEnum, Comparable>>() {{
        add(new Triple<>(Blocks.planks, BlockPlanks.VARIANT, BlockPlanks.EnumType.SPRUCE)); // 0
        add(new Triple<>(null, null, null)); // 1
        add(new Triple<>(Blocks.wall_sign, null, null)); // 2
        add(new Triple<>(null, null, null)); // 3
        add(new Triple<>(null, null, null)); // 4
        add(new Triple<>(null, null, null)); // 5
        add(new Triple<>(null, null, null)); // 6
        add(new Triple<>(Blocks.planks, BlockPlanks.VARIANT, BlockPlanks.EnumType.SPRUCE)); // 7
        add(new Triple<>(Blocks.tnt, null, null)); // 8
    }},StructureType.CH_MOB_SPOTS,LocationUtils.Island.CRYSTAL_HOLLOWS, CrystalHollowsQuarter.MITHRIL_DEPOSITS, "§bGrunt Rails 1",0,0,0),
    GRUNT_HERO_STATUE(new ArrayList<Triple<Block, PropertyEnum, Comparable>>() {{
        add(new Triple<>(Blocks.stone_slab, BlockSlab.HALF, BlockSlab.EnumBlockHalf.TOP)); // 0
        add(new Triple<>(Blocks.stone, BlockStone.VARIANT, BlockStone.EnumType.DIORITE)); // 1
        add(new Triple<>(Blocks.stone, BlockStone.VARIANT, BlockStone.EnumType.DIORITE)); // 2
        add(new Triple<>(Blocks.stone, BlockStone.VARIANT, BlockStone.EnumType.DIORITE)); // 3
        add(new Triple<>(Blocks.stone, BlockStone.VARIANT, BlockStone.EnumType.DIORITE)); // 4
        add(new Triple<>(Blocks.cobblestone, null, null)); // 5
        add(new Triple<>(Blocks.stone, BlockStone.VARIANT, BlockStone.EnumType.ANDESITE_SMOOTH)); // 6
        add(new Triple<>(Blocks.cobblestone, null, null)); // 7
        add(new Triple<>(Blocks.stone_stairs, null, null)); // 8
    }},StructureType.CH_MOB_SPOTS,LocationUtils.Island.CRYSTAL_HOLLOWS, CrystalHollowsQuarter.MITHRIL_DEPOSITS, "§bGrunt Hero Statue",0,0,0),
    SMALL_GRUNT_BRIDGE(new ArrayList<Triple<Block, PropertyEnum, Comparable>>() {{
        add(new Triple<>(Blocks.spruce_stairs, null, null)); // 0
        add(new Triple<>(Blocks.spruce_stairs, null, null)); // 1
        add(new Triple<>(null, null, null)); // 2
        add(new Triple<>(Blocks.spruce_stairs, null, null)); // 3
        add(new Triple<>(Blocks.log, BlockPlanks.VARIANT, BlockPlanks.EnumType.OAK)); // 4
        add(new Triple<>(Blocks.oak_fence, null, null)); // 5
        add(new Triple<>(Blocks.torch, null, null)); // 6
    }},StructureType.CH_MOB_SPOTS,LocationUtils.Island.CRYSTAL_HOLLOWS, CrystalHollowsQuarter.MITHRIL_DEPOSITS, "§bSmall Grunt Bridge",0,0,0),
    KEY_GUARDIAN_SPIRAL(new ArrayList<Triple<Block, PropertyEnum, Comparable>>() {{
        add(new Triple<>(Blocks.jungle_stairs, null, null)); // 0
        add(new Triple<>(Blocks.planks, null, null)); // 1
        add(new Triple<>(Blocks.glowstone, null, null)); // 2
        add(new Triple<>(Blocks.carpet, null, null)); // 3
        add(new Triple<>(null, null, null)); // 4
        add(new Triple<>(Blocks.wooden_slab, null, null)); // 5
        add(new Triple<>(null, null, null)); // 6
        add(new Triple<>(Blocks.jungle_stairs, null, null)); // 7
        add(new Triple<>(Blocks.stone, null, null)); // 8
        add(new Triple<>(Blocks.stone, null, null)); // 9
        add(new Triple<>(Blocks.stone, null, null)); // 10
    }},StructureType.CH_MOB_SPOTS,LocationUtils.Island.CRYSTAL_HOLLOWS, CrystalHollowsQuarter.JUNGLE, "§aKey Guardian Spiral",0,0,0),
    SLUDGE_WATERFALLS(new ArrayList<Triple<Block, PropertyEnum, Comparable>>() {{
        add(new Triple<>(Blocks.stone, null, null)); // 0
        add(new Triple<>(Blocks.dirt, null, null)); // 1
        add(new Triple<>(Blocks.stone, BlockStone.VARIANT, BlockStone.EnumType.GRANITE_SMOOTH)); // 2
        add(new Triple<>(Blocks.jungle_stairs, null, null)); // 3
        add(new Triple<>(Blocks.air, null, null)); // 4
        add(new Triple<>(Blocks.air, null, null)); // 5
        add(new Triple<>(Blocks.air, null, null)); // 6
        add(new Triple<>(Blocks.air, null, null)); // 7
        add(new Triple<>(Blocks.air, null, null)); // 8
    }},StructureType.CH_MOB_SPOTS,LocationUtils.Island.CRYSTAL_HOLLOWS, CrystalHollowsQuarter.JUNGLE, "§aSludge Waterfalls",0,0,0),
    SLUDGE_BRIDGES(new ArrayList<Triple<Block, PropertyEnum, Comparable>>() {{
        add(new Triple<>(Blocks.planks, BlockPlanks.VARIANT, BlockPlanks.EnumType.JUNGLE)); // 0
        add(new Triple<>(Blocks.planks, BlockPlanks.VARIANT, BlockPlanks.EnumType.JUNGLE)); // 1
        add(new Triple<>(Blocks.planks, BlockPlanks.VARIANT, BlockPlanks.EnumType.JUNGLE)); // 2
        add(new Triple<>(Blocks.jungle_stairs, null, null)); // 3
        add(new Triple<>(Blocks.planks, BlockPlanks.VARIANT, BlockPlanks.EnumType.JUNGLE)); // 4
        add(new Triple<>(Blocks.planks, BlockPlanks.VARIANT, BlockPlanks.EnumType.JUNGLE)); // 5
        add(new Triple<>(Blocks.planks, BlockPlanks.VARIANT, BlockPlanks.EnumType.JUNGLE)); // 6
        add(new Triple<>(Blocks.jungle_stairs, null, null)); // 7
        add(new Triple<>(Blocks.planks, BlockPlanks.VARIANT, BlockPlanks.EnumType.JUNGLE)); // 8
        add(new Triple<>(Blocks.stone, BlockStone.VARIANT, BlockStone.EnumType.GRANITE)); // 9
        add(new Triple<>(Blocks.stone, BlockStone.VARIANT, BlockStone.EnumType.GRANITE)); // 10
    }},StructureType.CH_MOB_SPOTS,LocationUtils.Island.CRYSTAL_HOLLOWS, CrystalHollowsQuarter.JUNGLE, "§aSludge Bridges",0,0,0),
    YOG_BRIDGE(new ArrayList<Triple<Block, PropertyEnum, Comparable>>() {{
        add(new Triple<>(Blocks.stonebrick, null, null)); // 0
        add(new Triple<>(Blocks.stone_brick_stairs, null, null)); // 1
        add(new Triple<>(Blocks.stonebrick, null, null)); // 2
        add(new Triple<>(Blocks.stone_brick_stairs, null, null)); // 3
        add(new Triple<>(Blocks.stone, BlockStone.VARIANT, BlockStone.EnumType.ANDESITE)); // 4
        add(new Triple<>(Blocks.stone_brick_stairs, null, null)); // 5
        add(new Triple<>(Blocks.stone_brick_stairs, null, null)); // 6
        add(new Triple<>(Blocks.stone_brick_stairs, null, null)); // 7
        add(new Triple<>(Blocks.stonebrick, null, null)); // 8
        add(new Triple<>(Blocks.stone_brick_stairs, null, null)); // 9
        add(new Triple<>(Blocks.stone, BlockStone.VARIANT, BlockStone.EnumType.ANDESITE)); // 10
        add(new Triple<>(Blocks.stonebrick, null, null)); // 11
        add(new Triple<>(Blocks.stonebrick, null, null)); // 12
        add(new Triple<>(Blocks.stonebrick, null, null)); // 13
        add(new Triple<>(Blocks.rail, null, null)); // 14
    }},StructureType.CH_MOB_SPOTS,LocationUtils.Island.CRYSTAL_HOLLOWS, CrystalHollowsQuarter.MAGMA_FIELDS, "§6Yog Bridge",0,15,0),
    ODAWA(new ArrayList<Triple<Block, PropertyEnum, Comparable>>() {{
        add(new Triple<>(Blocks.log, BlockPlanks.VARIANT, BlockPlanks.EnumType.JUNGLE)); // 0
        add(new Triple<>(Blocks.spruce_stairs, null, null)); // 1
        add(new Triple<>(Blocks.spruce_stairs, null, null)); // 2
        add(new Triple<>(Blocks.log, BlockPlanks.VARIANT, BlockPlanks.EnumType.JUNGLE)); // 3
        add(new Triple<>(Blocks.spruce_stairs, null, null)); // 4
        add(new Triple<>(Blocks.spruce_stairs, null, null)); // 5
        add(new Triple<>(Blocks.log, BlockPlanks.VARIANT, BlockPlanks.EnumType.JUNGLE)); // 6
        add(new Triple<>(Blocks.log, BlockPlanks.VARIANT, BlockPlanks.EnumType.JUNGLE)); // 7
        add(new Triple<>(Blocks.log, BlockPlanks.VARIANT, BlockPlanks.EnumType.JUNGLE)); // 8
        add(new Triple<>(Blocks.hay_block, null, null)); // 9
        add(new Triple<>(Blocks.stained_hardened_clay, BlockColored.COLOR, EnumDyeColor.YELLOW)); // 10
    }},StructureType.CH_MOB_SPOTS,LocationUtils.Island.CRYSTAL_HOLLOWS, CrystalHollowsQuarter.JUNGLE, "§aOdawa",0,0,0),
    MINI_JUNGLE_TEMPLE(new ArrayList<Triple<Block, PropertyEnum, Comparable>>() {{
        add(new Triple<>(Blocks.stone, BlockStone.VARIANT, BlockStone.EnumType.ANDESITE_SMOOTH)); // 0
        add(new Triple<>(Blocks.stone, BlockStone.VARIANT, BlockStone.EnumType.ANDESITE)); // 1
        add(new Triple<>(Blocks.stone_brick_stairs, null, null)); // 2
        add(new Triple<>(Blocks.stone, BlockStone.VARIANT, BlockStone.EnumType.ANDESITE)); // 3
        add(new Triple<>(Blocks.stone, BlockStone.VARIANT, BlockStone.EnumType.ANDESITE)); // 4
        add(new Triple<>(Blocks.stone, null, null)); // 5
        add(new Triple<>(Blocks.stone, BlockStone.VARIANT, BlockStone.EnumType.ANDESITE)); // 6
        add(new Triple<>(Blocks.stone, null, null)); // 7
        add(new Triple<>(Blocks.stone, BlockStone.VARIANT, BlockStone.EnumType.ANDESITE)); // 8
        add(new Triple<>(Blocks.stone, BlockStone.VARIANT, BlockStone.EnumType.ANDESITE)); // 9
        add(new Triple<>(Blocks.stone_brick_stairs, null, null)); // 10
        add(new Triple<>(Blocks.stone, BlockStone.VARIANT, BlockStone.EnumType.ANDESITE)); // 11
        add(new Triple<>(Blocks.stone, null, null)); // 12
        add(new Triple<>(Blocks.stone, BlockStone.VARIANT, BlockStone.EnumType.ANDESITE)); // 13
    }},StructureType.CH_MOB_SPOTS,LocationUtils.Island.CRYSTAL_HOLLOWS, CrystalHollowsQuarter.JUNGLE, "§aMini Jungle Temple",0,0,0),
    PRECURSOR_TRIPWIRE_CHAMBER(new ArrayList<Triple<Block, PropertyEnum, Comparable>>() {{
        add(new Triple<>(Blocks.stone, BlockStone.VARIANT, BlockStone.EnumType.DIORITE)); // 0
        add(new Triple<>(Blocks.stone, BlockStone.VARIANT, BlockStone.EnumType.DIORITE)); // 1
        add(new Triple<>(Blocks.double_stone_slab, null, null)); // 2
        add(new Triple<>(Blocks.double_stone_slab, null, null)); // 3
        add(new Triple<>(Blocks.double_stone_slab, null, null)); // 4
        add(new Triple<>(Blocks.stone, BlockStone.VARIANT, BlockStone.EnumType.DIORITE));
        add(new Triple<>(Blocks.stone, BlockStone.VARIANT, BlockStone.EnumType.DIORITE));
        add(new Triple<>(Blocks.stone, BlockStone.VARIANT, BlockStone.EnumType.DIORITE));
        add(new Triple<>(Blocks.double_stone_slab, null, null));
        add(new Triple<>(Blocks.double_stone_slab, null, null));
        add(new Triple<>(Blocks.stone, BlockStone.VARIANT, BlockStone.EnumType.DIORITE));
        add(new Triple<>(Blocks.double_stone_slab, null, null));
        add(new Triple<>(Blocks.double_stone_slab, null, null));
        add(new Triple<>(Blocks.double_stone_slab, null, null));
    }},StructureType.CH_MOB_SPOTS,LocationUtils.Island.CRYSTAL_HOLLOWS, CrystalHollowsQuarter.PRECURSOR_REMNANTS, "§bPrecursor Tripwire Chamber",0,0,0),
    PRECURSOR_TALL_PILLARS(new ArrayList<Triple<Block, PropertyEnum, Comparable>>() {{
        add(new Triple<>(Blocks.stone, BlockStone.VARIANT, BlockStone.EnumType.DIORITE_SMOOTH)); // 0
        add(new Triple<>(Blocks.stone, BlockStone.VARIANT, BlockStone.EnumType.DIORITE_SMOOTH)); // 1
        add(new Triple<>(Blocks.stone, BlockStone.VARIANT, BlockStone.EnumType.DIORITE)); // 2
        add(new Triple<>(Blocks.stone, BlockStone.VARIANT, BlockStone.EnumType.DIORITE)); // 3
        add(new Triple<>(Blocks.stone, BlockStone.VARIANT, BlockStone.EnumType.DIORITE_SMOOTH)); // 4
        add(new Triple<>(Blocks.stone, BlockStone.VARIANT, BlockStone.EnumType.DIORITE_SMOOTH)); // 5
        add(new Triple<>(Blocks.stone, BlockStone.VARIANT, BlockStone.EnumType.ANDESITE_SMOOTH)); // 6
        add(new Triple<>(Blocks.stone, BlockStone.VARIANT, BlockStone.EnumType.DIORITE)); // 7
        add(new Triple<>(Blocks.stone, BlockStone.VARIANT, BlockStone.EnumType.DIORITE)); // 8
        add(new Triple<>(Blocks.stone, BlockStone.VARIANT, BlockStone.EnumType.DIORITE)); // 9
        add(new Triple<>(Blocks.stone, BlockStone.VARIANT, BlockStone.EnumType.DIORITE)); // 10
        add(new Triple<>(Blocks.stone, BlockStone.VARIANT, BlockStone.EnumType.DIORITE_SMOOTH)); // 11
        add(new Triple<>(Blocks.stone, BlockStone.VARIANT, BlockStone.EnumType.DIORITE)); // 12
        add(new Triple<>(Blocks.stone, BlockStone.VARIANT, BlockStone.EnumType.ANDESITE_SMOOTH)); // 13
        add(new Triple<>(Blocks.stone, BlockStone.VARIANT, BlockStone.EnumType.DIORITE_SMOOTH)); // 14
        add(new Triple<>(Blocks.stone, BlockStone.VARIANT, BlockStone.EnumType.DIORITE)); // 15
        add(new Triple<>(Blocks.stone, BlockStone.VARIANT, BlockStone.EnumType.DIORITE)); // 16
        add(new Triple<>(Blocks.stone, BlockStone.VARIANT, BlockStone.EnumType.DIORITE)); // 17
        add(new Triple<>(Blocks.stone, BlockStone.VARIANT, BlockStone.EnumType.DIORITE)); // 18
        add(new Triple<>(Blocks.stone, BlockStone.VARIANT, BlockStone.EnumType.DIORITE_SMOOTH)); // 19
    }},StructureType.CH_MOB_SPOTS,LocationUtils.Island.CRYSTAL_HOLLOWS, CrystalHollowsQuarter.PRECURSOR_REMNANTS, "§bPrecursor Tall Pillars",0,0,0),
    GOBLIN_HOLE_CAMP(new ArrayList<Triple<Block, PropertyEnum, Comparable>>() {{
        add(new Triple<>(Blocks.netherrack, null, null)); // 0
        add(new Triple<>(Blocks.netherrack, null, null)); // 1
        add(new Triple<>(Blocks.oak_fence, null, null)); // 2
        add(new Triple<>(Blocks.oak_fence, null, null)); // 3
        add(new Triple<>(Blocks.log, BlockPlanks.VARIANT, BlockPlanks.EnumType.OAK)); // 4
        add(new Triple<>(Blocks.log, BlockPlanks.VARIANT, BlockPlanks.EnumType.OAK)); // 5
    }},StructureType.CH_MOB_SPOTS,LocationUtils.Island.CRYSTAL_HOLLOWS, CrystalHollowsQuarter.GOBLIN_HOLDOUT, "§6Goblin Hole Camp",0,0,0),

    GOLDEN_DRAGON(new ArrayList<Triple<Block, PropertyEnum, Comparable>>() {{
        add(new Triple<>(Blocks.stone, null, null));
        add(new Triple<>(Blocks.stained_hardened_clay, BlockColored.COLOR, EnumDyeColor.RED));
        add(new Triple<>(Blocks.stained_hardened_clay, BlockColored.COLOR, EnumDyeColor.RED));
        add(new Triple<>(Blocks.stained_hardened_clay, BlockColored.COLOR, EnumDyeColor.RED));
        add(new Triple<>(Blocks.skull, null, null));
        add(new Triple<>(Blocks.wool, BlockColored.COLOR, EnumDyeColor.RED));
    }}, StructureType.GOLDEN_DRAGON, LocationUtils.Island.CRYSTAL_HOLLOWS, CrystalHollowsQuarter.ANY, "", 0, -3, 5);

//    A(new ArrayList<Triple<Block, PropertyEnum, Comparable>>() {{
//        add(new Triple<>(null, null, null)); // 0
//        add(new Triple<>(null, null, null)); // 1
//        add(new Triple<>(null, null, null)); // 2
//        add(new Triple<>(null, null, null)); // 3
//        add(new Triple<>(null, null, null)); // 4
//        add(new Triple<>(null, null, null)); // 5
//        add(new Triple<>(null, null, null)); // 6
//        add(new Triple<>(null, null, null)); // 7
//        add(new Triple<>(null, null, null)); // 8
//        add(new Triple<>(null, null, null)); // 9
//        add(new Triple<>(null, null, null)); // 10
//        add(new Triple<>(null, null, null)); // 11
//        add(new Triple<>(null, null, null)); // 12
//        add(new Triple<>(null, null, null)); // 13
//        add(new Triple<>(null, null, null)); // 14
//        add(new Triple<>(null, null, null)); // 15
//        add(new Triple<>(null, null, null)); // 16
//        add(new Triple<>(null, null, null)); // 17
//        add(new Triple<>(null, null, null)); // 18
//        add(new Triple<>(null, null, null)); // 19
//        add(new Triple<>(null, null, null)); // 20
//        add(new Triple<>(null, null, null)); // 21
//        add(new Triple<>(null, null, null)); // 22
//        add(new Triple<>(null, null, null)); // 23
//        add(new Triple<>(null, null, null)); // 24
//        add(new Triple<>(null, null, null)); // 25
//        add(new Triple<>(null, null, null)); // 26
//        add(new Triple<>(null, null, null)); // 27
//    }},StructureType.CH_MOB_SPOTS,LocationUtils.Island.CRYSTAL_HOLLOWS,"",23,11,17),

    private final ArrayList<Triple<Block, PropertyEnum, Comparable>> states;
    private final StructureType structureType;
    private final LocationUtils.Island island;
    private final CrystalHollowsQuarter crystalHollowsQuarter;
    private final String name;
    private final int xOffset;
    private final int yOffset;
    private final int zOffset;


    public ArrayList<Triple<Block, PropertyEnum, Comparable>> getStates() {
        return this.states;
    }

    public StructureType getStructureType() {
        return this.structureType;
    }

    public LocationUtils.Island getIsland() {
        return this.island;
    }

    public CrystalHollowsQuarter getQuarter() {
        return this.crystalHollowsQuarter;
    }

    public String getName() {
        return this.name;
    }

    public int getXOffset() {
        return this.xOffset;
    }

    public int getYOffset() {
        return this.yOffset;
    }

    public int getZOffset() {
        return this.zOffset;
    }

    Structure(ArrayList<Triple<Block, PropertyEnum, Comparable>> states, StructureType structureType, LocationUtils.Island island, CrystalHollowsQuarter crystalHollowsQuarter, String name, int xOffset, int yOffset, int zOffset) {
        this.states = states;
        this.structureType = structureType;
        this.island = island;
        this.crystalHollowsQuarter = crystalHollowsQuarter;
        this.name = name;
        this.xOffset = xOffset;
        this.yOffset = yOffset;
        this.zOffset = zOffset;
    }
}
