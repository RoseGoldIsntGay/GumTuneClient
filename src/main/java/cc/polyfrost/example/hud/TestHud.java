package cc.polyfrost.example.hud;

import cc.polyfrost.oneconfig.hud.SingleTextHud;
import net.minecraft.client.Minecraft;

public class TestHud extends SingleTextHud {

    public TestHud() {
        super("Title", true);
    }

    @Override
    protected String getText(boolean example) {
        return Minecraft.getMinecraft().getSession().getUsername();
    }
}
