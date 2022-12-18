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
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.lwjgl.input.Keyboard;
import rosegold.gumtuneclient.config.GumTuneClientConfig;
import rosegold.gumtuneclient.config.pages.MobMacroFilter;
import rosegold.gumtuneclient.utils.LocationUtils;
import rosegold.gumtuneclient.utils.ModUtils;
import rosegold.gumtuneclient.utils.RaytracingUtils;
import rosegold.gumtuneclient.utils.RotationUtils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

public class MobMacro {
    private final Minecraft mc = Minecraft.getMinecraft();
    private boolean enabled;
    private int ticks = 0;
    private boolean sneak = false;
    private boolean activeEye = false;
    private final List<Entity> ignoreEntities = new ArrayList<>();
    private int counter = 0;

    @SubscribeEvent
    public void onKey(InputEvent.KeyInputEvent event) {
        if (Keyboard.getEventKeyState() || !LocationUtils.onSkyblock) return;
        int eventKey = Keyboard.getEventKey();
        ArrayList<Integer> keyBinds = GumTuneClientConfig.mobMacroKeyBind.getKeyBinds();
        if (keyBinds.size() > 0 && keyBinds.get(0) == eventKey) {
            enabled = !enabled;
            ModUtils.sendMessage((enabled ? "Enabled" : "Disabled") + " Mob Macro");
            KeyBinding.setKeyBindState(mc.gameSettings.keyBindJump.getKeyCode(), false);
            KeyBinding.setKeyBindState(mc.gameSettings.keyBindSneak.getKeyCode(), false);
            KeyBinding.setKeyBindState(mc.gameSettings.keyBindForward.getKeyCode(), false);
            RotationUtils.resetServerLook();
            sneak = false;
            activeEye = false;
            ticks = 0;
            ignoreEntities.clear();
        }
    }

    @SubscribeEvent
    public void onTick(TickEvent.ClientTickEvent event) {
        if (!enabled || event.phase != TickEvent.Phase.START || !LocationUtils.onSkyblock || mc.thePlayer == null || mc.theWorld == null) return;
        if (sneak) {
            KeyBinding.setKeyBindState(mc.gameSettings.keyBindSneak.getKeyCode(), false);
            sneak = false;
        } else if (activeEye) {
            KeyBinding.setKeyBindState(mc.gameSettings.keyBindSneak.getKeyCode(), true);
            sneak = true;
            activeEye = false;
        }
        if (++ticks < GumTuneClientConfig.mobMacroDelay) return;
        ticks = 0;
        if (GumTuneClientConfig.mobMacroJump)
            KeyBinding.setKeyBindState(mc.gameSettings.keyBindJump.getKeyCode(), true);
        if (GumTuneClientConfig.mobMacroWalk)
            KeyBinding.setKeyBindState(mc.gameSettings.keyBindForward.getKeyCode(), true);
        if (++counter % 5 == 0) ignoreEntities.clear();

        Optional<Entity> optional = mc.theWorld.loadedEntityList.stream()
                .filter((entity) -> !entity.isDead && !ignoreEntities.contains(entity) && canKill(entity))
                .filter((entity) -> {
                    RotationUtils.Rotation rotation = RotationUtils.getRotationToEntity(entity);
                    MovingObjectPosition ray = RaytracingUtils.raytrace(rotation.yaw, rotation.pitch, 120);
                    return ray != null && ray.typeOfHit == MovingObjectPosition.MovingObjectType.ENTITY && ray.entityHit == entity;
                }).min(Comparator.comparingDouble((entity) -> entity.getDistanceToEntity(mc.thePlayer)));

        if (optional.isPresent()) {
            Entity mob = optional.get();
            switch (GumTuneClientConfig.mobMacroRotation) {
                case 0:
                    RotationUtils.look(RotationUtils.getRotationToEntity(mob));
                    break;
                case 1:
                    RotationUtils.serverLook(RotationUtils.getRotationToEntity(mob));
                    break;
                case 3:
                    RotationUtils.smoothLook(RotationUtils.getRotationToEntity(mob), 2, null);
                    break;
            }
            KeyBinding.setKeyBindState(mc.gameSettings.keyBindSneak.getKeyCode(), true);
            ignoreEntities.add(mob);
            sneak = true;
            activeEye = true;
        } else RotationUtils.resetServerLook();
    }

    private boolean canKill(Entity entity) {
        return (entity instanceof EntityWolf && MobMacroFilter.wolves) ||
                (entity instanceof EntityZombie && MobMacroFilter.zombies) ||
                (entity instanceof EntitySpider && MobMacroFilter.spiders) ||
                (entity instanceof EntityEnderman && MobMacroFilter.endermen);
    }

    @Subscribe
    public void onWorldLoad(WorldLoadEvent event) {
        enabled = false;
        sneak = false;
        activeEye = false;
        ticks = 0;
        ignoreEntities.clear();
        RotationUtils.resetServerLook();
    }
}
