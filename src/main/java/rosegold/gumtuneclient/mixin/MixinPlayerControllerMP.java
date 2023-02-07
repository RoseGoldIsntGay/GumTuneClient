package rosegold.gumtuneclient.mixin;

import net.minecraft.client.multiplayer.PlayerControllerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import rosegold.gumtuneclient.modules.mining.MobxDrill;

@Mixin(PlayerControllerMP.class)
public class MixinPlayerControllerMP {

    @Shadow private ItemStack currentItemHittingBlock;

    @Shadow private BlockPos currentBlock;

    /**
     * @author mojang
     * @reason allow breaking progress on item lore update
     */
    @Overwrite
    private boolean isHittingPosition(BlockPos pos) {
        return MobxDrill.isHittingPosition(pos, currentItemHittingBlock, currentBlock);
    }
}
