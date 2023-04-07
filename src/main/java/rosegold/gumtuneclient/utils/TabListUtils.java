package rosegold.gumtuneclient.utils;

import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.util.StringUtils;
import rosegold.gumtuneclient.GumTuneClient;

import java.util.ArrayList;
import java.util.List;

public class TabListUtils {

    public static boolean tabListContains(String string) {
        for (String line : getTabList()) {
            if (removeFormatting(cleanSB(line)).contains(string)) {
                return true;
            }
        }
        return false;
    }

    public static boolean tabListContains(String string, List<String> tabList) {
        for (String line : tabList) {
            if (removeFormatting(cleanSB(line)).contains(string)) {
                return true;
            }
        }
        return false;
    }

    public static List<String> getTabList() {
        ArrayList<String> entries = new ArrayList<>();

        if (GumTuneClient.mc.thePlayer != null) {
            for (NetworkPlayerInfo networkPlayerInfo : GumTuneClient.mc.thePlayer.sendQueue.getPlayerInfoMap()) {
                entries.add(GumTuneClient.mc.ingameGUI.getTabList().getPlayerName(networkPlayerInfo));
            }
        }

        return entries;
    }

    public static String removeFormatting(String input) {
        return input.replaceAll("§[0-9a-fk-or]", "");
    }

    public static String cleanSB(String scoreboard) {
        char[] nvString = StringUtils.stripControlCodes(scoreboard).toCharArray();
        StringBuilder cleaned = new StringBuilder();

        for (char c : nvString) {
            if ((int) c > 20 && (int) c < 127) {
                cleaned.append(c);
            }
        }

        return cleaned.toString();
    }
}
