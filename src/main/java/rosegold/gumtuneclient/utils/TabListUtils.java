package rosegold.gumtuneclient.utils;

import net.minecraft.client.network.NetworkPlayerInfo;
import rosegold.gumtuneclient.GumTuneClient;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class TabListUtils {

    public static boolean tabListContains(String string) {
        return tabListContains(string, getTabList());
    }

    public static boolean tabListContains(String string, List<String> tabList) {
        return tabList.stream().map(line -> StringUtils.removeFormatting(cleanSB(line))).anyMatch(line -> line.contains(string));
    }

    public static List<String> getTabList() {
        if (GumTuneClient.mc.thePlayer != null) {
            return GumTuneClient.mc.thePlayer.sendQueue.getPlayerInfoMap().stream()
                    .map(GumTuneClient.mc.ingameGUI.getTabList()::getPlayerName)
                    .collect(Collectors.toList());
        } else {
            return new ArrayList<>();
        }
    }

    public static String cleanSB(String scoreboard) {
        char[] nvString = StringUtils.removeFormatting(scoreboard).toCharArray();
        StringBuilder cleaned = new StringBuilder();

        for (char c : nvString) {
            if ((int) c > 20 && (int) c < 127) {
                cleaned.append(c);
            }
        }

        return cleaned.toString();
    }
}
