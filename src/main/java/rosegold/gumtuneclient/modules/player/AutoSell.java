package rosegold.gumtuneclient.modules.player;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import rosegold.gumtuneclient.GumTuneClient;
import rosegold.gumtuneclient.config.GumTuneClientConfig;
import rosegold.gumtuneclient.events.SecondEvent;

public class AutoSell {

    @SubscribeEvent
    public void onSecond(SecondEvent event) {
        if (GumTuneClient.mc.thePlayer == null) return;
        if (!GumTuneClientConfig.autoSell) return;
        if (GumTuneClientConfig.autoSellOpenTradesInventoryFull && isInventoryFull() && GumTuneClient.mc.currentScreen == null) {
            GumTuneClient.mc.thePlayer.sendChatMessage("/trades");
        }
    }

    private boolean isInventoryFull() {
        for (ItemStack itemStack : GumTuneClient.mc.thePlayer.inventory.mainInventory) {
            if (itemStack == null) {
                return false;
            }
        }
        return true;
    }
}
