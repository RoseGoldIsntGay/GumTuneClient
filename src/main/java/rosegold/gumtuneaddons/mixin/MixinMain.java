package rosegold.gumtuneaddons.mixin;

import joptsimple.OptionParser;
import net.minecraft.client.main.Main;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Main.class)
public class MixinMain {
    @Inject(method = "main", at = @At("HEAD"))
    private static void fullScreenMinecrraft(String[] strings, CallbackInfo ci) {
        OptionParser optionParser = new OptionParser();
        optionParser.accepts("fullscreen");
    }
}
