package rosegold.gumtuneclient.modules.singleplayer.skyblockitems;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import rosegold.gumtuneclient.GumTuneClient;
import rosegold.gumtuneclient.utils.RaytracingUtils;
import rosegold.gumtuneclient.utils.RenderUtils;

import java.awt.*;

public class AspectOfTheVoid {

    private static int delay = 10;
    private static BlockPos lookingAt;

    @SubscribeEvent
    public void onInteract(PlayerInteractEvent event) {
        if (!GumTuneClient.mc.isIntegratedServerRunning()) return;
        if (delay != 0) return;
        if (event.action == PlayerInteractEvent.Action.LEFT_CLICK_BLOCK) return;
        if (!GumTuneClient.mc.thePlayer.isSneaking()) return;
        ItemStack itemStack = GumTuneClient.mc.thePlayer.getHeldItem();
        if (itemStack != null && itemStack.getItem() == Items.diamond_shovel && lookingAt != null) {
            GumTuneClient.mc.thePlayer.setPosition(
                    lookingAt.getX() + 0.5,
                    lookingAt.getY() + 1,
                    lookingAt.getZ() + 0.5
            );
            delay = 7;
        }
    }

    @SubscribeEvent
    public void onTick(TickEvent.ClientTickEvent event) {
        if (!GumTuneClient.mc.isIntegratedServerRunning()) return;
        if (GumTuneClient.mc.thePlayer == null || GumTuneClient.mc.theWorld == null) return;
        if (event.phase == TickEvent.Phase.START) return;
        if (delay > 0) {
            delay--;
        }
        lookingAt = null;
        ItemStack itemStack = GumTuneClient.mc.thePlayer.getHeldItem();
        if (itemStack != null && itemStack.getItem() == Items.diamond_shovel && GumTuneClient.mc.thePlayer.isSneaking()) {
            RaytracingUtils.RaytraceResult ray = RaytracingUtils.raytraceToBlock(GumTuneClient.mc.thePlayer, 1f, 64, 0.1f);
            if (ray != null) {
                BlockPos blockPos = ray.getBlockPos();
                Block block = ray.getIBlockState().getBlock();
                if (block.isCollidable() &&
                        block != Blocks.carpet && block != Blocks.skull &&
                        block.getCollisionBoundingBox(GumTuneClient.mc.theWorld, blockPos, ray.getIBlockState()) != null &&
                        block != Blocks.wall_sign && block != Blocks.standing_sign) {
                    BlockPos blockPosAbove = blockPos.add(0, 1, 0);
                    Block blockAbove = GumTuneClient.mc.theWorld.getBlockState(blockPosAbove).getBlock();

                    Block twoBlockAbove = GumTuneClient.mc.theWorld.getBlockState(blockPos.add(0, 2, 0)).getBlock();

                    if (blockAbove == Blocks.air && twoBlockAbove == Blocks.air) {
                        lookingAt = blockPos;
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public void onRender(RenderWorldLastEvent event) {
        if (!GumTuneClient.mc.isIntegratedServerRunning()) return;
        if (GumTuneClient.mc.thePlayer == null || GumTuneClient.mc.theWorld == null) return;
        RenderUtils.renderEspBox(lookingAt, event.partialTicks, Color.BLUE.getRGB());
    }
}
