package rosegold.gumtuneclient.utils.pathfinding;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.util.Vec3;
import rosegold.gumtuneclient.GumTuneClient;
import rosegold.gumtuneclient.modules.player.PathFinding;
import rosegold.gumtuneclient.utils.ModUtils;
import rosegold.gumtuneclient.utils.RaytracingUtils;
import rosegold.gumtuneclient.utils.VectorUtils;

import java.util.ArrayList;
import java.util.Comparator;

public class AStarCustomPathfinder {
    private final Vec3 startVec3;
    private final Vec3 endVec3;
    private ArrayList<Vec3> path = new ArrayList<>();
    private final ArrayList<Hub> hubs = new ArrayList<>();
    private final ArrayList<Hub> hubsToWork = new ArrayList<>();
    private final double minDistanceSquared;
    public static long counter = 0;

    private static final Vec3[] flatCardinalDirections = {
            new Vec3(1, 0, 0),
            new Vec3(-1, 0, 0),
            new Vec3(0, 0, 1),
            new Vec3(0, 0, -1)
    };

    public AStarCustomPathfinder(Vec3 startVec3, Vec3 endVec3, double minDistanceSquared) {
        this.startVec3 = VectorUtils.floorVec(startVec3);
        this.endVec3 = VectorUtils.floorVec(endVec3);
        this.minDistanceSquared = minDistanceSquared;
    }

    public ArrayList<Vec3> getPath() {
        return path;
    }

    public void compute() {
        compute(2000, 1);
    }

    public void compute(int loops, int depth) {
        counter = 0;
        PathFinding.renderHubs.clear();
        path.clear();
        hubsToWork.clear();
        ArrayList<Vec3> initPath = new ArrayList<>();
        initPath.add(startVec3);
        hubsToWork.add(new Hub(startVec3, null, initPath, startVec3.squareDistanceTo(endVec3), 0.0, 0.0));
        search:
        for (int i = 0; i < loops; ++i) {
            hubsToWork.sort(new CompareHub());
            int j = 0;
            if (hubsToWork.size() == 0) {
                break;
            }
            for (Hub hub : new ArrayList<>(hubsToWork)) {
                if (++j > depth) {
                    break;
                }

                hubsToWork.remove(hub);
                hubs.add(hub);

                PathFinding.renderHubs.put(new BlockPos(VectorUtils.ceilVec(hub.getLoc())), 1);

                for (BlockPos blockPos : RaytracingUtils.getAllTeleportableBlocksNew(VectorUtils.ceilVec(hub.getLoc()).addVector(0.5, 1.62 - 0.08 + 1, 0.5), 16)) {
                    Vec3 loc = new Vec3(blockPos);
                    if (addHub(hub, loc, 0)) {
                        break search;
                    }
                }
            }
        }
        ModUtils.sendMessage("Done calculating path, searched " + PathFinding.renderHubs.size() + " blocks, took: " + counter + "ms with an average of " + Math.round((double) counter / PathFinding.renderHubs.size() * 100) / 100 + "ms per block");
        hubs.sort(new CompareHub());
        path = hubs.get(0).getPath();
    }

    public static boolean checkPositionValidity(Vec3 loc) {
        return checkPositionValidity(new BlockPos((int) loc.xCoord, (int) loc.yCoord, (int) loc.zCoord));
    }

    public static boolean checkPositionValidity(BlockPos blockPos) {
        return canTeleportTo(blockPos);
    }

    private static boolean canVecBeSeen(Vec3 from, Vec3 to) {
        return RaytracingUtils.canVecBeSeenFromVec(from.addVector(0.5, 0.5, 0.5), to.addVector(0.5, 0.5, 0.5), 0.1f);
    }

