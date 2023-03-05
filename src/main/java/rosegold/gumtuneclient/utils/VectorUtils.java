package rosegold.gumtuneclient.utils;

import net.minecraft.client.settings.KeyBinding;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import net.minecraft.util.Vec3i;
import rosegold.gumtuneclient.GumTuneClient;

import java.util.ArrayList;
import java.util.HashMap;

public class VectorUtils {

    private static final HashMap<Integer, KeyBinding> keyBindMap = new HashMap<Integer, KeyBinding>() {
        {
            put(0, GumTuneClient.mc.gameSettings.keyBindForward);
            put(90, GumTuneClient.mc.gameSettings.keyBindLeft);
            put(180, GumTuneClient.mc.gameSettings.keyBindBack);
            put(-90, GumTuneClient.mc.gameSettings.keyBindRight);
        }
    };

    public static Vec3i addVector(Vec3i vec1, Vec3i vec2) {
        return new Vec3i(vec1.getX() + vec2.getX(), vec1.getY() + vec2.getY(), vec1.getZ() + vec2.getZ());
    }

    public static Vec3 addVector(Vec3 vec1, Vec3 vec2) {
        return new Vec3(vec1.xCoord + vec2.xCoord, vec1.yCoord + vec2.yCoord, vec1.zCoord + vec2.zCoord);
    }

    public static Vec3i scaleVec(Vec3i vec, int scale) {
        return new Vec3i(vec.getX() * scale, vec.getY() * scale, vec.getZ() * scale);
    }

    public static Vec3 scaleVec(Vec3i vec, float scale) {
        return new Vec3(vec.getX() * scale, vec.getY() * scale, vec.getZ() * scale);
    }

    public static Vec3 scaleVec(Vec3 vec, float scale) {
        return new Vec3(vec.xCoord * scale, vec.yCoord * scale, vec.zCoord * scale);
    }

    public static Vec3 floorVec(final Vec3 vec3) {
        return new Vec3(Math.floor(vec3.xCoord), Math.floor(vec3.yCoord), Math.floor(vec3.zCoord));
    }

    public static Vec3 ceilVec(final Vec3 vec3) {
        return new Vec3(Math.ceil(vec3.xCoord), Math.ceil(vec3.yCoord), Math.ceil(vec3.zCoord));
    }

    public static ArrayList<Vec3> vecDirections(Vec3 from, Vec3 to) {
        return vectorSigns(to.subtract(from));
    }

    public static ArrayList<Vec3> vectorSigns(Vec3 vec) {
        return new ArrayList<Vec3>() {
            {
                add(new Vec3(Math.signum(vec.xCoord), 0, 0));
                add(new Vec3(0, Math.signum(vec.yCoord), 0));
                add(new Vec3(0, 0, Math.signum(vec.zCoord)));
            }
        };
    }

    public static double getHorizontalDistance(final Vec3 vec1, final Vec3 vec2) {
        final double d0 = vec1.xCoord - vec2.xCoord;
        final double d2 = vec1.zCoord - vec2.zCoord;
        return MathHelper.sqrt_double(d0 * d0 + d2 * d2);
    }

    public static ArrayList<KeyBinding> getNeededKeyPresses(final Vec3 from, final Vec3 to) {
        final ArrayList<KeyBinding> e = new ArrayList<>();
        final RotationUtils.Rotation neededRot = RotationUtils.getNeededChange(RotationUtils.getRotation(from, to));
        final double neededYaw = neededRot.getYaw() * -1.0f;
        keyBindMap.forEach((k, v) -> {
            if (Math.abs(k - neededYaw) < 67.5 || Math.abs(k - (neededYaw + 360.0)) < 67.5) {
                e.add(v);
            }
            return;
        });
        return e;
    }
}
