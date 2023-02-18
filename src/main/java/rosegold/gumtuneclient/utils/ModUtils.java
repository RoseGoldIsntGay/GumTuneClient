package rosegold.gumtuneclient.utils;

import net.minecraft.util.ChatComponentText;
import rosegold.gumtuneclient.GumTuneClient;

public class ModUtils {
    public static void sendMessage(Object object) {
        String message = "null";
        if (object != null) {
            message = object.toString().replace("&", "§");
        }
        if (GumTuneClient.mc.thePlayer != null) {
            GumTuneClient.mc.thePlayer.addChatMessage(new ChatComponentText("§7[§d" + GumTuneClient.NAME + "§7] §f" + message));
        }
    }
}
