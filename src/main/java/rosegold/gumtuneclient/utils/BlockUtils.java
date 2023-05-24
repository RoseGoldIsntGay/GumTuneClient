package rosegold.gumtuneclient.utils;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.util.*;
import net.minecraft.world.World;
import rosegold.gumtuneclient.GumTuneClient;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.Predicate;

import static rosegold.gumtuneclient.GumTuneClient.mc;

public class BlockUtils {

    public static ConcurrentLinkedQueue<BlockPos> blockPosConcurrentLinkedQueue = new ConcurrentLinkedQueue<>();
    public static Vec3 source;
    public static Vec3 destination;

    private static final HashMap<EnumFacing, float[]> BLOCK_SIDES = new HashMap<EnumFacing, float[]>() {{
        put(EnumFacing.DOWN, new float[]{0.5f, 0.01f, 0.5f});
        put(EnumFacing.UP, new float[]{0.5f, 0.99f, 0.5f});
        put(EnumFacing.WEST, new float[]{0.01f, 0.5f, 0.5f});
        put(EnumFacing.EAST, new float[]{0.99f, 0.5f, 0.5f});
        put(EnumFacing.NORTH, new float[]{0.5f, 0.5f, 0.01f});
        put(EnumFacing.SOUTH, new float[]{0.5f, 0.5f, 0.99f});
    }};

    public static BlockPos getClosestBlock(int radius, int height, int depth, Predicate<? super BlockPos> predicate) {
        EntityPlayerSP player = mc.thePlayer;
        BlockPos playerPos = new BlockPos((int) Math.floor(player.posX), (int) Math.floor(player.posY) + 1, (int) Math.floor(player.posZ));
        Vec3i vec3Top = new Vec3i(radius, height, radius);
        Vec3i vec3Bottom = new Vec3i(radius, depth, radius);
        BlockPos closest = null;

        for (BlockPos blockPos : BlockPos.getAllInBox(playerPos.subtract(vec3Bottom), playerPos.add(vec3Top))) {
            if (predicate.test(blockPos)) {
                if (closest == null || player.getDistanceSq(blockPos) < player.getDistanceSq(closest)) {
                    closest = blockPos;
                }
            }
        }

        return closest;
    }

    public static BlockPos getEasiestBlock(int radius, int height, int depth, Predicate<? super BlockPos> predicate) {
        EntityPlayerSP player = mc.thePlayer;
        BlockPos playerPos = new BlockPos((int) Math.floor(player.posX), (int) Math.floor(player.posY) + 1, (int) Math.floor(player.posZ));
        Vec3i vec3Top = new Vec3i(radius, height, radius);
        Vec3i vec3Bottom = new Vec3i(radius, depth, radius);
        BlockPos easiest = null;

        for (BlockPos blockPos : BlockPos.getAllInBox(playerPos.subtract(vec3Bottom), playerPos.add(vec3Top))) {
            if (predicate.test(blockPos) && canBlockBeSeen(blockPos, 8, new Vec3(0, 0, 0), x -> false)) {
                if (easiest == null || RotationUtils.getServerNeededChange(RotationUtils.getRotation(blockPos)).getValue() < RotationUtils.getServerNeededChange(RotationUtils.getRotation(easiest)).getValue()) {
                    easiest = blockPos;
                }
            }
        }

        if (easiest != null) return easiest;

        for (BlockPos blockPos : BlockPos.getAllInBox(playerPos.subtract(vec3Bottom), playerPos.add(vec3Top))) {
            if (predicate.test(blockPos)) {
                if (easiest == null || RotationUtils.getServerNeededChange(RotationUtils.getRotation(blockPos)).getValue() < RotationUtils.getServerNeededChange(RotationUtils.getRotation(easiest)).getValue()) {
                    easiest = blockPos;
                }
            }
        }

        return easiest;
    }

    public static BlockPos getFurthestBlock(int radius, int height, int depth, Predicate<? super BlockPos> predicate) {
        EntityPlayerSP player = mc.thePlayer;
        BlockPos playerPos = player.getPosition().up();
        Vec3i vec3Top = new Vec3i(radius, height, radius);
        Vec3i vec3Bottom = new Vec3i(radius, depth, radius);
        BlockPos closest = null;

        for (BlockPos blockPos : BlockPos.getAllInBox(playerPos.subtract(vec3Bottom), playerPos.add(vec3Top))) {
            if (predicate.test(blockPos)) {
                if (closest == null || player.getDistanceSq(blockPos) > player.getDistanceSq(closest)) {
                    closest = blockPos;
                }
            }
        }

        return closest;
    }

