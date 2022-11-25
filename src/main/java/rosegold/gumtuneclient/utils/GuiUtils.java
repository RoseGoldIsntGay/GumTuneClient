package rosegold.gumtuneclient.utils;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.inventory.ContainerChest;

import static rosegold.gumtuneclient.GumTuneClient.mc;

public class GuiUtils {
    public static String getInventoryName(GuiScreen gui) {
        if (gui instanceof GuiChest) {
            return ((ContainerChest) ((GuiChest) gui).inventorySlots).getLowerChestInventory().getDisplayName().getUnformattedText();
        } else return "";
    }

    public static String getOpenInventoryName() {
        if (mc.thePlayer == null || mc.theWorld == null) {
            return "";
        } else return mc.thePlayer.openContainer.inventorySlots.get(0).inventory.getName();
    }
}
