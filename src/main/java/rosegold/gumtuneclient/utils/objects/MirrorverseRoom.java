package rosegold.gumtuneclient.utils.objects;

import net.minecraft.util.*;
import rosegold.gumtuneclient.modules.dev.PacketLogger;
import rosegold.gumtuneclient.utils.ModUtils;

public class MirrorverseRoom {
    private int index;
    private AxisAlignedBB roomBoundingBox;
    private AxisAlignedBB mirroredRoomBoundingBox;
    private EnumFacing mirrorEnumFacing;

    public MirrorverseRoom(int index, AxisAlignedBB roomBoundingBox, EnumFacing mirrorEnumFacing) {
        this.index = index;
        this.roomBoundingBox = roomBoundingBox;
        this.mirrorEnumFacing = mirrorEnumFacing;
        this.mirroredRoomBoundingBox = roomBoundingBox.offset(
                (roomBoundingBox.maxX - roomBoundingBox.minX) * mirrorEnumFacing.getDirectionVec().getX(),
                (roomBoundingBox.maxY - roomBoundingBox.minY) * mirrorEnumFacing.getDirectionVec().getY(),
                (roomBoundingBox.maxZ - roomBoundingBox.minZ) * mirrorEnumFacing.getDirectionVec().getZ()
        );
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public AxisAlignedBB getRoomBoundingBox() {
        return roomBoundingBox;
    }

    public void setRoomBoundingBox(AxisAlignedBB roomBoundingBox) {
        this.roomBoundingBox = roomBoundingBox;
    }

    public AxisAlignedBB getMirroredRoomBoundingBox() {
        return mirroredRoomBoundingBox;
    }

    public void setMirroredRoomBoundingBox(AxisAlignedBB mirroredRoomBoundingBox) {
        this.mirroredRoomBoundingBox = mirroredRoomBoundingBox;
    }

    public EnumFacing getMirrorEnumFacing() {
        return mirrorEnumFacing;
    }

    public void setMirrorEnumFacing(EnumFacing mirrorEnumFacing) {
        this.mirrorEnumFacing = mirrorEnumFacing;
    }

    public Vec3 getMirroredVec(Vec3 vec) {
        Vec3 mirrorAnchor = getMirrorAnchor();
        return new Vec3(
                vec.xCoord + (mirrorAnchor.xCoord - vec.xCoord) * 2 * Math.abs(mirrorEnumFacing.getDirectionVec().getX()),
                vec.yCoord + (mirrorAnchor.yCoord - vec.yCoord) * 2 * Math.abs(mirrorEnumFacing.getDirectionVec().getY()),
                vec.zCoord + (mirrorAnchor.zCoord - vec.zCoord) * 2 * Math.abs(mirrorEnumFacing.getDirectionVec().getZ())
        );
    }

    public BlockPos getMirroredBlock(BlockPos blockPos) {
        Vec3 mirrorAnchor = getMirrorAnchor();
        return new BlockPos(
                blockPos.getX() + (mirrorAnchor.xCoord - blockPos.getX()) * 2 * Math.abs(mirrorEnumFacing.getDirectionVec().getX()),
                blockPos.getY() + (mirrorAnchor.yCoord - blockPos.getY()) * 2 * Math.abs(mirrorEnumFacing.getDirectionVec().getY()),
                blockPos.getZ() + (mirrorAnchor.zCoord - blockPos.getZ()) * 2 * Math.abs(mirrorEnumFacing.getDirectionVec().getZ())
        );
    }

    public Vec3 getMirrorAnchor() {
        return getEdgeFromEnumFacing(this.mirrorEnumFacing);
    }

    public Vec3 getEdgeFromEnumFacing(EnumFacing enumFacing) {
        Vec3i directionVec = enumFacing.getDirectionVec();
        if (directionVec.getZ() == 1) {
            return new Vec3(0, 0, roomBoundingBox.maxZ);
        } else if (directionVec.getZ() == -1) {
            return new Vec3(0, 0, roomBoundingBox.minZ);
        } else if (directionVec.getX() == 1) {
            return new Vec3(roomBoundingBox.maxX, 0, 0);
        } else if (directionVec.getX() == -1) {
            return new Vec3(roomBoundingBox.minX, 0, 0);
        } else if (directionVec.getY() == 1) {
            return new Vec3(0, roomBoundingBox.maxY, 0);
        } else if (directionVec.getY() == -1) {
            return new Vec3(0, roomBoundingBox.minY, 0);
        }

        return new Vec3(0, 0, 0);
    }
}
