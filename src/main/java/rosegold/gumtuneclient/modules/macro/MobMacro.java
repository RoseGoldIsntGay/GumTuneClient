package rosegold.gumtuneclient.modules.macro;

import cc.polyfrost.oneconfig.events.event.WorldLoadEvent;
import cc.polyfrost.oneconfig.libs.eventbus.Subscribe;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.Entity;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.entity.monster.EntitySpider;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.passive.EntityWolf;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.lwjgl.input.Keyboard;
import rosegold.gumtuneclient.GumTuneClient;
import rosegold.gumtuneclient.config.GumTuneClientConfig;
import rosegold.gumtuneclient.config.pages.MobMacroFilter;
import rosegold.gumtuneclient.events.PlayerMoveEvent;
import rosegold.gumtuneclient.utils.LocationUtils;
import rosegold.gumtuneclient.utils.ModUtils;
import rosegold.gumtuneclient.utils.RaytracingUtils;
import rosegold.gumtuneclient.utils.RotationUtils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

public class MobMacro {
    private static final Minecraft mc = Minecraft.getMinecraft();
    public static boolean enabled;
    private int ticks = 0;
    private boolean sneak = false;
    private boolean activeEye = false;
    private final List<Entity> ignoreEntities = new ArrayList<>();
    private int counter = 0;
    private Entity lookAt;

    @SubscribeEvent
    public void onKey(InputEvent.KeyInputEvent event) {
        if (Keyboard.getEventKeyState() || !LocationUtils.onSkyblock || !GumTuneClientConfig.mobMacro) return;
        int eventKey = Keyboard.getEventKey();
        ArrayList<Integer> keyBinds = GumTuneClientConfig.mobMacroKeyBind.getKeyBinds();
        if (keyBinds.size() > 0 && keyBinds.get(0) == eventKey) {
            enabled = !enabled;
            ModUtils.sendMessage((enabled ? "Enabled" : "Disabled") + " Mob Macro");
            KeyBinding.setKeyBindState(mc.gameSettings.keyBindJump.getKeyCode(), false);
            KeyBinding.setKeyBindState(mc.gameSettings.keyBindSneak.getKeyCode(), false);
            KeyBinding.setKeyBindState(mc.gameSettings.keyBindForward.getKeyCode(), false);
            sneak = false;
            activeEye = false;
            ticks = 0;
            ignoreEntities.clear();
        }
    }

    @SubscribeEvent
    public void onTick(TickEvent.ClientTickEvent event) {
        if (!isEnabled()) return;
        ticks++;

        if (mc.currentScreen != null) {
            KeyBinding.setKeyBindState(mc.gameSettings.keyBindSneak.getKeyCode(), false);
            KeyBinding.setKeyBindState(mc.gameSettings.keyBindJump.getKeyCode(), false);
            KeyBinding.setKeyBindState(mc.gameSettings.keyBindForward.getKeyCode(), false);
            return;
        }

        if (sneak) {
            KeyBinding.setKeyBindState(mc.gameSettings.keyBindSneak.getKeyCode(), false);
            sneak = false;
        } else if (activeEye && (RotationUtils.done || GumTuneClientConfig.mobMacroRotation != 2)) {
            KeyBinding.setKeyBindState(mc.gameSettings.keyBindSneak.getKeyCode(), true);
            sneak = true;
            activeEye = false;
        }

        if (ticks < GumTuneClientConfig.mobMacroDelay) return;
        ticks = 0;

        if (GumTuneClientConfig.mobMacroJump)
            KeyBinding.setKeyBindState(mc.gameSettings.keyBindJump.getKeyCode(), true);
        if (GumTuneClientConfig.mobMacroWalk)
            KeyBinding.setKeyBindState(mc.gameSettings.keyBindForward.getKeyCode(), true);
        if (++counter % 5 == 0) ignoreEntities.clear();

        Entity entity = getEntity();
        if (entity != null) {
            switch (GumTuneClientConfig.mobMacroRotation) {
                case 0:
                    RotationUtils.look(RotationUtils.getRotation(entity));
                    break;
                case 1:
                    lookAt = entity;
                    break;
                case 2:
                    RotationUtils.smoothLook(RotationUtils.getRotation(entity), 200L);
                    break;
            }
            KeyBinding.setKeyBindState(mc.gameSettings.keyBindSneak.getKeyCode(), true);
            ignoreEntities.add(entity);
            sneak = true;
            activeEye = true;
        }
    }

    public static boolean isEnabled() {
        return GumTuneClientConfig.mobMacro && LocationUtils.onSkyblock && mc.theWorld != null && mc.thePlayer != null && enabled;
    }

    private Entity getEntity() {
        Optional<Entity> optional = mc.theWorld.loadedEntityList.stream()
                .filter(entity -> !entity.isDead && !ignoreEntities.contains(entity) && canKill(entity))
                .filter(entity -> {
                    RotationUtils.Rotation rotation = RotationUtils.getRotation(entity);
                    MovingObjectPosition ray = RaytracingUtils.raytrace(rotation.yaw, rotation.pitch, 120);
                    return ray != null && ray.typeOfHit == MovingObjectPosition.MovingObjectType.ENTITY && ray.entityHit == entity;
                }).min(Comparator.comparingDouble(entity -> entity.getDistanceToEntity(mc.thePlayer)));

        return optional.orElse(null);
    }

    private boolean canKill(Entity entity) {
        return (entity instanceof EntityWolf && MobMacroFilter.wolves) ||
                (entity instanceof EntityZombie && MobMacroFilter.zombies) ||
                (entity instanceof EntitySpider && MobMacroFilter.spiders) ||
                (entity instanceof EntityEnderman && MobMacroFilter.endermen);
    }

    @SubscribeEvent(priority = EventPriority.NORMAL)
    public void onUpdatePre(PlayerMoveEvent.Pre pre) {
        if (!isEnabled()) return;
        if (lookAt != null) {
            RotationUtils.look(RotationUtils.getRotation(lookAt));
        }
    }

    @SubscribeEvent
    public void onWorldUnload(WorldEvent.Unload event) {
        enabled = false;
        sneak = false;
        activeEye = false;
        ticks = 0;
        ignoreEntities.clear();
    }
}
