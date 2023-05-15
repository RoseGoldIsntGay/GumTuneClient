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
import rosegold.gumtuneclient.utils.StringUtils;

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
                String lore = InventoryUtils.getItemLore(slotIn.getStack(), i);
                if (lore != null && pattern.matcher(StringUtils.removeFormatting(lore)).find()) {
                    String cleanLore = StringUtils.removeFormatting(lore);
                    if (cleanLore.contains("x")) {
                        String[] split = cleanLore.split("x");
                        crops.put(split[0].trim(), Integer.parseInt(split[1].replace(",", "")));
                    } else {
                        crops.put(cleanLore.trim(), 1);
                    }
                }
            }

            VisitorHelpers.buyCrops(crops);
        }
    }

}
