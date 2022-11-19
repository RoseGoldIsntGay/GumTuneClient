package rosegold.gumtuneaddons.modules.world;

import net.minecraft.block.Block;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import rosegold.gumtuneaddons.GumTuneAddons;
import rosegold.gumtuneaddons.annotations.Module;
import rosegold.gumtuneaddons.config.GumTuneAddonsConfig;
import rosegold.gumtuneaddons.events.MillisecondEvent;
import rosegold.gumtuneaddons.events.PlayerMoveEvent;
import rosegold.gumtuneaddons.utils.BlockUtils;
import rosegold.gumtuneaddons.utils.PlayerUtils;
import rosegold.gumtuneaddons.utils.RotationUtils;

@Module
public class CanePlacer {

    private static Vec3 point;
    private static long lastPlanted = 0;

    @SubscribeEvent
    public void onMillisecond(MillisecondEvent event) {
        if (!GumTuneAddonsConfig.sugarCanePlacer) return;
        if (GumTuneAddons.mc.thePlayer == null || GumTuneAddons.mc.theWorld == null || GumTuneAddons.mc.playerController == null)
            return;
        if (event.timestamp - lastPlanted > 1000f / GumTuneAddonsConfig.sugarCanePlacerSpeed) {
            lastPlanted = event.timestamp;
            point = null;
            EntityPlayerSP player = GumTuneAddons.mc.thePlayer;
            BlockPos blockPos = BlockUtils.getClosestBlock(5, 0, 3, this::canPlantOnBlock);
            if (blockPos != null) {
                int sugarcaneSlot = PlayerUtils.findItemInHotbar("Cane");
                if (sugarcaneSlot != -1) {
                    player.inventory.currentItem = sugarcaneSlot;
                    point = new Vec3(blockPos.getX() + 0.5, blockPos.getY() + 0.99, blockPos.getZ() + 0.5);
                    GumTuneAddons.mc.playerController.onPlayerRightClick(
                            player,
                            GumTuneAddons.mc.theWorld,
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
        if (!GumTuneAddonsConfig.sugarCanePlacer) return;
        if (point != null) {
            RotationUtils.smoothLook(RotationUtils.getRotationToVec(point), 0, () -> {
            });
        }
    }

    private boolean canPlantOnBlock(BlockPos blockPos) {
        return (getBlock(blockPos) == Blocks.grass || getBlock(blockPos) == Blocks.dirt || getBlock(blockPos) == Blocks.sand) && getBlock(blockPos.up()) == Blocks.air &&
                blockPos.getY() < GumTuneAddons.mc.thePlayer.getPositionEyes(1f).yCoord &&
                (getBlock(blockPos.south()) == Blocks.water || getBlock(blockPos.south()) == Blocks.flowing_water ||
                        getBlock(blockPos.north()) == Blocks.water || getBlock(blockPos.north()) == Blocks.flowing_water ||
                        getBlock(blockPos.east()) == Blocks.water || getBlock(blockPos.east()) == Blocks.flowing_water ||
                        getBlock(blockPos.west()) == Blocks.water || getBlock(blockPos.west()) == Blocks.flowing_water) &&
                canBlockBeSeen(blockPos);
    }

    private boolean canBlockBeSeen(BlockPos blockPos) {
        Vec3 vec = new Vec3(blockPos.getX() + 0.5, blockPos.getY() + 0.99, blockPos.getZ() + 0.5);
        MovingObjectPosition mop = GumTuneAddons.mc.theWorld.rayTraceBlocks(GumTuneAddons.mc.thePlayer.getPositionEyes(1.0f), vec, false, true, false);
        if (mop != null && mop.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK) {
            return mop.getBlockPos().equals(blockPos) && vec.distanceTo(GumTuneAddons.mc.thePlayer.getPositionEyes(1.0f)) < GumTuneAddons.mc.playerController.getBlockReachDistance();
        }

        return false;
    }

    private Block getBlock(BlockPos blockPos) {
        return GumTuneAddons.mc.theWorld.getBlockState(blockPos).getBlock();
    }
}
