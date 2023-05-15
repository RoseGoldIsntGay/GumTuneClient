package rosegold.gumtuneclient.modules.combat;

import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityArmorStand;
import net.minecraft.util.Vec3;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import rosegold.gumtuneclient.GumTuneClient;
import rosegold.gumtuneclient.config.GumTuneClientConfig;
import rosegold.gumtuneclient.events.PlayerMoveEvent;
import rosegold.gumtuneclient.events.RenderLivingEntityEvent;
import rosegold.gumtuneclient.utils.LocationUtils;
import rosegold.gumtuneclient.utils.ModUtils;
import rosegold.gumtuneclient.utils.RotationUtils;
import rosegold.gumtuneclient.utils.StringUtils;

import java.util.Arrays;
import java.util.HashSet;

public class AntiShy {
    public static final HashSet<Entity> checked = new HashSet<>();
    public static final HashSet<Entity> shys = new HashSet<>();
    private static final String[] shyMessages = {
            "Eek!",
            "I'm ugly! :(",
            "Don't look at me!",
            "Look away!"
    };
    public static Entity closestLookAway;

    @SubscribeEvent
    public void onRenderEntityLiving(RenderLivingEntityEvent event) {
        if (checked.contains(event.entity)) return;
        if (!GumTuneClientConfig.antiShy) return;
        if (LocationUtils.currentIsland != LocationUtils.Island.THE_RIFT) return;
        if (event.entity instanceof EntityArmorStand) {
            if (event.entity.hasCustomName() && Arrays.stream(shyMessages).anyMatch(s -> StringUtils.removeFormatting(event.entity.getCustomNameTag()).equals(s)) && event.entity.getDistanceToEntity(GumTuneClient.mc.thePlayer) < 8) {
                shys.add(event.entity);
            }
        }
    }

    @SubscribeEvent
    public void onTick(TickEvent.ClientTickEvent event) {
        if (GumTuneClient.mc.thePlayer == null) return;
        if (!GumTuneClientConfig.antiShy) return;
        if (LocationUtils.currentIsland != LocationUtils.Island.THE_RIFT) return;
        if (GumTuneClient.mc.thePlayer.ticksExisted % 10 == 0) {
            checked.clear();
            if (!new HashSet<>(GumTuneClient.mc.theWorld.loadedEntityList).containsAll(shys)) {
                shys.clear();
            }
        }

        closestLookAway = null;
        closestLookAway = shys.stream().min((o1, o2) -> (int) (o1.getDistanceToEntity(GumTuneClient.mc.thePlayer) - o2.getDistanceToEntity(GumTuneClient.mc.thePlayer))).orElse(null);
    }

    @SubscribeEvent(priority = EventPriority.NORMAL)
    public void onUpdatePre(PlayerMoveEvent.Pre pre) {
        if (!GumTuneClientConfig.antiShy) return;
        if (closestLookAway != null) {
            RotationUtils.look(RotationUtils.getRotation(new Vec3(closestLookAway.posX - (closestLookAway.posX - GumTuneClient.mc.thePlayer.posX) * 2, closestLookAway.posY, closestLookAway.posZ - (closestLookAway.posZ - GumTuneClient.mc.thePlayer.posZ) * 2)));
        }
    }

    @SubscribeEvent
    public void onWorldLoad(WorldEvent.Load event) {
        checked.clear();
        shys.clear();
    }
}
