package rosegold.gumtuneclient.mixin;

import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.util.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import rosegold.gumtuneclient.config.GumTuneClientConfig;

@Mixin(EntityRenderer.class)
public class MixinEntityRenderer {
    @Redirect(method="orientCamera", at=@At(value="INVOKE", target="Lnet/minecraft/util/Vec3;distanceTo(Lnet/minecraft/util/Vec3;)D"))
    public double onCamera(Vec3 instance, Vec3 vec) {
        if(GumTuneClientConfig.phaseCameraThroughBlocks) return 999D;
        return instance.distanceTo(vec);
    }
}