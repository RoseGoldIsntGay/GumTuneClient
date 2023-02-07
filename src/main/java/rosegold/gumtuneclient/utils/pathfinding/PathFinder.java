package rosegold.gumtuneclient.utils.pathfinding;

import net.minecraft.util.BlockPos;
import net.minecraft.util.Vec3;

import java.util.ArrayList;

public class PathFinder {
    public static ArrayList<Vec3> path;
    public static BlockPos goal;
    public static boolean calculating = false;


    public static void setup(BlockPos from, BlockPos to, double minDistance, int loops) {
        calculating = true;
        AStarCustomPathfinder pathfinder = new AStarCustomPathfinder(new Vec3(from), new Vec3(to), minDistance);
        pathfinder.compute(loops, 1);
        path = pathfinder.getPath();
        goal = to;
        calculating = false;
    }

    public static Vec3 getCurrent() {
        return path != null && !path.isEmpty() ? path.get(0) : null;
    }

    public static boolean hasNext() {
        return path != null && path.size() > 1;
    }

    public static Vec3 getNext() {
        return path.get(1);
    }

    public static boolean goNext() {
        if (path != null && path.size() > 1) {
            path.remove(0);
            return true;
        } else {
            path = null;
            return false;
        }
    }

    public static boolean hasPath() {
        return path != null && !path.isEmpty();
    }

    public static Vec3 getGoal() {
        return path.get(path.size() - 1);
    }

    public static void reset() {
        path = null;
        goal = null;
    }
}