    public static boolean canBlockBeSeen(BlockPos blockPos, double dist, Vec3 offset, Predicate<? super Block> predicate) {
        Vec3 vec = new Vec3(blockPos.getX() + 0.5, blockPos.getY() + 0.5, blockPos.getZ() + 0.5).add(offset);
        MovingObjectPosition mop = rayTraceBlocks(GumTuneClient.mc.thePlayer.getPositionEyes(1.0f), vec, false, true, false, predicate);
        if (mop != null && mop.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK) {
            return mop.getBlockPos().equals(blockPos) && vec.distanceTo(GumTuneClient.mc.thePlayer.getPositionEyes(1.0f)) < dist;
        }

        return false;
    }

    public static EnumFacing calculateEnumfacing(Vec3 vec) {
        int x = MathHelper.floor_double(vec.xCoord);
        int y = MathHelper.floor_double(vec.yCoord);
        int z = MathHelper.floor_double(vec.zCoord);
        MovingObjectPosition position = calculateIntercept(new AxisAlignedBB(x, y, z, x + 1, y + 1, z + 1), vec, 50.0f);
        return (position != null) ? position.sideHit : null;
    }

    public static MovingObjectPosition calculateIntercept(AxisAlignedBB aabb, Vec3 vec, float range) {
        Vec3 playerPositionEyes = GumTuneClient.mc.thePlayer.getPositionEyes(1f);
        Vec3 blockVector = RotationUtils.getLook(vec);
        return aabb.calculateIntercept(playerPositionEyes, playerPositionEyes.addVector(blockVector.xCoord * range, blockVector.yCoord * range, blockVector.zCoord * range));
    }

    private static ArrayList<Vec3> getPointsOnBlock(BlockPos bp, EnumFacing enumFacing) {
        ArrayList<Vec3> points = new ArrayList<>();

        if (enumFacing != null) {
            for (int i = 0; i < 20; i++) {
                float x = BLOCK_SIDES.get(enumFacing)[0];
                float y = BLOCK_SIDES.get(enumFacing)[1];
                float z = BLOCK_SIDES.get(enumFacing)[2];
                if (x == 0.5) x = RandomUtils.randBetween(0.1f, 0.9f);
                if (y == 0.5) y = RandomUtils.randBetween(0.1f, 0.9f);
                if (z == 0.5) z = RandomUtils.randBetween(0.1f, 0.9f);

                points.add(new Vec3(bp).addVector(x, y, z));
            }
        } else {
            for (float[] side : BLOCK_SIDES.values()) {
                for (int i = 0; i < 20; i++) {
                    float x = side[0];
                    float y = side[1];
                    float z = side[2];

                    if (x == 0.5) x = RandomUtils.randBetween(0.1f, 0.9f);
                    if (y == 0.5) y = RandomUtils.randBetween(0.1f, 0.9f);
                    if (z == 0.5) z = RandomUtils.randBetween(0.1f, 0.9f);

                    points.add(new Vec3(bp).addVector(x, y, z));
                }
            }
        }

        return points;
    }

    public static ArrayList<Vec3> getViablePointsOnBlock(BlockPos blockPos, EnumFacing enumFacing, double range) {
        return getViablePointsOnBlock(blockPos, enumFacing, range, false, false);
    }

    public static ArrayList<Vec3> getViablePointsOnBlock(BlockPos blockPos, EnumFacing enumFacing, double range, boolean fullBlocks, boolean sneak) {
        ArrayList<Vec3> points = new ArrayList<>();
        ArrayList<Vec3> pointsOnBlock = getPointsOnBlock(blockPos, enumFacing);

        Vec3 playerPosition = new Vec3(GumTuneClient.mc.thePlayer.posX, GumTuneClient.mc.thePlayer.posY + (sneak ? 1.54D : 1.62D), GumTuneClient.mc.thePlayer.posZ);

        BlockUtils.blockPosConcurrentLinkedQueue.clear();

        for (Vec3 point : pointsOnBlock) {
            MovingObjectPosition mop = rayTraceBlocks(playerPosition, point, false, false, false, x -> false, false, fullBlocks);

            if (mop != null && mop.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK) {
                if (mop.getBlockPos().equals(blockPos) && point.distanceTo(playerPosition) < range) {
                    points.add(point);
                }
            }
        }

        return points;
    }

