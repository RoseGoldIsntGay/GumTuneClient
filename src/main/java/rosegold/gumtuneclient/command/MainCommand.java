package rosegold.gumtuneclient.command;

import cc.polyfrost.oneconfig.utils.Multithreading;
import cc.polyfrost.oneconfig.utils.commands.annotations.*;
import com.google.common.collect.Lists;
import net.minecraft.block.Block;
import net.minecraft.client.multiplayer.ChunkProviderClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityArmorStand;
import net.minecraft.init.Blocks;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Vec3;
import net.minecraft.world.chunk.Chunk;
import rosegold.gumtuneclient.GumTuneClient;
import rosegold.gumtuneclient.config.GumTuneClientConfig;
import rosegold.gumtuneclient.modules.player.PathFinding;
import rosegold.gumtuneclient.modules.render.ESPs;
import rosegold.gumtuneclient.modules.world.WorldScanner;
import rosegold.gumtuneclient.utils.*;
import rosegold.gumtuneclient.utils.pathfinding.PathFinder;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.lang.reflect.Field;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

@Command(value = GumTuneClient.MODID, description = "Access the " + GumTuneClient.NAME + " GUI.", aliases = {"gtc"})
public class MainCommand {

    @Main
    private static void main() {
        GumTuneClient.INSTANCE.config.openGui();
    }

    @SubCommand(description = "Copies all entities to clipboard")
    private void allentities() {
        saveToClipoard(GumTuneClient.mc.theWorld.loadedEntityList.toString());
    }

    @SubCommand(description = "Copies all entities in range to clipboard")
    private void entities(String arg) {
        if (!isInteger(arg)) {
            ModUtils.sendMessage("Invalid range.");
        }
        int range = Integer.parseInt(arg);
        List<Entity> entityList = GumTuneClient.mc.theWorld.loadedEntityList.stream().filter(
                entity -> entity.getDistanceToEntity(GumTuneClient.mc.thePlayer) <= range
        ).filter(
                entity -> entity != GumTuneClient.mc.thePlayer
        ).sorted(
                Comparator.comparingDouble(entity -> entity.getDistanceToEntity(GumTuneClient.mc.thePlayer))
        ).collect(Collectors.toList());

        StringBuilder stringBuilder = new StringBuilder();

        for (Entity entity : entityList) {
            stringBuilder.append(DevUtils.getEntityData(entity));
        }

        ModUtils.sendMessage("Copied NBT data of " + entityList.size() + " Entities");
        saveToClipoard(stringBuilder.toString());
    }

    @SubCommand(description = "Copies all armorstands to clipboard")
    private void armorstands(String arg) {
        if (!isInteger(arg)) {
            ModUtils.sendMessage("Invalid range.");
        }
        int range = Integer.parseInt(arg);
        List<Entity> entityList = GumTuneClient.mc.theWorld.loadedEntityList.stream().filter(
                entity -> entity instanceof EntityArmorStand && entity.getDistanceToEntity(GumTuneClient.mc.thePlayer) <= range
        ).sorted(
                Comparator.comparingDouble(entity -> entity.getDistanceToEntity(GumTuneClient.mc.thePlayer))
        ).collect(Collectors.toList());

        StringBuilder stringBuilder = new StringBuilder();

        for (Entity entity : entityList) {
            stringBuilder.append(DevUtils.getEntityData(entity));
        }

        ModUtils.sendMessage("Copied NBT data of " + entityList.size() + " Entities");
        saveToClipoard(stringBuilder.toString());
    }

    @SubCommand(description = "Rotate to <yaw, pitch>")
    private void rotate(String pitch, String yaw) {
        if (pitch == null || !isNumeric(yaw)) {
            ModUtils.sendMessage("&cInvalid pitch: " + pitch);
            return;
        }
        if (yaw == null || !isNumeric(yaw)) {
            ModUtils.sendMessage("&cInvalid yaw:" + yaw);
            return;
        }

        RotationUtils.smoothLook(new RotationUtils.Rotation(Float.parseFloat(pitch), Float.parseFloat(yaw)), 250);
    }

