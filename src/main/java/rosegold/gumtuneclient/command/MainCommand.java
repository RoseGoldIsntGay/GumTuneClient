package rosegold.gumtuneclient.command;

import cc.polyfrost.oneconfig.utils.commands.annotations.SubCommand;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityArmorStand;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.WorldServer;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.storage.ExtendedBlockStorage;
import net.minecraftforge.fml.common.FMLCommonHandler;
import rosegold.gumtuneclient.GumTuneClient;
import cc.polyfrost.oneconfig.utils.commands.annotations.Command;
import cc.polyfrost.oneconfig.utils.commands.annotations.Main;
import rosegold.gumtuneclient.config.GumTuneClientConfig;
import rosegold.gumtuneclient.utils.EntityUtils;
import rosegold.gumtuneclient.utils.ModUtils;
import rosegold.gumtuneclient.utils.PlayerUtils;
import rosegold.gumtuneclient.utils.RotationUtils;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
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

    @SubCommand(description = "Copies all entities to clipboard", aliases = {"allentities"})
    private void allEntities() {
        StringSelection selection = new StringSelection(GumTuneClient.mc.theWorld.loadedEntityList.toString());
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents(selection, selection);
    }

    @SubCommand(description = "Copies all entities to clipboard", aliases = {"armorstands"})
    private void armorStands(String arg) {
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

    @SubCommand(description = "Rotate to <yaw, pitch>", aliases = {"rotate"})
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
    private void breakBlock(String x, String y, String z) {
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

    @SubCommand(description = "Break specified block", aliases = {"storage"})
    private void printStorageArrays() {
        List<Chunk> loadedChunks = FMLCommonHandler.instance().getMinecraftServerInstance().worldServerForDimension(GumTuneClient.mc.thePlayer.dimension).theChunkProviderServer.func_152380_a();
        for (Chunk chunk : loadedChunks) {
            for (ExtendedBlockStorage blockStorage : chunk.getBlockStorageArray()) {

            }
        }
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