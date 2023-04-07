package rosegold.gumtuneclient.modules.slayer;

import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import rosegold.gumtuneclient.GumTuneClient;
import rosegold.gumtuneclient.config.GumTuneClientConfig;
import rosegold.gumtuneclient.utils.RenderUtils;

import java.awt.*;

public class HighlightSlayerBoss {
    @SubscribeEvent
    public void onRenderWorld(RenderWorldLastEvent event) {
        if (!GumTuneClientConfig.highlightSlayerBoss) return;
        if (SlayerHandler.currentSlayerBoss != null) {
            GumTuneClient.mc.theWorld.loadedEntityList.forEach(entity -> {
                if (entity.equals(SlayerHandler.currentSlayerBoss)) {
                    RenderUtils.renderBoundingBox(entity, event.partialTicks, Color.BLUE.getRGB());
                }
            });
        }
    }

//    @SubscribeEvent
//    public void onOverlayRender(RenderGameOverlayEvent.Post event) {
//        if (!GumTuneClientConfig.highlightSlayerBoss) return;
//        if (event.type == RenderGameOverlayEvent.ElementType.ALL) {
//            if (SlayerHandler.currentSlayerBossBar != null && GumTuneClient.mc.theWorld.loadedEntityList.contains(SlayerHandler.currentSlayerBossBar) && SlayerHandler.currentSlayerBossBar.hasCustomName()) {
//                FontUtils.drawScaledString(SlayerHandler.currentSlayerBossBar.getCustomNameTag(), 1, 300, 100, true);
//            }
//        }
//    }
}
