package rosegold.gumtuneclient.mixin;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.inventory.Slot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import rosegold.gumtuneclient.config.GumTuneClientConfig;
import rosegold.gumtuneclient.modules.farming.VisitorHelpers;
import rosegold.gumtuneclient.utils.InventoryUtils;

@Mixin(GuiContainer.class)
public class MixinGuiContainer {

    @Inject(method = "handleMouseClick", at = @At("HEAD"))
    private void handleMouseClick(Slot slotIn, int slotId, int clickedButton, int clickType, CallbackInfo ci) {
        if (GumTuneClientConfig.visitorQuickBuy && GumTuneClientConfig.visitorQuickBuyMode == 0 && clickType == 1 && slotIn.getHasStack() && slotIn.getStack().getDisplayName().contains("Accept Offer")) {
            String requirementString = removeFormatting(InventoryUtils.getItemLore(slotIn.getStack(), 1));
            if (requirementString.contains("x")) {
                String[] split = requirementString.split("x");

                VisitorHelpers.buyCrop(split[0].trim(), Integer.parseInt(split[1]));
            }
        }
    }

    @Unique
    private String removeFormatting(String input) {
        return input.replaceAll("§[0-9a-fk-or]", "");
    }
}
