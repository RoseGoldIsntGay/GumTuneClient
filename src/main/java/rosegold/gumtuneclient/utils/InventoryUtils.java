package rosegold.gumtuneclient.utils;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
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

    public static int findItemInHotbarSkyblockId(String id) {
        InventoryPlayer inv = GumTuneClient.mc.thePlayer.inventory;
        for (int i = 0; i < 9; i++) {
            ItemStack curStack = inv.getStackInSlot(i);
            if (curStack != null) {
                String skyblockId = getSkyBlockItemId(curStack);
                if (skyblockId != null && skyblockId.equals(id)) {
                    return i;
                }
            }
        }
        return -1;
    }

    public static String getItemLore(ItemStack itemStack) {
        if (itemStack.hasTagCompound()) {
            return itemStack.getTagCompound().getCompoundTag("display").getTagList("Lore", 8).toString();
        }
        return null;
    }

    public static String getItemLore(ItemStack itemStack, int index) {
        if (itemStack.hasTagCompound()) {
            return itemStack.getTagCompound().getCompoundTag("display").getTagList("Lore", 8).getStringTagAt(index);
        }
        return null;
    }

    public static String getSkyBlockItemId(ItemStack itemStack) {
        NBTTagCompound extraAttributes = getExtraAttributes(itemStack);
        if (extraAttributes != null && extraAttributes.hasKey("id", 8)) {
            return extraAttributes.getString("id");
        }
        return null;
    }

    public static NBTTagCompound getExtraAttributes(ItemStack itemStack) {
        if (itemStack.hasTagCompound()) {
            return itemStack.getTagCompound().getCompoundTag("ExtraAttributes");
        }
        return null;
    }

    public static int getFilledSlotCount() {
        int count = 0;
        for (ItemStack itemStack : GumTuneClient.mc.thePlayer.inventory.mainInventory) {
            if (itemStack != null) {
                count++;
            }
        }
        return count;
    }
}
