package rosegold.gumtuneclient.utils;

import net.minecraft.client.settings.KeyBinding;
import net.minecraft.util.MovingObjectPosition;
import rosegold.gumtuneclient.GumTuneClient;

public class PlayerUtils {

    public static void swingHand(MovingObjectPosition objectMouseOver) {
        if (objectMouseOver == null) {
            objectMouseOver = GumTuneClient.mc.objectMouseOver;
        }
        if (objectMouseOver != null && objectMouseOver.entityHit == null) {
            GumTuneClient.mc.thePlayer.swingItem();
        }
    }

    public static void rightClick() {
        if(!ReflectionUtils.invoke(GumTuneClient.mc, "func_147121_ag")) {
            ReflectionUtils.invoke(GumTuneClient.mc, "rightClickMouse");
        }
    }

    public static void leftClick() {
        if(!ReflectionUtils.invoke(GumTuneClient.mc, "func_147116_af")) {
            ReflectionUtils.invoke(GumTuneClient.mc, "clickMouse");
        }
    }

    public static void middleClick() {
        if(!ReflectionUtils.invoke(GumTuneClient.mc, "func_147112_ai")) {
            ReflectionUtils.invoke(GumTuneClient.mc, "middleClickMouse");
        }
    }

    public static void updateKeys(boolean forward, boolean back, boolean right, boolean left, boolean attack) {
        updateKeys(forward, back, right, left, attack, false, false);
    }

    public static void updateKeys(boolean forward, boolean back, boolean right, boolean left, boolean attack, boolean crouch, boolean space) {
        if (GumTuneClient.mc.currentScreen != null) {
            stopMovement();
            return;
        }
        KeyBinding.setKeyBindState(GumTuneClient.mc.gameSettings.keyBindForward.getKeyCode(), forward);
        KeyBinding.setKeyBindState(GumTuneClient.mc.gameSettings.keyBindBack.getKeyCode(), back);
        KeyBinding.setKeyBindState(GumTuneClient.mc.gameSettings.keyBindRight.getKeyCode(), right);
        KeyBinding.setKeyBindState(GumTuneClient.mc.gameSettings.keyBindLeft.getKeyCode(), left);
        KeyBinding.setKeyBindState(GumTuneClient.mc.gameSettings.keyBindAttack.getKeyCode(), attack);
        KeyBinding.setKeyBindState(GumTuneClient.mc.gameSettings.keyBindSneak.getKeyCode(), crouch);
        KeyBinding.setKeyBindState(GumTuneClient.mc.gameSettings.keyBindJump.getKeyCode(), space);
    }

    public static void stopMovement() {
        KeyBinding.setKeyBindState(GumTuneClient.mc.gameSettings.keyBindForward.getKeyCode(), false);
        KeyBinding.setKeyBindState(GumTuneClient.mc.gameSettings.keyBindBack.getKeyCode(), false);
        KeyBinding.setKeyBindState(GumTuneClient.mc.gameSettings.keyBindRight.getKeyCode(), false);
        KeyBinding.setKeyBindState(GumTuneClient.mc.gameSettings.keyBindLeft.getKeyCode(), false);
        KeyBinding.setKeyBindState(GumTuneClient.mc.gameSettings.keyBindAttack.getKeyCode(), false);
        KeyBinding.setKeyBindState(GumTuneClient.mc.gameSettings.keyBindSneak.getKeyCode(), false);
        KeyBinding.setKeyBindState(GumTuneClient.mc.gameSettings.keyBindJump.getKeyCode(), false);
    }
}
