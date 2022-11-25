package rosegold.gumtuneclient.utils;

import net.minecraft.util.ChatComponentText;
import rosegold.gumtuneclient.GumTuneClient;

public class ModUtils {
    public static void sendMessage(Object object) {
        if (object == null) {
            object = "null";
        }
        GumTuneClient.mc.thePlayer.addChatMessage(new ChatComponentText("§7[§5" + GumTuneClient.NAME + "§7] §f" + object));
    }
}
