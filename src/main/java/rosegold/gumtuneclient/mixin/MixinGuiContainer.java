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
import rosegold.gumtuneclient.utils.GuiUtils;
import rosegold.gumtuneclient.utils.InventoryUtils;

import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

@Mixin(GuiContainer.class)
public class MixinGuiContainer {

    @Unique
    private static final Pattern pattern = Pattern.compile("^ [\\w ]+", Pattern.CASE_INSENSITIVE);

    @Inject(method = "handleMouseClick", at = @At("HEAD"))
    private void handleMouseClick(Slot slotIn, int slotId, int clickedButton, int clickType, CallbackInfo ci) {
        if (GumTuneClientConfig.visitorQuickBuy && GumTuneClientConfig.visitorQuickBuyMode == 0 && clickType == 1 && slotIn.getHasStack() && slotIn.getStack().getDisplayName().contains("Accept Offer")) {
            VisitorHelpers.visitorName = GuiUtils.getOpenInventoryName();

            ConcurrentHashMap<String, Integer> crops = new ConcurrentHashMap<>();

            for (int i = 1; i < 5; i++) {
                String lore = removeFormatting(InventoryUtils.getItemLore(slotIn.getStack(), i));
                if (pattern.matcher(removeFormatting(lore)).find()) {
                    if (lore.contains("x")) {
                        String[] split = lore.split("x");
                        crops.put(split[0].trim(), Integer.parseInt(split[1].replace(",", "")));
                    } else {
                        crops.put(lore.trim(), 1);
                    }
                }
            }

            VisitorHelpers.buyCrops(crops);
        }
    }

    @Unique
    private String removeFormatting(String input) {
        return input.replaceAll("§[0-9a-fk-or]", "");
    }
}
