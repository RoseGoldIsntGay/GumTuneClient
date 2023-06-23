package rosegold.gumtuneclient.mixin.blocks;

import net.minecraft.block.BlockBush;
import net.minecraft.block.BlockMushroom;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import rosegold.gumtuneclient.modules.farming.AgaricusCapHelper;

@Mixin(BlockMushroom.class)
public class MixinBlockMushroom extends BlockBush {
    @Override
    public AxisAlignedBB getSelectedBoundingBox(World worldIn, BlockPos pos) {
        AgaricusCapHelper.setMushroomBoundingBox(worldIn, pos, this);
        return super.getSelectedBoundingBox(worldIn, pos);
    }

    @Override
    public MovingObjectPosition collisionRayTrace(World worldIn, BlockPos pos, Vec3 start, Vec3 end) {
        AgaricusCapHelper.setMushroomBoundingBox(worldIn, pos, this);
        return super.collisionRayTrace(worldIn, pos, start, end);
    }
}
