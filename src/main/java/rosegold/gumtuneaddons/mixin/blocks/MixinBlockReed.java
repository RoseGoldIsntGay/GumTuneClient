package rosegold.gumtuneaddons.mixin.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockReed;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import rosegold.gumtuneaddons.config.GumTuneAddonsConfig;

@Mixin(BlockReed.class)
public class MixinBlockReed extends Block {

    public MixinBlockReed(Material p_i46399_1_, MapColor p_i46399_2_) {
        super(p_i46399_1_, p_i46399_2_);
    }

    @Inject(method = "<init>", at = @At("RETURN"), cancellable = true)
    private void init(CallbackInfo ci) {
        if (GumTuneAddonsConfig.sugarCanePlacer) {
            this.setBlockBounds(0, 0, 0, 0, 0, 0);
            ci.cancel();
        }
    }
}
