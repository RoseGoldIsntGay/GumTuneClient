package rosegold.gumtuneclient.utils;

import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.util.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.function.Predicate;

import static rosegold.gumtuneclient.GumTuneClient.mc;

public class BlockUtils {

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
        BlockPos playerPos = player.getPosition().up();
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

    public static ArrayList<Vec3> getViablePointsOnBlock(BlockPos blockPos, EnumFacing enumFacing) {
        ArrayList<Vec3> points = new ArrayList<>();
        ArrayList<Vec3> pointsOnBlock = getPointsOnBlock(blockPos, enumFacing);

        for (Vec3 point : pointsOnBlock) {
            MovingObjectPosition mop = mc.theWorld.rayTraceBlocks(mc.thePlayer.getPositionEyes(1.0f), point);

            if (mop != null && mop.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK) {
                if (mop.getBlockPos().equals(blockPos) && point.distanceTo(mc.thePlayer.getPositionEyes(1.0f)) < mc.playerController.getBlockReachDistance()) {
                    points.add(point);
                }
            }
        }

        return points;
    }
}
