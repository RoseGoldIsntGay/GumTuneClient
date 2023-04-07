package rosegold.gumtuneclient.modules.farming;

import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import rosegold.gumtuneclient.GumTuneClient;
import rosegold.gumtuneclient.config.GumTuneClientConfig;
import rosegold.gumtuneclient.utils.ModUtils;

public class PreventRenderingCrops {

    private boolean lastState = GumTuneClientConfig.preventRenderingCrops;

    @SubscribeEvent
    public void onTick(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.START) return;
        if (lastState != GumTuneClientConfig.preventRenderingCrops) {
            ModUtils.sendMessage("Reloading Chunks");
            GumTuneClient.mc.renderGlobal.loadRenderers();
            lastState = GumTuneClientConfig.preventRenderingCrops;
        }
    }
}
