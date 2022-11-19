package rosegold.gumtuneaddons.utils;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import rosegold.gumtuneaddons.GumTuneAddons;

public class PlayerUtils {

    public static int findItemInHotbar(String name) {
        InventoryPlayer inv = GumTuneAddons.mc.thePlayer.inventory;
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
}
