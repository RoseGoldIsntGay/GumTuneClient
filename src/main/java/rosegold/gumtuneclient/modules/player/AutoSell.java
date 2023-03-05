package rosegold.gumtuneclient.modules.player;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import rosegold.gumtuneclient.GumTuneClient;
import rosegold.gumtuneclient.config.GumTuneClientConfig;
import rosegold.gumtuneclient.events.SecondEvent;
import rosegold.gumtuneclient.utils.LocationUtils;

public class AutoSell {

    @SubscribeEvent
    public void onSecond(SecondEvent event) {
        if (GumTuneClient.mc.thePlayer == null) return;
        if (!GumTuneClientConfig.autoSell) return;
        if (!LocationUtils.onSkyblock) return;
        if ((getFilledSlotCount() / 36f) * 100 >= GumTuneClientConfig.autoSellOpenTradesInventoryFull && GumTuneClient.mc.currentScreen == null) {
            GumTuneClient.mc.thePlayer.sendChatMessage("/trades");
        }
    }

    private int getFilledSlotCount() {
        int count = 0;
        for (ItemStack itemStack : GumTuneClient.mc.thePlayer.inventory.mainInventory) {
            if (itemStack != null) {
                count++;
            }
        }
        return count;
    }
}
