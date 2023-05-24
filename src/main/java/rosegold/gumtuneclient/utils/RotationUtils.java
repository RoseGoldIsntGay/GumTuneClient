package rosegold.gumtuneclient.utils;

import net.minecraft.entity.Entity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import rosegold.gumtuneclient.events.PlayerMoveEvent;

import static rosegold.gumtuneclient.GumTuneClient.mc;

public class RotationUtils {

    public static Rotation startRot;
    public static Rotation endRot;
    private static long startTime;
    private static long endTime;

    private static float serverPitch;
    private static float serverYaw;
    public static float currentFakeYaw;
    public static float currentFakePitch;
    public static boolean done = true;
    private enum RotationType {
        NORMAL,
        SERVER
    }

    private static RotationType rotationType;

    public static class Rotation {
        public float pitch;
        public float yaw;

        public Rotation(float pitch, float yaw) {
            this.pitch = pitch;
            this.yaw = yaw;
        }

        public float getValue() {
            return Math.abs(this.yaw) + Math.abs(this.pitch);
        }

        public float getPitch() {
            return this.pitch;
        }

        public float getYaw() {
            return this.yaw;
        }

        public void setPitch(float pitch) {
            this.pitch = pitch;
        }

        @Override
        public String toString() {
            return "pitch=" + pitch +
                    ", yaw=" + yaw;
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

    public static Rotation getRotation(final Vec3 from, final Vec3 to) {
        double diffX = to.xCoord - from.xCoord;
        double diffY = to.yCoord - from.yCoord;
        double diffZ = to.zCoord - from.zCoord;
        double dist = Math.sqrt(diffX * diffX + diffZ * diffZ);

        float pitch = (float) -Math.atan2(dist, diffY);
        float yaw = (float) Math.atan2(diffZ, diffX);
        pitch = (float) wrapAngleTo180((pitch * 180F / Math.PI + 90) * -1);
        yaw = (float) wrapAngleTo180((yaw * 180 / Math.PI) - 90);

        return new Rotation(pitch, yaw);
    }

    public static Rotation getRotation(Vec3 vec3) {
        return getRotation(new Vec3(mc.thePlayer.posX, mc.thePlayer.posY + mc.thePlayer.getEyeHeight(), mc.thePlayer.posZ), vec3);
    }

    public static Rotation getRotation(BlockPos block) {
        return getRotation(new Vec3(block.getX() + 0.5, block.getY() + 0.5, block.getZ() + 0.5));
    }

    public static Rotation getRotation(Entity entity) {
        return getRotation(new Vec3(entity.posX, entity.posY + entity.getEyeHeight(), entity.posZ));
    }

    public static Rotation getRotation(Entity entity, Vec3 offset) {
        return getRotation(new Vec3(entity.posX + offset.xCoord, entity.posY + offset.yCoord, entity.posZ + offset.zCoord));
    }

    public static Rotation getNeededChange(Rotation startRot, Rotation endRot) {
        float yawDiff = wrapAngleTo180(endRot.yaw) - wrapAngleTo180(startRot.yaw);

        if (yawDiff <= -180) {
            yawDiff += 360;
        } else if (yawDiff > 180) {
            yawDiff -= 360;
        }

        return new Rotation(endRot.pitch - startRot.pitch, yawDiff);
    }

    public static Vec3 getVectorForRotation(final float pitch, final float yaw) {
        final float f2 = -MathHelper.cos(-pitch * 0.017453292f);
        return new Vec3(MathHelper.sin(-yaw * 0.017453292f - 3.1415927f) * f2, MathHelper.sin(-pitch * 0.017453292f), MathHelper.cos(-yaw * 0.017453292f - 3.1415927f) * f2);
    }

    public static Vec3 getLook(final Vec3 vec) {
        final double diffX = vec.xCoord - mc.thePlayer.posX;
        final double diffY = vec.yCoord - (mc.thePlayer.posY + mc.thePlayer.getEyeHeight());
        final double diffZ = vec.zCoord - mc.thePlayer.posZ;
        final double dist = MathHelper.sqrt_double(diffX * diffX + diffZ * diffZ);
        return getVectorForRotation((float)(-(MathHelper.atan2(diffY, dist) * 180.0 / 3.141592653589793)), (float)(MathHelper.atan2(diffZ, diffX) * 180.0 / 3.141592653589793 - 90.0));
    }

    public static Rotation getNeededChange(Rotation endRot) {
        return getNeededChange(new Rotation(mc.thePlayer.rotationPitch, mc.thePlayer.rotationYaw), endRot);
    }

    public static Rotation getServerNeededChange(Rotation endRotation) {
        return endRot == null ? getNeededChange(endRotation) : getNeededChange(endRot, endRotation);
    }

    private static float interpolate(float start, float end) {
        return (end - start) * easeOutCubic((float) (System.currentTimeMillis() - startTime) / (endTime - startTime)) + start;
    }

    public static float easeOutCubic(double number) {
        return (float) Math.max(0, Math.min(1, 1 - Math.pow(1 - number, 3)));
    }

    public static void smoothLookRelative(Rotation rotation, long time) {
        rotationType = RotationType.NORMAL;
        done = false;

        startRot = new Rotation(mc.thePlayer.rotationPitch, mc.thePlayer.rotationYaw);

        endRot = new Rotation(startRot.pitch + rotation.pitch, startRot.yaw + rotation.yaw);

        startTime = System.currentTimeMillis();
        endTime = System.currentTimeMillis() + time;
    }

    public static void smoothLook(Rotation rotation, long time) {
        rotationType = RotationType.NORMAL;
        done = false;
        startRot = new Rotation(mc.thePlayer.rotationPitch, mc.thePlayer.rotationYaw);

        Rotation neededChange = getNeededChange(startRot, rotation);

        endRot = new Rotation(startRot.pitch + neededChange.pitch, startRot.yaw + neededChange.yaw);

        startTime = System.currentTimeMillis();
        endTime = System.currentTimeMillis() + time;
    }

    public static void smartSmoothLook(Rotation rotation, int msPer180) {
        float rotationDifference = wrapAngleTo180(Math.max(
                Math.abs(rotation.pitch - mc.thePlayer.rotationPitch),
                Math.abs(rotation.yaw - mc.thePlayer.rotationYaw)
        ));
        smoothLook(rotation, (int) (rotationDifference / 180 * msPer180));
    }

    public static void serverSmoothLookRelative(Rotation rotation, long time) {
        rotationType = RotationType.SERVER;
        done = false;

        if (currentFakePitch == 0) currentFakePitch = mc.thePlayer.rotationPitch;
        if (currentFakeYaw == 0) currentFakeYaw = mc.thePlayer.rotationYaw;

        startRot = new Rotation(currentFakePitch, currentFakeYaw);

        endRot = new Rotation(startRot.pitch + rotation.pitch, startRot.yaw + rotation.yaw);

        startTime = System.currentTimeMillis();
        endTime = System.currentTimeMillis() + time;
    }

    public static void serverSmoothLook(Rotation rotation, long time) {
        rotationType = RotationType.SERVER;
        done = false;

        if (currentFakePitch == 0) currentFakePitch = mc.thePlayer.rotationPitch;
        if (currentFakeYaw == 0) currentFakeYaw = mc.thePlayer.rotationYaw;

        startRot = new Rotation(currentFakePitch, currentFakeYaw);

        Rotation neededChange = getNeededChange(startRot, rotation);

        endRot = new Rotation(startRot.pitch + neededChange.pitch, startRot.yaw + neededChange.yaw);

        startTime = System.currentTimeMillis();
        endTime = System.currentTimeMillis() + time;
    }

    public static void updateServerLookResetting() {
        if (System.currentTimeMillis() <= endTime) {
            mc.thePlayer.rotationYaw = interpolate(startRot.getYaw(), endRot.getYaw());
            mc.thePlayer.rotationPitch = interpolate(startRot.getPitch(), endRot.getPitch());

            currentFakeYaw = mc.thePlayer.rotationYaw;
            currentFakePitch = mc.thePlayer.rotationPitch;
        } else {
            if (!done) {
                mc.thePlayer.rotationYaw = endRot.getYaw();
                mc.thePlayer.rotationPitch = endRot.getPitch();

                currentFakeYaw = mc.thePlayer.rotationYaw;
                currentFakePitch = mc.thePlayer.rotationPitch;

                reset();
            }
        }
    }

    public static void updateServerLook() {
        if (System.currentTimeMillis() <= endTime) {
            mc.thePlayer.rotationYaw = interpolate(startRot.getYaw(), endRot.getYaw());
            mc.thePlayer.rotationPitch = interpolate(startRot.getPitch(), endRot.getPitch());

            currentFakeYaw = mc.thePlayer.rotationYaw;
            currentFakePitch = mc.thePlayer.rotationPitch;
        } else {
            if (!done) {
                mc.thePlayer.rotationYaw = endRot.getYaw();
                mc.thePlayer.rotationPitch = endRot.getPitch();

                currentFakeYaw = mc.thePlayer.rotationYaw;
                currentFakePitch = mc.thePlayer.rotationPitch;
            }
        }
    }

    public static void look(Rotation rotation) {
        mc.thePlayer.rotationPitch = rotation.pitch;
        mc.thePlayer.rotationYaw = rotation.yaw;
    }

    public static void reset() {
        done = true;
        startRot = null;
        endRot = null;
        startTime = 0;
        endTime = 0;
        currentFakeYaw = 0;
        currentFakePitch = 0;
    }

    @SubscribeEvent
    public void onRenderWorld(RenderWorldLastEvent event) {
        if (rotationType != RotationType.NORMAL) return;
        if (System.currentTimeMillis() <= endTime) {
            mc.thePlayer.rotationPitch = interpolate(startRot.pitch, endRot.pitch);
            mc.thePlayer.rotationYaw = interpolate(startRot.yaw, endRot.yaw);
        } else {
            if (!done) {
                mc.thePlayer.rotationYaw = endRot.yaw;
                mc.thePlayer.rotationPitch = endRot.pitch;

                reset();
            }
        }
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