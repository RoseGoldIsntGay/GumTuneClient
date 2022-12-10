package rosegold.gumtuneclient.command;

import cc.polyfrost.oneconfig.utils.commands.annotations.SubCommand;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityArmorStand;
import rosegold.gumtuneclient.GumTuneClient;
import cc.polyfrost.oneconfig.utils.commands.annotations.Command;
import cc.polyfrost.oneconfig.utils.commands.annotations.Main;
import rosegold.gumtuneclient.utils.EntityUtils;
import rosegold.gumtuneclient.utils.ModUtils;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;
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

    private boolean isInteger(String s) {
        return isInteger(s,10);
    }

    private boolean isInteger(String s, int radix) {
        Scanner sc = new Scanner(s.trim());
        if(!sc.hasNextInt(radix)) return false;
        sc.nextInt(radix);
        return !sc.hasNext();
    }
}