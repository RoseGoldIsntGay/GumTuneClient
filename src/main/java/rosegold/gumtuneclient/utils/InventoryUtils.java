package rosegold.gumtuneclient.utils;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import rosegold.gumtuneclient.GumTuneClient;

public class InventoryUtils {
    public static int findItemInHotbar(String name) {
        InventoryPlayer inv = GumTuneClient.mc.thePlayer.inventory;
        for (int i = 0; i < 9; i++) {
            ItemStack curStack = inv.getStackInSlot(i);
            if (curStack != null) {
                if (curStack.getDisplayName().contains(name)) {
                    return i;
                }
            }
        }
        return -1;
    }

    public static String getItemLore(ItemStack itemStack) {
        return itemStack.getTagCompound().getCompoundTag("display").getTagList("Lore", 8).toString();
    }

    public static String getItemLore(ItemStack itemStack, int index) {
        return itemStack.getTagCompound().getCompoundTag("display").getTagList("Lore", 8).getStringTagAt(index);
    }
}
