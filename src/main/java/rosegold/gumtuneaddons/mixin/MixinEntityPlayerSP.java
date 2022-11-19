package rosegold.gumtuneaddons.mixin;

import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraftforge.common.MinecraftForge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import rosegold.gumtuneaddons.events.PlayerMoveEvent;
import rosegold.gumtuneaddons.events.ScreenClosedEvent;

@Mixin(EntityPlayerSP.class)
public class MixinEntityPlayerSP {
    @Inject(method = "closeScreen", at = @At("HEAD"), cancellable = true)
    public void closeScreen(CallbackInfo ci) {
        if (MinecraftForge.EVENT_BUS.post(new ScreenClosedEvent())) ci.cancel();
    }

    @Inject(method = "onUpdateWalkingPlayer", at = @At("HEAD"), cancellable = true)
    public void onUpdateWalking(CallbackInfo ci) {
        if (MinecraftForge.EVENT_BUS.post(new PlayerMoveEvent.Pre())) ci.cancel();
    }

    @Inject(method = "onUpdateWalkingPlayer", at = @At("RETURN"), cancellable = true)
    public void onWalking(CallbackInfo ci) {
        if (MinecraftForge.EVENT_BUS.post(new PlayerMoveEvent.Post())) ci.cancel();
    }
}