    @SubCommand(description = "Break specified block", aliases = {"break"})
    private void breakblock(String x, String y, String z) {
        if (x == null || !isInteger(x)) {
            ModUtils.sendMessage("Invalid x coordinate: " + x);
            return;
        }
        if (y == null || !isInteger(y)) {
            ModUtils.sendMessage("Invalid y coordinate: " + y);
            return;
        }
        if (z == null || !isInteger(z)) {
            ModUtils.sendMessage("Invalid z coordinate: " + z);
            return;
        }

        BlockPos blockPos = new BlockPos(Integer.parseInt(x), Integer.parseInt(y), Integer.parseInt(z));
        MovingObjectPosition objectMouseOver = GumTuneClient.mc.objectMouseOver;
        objectMouseOver.hitVec = new Vec3(blockPos);
        if (objectMouseOver.sideHit != null) {
            PlayerUtils.swingHand(null);
            GumTuneClient.mc.thePlayer.sendQueue.addToSendQueue(new C07PacketPlayerDigging(
                    C07PacketPlayerDigging.Action.START_DESTROY_BLOCK,
                    blockPos,
                    objectMouseOver.sideHit)
            );
        }
    }

    @SubCommand(description = "Rescan all loaded chunks with WorldScanner", aliases = {"reloadchunks"})
    private void reloadchunks() {
        try {
            ChunkProviderClient chunkProvider = (ChunkProviderClient) GumTuneClient.mc.theWorld.getChunkProvider();
            Field chunkListingField = chunkProvider.getClass().getDeclaredField("field_73237_c");
            chunkListingField.setAccessible(true);
            List<Chunk> chunkList = (List<Chunk>) chunkListingField.get(chunkProvider);
            for (Chunk chunk : chunkList) {
                WorldScanner.handleChunkLoad(chunk, WorldScanner.worlds.get(LocationUtils.serverName));
            }
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    @SubCommand(description = "walk to blockpos")
    private void pathfind(String x, String y, String z) {
        if (x == null || !isInteger(x)) {
            ModUtils.sendMessage("Invalid x coordinate: " + x);
            return;
        }
        if (y == null || !isInteger(y)) {
            ModUtils.sendMessage("Invalid y coordinate: " + y);
            return;
        }
        if (z == null || !isInteger(z)) {
            ModUtils.sendMessage("Invalid z coordinate: " + z);
            return;
        }

        Multithreading.runAsync(() -> {
            PathFinding.initTeleport();
            PathFinder.setup(new BlockPos(VectorUtils.floorVec(GumTuneClient.mc.thePlayer.getPositionVector())), new BlockPos(Integer.parseInt(x), Integer.parseInt(y), Integer.parseInt(z)), 0.0, 2000);
        });
    }

    @SubCommand(description = "raytrace")
    private void raytrace(String x, String y, String z, String fullBlocks) {
        if (x == null || !isNumeric(x)) {
            ModUtils.sendMessage("Invalid x coordinate: " + x);
            return;
        }
        if (y == null || !isNumeric(y)) {
            ModUtils.sendMessage("Invalid y coordinate: " + y);
            return;
        }
        if (z == null || !isNumeric(z)) {
            ModUtils.sendMessage("Invalid z coordinate: " + z);
            return;
        }
        if (fullBlocks == null || !isBooleanic(fullBlocks)) {
            ModUtils.sendMessage("Invalid boolean fullBlocks: " + fullBlocks);
            return;
        }

        BlockUtils.blockPosConcurrentLinkedQueue.clear();
        BlockUtils.source = GumTuneClient.mc.thePlayer.getPositionEyes(1f);
        BlockUtils.destination = new Vec3(Double.parseDouble(x), Double.parseDouble(y), Double.parseDouble(z));
        BlockUtils.rayTraceBlocks(
                GumTuneClient.mc.thePlayer.getPositionEyes(1f),
                new Vec3(Double.parseDouble(x), Double.parseDouble(y), Double.parseDouble(z)),
                false,
                true,
                false,
                block -> block == Blocks.cocoa,
                true,
                Boolean.parseBoolean(fullBlocks)
        );
    }

    @SubCommand(description = "enable debug")
    private void debug() {
        GumTuneClient.debug = !GumTuneClient.debug;
        ModUtils.sendMessage("Debug Mode: " + (GumTuneClient.debug ? "Enabled" : "Disabled"));
    }

    @SubCommand(description = "copy")
    private void setclipboard(String args) {
        StringBuilder stringBuilder = new StringBuilder();
        for (String arg : args.split(";")) {
            stringBuilder.append(arg).append(" ");
        }
        stringBuilder.deleteCharAt(stringBuilder.length() - 1);
        saveToClipoard(stringBuilder.toString());
    }

    @SubCommand(description = "tablist")
    private void tablist() {
        for (String entry : TabListUtils.getTabList()) {
            ModUtils.sendMessage(entry);
        }
    }

    @SubCommandGroup(value = "esp")
    private class ESPSubCommandGroup {
        @Main
        private void method() {
            ModUtils.sendMessage("usage /gtc esp <add | remove | reset>, block name, hex color");
        }

        @SubCommand()
        private void add(@Description(autoCompletesTo = {"air", "stone", "grass", "dirt", "cobblestone", "planks", "sapling", "bedrock", "flowing_water", "water", "flowing_lava", "lava", "sand", "gravel", "gold_ore", "iron_ore", "coal_ore", "log", "leaves", "sponge", "glass", "lapis_ore", "lapis_block", "dispenser", "sandstone", "noteblock", "bed", "golden_rail", "detector_rail", "sticky_piston", "web", "tallgrass", "deadbush", "piston", "piston_head", "wool", "piston_extension", "yellow_flower", "red_flower", "brown_mushroom", "red_mushroom", "gold_block", "iron_block", "double_stone_slab", "stone_slab", "brick_block", "tnt", "bookshelf", "mossy_cobblestone", "obsidian", "torch", "fire", "mob_spawner", "oak_stairs", "chest", "redstone_wire", "diamond_ore", "diamond_block", "crafting_table", "wheat", "farmland", "furnace", "lit_furnace", "standing_sign", "wooden_door", "ladder", "rail", "stone_stairs", "wall_sign", "lever", "stone_pressure_plate", "iron_door", "wooden_pressure_plate", "redstone_ore", "lit_redstone_ore", "unlit_redstone_torch", "redstone_torch", "stone_button", "snow_layer", "ice", "snow", "cactus", "clay", "reeds", "jukebox", "fence", "pumpkin", "netherrack", "soul_sand", "glowstone", "portal", "lit_pumpkin", "cake", "unpowered_repeater", "powered_repeater", "stained_glass", "trapdoor", "monster_egg", "stonebrick", "brown_mushroom_block", "red_mushroom_block", "iron_bars", "glass_pane", "melon_block", "pumpkin_stem", "melon_stem", "vine", "fence_gate", "brick_stairs", "stone_brick_stairs", "mycelium", "waterlily", "nether_brick", "nether_brick_fence", "nether_brick_stairs", "nether_wart", "enchanting_table", "brewing_stand", "cauldron", "end_portal", "end_portal_frame", "end_stone", "dragon_egg", "redstone_lamp", "lit_redstone_lamp", "double_wooden_slab", "wooden_slab", "cocoa", "sandstone_stairs", "emerald_ore", "ender_chest", "tripwire_hook", "tripwire", "emerald_block", "spruce_stairs", "birch_stairs", "jungle_stairs", "command_block", "beacon", "cobblestone_wall", "flower_pot", "carrots", "potatoes", "wooden_button", "skull", "anvil", "trapped_chest", "light_weighted_pressure_plate", "heavy_weighted_pressure_plate", "unpowered_comparator", "powered_comparator", "daylight_detector", "redstone_block", "quartz_ore", "hopper", "quartz_block", "quartz_stairs", "activator_rail", "dropper", "stained_hardened_clay", "stained_glass_pane", "leaves2", "log2", "acacia_stairs", "dark_oak_stairs", "slime", "barrier", "iron_trapdoor", "prismarine", "sea_lantern", "hay_block", "carpet", "hardened_clay", "coal_block", "packed_ice", "double_plant", "standing_banner", "wall_banner", "daylight_detector_inverted", "red_sandstone", "red_sandstone_stairs", "double_stone_slab2", "stone_slab2", "spruce_fence_gate", "birch_fence_gate", "jungle_fence_gate", "dark_oak_fence_gate", "acacia_fence_gate", "spruce_fence", "birch_fence", "jungle_fence", "dark_oak_fence", "acacia_fence", "spruce_door", "birch_door", "jungle_door", "acacia_door", "dark_oak_door"}) String blockName, String color) {
            Block block = Block.blockRegistry.getObject(new ResourceLocation(blockName));
            if (block == null) {
                ModUtils.sendMessage("Invalid block!");
                return;
            }
            ESPs.blockEsp.put(block, Color.decode(color));

            if (GumTuneClientConfig.customESPForceRecheck) {
                Object object = ReflectionUtils.field(GumTuneClient.mc.theWorld.getChunkProvider(), "field_73237_c");
                if (object != null && object.getClass() == Lists.newArrayList().getClass()) {
                    for (Chunk chunk : (List<Chunk>) object) {
                        Multithreading.runAsync(() -> ESPs.handleChunkLoad(chunk));
                    }
                }
            }
        }

        @SubCommand()
        private void remove(@Description(autoCompletesTo = {"air", "stone", "grass", "dirt", "cobblestone", "planks", "sapling", "bedrock", "flowing_water", "water", "flowing_lava", "lava", "sand", "gravel", "gold_ore", "iron_ore", "coal_ore", "log", "leaves", "sponge", "glass", "lapis_ore", "lapis_block", "dispenser", "sandstone", "noteblock", "bed", "golden_rail", "detector_rail", "sticky_piston", "web", "tallgrass", "deadbush", "piston", "piston_head", "wool", "piston_extension", "yellow_flower", "red_flower", "brown_mushroom", "red_mushroom", "gold_block", "iron_block", "double_stone_slab", "stone_slab", "brick_block", "tnt", "bookshelf", "mossy_cobblestone", "obsidian", "torch", "fire", "mob_spawner", "oak_stairs", "chest", "redstone_wire", "diamond_ore", "diamond_block", "crafting_table", "wheat", "farmland", "furnace", "lit_furnace", "standing_sign", "wooden_door", "ladder", "rail", "stone_stairs", "wall_sign", "lever", "stone_pressure_plate", "iron_door", "wooden_pressure_plate", "redstone_ore", "lit_redstone_ore", "unlit_redstone_torch", "redstone_torch", "stone_button", "snow_layer", "ice", "snow", "cactus", "clay", "reeds", "jukebox", "fence", "pumpkin", "netherrack", "soul_sand", "glowstone", "portal", "lit_pumpkin", "cake", "unpowered_repeater", "powered_repeater", "stained_glass", "trapdoor", "monster_egg", "stonebrick", "brown_mushroom_block", "red_mushroom_block", "iron_bars", "glass_pane", "melon_block", "pumpkin_stem", "melon_stem", "vine", "fence_gate", "brick_stairs", "stone_brick_stairs", "mycelium", "waterlily", "nether_brick", "nether_brick_fence", "nether_brick_stairs", "nether_wart", "enchanting_table", "brewing_stand", "cauldron", "end_portal", "end_portal_frame", "end_stone", "dragon_egg", "redstone_lamp", "lit_redstone_lamp", "double_wooden_slab", "wooden_slab", "cocoa", "sandstone_stairs", "emerald_ore", "ender_chest", "tripwire_hook", "tripwire", "emerald_block", "spruce_stairs", "birch_stairs", "jungle_stairs", "command_block", "beacon", "cobblestone_wall", "flower_pot", "carrots", "potatoes", "wooden_button", "skull", "anvil", "trapped_chest", "light_weighted_pressure_plate", "heavy_weighted_pressure_plate", "unpowered_comparator", "powered_comparator", "daylight_detector", "redstone_block", "quartz_ore", "hopper", "quartz_block", "quartz_stairs", "activator_rail", "dropper", "stained_hardened_clay", "stained_glass_pane", "leaves2", "log2", "acacia_stairs", "dark_oak_stairs", "slime", "barrier", "iron_trapdoor", "prismarine", "sea_lantern", "hay_block", "carpet", "hardened_clay", "coal_block", "packed_ice", "double_plant", "standing_banner", "wall_banner", "daylight_detector_inverted", "red_sandstone", "red_sandstone_stairs", "double_stone_slab2", "stone_slab2", "spruce_fence_gate", "birch_fence_gate", "jungle_fence_gate", "dark_oak_fence_gate", "acacia_fence_gate", "spruce_fence", "birch_fence", "jungle_fence", "dark_oak_fence", "acacia_fence", "spruce_door", "birch_door", "jungle_door", "acacia_door", "dark_oak_door"}) String blockName) {
            ESPs.blockEsp.remove(Block.blockRegistry.getObject(new ResourceLocation(blockName)));

            if (GumTuneClientConfig.customESPForceRecheck) {
                Object object = ReflectionUtils.field(GumTuneClient.mc.theWorld.getChunkProvider(), "field_73237_c");
                if (object != null && object.getClass() == Lists.newArrayList().getClass()) {
                    System.out.println(object);
                    for (Chunk chunk : (List<Chunk>) object) {
                        Multithreading.runAsync(() -> ESPs.handleChunkLoad(chunk));
                    }
                }
            }
        }

        @SubCommand()
        private void reset() {
            ESPs.blockEsp.clear();
        }
    }

    @SubCommand(description = "apikey")
    private void apikey() {
        ModUtils.sendMessage(GumTuneClientConfig.hypixelApiKey);
    }

    private void saveToClipoard(String string){
        StringSelection selection = new StringSelection(string);
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents(selection, selection);
    }

    private boolean isBooleanic(String str) {
        return str.equals("true") || str.equals("false");
    }

    private boolean isNumeric(String str) {
        try {
            Double.parseDouble(str);
        } catch (NumberFormatException nfe) {
            return false;
        }
        return true;
    }

    private boolean isInteger(String str) {
        try {
            Integer.parseInt(str);
        } catch (NumberFormatException nfe) {
            return false;
        }
        return true;
    }
}