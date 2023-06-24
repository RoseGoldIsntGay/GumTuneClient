package rosegold.gumtuneclient.command;

import cc.polyfrost.oneconfig.utils.Multithreading;
import cc.polyfrost.oneconfig.utils.commands.annotations.Command;
import cc.polyfrost.oneconfig.utils.commands.annotations.Main;
import cc.polyfrost.oneconfig.utils.commands.annotations.SubCommand;
import cc.polyfrost.oneconfig.utils.commands.annotations.SubCommandGroup;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityArmorStand;
import net.minecraft.init.Blocks;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import rosegold.gumtuneclient.GumTuneClient;
import rosegold.gumtuneclient.modules.player.PathFinding;
import rosegold.gumtuneclient.modules.render.CustomBlockESP;
import rosegold.gumtuneclient.utils.*;
import rosegold.gumtuneclient.utils.pathfinding.PathFinder;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Command(value = GumTuneClient.MODID, description = "Access the " + GumTuneClient.NAME + " GUI.", aliases = {"gtc"})
public class MainCommand {

    @Main
    private static void main() {
        GumTuneClient.config.openGui();
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
                blockPos -> GumTuneClient.mc.theWorld.getBlockState(blockPos).getBlock() == Blocks.cocoa,
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

    @SubCommand(description = "print version in chat")
    private void version() {
        ModUtils.sendMessage(GumTuneClient.VERSION);
    }

    @SubCommandGroup(value = "esp")
    private class ESPSubCommandGroup {
        @Main
        private void method() {
            ModUtils.sendMessage("usage:");
            ModUtils.sendMessage("/gtc esp add block <name:meta | id:meta>, color <hex>");
            ModUtils.sendMessage("/gtc esp remove block <name:meta | id:meta>");
            ModUtils.sendMessage("/gtc esp reset");
            ModUtils.sendMessage("/gtc esp list");
        }

        @SubCommand()
        private void add(String blockName, String color) {
            IBlockState blockState;
            boolean wildcard = false;

            if (blockName.contains(":")) {
                String[] split = blockName.split(":");

                if (split.length > 2 || Block.getBlockFromName(split[0]) == null) {
                    ModUtils.sendMessage("Invalid block!");
                    return;
                }

                if (!isInteger(split[1])) {
                    ModUtils.sendMessage("Invalid meta!");
                    return;
                }

                blockState = Block.getBlockFromName(split[0]).getStateFromMeta(Integer.parseInt(split[1]));
            } else {
                Block block = Block.getBlockFromName(blockName);

                if (block == null) {
                    ModUtils.sendMessage("Invalid block!");
                    return;
                }

                blockState = block.getDefaultState();
                wildcard = true;
            }

            ModUtils.sendMessage("Added " + (wildcard ? blockState.getBlock() : blockState) + " with color " + color + " to Custom Block ESP filter");
            CustomBlockESP.addBlock(blockState, Color.decode(color), wildcard);
        }

        @SubCommand()
        private void remove(String blockName) {
            IBlockState blockState;
            boolean wildcard = false;

            if (blockName.contains(":")) {
                String[] split = blockName.split(":");

                if (split.length > 2 || Block.getBlockFromName(split[0]) == null) {
                    ModUtils.sendMessage("Invalid block!");
                    return;
                }

                if (!isInteger(split[1])) {
                    ModUtils.sendMessage("Invalid meta!");
                    return;
                }

                blockState = Block.getBlockFromName(split[0]).getStateFromMeta(Integer.parseInt(split[1]));
            } else {
                Block block = Block.getBlockFromName(blockName);

                if (block == null) {
                    ModUtils.sendMessage("Invalid block!");
                    return;
                }

                blockState = block.getDefaultState();
                wildcard = true;
            }

            ModUtils.sendMessage("Removed " + blockState + " from Custom Block ESP filter");
            CustomBlockESP.removeBlock(blockState, wildcard);
        }

        @SubCommand()
        private void reset() {
            ModUtils.sendMessage("Reset Custom Block ESP filter");
            CustomBlockESP.reset();
        }

        @SubCommand()
        private void list() {
            ModUtils.sendMessage("Custom Block ESP filters:");
            CustomBlockESP.list();
        }

        @SubCommand
        private void dump() {
            CustomBlockESP.dump();
        }

        @SubCommand
        private void reload() {
            ModUtils.sendMessage("Reloading chunks");
            CustomBlockESP.reload();
        }
    }

    private void saveToClipoard(String string) {
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