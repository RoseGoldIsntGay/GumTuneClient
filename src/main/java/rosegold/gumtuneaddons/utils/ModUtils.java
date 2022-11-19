package rosegold.gumtuneaddons.utils;

import net.minecraft.util.ChatComponentText;
import rosegold.gumtuneaddons.GumTuneAddons;

public class ModUtils {
    public static void sendMessage(Object object) {
        if (object == null) {
            object = "null";
        }
        GumTuneAddons.mc.thePlayer.addChatMessage(new ChatComponentText("§7[§5GumTuneAddons§7] §f" + object));
    }
}
