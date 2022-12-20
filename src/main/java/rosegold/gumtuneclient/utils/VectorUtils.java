package rosegold.gumtuneclient.utils;

import net.minecraft.util.Vec3;
import net.minecraft.util.Vec3i;

public class VectorUtils {
    public static Vec3i addVector(Vec3i vec1, Vec3i vec2) {
        return new Vec3i(vec1.getX() + vec2.getX(), vec1.getY() + vec2.getY(), vec1.getZ() + vec2.getZ());
    }

    public static Vec3 addVector(Vec3 vec1, Vec3 vec2) {
        return new Vec3(vec1.xCoord + vec2.xCoord, vec1.yCoord + vec2.yCoord, vec1.zCoord + vec2.zCoord);
    }

    public static Vec3i multVector(Vec3i vec1, int multiply) {
        return new Vec3i(vec1.getX() * multiply, vec1.getY() * multiply, vec1.getZ() * multiply);
    }

    public static Vec3 multVector(Vec3 vec1, double multiply) {
        return new Vec3(vec1.xCoord * multiply, vec1.yCoord * multiply, vec1.zCoord * multiply);
    }

//    public static Vec3i rotateVector(Vec3i vec1, int angle) {
//        return new Vec3i(vec1.getX() * multiply, vec1.getY() * multiply, vec1.getZ() * multiply);
//    }
//
//    public static Vec3 rotateVector(Vec3 vec1, double angle) {
//        return new Vec3(vec1.xCoord * multiply, vec1.yCoord * multiply, vec1.zCoord * multiply);
//    }
}
