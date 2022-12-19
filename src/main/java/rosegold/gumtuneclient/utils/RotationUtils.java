package rosegold.gumtuneclient.utils;

import net.minecraft.entity.Entity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.Vec3;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import rosegold.gumtuneclient.events.PlayerMoveEvent;

import static rosegold.gumtuneclient.GumTuneClient.mc;

public class RotationUtils {

    private static Rotation startSmoothRotation;
    private static Rotation targetSmoothRotation;
    private static long startTime = -1;
    private static long endTime = -1;
    private static Runnable callback = null;
    public static boolean smoothDone = true;

    private static float currentPitch;
    private static float currentYaw;
    private static Rotation targetServerRotation;

    public static class Rotation {
        public float pitch;
        public float yaw;

        public Rotation(float pitch, float yaw) {
            this.pitch = pitch;
            this.yaw = yaw;
        }
    }

    public static double wrapAngleTo180(double angle) {
        return angle - Math.floor(angle / 360 + 0.5) * 360;
    }

    public static float wrapAngleTo180(float angle) {
        return (float) (angle - Math.floor(angle / 360 + 0.5) * 360);
    }

    public static float fovToVec3(Vec3 vec) {
        double x = vec.xCoord - mc.thePlayer.posX;
        double z = vec.zCoord - mc.thePlayer.posZ;
        double yaw = Math.atan2(x, z) * 57.2957795;
        return (float) (yaw * -1.0);
    }

    public static Rotation getRotationToVec(Vec3 vec3) {
        double diffX = vec3.xCoord - mc.thePlayer.posX;
        double diffY = vec3.yCoord - mc.thePlayer.posY - mc.thePlayer.getEyeHeight();
        double diffZ = vec3.zCoord - mc.thePlayer.posZ;
        double dist = Math.sqrt(diffX * diffX + diffZ * diffZ);

        float pitch = (float) -Math.atan2(dist, diffY);
        float yaw = (float) Math.atan2(diffZ, diffX);
        pitch = (float) wrapAngleTo180((pitch * 180F / Math.PI + 90) * -1);
        yaw = (float) wrapAngleTo180((yaw * 180 / Math.PI) - 90);

        return new Rotation(pitch, yaw);
    }

    public static Rotation getRotationToBlock(BlockPos block) {
        return getRotationToVec(new Vec3(block.getX() + 0.5, block.getY() + 0.5, block.getZ() + 0.5));
    }

    public static Rotation getRotationToEntity(Entity entity) {
        return getRotationToVec(new Vec3(entity.posX, entity.posY + entity.getEyeHeight(), entity.posZ));
    }

    public static void serverLook(Rotation rotation) {
        targetServerRotation = rotation;
    }

    public static void resetServerLook() {
        targetServerRotation = null;
    }

    public static void smoothLook(Rotation rotation, long time, Runnable callback) {
        if (time == 0) {
            look(rotation);
            if (callback != null) callback.run();
            return;
        }

        RotationUtils.callback = callback;
        startSmoothRotation = new Rotation(mc.thePlayer.rotationPitch, mc.thePlayer.rotationYaw);
        float pitchDiff = wrapAngleTo180(rotation.pitch - startSmoothRotation.pitch);
        float yawDiff = wrapAngleTo180(rotation.yaw - startSmoothRotation.yaw);
        targetSmoothRotation = new Rotation(startSmoothRotation.pitch + pitchDiff, startSmoothRotation.yaw + yawDiff);
        startTime = System.currentTimeMillis();
        endTime = startTime + time;
        smoothDone = false;
    }

    public static void cancelSmoothLook() {
        smoothDone = true;
        endTime = -1;
        startTime = -1;
        startSmoothRotation = null;
        targetSmoothRotation = null;
        callback = null;
    }

    public static void look(Rotation rotation) {
        mc.thePlayer.rotationPitch = rotation.pitch;
        mc.thePlayer.rotationYaw = rotation.yaw;
    }

    @SubscribeEvent
    public void onRender(RenderWorldLastEvent event) {
        long currentTime = System.currentTimeMillis();
        if (!smoothDone && currentTime < endTime) {
            mc.thePlayer.rotationPitch = interpolate(startSmoothRotation.pitch, targetSmoothRotation.pitch);
            mc.thePlayer.rotationYaw = interpolate(startSmoothRotation.yaw, targetSmoothRotation.yaw);
        } else if (!smoothDone) {
            mc.thePlayer.rotationPitch = targetSmoothRotation.pitch;
            mc.thePlayer.rotationYaw = targetSmoothRotation.yaw;
            smoothDone = true;
            if (callback != null) {
                callback.run();
                callback = null;
            }
        }
    }

    private float interpolate(float start, float target) {
        float progress = (float) (System.currentTimeMillis() - startTime) / (float) (endTime - startTime);
        return start + (target - start) * easeOutQuint(progress);
    }

    private float easeOutQuint(float number) {
        return  1 - (float) Math.pow(1 - number, 5);
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onUpdatePre(PlayerMoveEvent.Pre pre) {
        currentPitch = mc.thePlayer.rotationPitch;
        currentYaw = mc.thePlayer.rotationYaw;
        if (targetServerRotation == null) return;
        mc.thePlayer.rotationPitch = targetServerRotation.pitch;
        mc.thePlayer.rotationYaw = targetServerRotation.yaw;
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onUpdatePost(PlayerMoveEvent.Post post) {
        if (targetServerRotation == null) return;
        mc.thePlayer.rotationPitch = currentPitch;
        mc.thePlayer.rotationYaw = currentYaw;
    }
}
