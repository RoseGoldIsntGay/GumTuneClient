package rosegold.gumtuneclient.utils.objects;

import com.google.gson.annotations.Expose;
import net.minecraft.util.BlockPos;
import net.minecraft.util.Vec3i;

public class BlockPosition {
    @Expose
    public int x;
    @Expose
    public int y;
    @Expose
    public int z;

    public BlockPosition(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public BlockPosition(BlockPos pos) {
        this.x = pos.getX();
        this.y = pos.getY();
        this.z = pos.getZ();
    }

    public boolean equals(BlockPos blockPos) {
        return blockPos.getX() == this.x && blockPos.getY() == this.y && blockPos.getZ() == this.z;
    }

    public BlockPos toBlockPos() {
        return new BlockPos(this.x, this.y, this.z);
    }

    @Override
    public int hashCode() {
        return (this.y + this.z * 31) * 31 + this.x;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }

        if (!(object instanceof BlockPosition)) {
            return false;
        }

        BlockPosition blockPosition = (BlockPosition) object;

        return this.x == blockPosition.x && this.y == blockPosition.y && this.z == blockPosition.z;
    }
}
