package rosegold.gumtuneclient.utils;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MovingObjectPosition;
import rosegold.gumtuneclient.GumTuneClient;

public class PlayerUtils {

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

    public static void swingHand(MovingObjectPosition objectMouseOver) {
        if (objectMouseOver == null) {
            objectMouseOver = GumTuneClient.mc.objectMouseOver;
        }
        if (objectMouseOver != null && objectMouseOver.entityHit == null) {
            GumTuneClient.mc.thePlayer.swingItem();
        }
    }

    public static void rightClick() {
        if(!ReflectionUtils.invoke(GumTuneClient.mc, "func_147121_ag")) {
            ReflectionUtils.invoke(GumTuneClient.mc, "rightClickMouse");
        }
    }

    public static void leftClick() {
        if(!ReflectionUtils.invoke(GumTuneClient.mc, "func_147116_af")) {
            ReflectionUtils.invoke(GumTuneClient.mc, "clickMouse");
        }
    }

    public static void middleClick() {
        if(!ReflectionUtils.invoke(GumTuneClient.mc, "func_147112_ai")) {
            ReflectionUtils.invoke(GumTuneClient.mc, "middleClickMouse");
        }
    }
}