    public static MovingObjectPosition rayTraceBlocks(Vec3 vec31, Vec3 vec32, boolean stopOnLiquid, boolean ignoreBlockWithoutBoundingBox, boolean returnLastUncollidableBlock, Predicate<? super Block> predicate) {
        return rayTraceBlocks(vec31, vec32, stopOnLiquid, ignoreBlockWithoutBoundingBox, returnLastUncollidableBlock, predicate, false, false);
    }

    public static MovingObjectPosition rayTraceBlocks(Vec3 vec31, Vec3 vec32, boolean stopOnLiquid, boolean ignoreBlockWithoutBoundingBox, boolean returnLastUncollidableBlock, Predicate<? super Block> predicate, boolean visualize) {
        return rayTraceBlocks(vec31, vec32, stopOnLiquid, ignoreBlockWithoutBoundingBox, returnLastUncollidableBlock, predicate, visualize, false);
    }

    public static MovingObjectPosition rayTraceBlocks(Vec3 vec31, Vec3 vec32, boolean stopOnLiquid, boolean ignoreBlockWithoutBoundingBox, boolean returnLastUncollidableBlock, Predicate<? super Block> predicate, boolean visualize, boolean fullBlocks) {
        if (!(Double.isNaN(vec31.xCoord) || Double.isNaN(vec31.yCoord) || Double.isNaN(vec31.zCoord))) {
            if (!(Double.isNaN(vec32.xCoord) || Double.isNaN(vec32.yCoord) || Double.isNaN(vec32.zCoord))) {
                MovingObjectPosition movingobjectposition;
                int i = MathHelper.floor_double(vec32.xCoord);
                int j = MathHelper.floor_double(vec32.yCoord);
                int k = MathHelper.floor_double(vec32.zCoord);
                int l = MathHelper.floor_double(vec31.xCoord);
                int i1 = MathHelper.floor_double(vec31.yCoord);
                int j1 = MathHelper.floor_double(vec31.zCoord);
                BlockPos blockpos = new BlockPos(l, i1, j1);
                IBlockState iblockstate = getBlockState(blockpos);
                Block block = iblockstate.getBlock();
                if (visualize) blockPosConcurrentLinkedQueue.add(blockpos);
                if (!predicate.test(block) && (!ignoreBlockWithoutBoundingBox || block.getCollisionBoundingBox(mc.theWorld, blockpos, iblockstate) != null) && block.canCollideCheck(iblockstate, stopOnLiquid) && (movingobjectposition = collisionRayTrace(block, blockpos, vec31, vec32, fullBlocks)) != null) {
                    return movingobjectposition;
                }
                MovingObjectPosition movingobjectposition2 = null;
                int k1 = 200;
                while (k1-- >= 0) {
                    EnumFacing enumfacing;
                    if (Double.isNaN(vec31.xCoord) || Double.isNaN(vec31.yCoord) || Double.isNaN(vec31.zCoord)) {
                        return null;
                    }
                    if (l == i && i1 == j && j1 == k) {
                        return returnLastUncollidableBlock ? movingobjectposition2 : null;
                    }
                    boolean flag2 = true;
                    boolean flag = true;
                    boolean flag1 = true;
                    double d0 = 999.0;
                    double d1 = 999.0;
                    double d2 = 999.0;
                    if (i > l) {
                        d0 = (double)l + 1.0;
                    } else if (i < l) {
                        d0 = (double)l + 0.0;
                    } else {
                        flag2 = false;
                    }
                    if (j > i1) {
                        d1 = (double)i1 + 1.0;
                    } else if (j < i1) {
                        d1 = (double)i1 + 0.0;
                    } else {
                        flag = false;
                    }
                    if (k > j1) {
                        d2 = (double)j1 + 1.0;
                    } else if (k < j1) {
                        d2 = (double)j1 + 0.0;
                    } else {
                        flag1 = false;
                    }
                    double d3 = 999.0;
                    double d4 = 999.0;
                    double d5 = 999.0;
                    double d6 = vec32.xCoord - vec31.xCoord;
                    double d7 = vec32.yCoord - vec31.yCoord;
                    double d8 = vec32.zCoord - vec31.zCoord;
                    if (flag2) {
                        d3 = (d0 - vec31.xCoord) / d6;
                    }
                    if (flag) {
                        d4 = (d1 - vec31.yCoord) / d7;
                    }
                    if (flag1) {
                        d5 = (d2 - vec31.zCoord) / d8;
                    }
                    if (d3 == -0.0) {
                        d3 = -1.0E-4;
                    }
                    if (d4 == -0.0) {
                        d4 = -1.0E-4;
                    }
                    if (d5 == -0.0) {
                        d5 = -1.0E-4;
                    }
                    if (d3 < d4 && d3 < d5) {
                        enumfacing = i > l ? EnumFacing.WEST : EnumFacing.EAST;
                        vec31 = new Vec3(d0, vec31.yCoord + d7 * d3, vec31.zCoord + d8 * d3);
                    } else if (d4 < d5) {
                        enumfacing = j > i1 ? EnumFacing.DOWN : EnumFacing.UP;
                        vec31 = new Vec3(vec31.xCoord + d6 * d4, d1, vec31.zCoord + d8 * d4);
                    } else {
                        enumfacing = k > j1 ? EnumFacing.NORTH : EnumFacing.SOUTH;
                        vec31 = new Vec3(vec31.xCoord + d6 * d5, vec31.yCoord + d7 * d5, d2);
                    }
                    l = MathHelper.floor_double(vec31.xCoord) - (enumfacing == EnumFacing.EAST ? 1 : 0);
                    i1 = MathHelper.floor_double(vec31.yCoord) - (enumfacing == EnumFacing.UP ? 1 : 0);
                    j1 = MathHelper.floor_double(vec31.zCoord) - (enumfacing == EnumFacing.SOUTH ? 1 : 0);
                    blockpos = new BlockPos(l, i1, j1);
                    IBlockState iblockstate1 = getBlockState(blockpos);
                    Block block1 = iblockstate1.getBlock();
                    if (visualize) blockPosConcurrentLinkedQueue.add(blockpos);
                    if (ignoreBlockWithoutBoundingBox && block1.getCollisionBoundingBox(mc.theWorld, blockpos, iblockstate1) == null) continue;
                    if (predicate.test(block1)) continue;
                    if (block1.canCollideCheck(iblockstate1, stopOnLiquid)) {
                        MovingObjectPosition movingobjectposition1 = collisionRayTrace(block1, blockpos, vec31, vec32, fullBlocks);
                        if (movingobjectposition1 == null) continue;
                        return movingobjectposition1;
                    }
                    movingobjectposition2 = new MovingObjectPosition(MovingObjectPosition.MovingObjectType.MISS, vec31, enumfacing, blockpos);
                }
                return returnLastUncollidableBlock ? movingobjectposition2 : null;
            }
            return null;
        }
        return null;
    }

