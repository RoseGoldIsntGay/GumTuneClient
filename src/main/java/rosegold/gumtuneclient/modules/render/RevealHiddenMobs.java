package rosegold.gumtuneclient.modules.render;

import net.minecraft.entity.Entity;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import rosegold.gumtuneclient.GumTuneClient;
import rosegold.gumtuneclient.config.GumTuneClientConfig;
import rosegold.gumtuneclient.config.pages.RevealHiddenMobsFilter;
import rosegold.gumtuneclient.events.RenderLivingEntityEvent;
import rosegold.gumtuneclient.utils.LocationUtils;

import java.util.HashSet;

public class RevealHiddenMobs {

    private static final HashSet<Entity> checked = new HashSet<>();

    @SubscribeEvent
    public void onRenderEntityLiving(RenderLivingEntityEvent event) {
        if (checked.contains(event.entity)) return;
        if (!GumTuneClientConfig.revealHiddenMobs) return;

        if (RevealHiddenMobsFilter.sneakyCreepers && event.entity instanceof EntityCreeper && LocationUtils.currentIsland == LocationUtils.Island.DEEP_CAVERNS) {
            event.entity.setInvisible(false);
        }

        checked.add(event.entity);
    }

    @SubscribeEvent
    public void onTick(TickEvent.ClientTickEvent event) {
        if (GumTuneClient.mc.thePlayer == null) return;
        if (!GumTuneClientConfig.revealHiddenMobs) return;
        if (GumTuneClient.mc.thePlayer.ticksExisted % 40 == 0) {
            checked.clear();
        }
    }

    @SubscribeEvent
    public void onWorldLoad(WorldEvent.Load event) {
        checked.clear();
    }
}
