package rosegold.gumtuneclient.command;

import cc.polyfrost.oneconfig.utils.Multithreading;
import cc.polyfrost.oneconfig.utils.commands.annotations.Command;
import cc.polyfrost.oneconfig.utils.commands.annotations.Main;
import cc.polyfrost.oneconfig.utils.commands.annotations.SubCommand;
import net.minecraft.client.multiplayer.ChunkProviderClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityArmorStand;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.chunk.Chunk;
import rosegold.gumtuneclient.GumTuneClient;
import rosegold.gumtuneclient.config.GumTuneClientConfig;
import rosegold.gumtuneclient.modules.player.PathFinding;
import rosegold.gumtuneclient.modules.world.WorldScanner;
import rosegold.gumtuneclient.utils.*;
import rosegold.gumtuneclient.utils.pathfinding.PathFinder;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.lang.reflect.Field;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.StreamSupport;

@Command(value = GumTuneClient.MODID, description = "Access the " + GumTuneClient.NAME + " GUI.", aliases = {"gtc"})
public class MainCommand {
    @Main
    private static void main() {
        GumTuneClient.INSTANCE.config.openGui();
    }

    @SubCommand(description = "Copies all entities to clipboard")
    private void allentities() {
        StringSelection selection = new StringSelection(GumTuneClient.mc.theWorld.loadedEntityList.toString());
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents(selection, selection);
    }

    @SubCommand(description = "Copies all entities to clipboard")
    private void armorstands(String arg) {
        if(!isInteger(arg)) {
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
            stringBuilder.append(EntityUtils.getEntityData(entity));
        }

        ModUtils.sendMessage("Copied NBT date of " + entityList.size() + " Entities");
        StringSelection selection = new StringSelection(stringBuilder.toString());
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents(selection, selection);
    }

    @SubCommand(description = "Rotate to <yaw, pitch>")
    private void rotate(String pitch, String yaw, String mode) {
        if (pitch == null || !isNumeric(yaw)) {
            ModUtils.sendMessage("&cInvalid pitch: " + pitch);
            return;
        }
        if (yaw == null || !isNumeric(yaw)) {
            ModUtils.sendMessage("&cInvalid yaw:" + yaw);
            return;
        }
        if (mode == null || !mode.toLowerCase(Locale.ROOT).equals("instant") &&
                !mode.toLowerCase(Locale.ROOT).equals("smooth") && !mode.toLowerCase(Locale.ROOT).equals("serversmooth")) {
            ModUtils.sendMessage("&cInvalid mode " + mode + " please select either <instant, smooth, serversmooth>");
            return;
        }

        switch (mode.toLowerCase(Locale.ROOT)) {
            case "instant":
                RotationUtils.look(new RotationUtils.Rotation(Float.parseFloat(pitch), Float.parseFloat(yaw)));
                break;
            case "smooth":
                RotationUtils.smoothLook(new RotationUtils.Rotation(Float.parseFloat(pitch), Float.parseFloat(yaw)), 250);
                break;
            case "serversmooth":
                if (!GumTuneClientConfig.alwaysShowServerRotations) {
                    ModUtils.sendMessage("Turn on \"Always show server rotations\" under config");
                }
                RotationUtils.serverSmoothLook(new RotationUtils.Rotation(Float.parseFloat(pitch), Float.parseFloat(yaw)), 250);
                break;
        }
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
            PathFinder.setup(new BlockPos(VectorUtils.floorVec(GumTuneClient.mc.thePlayer.getPositionVector())), new BlockPos(Integer.parseInt(x), Integer.parseInt(y), Integer.parseInt(z)), 0.0);
        });
    }

    @SubCommand(description = "test")
    private void test(String range) {
        PathFinding.points.clear();
        PathFinding.temp = RaytracingUtils.getAllTeleportableBlocks(GumTuneClient.mc.thePlayer.getPositionEyes(1f), Float.parseFloat(range));
    }

    @SubCommand(description = "enable debug")
    private void debug() {
        GumTuneClient.debug = !GumTuneClient.debug;
        ModUtils.sendMessage("Debug Mode: " + (GumTuneClient.debug ? "Enabled" : "Disabled"));
    }

    @SubCommand(description = "test2")
    private void test2(String x, String y, String z) {
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

        PathFinding.points.clear();
        PathFinding.temp = RaytracingUtils.getAllTeleportableBlocksNew(GumTuneClient.mc.thePlayer.getPositionEyes(1f), 16f);
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