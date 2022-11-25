package rosegold.gumtuneclient.utils;

import java.awt.*;

public class ColorUtils {
    private static int[] hexColors = generateHexColors();

    public static int getChroma(float speed, int offset) {
        return Color.HSBtoRGB(((System.currentTimeMillis() - offset * 10L) % (long) speed) / speed, 0.88F, 0.88F);
    }

    private static int[] generateHexColors() {
        int[] ret = new int[16];

        for (int i = 0; i < 16; i++) {
            int base = (i >> 3 & 1) * 85;

            int red = (i >> 2 & 1) * 170 + base + (i == 6 ? 85 : 0);
            int green = (i >> 1 & 1) * 170 + base;
            int blue = (i & 1) * 170 + base;

            ret[i] = ((red & 255) << 16) | ((green & 255) << 8) | (blue & 255);
        }

        return ret;
    }

    public static int getHexColor(int colorIndex) {
        return hexColors[colorIndex];
    }
}
