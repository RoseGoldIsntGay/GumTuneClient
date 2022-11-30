package rosegold.gumtuneclient.utils;

import net.minecraft.client.renderer.GlStateManager;
import rosegold.gumtuneclient.GumTuneClient;

import java.awt.*;

public class FontUtils {
    public static String enforceWidth(String text, int width) {
        String[] splitText = text.split(" ");
        int lineWidth = 0;
        StringBuilder result = new StringBuilder();
        for(String word : splitText) {
            int wordWidth = getStringWidth(word);
            if(wordWidth + lineWidth > width) {
                result.append(word).append("\n");
                lineWidth = 0;
            } else {
                result.append(word).append(" ");
                lineWidth += wordWidth + getStringWidth(" ");
            }
        }
        return result.toString();
    }

    public static void drawCenteredString(String text, int x, int y, boolean shadow) {
        y -= getStringHeight(text)/2;
        String[] lines = text.split("\n");
        for(String line : lines) {
            drawString(line, x-getStringWidth(line)/2, y, shadow);
            y += getLineHeight() + 1;
        }
    }

    public static void drawCenteredString(String text, int x, int y) {
        drawCenteredString(text, x, y, true);
    }

    public static void drawString(String text, int x, int y, boolean shadow) {
        String[] lines = text.split("\n");
        for(String line : lines) {
            GumTuneClient.mc.fontRendererObj.drawString(line, x, y, Color.WHITE.getRGB(), shadow);
            y += getLineHeight() + 1;
        }
    }

    public static int getStringHeight(String text) {
        int lines = text.split("\n").length;
        return lines > 1 ? lines * (getLineHeight() + 1) - 1 : getLineHeight();
    }

    public static int getStringWidth(String text) {
        String[] lines = text.split("\n");
        int longestLine = 0;
        for(String line : lines) {
            int lineWidth = GumTuneClient.mc.fontRendererObj.getStringWidth(line);
            if(lineWidth > longestLine) longestLine = lineWidth;
        }
        return longestLine;
    }

    public static int getLineHeight() {
        return GumTuneClient.mc.fontRendererObj.FONT_HEIGHT;
    }

    public static void drawScaledString(String string, float scale, int x, int y, boolean shadow) {
        GlStateManager.pushMatrix();
        GlStateManager.scale(scale, scale, scale);
        drawString(string, (int)(x/scale), (int)(y/scale), shadow);
        GlStateManager.popMatrix();
    }

    public static void drawScaledCenteredString(String string, float scale, int x, int y, boolean shadow) {
        GlStateManager.pushMatrix();
        GlStateManager.scale(scale, scale, scale);
        drawCenteredString(string, (int)(x/scale), (int)(y/scale), shadow);
        GlStateManager.popMatrix();
    }
}
