package rosegold.gumtuneclient.modules.macro;

import cc.polyfrost.oneconfig.events.event.WorldLoadEvent;
import cc.polyfrost.oneconfig.libs.eventbus.Subscribe;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.EntityWolf;
import net.minecraft.util.MovingObjectPosition;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.lwjgl.input.Keyboard;
import rosegold.gumtuneclient.config.GumTuneClientConfig;
import rosegold.gumtuneclient.utils.LocationUtils;
import rosegold.gumtuneclient.utils.ModUtils;
import rosegold.gumtuneclient.utils.RaytracingUtils;
import rosegold.gumtuneclient.utils.RotationUtils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Optional;

public class SvenMacro {
    private final Minecraft mc = Minecraft.getMinecraft();
    private boolean enabled;
    private int ticks = 0;
    private boolean sneak = false;

    @SubscribeEvent
    public void onKey(InputEvent.KeyInputEvent event) {
        if (Keyboard.getEventKeyState() || !LocationUtils.onSkyblock) return;
        int eventKey = Keyboard.getEventKey();
        ArrayList<Integer> keyBinds = GumTuneClientConfig.svenMacroKeyBind.getKeyBinds();
        if (keyBinds.size() > 0 && keyBinds.get(0) == eventKey) {
            enabled = !enabled;
            ModUtils.sendMessage((enabled ? "Enabled" : "Disabled") + " Sven Macro");
            KeyBinding.setKeyBindState(mc.gameSettings.keyBindJump.getKeyCode(), false);
            KeyBinding.setKeyBindState(mc.gameSettings.keyBindSneak.getKeyCode(), false);
            sneak = false;
            ticks = 0;
        }
    }

    @SubscribeEvent
    public void onTick(TickEvent.ClientTickEvent event) {
        if (!enabled || event.phase != TickEvent.Phase.START || !LocationUtils.onSkyblock || mc.thePlayer == null || mc.theWorld == null) return;
        if (sneak) {
            KeyBinding.setKeyBindState(mc.gameSettings.keyBindSneak.getKeyCode(), false);
            sneak = false;
        }
        if (++ticks < GumTuneClientConfig.svenMacroDelay) return;
        ticks = 0;
        KeyBinding.setKeyBindState(mc.gameSettings.keyBindJump.getKeyCode(), true);

        Optional<Entity> optional = mc.theWorld.loadedEntityList.stream()
                .filter((entity) -> entity instanceof EntityWolf && !entity.isDead)
                .filter((entity) -> {
                    RotationUtils.Rotation rotation = RotationUtils.getRotationToEntity(entity);
                    MovingObjectPosition ray = RaytracingUtils.raytrace(rotation.yaw, rotation.pitch, 120);
                    return ray != null && ray.typeOfHit == MovingObjectPosition.MovingObjectType.ENTITY && ray.entityHit == entity;
                }).min(Comparator.comparingDouble((entity) -> entity.getDistanceToEntity(mc.thePlayer)));

        if (optional.isPresent()) {
            Entity wolf = optional.get();
            RotationUtils.look(RotationUtils.getRotationToEntity(wolf));
            KeyBinding.setKeyBindState(mc.gameSettings.keyBindSneak.getKeyCode(), true);
            sneak = true;
        }
    }

    @Subscribe
    public void onWorldLoad(WorldLoadEvent event) {
        enabled = false;
        sneak = false;
        ticks = 0;
    }
}
