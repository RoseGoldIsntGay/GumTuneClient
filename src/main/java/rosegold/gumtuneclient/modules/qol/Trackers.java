package rosegold.gumtuneclient.modules.qol;

import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import rosegold.gumtuneclient.config.GumTuneClientConfig;
import rosegold.gumtuneclient.utils.StringUtils;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Trackers {
    private final String POWDER_CHEST_UNLOCK = "You have successfully picked the lock on this chest!";

    private final Pattern POWDER_RECEIVED_REGEX = Pattern.compile("You received \\+(\\d+) (Gemstone|Mithril) Powder\\.");

    public static final HashMap<String, Long> powderChestStats = new HashMap<>();

    @SubscribeEvent(receiveCanceled = true)
    public void onChat(ClientChatReceivedEvent event) {
        if (!GumTuneClientConfig.trackers) return;
        String message = StringUtils.removeFormatting(event.message.getUnformattedText());

        if (GumTuneClientConfig.powderChestTracker) {
            if (message.equals(POWDER_CHEST_UNLOCK)) {
                updatePowderChestStats("Powder Chests", 1);
            }

            Matcher powderReceivedMatcher = POWDER_RECEIVED_REGEX.matcher(message);
            if (powderReceivedMatcher.find()) {
                updatePowderChestStats(powderReceivedMatcher.group(2) + " Powder", Long.parseLong(powderReceivedMatcher.group(1)));
            }
        }
    }

    @SubscribeEvent
    public void onWorldUnload(WorldEvent.Unload event) {
        powderChestStats.clear();
    }

    public static long getUptime() {
        return powderChestStats.containsKey("Timestamp") ? System.currentTimeMillis() - powderChestStats.get("Timestamp") : 0;
    }

    private void updatePowderChestStats(String key, long d) {
        if (powderChestStats.containsKey(key)) {
            powderChestStats.put(key, powderChestStats.get(key) + d);
        } else {
            powderChestStats.put("Timestamp", System.currentTimeMillis());
            powderChestStats.put(key, d);
        }
    }
}
