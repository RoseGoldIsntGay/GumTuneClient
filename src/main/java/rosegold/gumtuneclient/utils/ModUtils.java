package rosegold.gumtuneclient.utils;

import net.minecraft.util.ChatComponentText;
import rosegold.gumtuneclient.GumTuneClient;

public class ModUtils {
    public static void sendMessage(Object object) {
        String message = "null";
        if (object != null) {
            message = object.toString().replace("&", "§");
        }
        GumTuneClient.mc.thePlayer.addChatMessage(new ChatComponentText("§7[§5" + GumTuneClient.NAME + "§7] §f" + message));
    }
}