    private static boolean canTeleportTo(BlockPos blockPos) {
        IBlockState blockState = GumTuneClient.mc.theWorld.getBlockState(blockPos);
        Block block = blockState.getBlock();
        return block.isCollidable() && block != Blocks.carpet && block != Blocks.skull &&
                block.getCollisionBoundingBox(GumTuneClient.mc.theWorld, blockPos, blockState) != null &&
                block != Blocks.wall_sign && block != Blocks.standing_sign &&
                GumTuneClient.mc.theWorld.getBlockState(blockPos.add(0, 1, 0)).getBlock() == Blocks.air &&
                GumTuneClient.mc.theWorld.getBlockState(blockPos.add(0, 2, 0)).getBlock() == Blocks.air;
    }

    public Hub isHubExisting(Vec3 loc) {
        for (Hub hub : hubs) {
            if (hub.getLoc().xCoord == loc.xCoord && hub.getLoc().yCoord == loc.yCoord && hub.getLoc().zCoord == loc.zCoord) {
                return hub;
            }
        }
        for (Hub hub : hubsToWork) {
            if (hub.getLoc().xCoord == loc.xCoord && hub.getLoc().yCoord == loc.yCoord && hub.getLoc().zCoord == loc.zCoord) {
                return hub;
            }
        }
        return null;
    }

    public boolean addHub(Hub parent, Vec3 loc, double cost) {
        Hub existingHub = isHubExisting(loc);
        double totalCost = cost;
        if (parent != null) {
            totalCost += parent.getTotalCost();
        }
        if (existingHub == null) {
            if ((loc.xCoord == endVec3.xCoord && loc.yCoord == endVec3.yCoord && loc.zCoord == endVec3.zCoord) || (minDistanceSquared != 0.0 && loc.squareDistanceTo(endVec3) <= minDistanceSquared)) {
                path.clear();
                (path = parent.getPath()).add(loc);
                return true;
            }
            ArrayList<Vec3> path = new ArrayList<>(parent.getPath());
            path.add(loc);
            hubsToWork.add(new Hub(loc, parent, path, loc.squareDistanceTo(endVec3), cost, totalCost));
        } else if (existingHub.getCost() > cost) {
            ArrayList<Vec3> path = new ArrayList<>(parent.getPath());
            path.add(loc);
            existingHub.setLoc(loc);
            existingHub.setParent(parent);
            existingHub.setPath(path);
            existingHub.setSquareDistanceToFromTarget(loc.squareDistanceTo(endVec3));
            existingHub.setCost(cost);
            existingHub.setTotalCost(totalCost);
        }
        return false;
    }

    private static class Hub {
        private Vec3 loc;
        private Hub parent;
        private ArrayList<Vec3> path;
        private double squareDistanceToFromTarget;
        private double cost;
        private double totalCost;

        public Hub(Vec3 loc, Hub parent, ArrayList<Vec3> path, double squareDistanceToFromTarget, double cost, double totalCost) {
            this.loc = loc;
            this.parent = parent;
            this.path = path;
            this.squareDistanceToFromTarget = squareDistanceToFromTarget;
            this.cost = cost;
            this.totalCost = totalCost;
        }

        public Vec3 getLoc() {
            return loc;
        }

        public Hub getParent() {
            return parent;
        }

        public ArrayList<Vec3> getPath() {
            return path;
        }

        public double getSquareDistanceToFromTarget() {
            return squareDistanceToFromTarget;
        }

        public double getCost() {
            return cost;
        }

        public void setLoc(Vec3 loc) {
            this.loc = loc;
        }

        public void setParent(Hub parent) {
            this.parent = parent;
        }

        public void setPath(ArrayList<Vec3> path) {
            this.path = path;
        }

        public void setSquareDistanceToFromTarget(double squareDistanceToFromTarget) {
            this.squareDistanceToFromTarget = squareDistanceToFromTarget;
        }

        public void setCost(double cost) {
            this.cost = cost;
        }

        public double getTotalCost() {
            return totalCost;
        }

        public void setTotalCost(double totalCost) {
            this.totalCost = totalCost;
        }
    }

    public static class CompareHub implements Comparator<Hub> {
        @Override
        public int compare(Hub o1, Hub o2) {
            return (int) (o1.getSquareDistanceToFromTarget() + o1.getTotalCost() - (o2.getSquareDistanceToFromTarget() + o2.getTotalCost()));
        }
    }
}