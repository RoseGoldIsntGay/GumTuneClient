package rosegold.gumtuneclient.modules.farming;

import net.minecraft.block.BlockBush;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import rosegold.gumtuneclient.config.GumTuneClientConfig;
import rosegold.gumtuneclient.utils.LocationUtils;

public class AgaricusCapHelper {
    public static void setMushroomBoundingBox(World worldIn, BlockPos pos, BlockBush blockBush) {
        if (LocationUtils.currentIsland == LocationUtils.Island.THE_RIFT && GumTuneClientConfig.agaricusCapHelper) {
            if (worldIn.getBlockState(pos).getBlock() == Blocks.brown_mushroom) {
                blockBush.setBlockBounds(0, 0, 0, 0, 0, 0);
            } else if (worldIn.getBlockState(pos).getBlock() == Blocks.red_mushroom) {
                blockBush.setBlockBounds(0, 0, 0, 1, 0.4f, 1);
            }
        } else {
            if (worldIn.getBlockState(pos).getBlock() == Blocks.brown_mushroom || worldIn.getBlockState(pos).getBlock() == Blocks.red_mushroom) {
                blockBush.setBlockBounds(0.3f, 0, 0.3f, 0.7f, 0.4f, 0.7f);
            }
        }
    }
}
