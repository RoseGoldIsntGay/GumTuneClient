package rosegold.gumtuneclient.utils;

import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.util.StringUtils;
import rosegold.gumtuneclient.GumTuneClient;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class TabListUtils {

    public static boolean tabListContains(String string) {
        return tabListContains(string, getTabList());
    }

    // why??
    public static boolean tabListContains(String string, List<String> tabList) {
        return tabList.stream().map(line -> removeFormatting(cleanSB(line))).anyMatch(line -> line.contains(string));
    }

    public static List<String> getTabList() {
        if (GumTuneClient.mc.thePlayer != null) {
            return GumTuneClient.mc.thePlayer.sendQueue.getPlayerInfoMap().stream()
                    .map(GumTuneClient.mc.ingameGUI.getTabList()::getPlayerName)
                    .collect(Collectors.toList());
        } else {
            return new ArrayList<String>();
        }

    }

    // TODO: This should be used in a lot of places. I saw this function a couple of times already!
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
