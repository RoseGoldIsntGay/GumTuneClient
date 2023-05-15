package rosegold.gumtuneclient.modules.macro;

import cc.polyfrost.oneconfig.events.event.WorldLoadEvent;
import cc.polyfrost.oneconfig.libs.eventbus.Subscribe;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.*;
import net.minecraft.entity.passive.EntityWolf;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;
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
import rosegold.gumtuneclient.utils.*;
import rosegold.gumtuneclient.utils.objects.TimedSet;

import java.awt.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

public class MobMacro {
    private static final Minecraft mc = Minecraft.getMinecraft();
    public static boolean enabled;
    private int ticks = 0;
    private boolean sneak = false;
    private boolean activeEye = false;
    private final TimedSet<Entity> ignoreEntities = new TimedSet<>(2, TimeUnit.SECONDS, true);
    private int stuckTicks = 0;
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
    public void onRenderOverlay(RenderGameOverlayEvent.Post event) {
        if (!isEnabled()) return;
        if (event.type == RenderGameOverlayEvent.ElementType.ALL) {
            FontUtils.drawScaledString("Stuck ticks: " + stuckTicks, 1, 200, 100, true);
        }
    }

    @SubscribeEvent
    public void onRenderWorld(RenderWorldLastEvent event) {
        if (!isEnabled()) return;
        if (lookAt != null) {
            RenderUtils.renderBoundingBox(lookAt, event.partialTicks, Color.RED.getRGB());
            if (mc.thePlayer.getDistanceToEntity(lookAt) > 1) {
                switch (GumTuneClientConfig.mobMacroRotation) {
                    case 0:
                        RotationUtils.look(RotationUtils.getRotation(lookAt));
                        break;
                    case 2:
                        if (lookAt.posY > mc.thePlayer.posY) {
                            RotationUtils.smoothLook(RotationUtils.getRotation(lookAt, new Vec3(0, 0.1, 0)), 200);
                        } else {
                            RotationUtils.smoothLook(RotationUtils.getRotation(lookAt, new Vec3(0, mc.thePlayer.getEyeHeight(), 0)), 200);
                        }
                        break;
                }
            }
        }

        for (Entity entity : ignoreEntities) {
            if (entity != null) {
                RenderUtils.renderBoundingBox(entity, event.partialTicks, Color.BLUE.getRGB());
            }
        }
    }

    @SubscribeEvent
    public void onTick(TickEvent.ClientTickEvent event) {
        if (!isEnabled()) return;
        Entity previous = lookAt;
        ticks++;

        if (mc.currentScreen != null) {
            KeyBinding.setKeyBindState(mc.gameSettings.keyBindSneak.getKeyCode(), false);
            KeyBinding.setKeyBindState(mc.gameSettings.keyBindJump.getKeyCode(), false);
            KeyBinding.setKeyBindState(mc.gameSettings.keyBindForward.getKeyCode(), false);
            return;
        }

        if (sneak) {
            if (GumTuneClientConfig.mobMacroAttackType == 0) {
                KeyBinding.setKeyBindState(mc.gameSettings.keyBindSneak.getKeyCode(), false);
                sneak = false;
            }
        } else if (activeEye && RotationUtils.done) {
            if (GumTuneClientConfig.mobMacroAttackType == 0) {
                KeyBinding.setKeyBindState(mc.gameSettings.keyBindSneak.getKeyCode(), true);
                sneak = true;
                activeEye = false;
            }
        }

        if (!GumTuneClientConfig.mobMacroEntityLock || releaseLock(lookAt)) {
            lookAt = getEntity();
        }

        if (previous == lookAt) {
            stuckTicks++;
        } else {
            stuckTicks = 0;
        }

        if (ticks < GumTuneClientConfig.mobMacroDelay) return;
        ticks = 0;

        if (GumTuneClientConfig.mobMacroJump)
            KeyBinding.setKeyBindState(mc.gameSettings.keyBindJump.getKeyCode(), true);
        if (GumTuneClientConfig.mobMacroWalk) {
            KeyBinding.setKeyBindState(mc.gameSettings.keyBindForward.getKeyCode(), lookAt != null);
            if (GumTuneClientConfig.mobMacroSmartSprint && lookAt != null) {
                KeyBinding.setKeyBindState(mc.gameSettings.keyBindSprint.getKeyCode(), mc.thePlayer.getDistanceToEntity(lookAt) > 12);
            }
        }

        if (stuckTicks > 100 && lookAt != null) {
            ignoreEntities.put(lookAt);
            return;
        }

        if (lookAt != null) {
            switch (GumTuneClientConfig.mobMacroAttackType) {
                case 0:
                    KeyBinding.setKeyBindState(mc.gameSettings.keyBindSneak.getKeyCode(), true);
                    ignoreEntities.put(lookAt);
                    break;
                case 1:
                    PlayerUtils.rightClick();
                    ignoreEntities.put(lookAt);
                    break;
                case 2:
                    if (mc.objectMouseOver.typeOfHit == MovingObjectPosition.MovingObjectType.ENTITY && mc.objectMouseOver.entityHit.equals(lookAt)) {
                        PlayerUtils.leftClick();
                        ignoreEntities.put(lookAt);
                    } else if (mc.thePlayer.getDistanceToEntity(lookAt) < 1.5) {
                        ignoreEntities.put(lookAt);
                    }
                    break;
            }

            sneak = true;
            activeEye = true;
        }
    }

    public static boolean isEnabled() {
        return GumTuneClientConfig.mobMacro && LocationUtils.onSkyblock && mc.theWorld != null && mc.thePlayer != null && enabled;
    }

    private Entity getEntity() {
        Optional<Entity> optional = mc.theWorld.loadedEntityList.stream()
                .filter(entity -> {
                    if (LocationUtils.currentIsland == LocationUtils.Island.DWARVEN_MINES && MobMacroFilter.creepers) {
                        return entity.posX > 110;
                    }
                    return true;
                })
                .filter(entity -> !entity.isDead && !ignoreEntities.contains(entity) && canKill(entity))
                .filter(entity -> ((EntityLivingBase) entity).getHealth() > 0 && ((EntityLivingBase) entity).getAge() > 20)
                .filter(entity -> {
                    if (GumTuneClientConfig.mobMacroAttackType == 2 && Math.abs(entity.posY - mc.thePlayer.posY) > 3) {
                        return false;
                    }
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
                (entity instanceof EntityEnderman && MobMacroFilter.endermen) ||
                (entity instanceof EntitySlime && MobMacroFilter.slime) ||
                (entity instanceof EntityMagmaCube && MobMacroFilter.magmaCubes) ||
                (entity instanceof EntityCreeper && MobMacroFilter.creepers);
    }

    private boolean releaseLock(Entity entity) {
        if (entity == null) return true;
        if (entity.isDead) return true;
        if (((EntityLivingBase) entity).getHealth() < 0) return true;
        if (ignoreEntities.contains(entity)) return true;
        RotationUtils.Rotation rotation = RotationUtils.getRotation(entity);
        MovingObjectPosition ray = RaytracingUtils.raytrace(rotation.yaw, rotation.pitch, 120);
        return ray == null || ray.typeOfHit != MovingObjectPosition.MovingObjectType.ENTITY || !ray.entityHit.equals(entity);
    }

    @SubscribeEvent(priority = EventPriority.NORMAL)
    public void onUpdatePre(PlayerMoveEvent.Pre pre) {
        if (!isEnabled()) return;
        if (lookAt != null && GumTuneClientConfig.mobMacroRotation == 1) {
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
