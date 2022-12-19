package rosegold.gumtuneclient.modules.world;

import net.minecraft.block.Block;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import rosegold.gumtuneclient.GumTuneClient;
import rosegold.gumtuneclient.config.GumTuneClientConfig;
import rosegold.gumtuneclient.events.MillisecondEvent;
import rosegold.gumtuneclient.events.PlayerMoveEvent;
import rosegold.gumtuneclient.utils.BlockUtils;
import rosegold.gumtuneclient.utils.PlayerUtils;
import rosegold.gumtuneclient.utils.RotationUtils;

public class CanePlacer {

    private static Vec3 point;
    private static long lastPlanted = 0;

    @SubscribeEvent
    public void onMillisecond(MillisecondEvent event) {
        if (!GumTuneClientConfig.sugarCanePlacer) return;
        if (GumTuneClient.mc.thePlayer == null || GumTuneClient.mc.theWorld == null || GumTuneClient.mc.playerController == null)
            return;
        if (event.timestamp - lastPlanted > 1000f / GumTuneClientConfig.sugarCanePlacerSpeed) {
            lastPlanted = event.timestamp;
            point = null;
            EntityPlayerSP player = GumTuneClient.mc.thePlayer;
            BlockPos blockPos = BlockUtils.getClosestBlock(5, 0, 3, this::canPlantOnBlock);
            if (blockPos != null) {
                int sugarcaneSlot = PlayerUtils.findItemInHotbar("Cane");
                if (sugarcaneSlot != -1) {
                    player.inventory.currentItem = sugarcaneSlot;
                    point = new Vec3(blockPos.getX() + 0.5, blockPos.getY() + 0.99, blockPos.getZ() + 0.5);
                    GumTuneClient.mc.playerController.onPlayerRightClick(
                            player,
                            GumTuneClient.mc.theWorld,
                            player.inventory.getCurrentItem(),
                            new BlockPos(point),
                            EnumFacing.UP,
                            point
                    );
                }
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.NORMAL)
    public void onUpdatePre(PlayerMoveEvent.Pre pre) {
        if (!GumTuneClientConfig.sugarCanePlacer) return;
        RotationUtils.resetServerLook();
        if (point != null) RotationUtils.serverLook(RotationUtils.getRotationToVec(point));
    }

    private boolean canPlantOnBlock(BlockPos blockPos) {
        return (getBlock(blockPos) == Blocks.grass || getBlock(blockPos) == Blocks.dirt || getBlock(blockPos) == Blocks.sand) && getBlock(blockPos.up()) == Blocks.air &&
                blockPos.getY() < GumTuneClient.mc.thePlayer.getPositionEyes(1f).yCoord &&
                (getBlock(blockPos.south()) == Blocks.water || getBlock(blockPos.south()) == Blocks.flowing_water ||
                        getBlock(blockPos.north()) == Blocks.water || getBlock(blockPos.north()) == Blocks.flowing_water ||
                        getBlock(blockPos.east()) == Blocks.water || getBlock(blockPos.east()) == Blocks.flowing_water ||
                        getBlock(blockPos.west()) == Blocks.water || getBlock(blockPos.west()) == Blocks.flowing_water) &&
                canBlockBeSeen(blockPos);
    }

    private boolean canBlockBeSeen(BlockPos blockPos) {
        Vec3 vec = new Vec3(blockPos.getX() + 0.5, blockPos.getY() + 0.99, blockPos.getZ() + 0.5);
        MovingObjectPosition mop = GumTuneClient.mc.theWorld.rayTraceBlocks(GumTuneClient.mc.thePlayer.getPositionEyes(1.0f), vec, false, true, false);
        if (mop != null && mop.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK) {
            return mop.getBlockPos().equals(blockPos) && vec.distanceTo(GumTuneClient.mc.thePlayer.getPositionEyes(1.0f)) < GumTuneClient.mc.playerController.getBlockReachDistance();
        }

        return false;
    }

    private Block getBlock(BlockPos blockPos) {
        return GumTuneClient.mc.theWorld.getBlockState(blockPos).getBlock();
    }
}
