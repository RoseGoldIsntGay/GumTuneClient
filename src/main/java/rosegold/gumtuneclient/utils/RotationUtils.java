package rosegold.gumtuneclient.utils;

import net.minecraft.entity.Entity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.Vec3;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import rosegold.gumtuneclient.events.PlayerMoveEvent;

import static rosegold.gumtuneclient.GumTuneClient.mc;

public class RotationUtils {

    private static float pitchDifference;
    public static float yawDifference;
    private static int ticks = -1;
    private static int tickCounter = 0;
    private static Runnable callback = null;

    private static float serverPitch;
    private static float serverYaw;

    public static class Rotation {
        public float pitch;
        public float yaw;

        public Rotation(float pitch, float yaw) {
            this.pitch = pitch;
            this.yaw = yaw;
        }
    }

    private static double wrapAngleTo180(double angle) {
        return angle - Math.floor(angle / 360 + 0.5) * 360;
    }

    private static float wrapAngleTo180(float angle) {
        return (float) (angle - Math.floor(angle / 360 + 0.5) * 360);
    }

    public static Rotation getRotationToVec(Vec3 vec3) {
        double diffX = vec3.xCoord - mc.thePlayer.posX;
        double diffY = vec3.yCoord - mc.thePlayer.posY - mc.thePlayer.getEyeHeight();
        double diffZ = vec3.zCoord - mc.thePlayer.posZ;
        double dist = Math.sqrt(diffX * diffX + diffZ * diffZ);

        float pitch = (float) -Math.atan2(dist, diffY);
        float yaw = (float) Math.atan2(diffZ, diffX);
        pitch = (float) wrapAngleTo180((pitch * 180F / Math.PI + 90)*-1);
        yaw = (float) wrapAngleTo180((yaw * 180 / Math.PI) - 90);

        return new Rotation(pitch, yaw);
    }

    public static Rotation getRotationToBlock(BlockPos block) {
        return getRotationToVec(new Vec3(block.getX() + 0.5, block.getY() + 0.5, block.getZ() + 0.5));
    }

    public static Rotation getRotationToEntity(Entity entity) {
        return getRotationToVec(new Vec3(entity.posX, entity.posY + entity.getEyeHeight(), entity.posZ));
    }

    public static void serverLook(Rotation rotation, Runnable callback) {
        look(rotation);
        callback.run();
    }

    public static void smoothLook(Rotation rotation, int ticks, Runnable callback) {
        if(ticks == 0) {
            look(rotation);
            callback.run();
            return;
        }

        RotationUtils.callback = callback;

        pitchDifference = wrapAngleTo180(rotation.pitch - mc.thePlayer.rotationPitch);
        yawDifference = wrapAngleTo180(rotation.yaw - mc.thePlayer.rotationYaw);

        RotationUtils.ticks = ticks * 20;
        RotationUtils.tickCounter = 0;
    }

    public static void look(Rotation rotation) {
        mc.thePlayer.rotationPitch = rotation.pitch;
        mc.thePlayer.rotationYaw = rotation.yaw;
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onUpdatePre(PlayerMoveEvent.Pre pre) {
        serverPitch = mc.thePlayer.rotationPitch;
        serverYaw = mc.thePlayer.rotationYaw;
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onUpdatePost(PlayerMoveEvent.Post post) {
        mc.thePlayer.rotationPitch = serverPitch;
        mc.thePlayer.rotationYaw = serverYaw;
    }
}
