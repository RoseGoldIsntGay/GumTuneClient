package rosegold.gumtuneclient.modules.player;

import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityArmorStand;
import net.minecraft.item.ItemSkull;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import rosegold.gumtuneclient.GumTuneClient;
import rosegold.gumtuneclient.config.GumTuneClientConfig;
import rosegold.gumtuneclient.events.PlayerMoveEvent;
import rosegold.gumtuneclient.events.RenderLivingEntityEvent;
import rosegold.gumtuneclient.utils.RotationUtils;

import java.util.HashSet;

public class FairySoulAura {

    public static final HashSet<Entity> checked = new HashSet<>();
    private static final HashSet<Entity> fairySouls = new HashSet<>();
    private static final String FAIRY_SOUL_TEXTURE = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYjk2OTIzYWQyNDczMTAwMDdmNmFlNWQzMjZkODQ3YWQ1Mzg2NGNmMTZjMzU2NWExODFkYzhlNmIyMGJlMjM4NyJ9fX0=";
    private static Entity fairySoul;
    private static int debounceTicks = 0;

    @SubscribeEvent
    public void onRenderEntityLiving(RenderLivingEntityEvent event) {
        if (!GumTuneClientConfig.fairySoulAura) return;
        if (checked.contains(event.entity)) return;
        if (event.entity instanceof EntityArmorStand) {
            if (GumTuneClientConfig.fairySoulESP && isFairySoul((EntityArmorStand) event.entity)) {
                fairySouls.add(event.entity);
            }

            checked.add(event.entity);
        }
    }

    @SubscribeEvent
    public void onTick(TickEvent.ClientTickEvent event) {
        if (!GumTuneClientConfig.fairySoulAura) return;
        if (GumTuneClient.mc.thePlayer == null) return;
        if (debounceTicks > 0) {
            debounceTicks--;
            return;
        }

        fairySoul = null;
        for (Entity entity : fairySouls) {
            if (GumTuneClient.mc.thePlayer.getPositionEyes(1f).distanceTo(entity.getPositionEyes(1f)) < 3.5) {
                fairySoul = entity;
                GumTuneClient.mc.getNetHandler().addToSendQueue(new C02PacketUseEntity(fairySoul, RotationUtils.getLook(fairySoul.getPositionEyes(1f))));
                debounceTicks = 20;
            }
        }

        if (GumTuneClient.mc.thePlayer.ticksExisted % 40 == 0) {
            checked.clear();
        }
    }

    @SubscribeEvent(priority = EventPriority.NORMAL)
    public void onUpdatePre(PlayerMoveEvent.Pre pre) {
        if (!GumTuneClientConfig.fairySoulAura) return;
        if (fairySoul != null) {
            RotationUtils.look(RotationUtils.getRotation(fairySoul));
        }
    }

    private boolean isFairySoul(EntityArmorStand entity) {
        ItemStack helmetItemStack = entity.getCurrentArmor(3);
        if (helmetItemStack != null && helmetItemStack.getItem() instanceof ItemSkull) {
            NBTTagList textures = helmetItemStack.serializeNBT().getCompoundTag("tag").getCompoundTag("SkullOwner").getCompoundTag("Properties").getTagList("textures", Constants.NBT.TAG_COMPOUND);
            for (int i = 0; i < textures.tagCount(); i++) {
                if (textures.getCompoundTagAt(i).getString("Value").equals(FAIRY_SOUL_TEXTURE)) {
                    return true;
                }
            }
        }

        return false;
    }
}
