package rosegold.gumtuneclient.modules.slayer;

import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityArmorStand;
import net.minecraft.entity.monster.EntityBlaze;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.entity.monster.EntitySpider;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.passive.EntityWolf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import rosegold.gumtuneclient.GumTuneClient;
import rosegold.gumtuneclient.events.RenderLivingEntityEvent;
import rosegold.gumtuneclient.utils.ScoreboardUtils;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import com.google.common.base.Predicate;
import rosegold.gumtuneclient.utils.StringUtils;

public class SlayerHandler {
    public static Entity currentSlayerBoss;
    public static EntityArmorStand currentSlayerBossBar;
    public static final HashSet<Entity> checked = new HashSet<>();
    private static final HashMap<String, Predicate<? super Entity>> bosses = new HashMap<String, Predicate<? super Entity>>() {{
        put("Revenant Horror", o -> o instanceof EntityZombie);
        put("Tarantula Broodfather", o -> o instanceof EntitySpider);
        put("Sven Packmaster", o -> o instanceof EntityWolf);
        put("Voidgloom Seraph", o -> o instanceof EntityEnderman);
        put("Inferno Demonlord", o -> o instanceof EntityBlaze);
    }};

    @SubscribeEvent
    public void onRenderEntityLiving(RenderLivingEntityEvent event) {
        if (GumTuneClient.mc.thePlayer == null) return;
        Entity entity = event.entity;
        if (checked.contains(entity)) return;
        List<String> scoreboard = ScoreboardUtils.getScoreboard();

        if (ScoreboardUtils.scoreboardContains("Slay the boss", scoreboard)) {
            if (entity instanceof EntityArmorStand && entity.hasCustomName()) {
                if (currentSlayerBoss != null && GumTuneClient.mc.theWorld.loadedEntityList.contains(currentSlayerBoss)) {
                    for (Map.Entry<String, Predicate<? super Entity>> boss : bosses.entrySet()) {
                        if (ScoreboardUtils.scoreboardContains(boss.getKey(), scoreboard) && entity.getCustomNameTag().contains(boss.getKey()) && entity.getDistanceToEntity(currentSlayerBoss) < 4) {
                            currentSlayerBossBar = (EntityArmorStand) entity;
                            return;
                        }
                    }
                }

                if (StringUtils.removeFormatting(entity.getCustomNameTag()).equals("Spawned by: " + GumTuneClient.mc.thePlayer.getName())) {
                    for (Map.Entry<String, Predicate<? super Entity>> boss : bosses.entrySet()) {
                        if (ScoreboardUtils.scoreboardContains(boss.getKey(), scoreboard)) {
                            List<Entity> possibleSlayerBosses = entity.getEntityWorld().getEntitiesInAABBexcluding(entity, entity.getEntityBoundingBox().expand(0, 2, 0), boss.getValue());
                            if (!possibleSlayerBosses.isEmpty()) {
                                currentSlayerBoss = possibleSlayerBosses.get(0);
                                checked.add(currentSlayerBoss);
                            }
                        }
                    }
                }
            }

            checked.add(entity);
        }
    }

    @SubscribeEvent
    public void onTick(TickEvent.ClientTickEvent event) {
        if (GumTuneClient.mc.thePlayer == null) return;
        if (GumTuneClient.mc.thePlayer.ticksExisted % 20 == 0) {
            checked.clear();
        }
    }

    @SubscribeEvent
    public void onWorldLoad(WorldEvent.Load event) {
        checked.clear();
    }

}
