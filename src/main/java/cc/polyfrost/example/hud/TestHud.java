package cc.polyfrost.example.hud;

import cc.polyfrost.oneconfig.hud.TextHud;
import com.google.common.collect.Lists;
import net.minecraft.client.Minecraft;

import java.util.List;

public class TestHud extends TextHud {
    public TestHud(boolean enabled, int x, int y) {
        super(enabled, x, y);
    }

    @Override
    public List<String> getLines() {
        return Lists.newArrayList("Hello, world! " + Minecraft.getDebugFPS());
    }
}
