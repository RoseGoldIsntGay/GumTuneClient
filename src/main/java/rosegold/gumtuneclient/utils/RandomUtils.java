package rosegold.gumtuneclient.utils;

import net.minecraft.util.Vec3;

import java.util.Random;

public class RandomUtils {
    private static final Random rand = new Random();

    public static Vec3 randomVec() {
        return new Vec3(rand.nextDouble(), rand.nextDouble(), rand.nextDouble());
    }

    public static int randBetween(int a, int b) {
        return rand.nextInt(b - a + 1) + a;
    }

    public static double randBetween(double a, double b) {
        return (rand.nextDouble() * (b - a)) + a;
    }

    public static float randBetween(float a, float b) {
        return (rand.nextFloat() * (b - a)) + a;
    }

    public static int nextInt(int yep) {
        return rand.nextInt(yep);
    }
}