    public static MovingObjectPosition collisionRayTrace(Block block, BlockPos pos, Vec3 start, Vec3 end, boolean fullBlocks) {
        start = start.addVector(-pos.getX(), -pos.getY(), -pos.getZ());
        end = end.addVector(-pos.getX(), -pos.getY(), -pos.getZ());

        Vec3 vec3 = start.getIntermediateWithXValue(end, fullBlocks ? 0.0 : block.getBlockBoundsMinX());
        Vec3 vec31 = start.getIntermediateWithXValue(end, fullBlocks ? 1.0 : block.getBlockBoundsMaxX());
        Vec3 vec32 = start.getIntermediateWithYValue(end, fullBlocks ? 0.0 : block.getBlockBoundsMinY());
        Vec3 vec33 = start.getIntermediateWithYValue(end, fullBlocks ? 1.0 : block.getBlockBoundsMaxY());
        Vec3 vec34 = start.getIntermediateWithZValue(end, fullBlocks ? 0.0 : block.getBlockBoundsMinZ());
        Vec3 vec35 = start.getIntermediateWithZValue(end, fullBlocks ? 1.0 : block.getBlockBoundsMaxZ());

        if (!isVecInsideYZBounds(block, vec3, fullBlocks)) {
            vec3 = null;
        }
        if (!isVecInsideYZBounds(block, vec31, fullBlocks)) {
            vec31 = null;
        }
        if (!isVecInsideXZBounds(block, vec32, fullBlocks)) {
            vec32 = null;
        }
        if (!isVecInsideXZBounds(block, vec33, fullBlocks)) {
            vec33 = null;
        }
        if (!isVecInsideXYBounds(block, vec34, fullBlocks)) {
            vec34 = null;
        }
        if (!isVecInsideXYBounds(block, vec35, fullBlocks)) {
            vec35 = null;
        }

        Vec3 vec36 = null;

        if (vec3 != null) {
            vec36 = vec3;
        }
        if (vec31 != null && (vec36 == null || start.squareDistanceTo(vec31) < start.squareDistanceTo(vec36))) {
            vec36 = vec31;
        }
        if (vec32 != null && (vec36 == null || start.squareDistanceTo(vec32) < start.squareDistanceTo(vec36))) {
            vec36 = vec32;
        }
        if (vec33 != null && (vec36 == null || start.squareDistanceTo(vec33) < start.squareDistanceTo(vec36))) {
            vec36 = vec33;
        }
        if (vec34 != null && (vec36 == null || start.squareDistanceTo(vec34) < start.squareDistanceTo(vec36))) {
            vec36 = vec34;
        }
        if (vec35 != null && (vec36 == null || start.squareDistanceTo(vec35) < start.squareDistanceTo(vec36))) {
            vec36 = vec35;
        }
        if (vec36 == null) {
            return null;
        }
        EnumFacing enumfacing = null;
        if (vec36 == vec3) {
            enumfacing = EnumFacing.WEST;
        }
        if (vec36 == vec31) {
            enumfacing = EnumFacing.EAST;
        }
        if (vec36 == vec32) {
            enumfacing = EnumFacing.DOWN;
        }
        if (vec36 == vec33) {
            enumfacing = EnumFacing.UP;
        }
        if (vec36 == vec34) {
            enumfacing = EnumFacing.NORTH;
        }
        if (vec36 == vec35) {
            enumfacing = EnumFacing.SOUTH;
        }
        return new MovingObjectPosition(vec36.addVector(pos.getX(), pos.getY(), pos.getZ()), enumfacing, pos);
    }

