package rosegold.gumtuneaddons.hud;

import cc.polyfrost.oneconfig.config.core.OneColor;
import cc.polyfrost.oneconfig.hud.SingleTextHud;
import net.minecraft.client.Minecraft;

public class TestHud extends SingleTextHud {

    public TestHud() {
        super("Title", true, 0, 0, 1f, true, false, 2, 56, 18, new OneColor(0, 0, 0, 120), false, 2, new OneColor(0, 0, 0));
    }

    @Override
    protected String getText(boolean example) {
        return Minecraft.getMinecraft().getSession().getUsername() + "\nCool Stuffs!";
    }
}
