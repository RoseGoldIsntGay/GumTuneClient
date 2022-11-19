package rosegold.gumtuneaddons.mixin;

import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.MathHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import rosegold.gumtuneaddons.GumTuneAddons;
import rosegold.gumtuneaddons.mixin.accessors.EntityPlayerSPAccessor;
import rosegold.gumtuneaddons.mixin.accessors.MinecraftAccessor;

@Mixin(ModelBiped.class)
public class MixinModelBiped {
    @Shadow
    public ModelRenderer bipedHead;

    @Inject(method = "setRotationAngles", at = @At(value = "FIELD", target = "Lnet/minecraft/client/model/ModelBiped;swingProgress:F"))
    private void setRotationAngles(float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor, Entity entityIn, CallbackInfo ci) {
        if (entityIn != null && entityIn == GumTuneAddons.mc.thePlayer) {
            bipedHead.rotateAngleX = ((EntityPlayerSPAccessor) entityIn).getLastReportedPitch() / 57.295776f;

            float partialTicks = ((MinecraftAccessor) GumTuneAddons.mc).getTimer().renderPartialTicks;
            float yawOffset = interpolateRotation(GumTuneAddons.mc.thePlayer.prevRenderYawOffset, GumTuneAddons.mc.thePlayer.renderYawOffset, partialTicks); //Body
            float fakeHead = ((EntityPlayerSPAccessor) entityIn).getLastReportedYaw(); //Head
            float calcNetHead = fakeHead - yawOffset;
            calcNetHead = MathHelper.wrapAngleTo180_float(calcNetHead);

            bipedHead.rotateAngleY = calcNetHead / 57.295776f;
        }
    }

    protected float interpolateRotation(float par1, float par2, float par3)
    {
        float f;

        for (f = par2 - par1; f < -180.0F; f += 360.0F)
        {
            ;
        }

        while (f >= 180.0F)
        {
            f -= 360.0F;
        }

        return par1 + par3 * f;
    }
}