    private static boolean isVecInsideYZBounds(Block block, Vec3 point, boolean fullBlocks) {
        return point != null && point.yCoord >= (fullBlocks ? 0.0 : block.getBlockBoundsMinY()) && point.yCoord <= (fullBlocks ? 1.0 : block.getBlockBoundsMaxY()) && point.zCoord >= (fullBlocks ? 0.0 : block.getBlockBoundsMinZ()) && point.zCoord <= (fullBlocks ? 1.0 : block.getBlockBoundsMaxZ());
    }

    private static boolean isVecInsideXZBounds(Block block, Vec3 point, boolean fullBlocks) {
        return point != null && point.xCoord >= (fullBlocks ? 0.0 : block.getBlockBoundsMinX()) && point.xCoord <= (fullBlocks ? 1.0 : block.getBlockBoundsMaxX()) && point.zCoord >= (fullBlocks ? 0.0 : block.getBlockBoundsMinZ()) && point.zCoord <= (fullBlocks ? 1.0 : block.getBlockBoundsMaxZ());
    }

    private static boolean isVecInsideXYBounds(Block block, Vec3 point, boolean fullBlocks) {
        return point != null && point.xCoord >= (fullBlocks ? 0.0 : block.getBlockBoundsMinX()) && point.xCoord <= (fullBlocks ? 1.0 : block.getBlockBoundsMaxX()) && point.yCoord >= (fullBlocks ? 0.0 : block.getBlockBoundsMinY()) && point.yCoord <= (fullBlocks ? 1.0 : block.getBlockBoundsMaxY());
    }

    private static IBlockState getBlockState(BlockPos blockPos) {
        if (GumTuneClient.mc.theWorld == null) return null;
        return GumTuneClient.mc.theWorld.getBlockState(blockPos);
    }
}
