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
import net.minecraft.util.ResourceLocation;
import org.apache.commons.io.IOUtils;
import org.lwjgl.opengl.Display;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import rosegold.gumtuneclient.GumTuneClient;
import rosegold.gumtuneclient.config.GumTuneClientConfig;
import rosegold.gumtuneclient.modules.farming.AvoidBreakingCrops;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

@Mixin(Minecraft.class)
public class MixinMinecraft {

    @Shadow
    private int leftClickCounter;

    @Shadow
    public MovingObjectPosition objectMouseOver;

    @Shadow
    public EntityPlayerSP thePlayer;

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

    @Inject(method = "setWindowIcon", at = @At("RETURN"))
    private void fuckNewIcon(CallbackInfo ci) {
        if (!GumTuneClientConfig.oldMinecraftLogo) return;

        InputStream inputStream16 = null;
        InputStream inputStream32 = null;
        try {
            inputStream16 = GumTuneClient.mc.mcDefaultResourcePack.getInputStreamAssets(new ResourceLocation("old_icon_16x16.png"));
            inputStream32 = GumTuneClient.mc.mcDefaultResourcePack.getInputStreamAssets(new ResourceLocation("old_icon_32x32.png"));

            if (inputStream16 == null || inputStream32 == null) return;

            System.out.println("Set icon to old one!");
            Display.setIcon(new ByteBuffer[]{readImageToBuffer(inputStream16), readImageToBuffer(inputStream32)});
        } catch (IOException ioexception) {
            IOUtils.closeQuietly(inputStream16);
            IOUtils.closeQuietly(inputStream32);
            ioexception.printStackTrace();
        }
    }

    private ByteBuffer readImageToBuffer(InputStream imageStream) throws IOException {
        BufferedImage bufferedimage = ImageIO.read(imageStream);
        int[] aint = bufferedimage.getRGB(0, 0, bufferedimage.getWidth(), bufferedimage.getHeight(), null, 0, bufferedimage.getWidth());
        ByteBuffer bytebuffer = ByteBuffer.allocate(4 * aint.length);
        for (int i : aint) {
            bytebuffer.putInt(i << 8 | i >> 24 & 0xFF);
        }
        bytebuffer.flip();
        return bytebuffer;
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
            } else if (blockState.getBlock() instanceof BlockCrops) {
                return blockState.getValue(BlockCrops.AGE) != 7;
            } else if (blockState.getBlock() instanceof BlockCocoa) {
                return blockState.getValue(BlockCocoa.AGE) != 2;
            }
        }

        return false;
    }
}
