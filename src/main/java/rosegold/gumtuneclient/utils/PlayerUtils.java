package rosegold.gumtuneclient.utils;

import net.minecraft.util.MovingObjectPosition;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import rosegold.gumtuneclient.GumTuneClient;

public class PlayerUtils {
    public static boolean pickaxeAbilityReady = false;

    @SubscribeEvent(receiveCanceled = true)
    public void onChat(ClientChatReceivedEvent event) {
        String message = StringUtils.removeFormatting(event.message.getUnformattedText());
        if (message.contains(":") || message.contains(">")) return;
        if (message.startsWith("You used your Mining Speed Boost Pickaxe Ability!")) {
            pickaxeAbilityReady = false;
        } else if (message.equals("Mining Speed Boost is now available!")) {
            pickaxeAbilityReady = true;
        }
    }

    @SubscribeEvent
    public void onWorldChange(WorldEvent.Unload event) {
        pickaxeAbilityReady = false;
    }

    public static void swingHand(MovingObjectPosition objectMouseOver) {
        if (objectMouseOver == null) {
            objectMouseOver = GumTuneClient.mc.objectMouseOver;
        }
        if (objectMouseOver != null && objectMouseOver.entityHit == null) {
            GumTuneClient.mc.thePlayer.swingItem();
        }
    }

    public static void rightClick() {
        if (!ReflectionUtils.invoke(GumTuneClient.mc, "func_147121_ag")) {
            ReflectionUtils.invoke(GumTuneClient.mc, "rightClickMouse");
        }
    }

    public static void leftClick() {
        if (!ReflectionUtils.invoke(GumTuneClient.mc, "func_147116_af")) {
            ReflectionUtils.invoke(GumTuneClient.mc, "clickMouse");
        }
    }

    public static void middleClick() {
        if (!ReflectionUtils.invoke(GumTuneClient.mc, "func_147112_ai")) {
            ReflectionUtils.invoke(GumTuneClient.mc, "middleClickMouse");
        }
    }
}
