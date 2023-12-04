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

    public static boolean canBlockBeSeen(BlockPos blockPos, double dist, Vec3 offset, Predicate<? super BlockPos> predicate) {
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

    public static MovingObjectPosition rayTraceBlocks(Vec3 vec31, Vec3 vec32, boolean stopOnLiquid, boolean ignoreBlockWithoutBoundingBox, boolean returnLastUncollidableBlock, Predicate<? super BlockPos> predicate) {
        return rayTraceBlocks(vec31, vec32, stopOnLiquid, ignoreBlockWithoutBoundingBox, returnLastUncollidableBlock, predicate, false, false);
    }

    public static MovingObjectPosition rayTraceBlocks(Vec3 vec31, Vec3 vec32, boolean stopOnLiquid, boolean ignoreBlockWithoutBoundingBox, boolean returnLastUncollidableBlock, Predicate<? super BlockPos> predicate, boolean visualize) {
        return rayTraceBlocks(vec31, vec32, stopOnLiquid, ignoreBlockWithoutBoundingBox, returnLastUncollidableBlock, predicate, visualize, false);
    }

    public static MovingObjectPosition rayTraceBlocks(Vec3 from, Vec3 goal, boolean stopOnLiquid, boolean ignoreBlockWithoutBoundingBox, boolean returnLastUncollidableBlock, Predicate<? super BlockPos> predicate, boolean visualize, boolean fullBlocks) {
        if (!(Double.isNaN(from.xCoord) || Double.isNaN(from.yCoord) || Double.isNaN(from.zCoord))) {
            if (!(Double.isNaN(goal.xCoord) || Double.isNaN(goal.yCoord) || Double.isNaN(goal.zCoord))) {
                MovingObjectPosition movingobjectposition;
                int xGoal = MathHelper.floor_double(goal.xCoord);
                int yGoal = MathHelper.floor_double(goal.yCoord);
                int zGoal = MathHelper.floor_double(goal.zCoord);
                int xCurrent = MathHelper.floor_double(from.xCoord);
                int yCurrent = MathHelper.floor_double(from.yCoord);
                int zCurrent = MathHelper.floor_double(from.zCoord);
                BlockPos blockpos = new BlockPos(xCurrent, yCurrent, zCurrent);
                IBlockState iblockstate = getBlockState(blockpos);
                Block block = iblockstate.getBlock();
                if (visualize) blockPosConcurrentLinkedQueue.add(blockpos);
                if (!predicate.test(blockpos) && (!ignoreBlockWithoutBoundingBox || block.getCollisionBoundingBox(mc.theWorld, blockpos, iblockstate) != null) && block.canCollideCheck(iblockstate, stopOnLiquid) && (movingobjectposition = collisionRayTrace(block, blockpos, from, goal, fullBlocks)) != null) {
                    return movingobjectposition;
                }
                MovingObjectPosition movingobjectposition2 = null;
                int k1 = 200;
                while (k1-- >= 0) {
                    EnumFacing enumfacing;
                    if (Double.isNaN(from.xCoord) || Double.isNaN(from.yCoord) || Double.isNaN(from.zCoord)) {
                        return null;
                    }
                    if (xCurrent == xGoal && yCurrent == yGoal && zCurrent == zGoal) {
                        return returnLastUncollidableBlock ? movingobjectposition2 : null;
                    }
                    boolean flagX = true;
                    boolean flagY = true;
                    boolean flagZ = true;
                    double fullNextX = 999.0;
                    double fullNextY = 999.0;
                    double fullNextZ = 999.0;
                    if (xGoal > xCurrent) {
                        fullNextX = (double) xCurrent + 1.0;
                    } else if (xGoal < xCurrent) {
                        fullNextX = (double) xCurrent + 0.0;
                    } else {
                        flagX = false;
                    }
                    if (yGoal > yCurrent) {
                        fullNextY = (double) yCurrent + 1.0;
                    } else if (yGoal < yCurrent) {
                        fullNextY = (double) yCurrent + 0.0;
                    } else {
                        flagY = false;
                    }
                    if (zGoal > zCurrent) {
                        fullNextZ = (double) zCurrent + 1.0;
                    } else if (zGoal < zCurrent) {
                        fullNextZ = (double) zCurrent + 0.0;
                    } else {
                        flagZ = false;
                    }
                    double nextX = 999.0;
                    double nextY = 999.0;
                    double nextZ = 999.0;
                    double dx = goal.xCoord - from.xCoord;
                    double dy = goal.yCoord - from.yCoord;
                    double dz = goal.zCoord - from.zCoord;
                    if (flagX) {
                        nextX = (fullNextX - from.xCoord) / dx;
                    }
                    if (flagY) {
                        nextY = (fullNextY - from.yCoord) / dy;
                    }
                    if (flagZ) {
                        nextZ = (fullNextZ - from.zCoord) / dz;
                    }
                    if (nextX == -0.0) {
                        nextX = -1.0E-4;
                    }
                    if (nextY == -0.0) {
                        nextY = -1.0E-4;
                    }
                    if (nextZ == -0.0) {
                        nextZ = -1.0E-4;
                    }
                    if (nextX < nextY && nextX < nextZ) {
                        enumfacing = xGoal > xCurrent ? EnumFacing.WEST : EnumFacing.EAST;
                        from = new Vec3(fullNextX, from.yCoord + dy * nextX, from.zCoord + dz * nextX);
                    } else if (nextY < nextZ) {
                        enumfacing = yGoal > yCurrent ? EnumFacing.DOWN : EnumFacing.UP;
                        from = new Vec3(from.xCoord + dx * nextY, fullNextY, from.zCoord + dz * nextY);
                    } else {
                        enumfacing = zGoal > zCurrent ? EnumFacing.NORTH : EnumFacing.SOUTH;
                        from = new Vec3(from.xCoord + dx * nextZ, from.yCoord + dy * nextZ, fullNextZ);
                    }
                    xCurrent = MathHelper.floor_double(from.xCoord) - (enumfacing == EnumFacing.EAST ? 1 : 0);
                    yCurrent = MathHelper.floor_double(from.yCoord) - (enumfacing == EnumFacing.UP ? 1 : 0);
                    zCurrent = MathHelper.floor_double(from.zCoord) - (enumfacing == EnumFacing.SOUTH ? 1 : 0);
                    blockpos = new BlockPos(xCurrent, yCurrent, zCurrent);
                    IBlockState iblockstate1 = getBlockState(blockpos);
                    Block block1 = iblockstate1.getBlock();
                    if (visualize) blockPosConcurrentLinkedQueue.add(blockpos);
                    if (ignoreBlockWithoutBoundingBox && block1.getCollisionBoundingBox(mc.theWorld, blockpos, iblockstate1) == null)
                        continue;
                    if (predicate.test(blockpos)) continue;
                    if (block1.canCollideCheck(iblockstate1, stopOnLiquid)) {
                        MovingObjectPosition movingobjectposition1 = collisionRayTrace(block1, blockpos, from, goal, fullBlocks);
                        if (movingobjectposition1 == null) continue;
                        return movingobjectposition1;
                    }
                    movingobjectposition2 = new MovingObjectPosition(MovingObjectPosition.MovingObjectType.MISS, from, enumfacing, blockpos);
                }
                return returnLastUncollidableBlock ? movingobjectposition2 : null;
            }
            return null;
        }
        return null;
    }

    public static ArrayList<BlockPos> rayTraceBlockList(Vec3 from, Vec3 goal, boolean ignoreBlockWithoutBoundingBox, Predicate<? super BlockPos> predicate, boolean fullBlocks) {
        ArrayList<BlockPos> blockPosArrayList = new ArrayList<>();

        if (Double.isNaN(from.xCoord) || Double.isNaN(from.yCoord) || Double.isNaN(from.zCoord)) return blockPosArrayList;
        if (Double.isNaN(goal.xCoord) || Double.isNaN(goal.yCoord) || Double.isNaN(goal.zCoord)) return blockPosArrayList;

        int xGoal = MathHelper.floor_double(goal.xCoord);
        int yGoal = MathHelper.floor_double(goal.yCoord);
        int zGoal = MathHelper.floor_double(goal.zCoord);

        int xCurrent = MathHelper.floor_double(from.xCoord);
        int yCurrent = MathHelper.floor_double(from.yCoord);
        int zCurrent = MathHelper.floor_double(from.zCoord);

        BlockPos blockpos = new BlockPos(xCurrent, yCurrent, zCurrent);
        IBlockState iBlockState = getBlockState(blockpos);
        Block block = iBlockState.getBlock();

        if (!predicate.test(blockpos)) {
            if (!ignoreBlockWithoutBoundingBox || block.getCollisionBoundingBox(mc.theWorld, blockpos, iBlockState) != null) {
                blockPosArrayList.add(blockpos);
            }
        }

        for (int step = 200; step >= 0; step--) {
            if (xCurrent == xGoal && yCurrent == yGoal && zCurrent == zGoal) {
                return blockPosArrayList;
            }

            EnumFacing enumfacing;

            double dx = goal.xCoord - from.xCoord;
            double dy = goal.yCoord - from.yCoord;
            double dz = goal.zCoord - from.zCoord;

            double fullNextX = 999;
            double fullNextY = 999;
            double fullNextZ = 999;

            boolean flagX = true;
            boolean flagY = true;
            boolean flagZ = true;

            double nextX = 999;
            double nextY = 999;
            double nextZ = 999;

            if (xGoal > xCurrent) {
                fullNextX = (double) xCurrent + 1;
            } else if (xGoal < xCurrent) {
                fullNextX = (double) xCurrent + 0;
            } else {
                flagX = false;
            }

            if (yGoal > yCurrent) {
                fullNextY = (double) yCurrent + 1;
            } else if (yGoal < yCurrent) {
                fullNextY = (double) yCurrent + 0;
            } else {
                flagY = false;
            }

            if (zGoal > zCurrent) {
                fullNextZ = (double) zCurrent + 1;
            } else if (zGoal < zCurrent) {
                fullNextZ = (double) zCurrent + 0;
            } else {
                flagZ = false;
            }

            if (flagX) {
                nextX = (fullNextX - from.xCoord) / dx;
            }
            if (flagY) {
                nextY = (fullNextY - from.yCoord) / dy;
            }
            if (flagZ) {
                nextZ = (fullNextZ - from.zCoord) / dz;
            }

            if (nextX == -0.0) {
                nextX = -1.0E-4;
            }
            if (nextY == -0.0) {
                nextY = -1.0E-4;
            }
            if (nextZ == -0.0) {
                nextZ = -1.0E-4;
            }

            if (nextX < nextY && nextX < nextZ) {
                enumfacing = xGoal > xCurrent ? EnumFacing.WEST : EnumFacing.EAST;
                from = new Vec3(fullNextX, from.yCoord + dy * nextX, from.zCoord + dz * nextX);
            } else if (nextY < nextZ) {
                enumfacing = yGoal > yCurrent ? EnumFacing.DOWN : EnumFacing.UP;
                from = new Vec3(from.xCoord + dx * nextY, fullNextY, from.zCoord + dz * nextY);
            } else {
                enumfacing = zGoal > zCurrent ? EnumFacing.NORTH : EnumFacing.SOUTH;
                from = new Vec3(from.xCoord + dx * nextZ, from.yCoord + dy * nextZ, fullNextZ);
            }

            xCurrent = MathHelper.floor_double(from.xCoord) - (enumfacing == EnumFacing.EAST ? 1 : 0);
            yCurrent = MathHelper.floor_double(from.yCoord) - (enumfacing == EnumFacing.UP ? 1 : 0);
            zCurrent = MathHelper.floor_double(from.zCoord) - (enumfacing == EnumFacing.SOUTH ? 1 : 0);

            blockpos = new BlockPos(xCurrent, yCurrent, zCurrent);
            iBlockState = getBlockState(blockpos);
            block = iBlockState.getBlock();

            if (!predicate.test(blockpos)) {
                if (!ignoreBlockWithoutBoundingBox || block.getCollisionBoundingBox(mc.theWorld, blockpos, iBlockState) != null) {
                    blockPosArrayList.add(blockpos);
                }
            }
        }

        return blockPosArrayList;
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
