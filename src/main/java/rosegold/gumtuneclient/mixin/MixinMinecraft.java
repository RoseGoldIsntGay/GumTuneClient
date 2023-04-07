package rosegold.gumtuneclient.mixin;

import net.minecraft.block.BlockCocoa;
import net.minecraft.block.BlockCrops;
import net.minecraft.block.BlockNetherWart;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MovingObjectPosition;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import rosegold.gumtuneclient.GumTuneClient;
import rosegold.gumtuneclient.config.GumTuneClientConfig;
import rosegold.gumtuneclient.modules.farming.AvoidBreakingCrops;

@Mixin(Minecraft.class)
public class MixinMinecraft {

    @Shadow private int leftClickCounter;

    @Shadow public MovingObjectPosition objectMouseOver;

    @Shadow public EntityPlayerSP thePlayer;

    @Inject(method = "clickMouse", at = @At(value = "HEAD"), cancellable = true)
    private void onClickMouse(CallbackInfo ci) {
        if (leftClickCounter <= 0 && objectMouseOver != null && objectMouseOver.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK) {
            if (shouldCancelBreak()) {
                if (GumTuneClientConfig.avoidBreakingMode == 1) {
                    BlockPos blockPos = objectMouseOver.getBlockPos();
                    AvoidBreakingCrops.addBlock(blockPos, GumTuneClient.mc.theWorld.getBlockState(blockPos));
                    GumTuneClient.mc.theWorld.setBlockToAir(blockPos);
                }
                ci.cancel();
            }
        }
    }

    @Inject(method = "sendClickBlockToController", at = @At(value = "HEAD"), cancellable = true)
    private void onSendClickBlockToController(boolean leftClick, CallbackInfo ci) {
        if (leftClickCounter <= 0 && !thePlayer.isUsingItem() && leftClick && objectMouseOver != null && objectMouseOver.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK) {
            if (shouldCancelBreak()) {
                if (GumTuneClientConfig.avoidBreakingMode == 1) {
                    BlockPos blockPos = objectMouseOver.getBlockPos();
                    AvoidBreakingCrops.addBlock(blockPos, GumTuneClient.mc.theWorld.getBlockState(blockPos));
                    GumTuneClient.mc.theWorld.setBlockToAir(blockPos);
                }
                ci.cancel();
            }
        }
    }

    private boolean shouldCancelBreak() {
        BlockPos blockPos = objectMouseOver.getBlockPos();
        IBlockState blockState = GumTuneClient.mc.theWorld.getBlockState(blockPos);

        if (GumTuneClientConfig.avoidBreakingStems && (blockState.getBlock() == Blocks.melon_stem || blockState.getBlock() == Blocks.pumpkin_stem)) {
            return true;
        }

        if (GumTuneClientConfig.avoidBreakingBottomSugarCane && blockState.getBlock() == Blocks.reeds && GumTuneClient.mc.theWorld.getBlockState(blockPos.add(0, -1, 0)).getBlock() != Blocks.reeds) {
            return true;
        }

        if (GumTuneClientConfig.avoidBreakingChildCrops) {
            if (blockState.getBlock() instanceof BlockNetherWart) {
                return blockState.getValue(BlockNetherWart.AGE) != 3;
            }
            else if (blockState.getBlock() instanceof BlockCrops) {
                return blockState.getValue(BlockCrops.AGE) != 7;
            }
            else if (blockState.getBlock() instanceof BlockCocoa) {
                return blockState.getValue(BlockCocoa.AGE) != 2;
            }
        }

        return false;
    }
}
