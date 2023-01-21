package rosegold.gumtuneclient.mixin;

import cc.polyfrost.oneconfig.config.data.Mod;
import cc.polyfrost.oneconfig.gui.elements.ModCard;
import cc.polyfrost.oneconfig.renderer.NanoVGHelper;
import cc.polyfrost.oneconfig.utils.InputHandler;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;
import rosegold.gumtuneclient.GumTuneClient;
import rosegold.gumtuneclient.utils.ModUtils;

@Mixin(value = ModCard.class, remap = false)
public class MixinModCard {

    @Final
    @Shadow
    private Mod modData;

    @Inject(method = "draw", at = @At(value = "INVOKE", target = "Lcc/polyfrost/oneconfig/renderer/NanoVGHelper;drawImage(JLjava/lang/String;FFFF)V", shift = At.Shift.AFTER), remap = false)
    private void modifyGumTuneClientLogo(long vg, float x, float y, InputHandler inputHandler, CallbackInfo ci) {
        if (!modData.name.equals(GumTuneClient.NAME)) return;
        NanoVGHelper nanoVGHelper = NanoVGHelper.INSTANCE;
        nanoVGHelper.drawImage(vg, modData.modIcon, x + 98 - 18, y + 19 - 18, 84, 84);
    }
}
