package rosegold.gumtuneclient.utils.objects;

import com.google.gson.annotations.Expose;
import net.minecraft.util.BlockPos;

import java.util.HashMap;
import java.util.Map;

public class WaypointList {
    @Expose
    public boolean enabled;
    @Expose
    public String name;
    @Expose
    public boolean showCoords;
    @Expose
    public HashMap<Integer, Waypoint> waypoints;


    public WaypointList(String name) {
        this.enabled = false;
        this.showCoords = false;
        this.waypoints = new HashMap<>();
        this.name = name;
    }

    public WaypointList(String name, boolean enabled, boolean showCoords, HashMap<Integer, Waypoint> waypoints) {
        this.enabled = enabled;
        this.showCoords = showCoords;
        this.waypoints = waypoints;
        this.name = name;
    }

    public Waypoint getValue(Waypoint waypointIn) {
        for (Waypoint waypoint : waypoints.values()) {
            if (waypoint.equals(waypointIn)) {
                return waypoint;
            }
        }

        return null;
    }

    public void removeValue(Waypoint waypointIn) {
        int key = -1;
        for (Map.Entry<Integer, Waypoint> entry : waypoints.entrySet()) {
            if (entry.getValue().equals(waypointIn)) {
                key = entry.getKey();
            }
        }

        if (key != -1) {
            waypoints.remove(key);
        }
    }

    public boolean containsValue(BlockPos blockPos) {
        for (Waypoint waypoint : this.waypoints.values()) {
            if (waypoint.equals(blockPos)) {
                return true;
            }
        }

        return false;
    }

    public Integer getKey(BlockPos blockPos) {
        for (Map.Entry<Integer, Waypoint> entry : this.waypoints.entrySet()) {
            if (entry.getValue().equals(blockPos)) {
                return entry.getKey();
            }
        }

        return null;
    }

    public Integer getEmptyIndex() {
        if (this.waypoints.isEmpty()) return 0;

        for (int i = 0; i < getLastIndex(); i++) {
            if (!this.waypoints.containsKey(i)) return i;
        }

        return getLastIndex() + 1;
    }


    public Integer getFirstIndex() {
        int firstIndex = 0;

        for (int index : this.waypoints.keySet()) {
            if (index < firstIndex) firstIndex = index;
        }

        return firstIndex;
    }

    public Integer getLastIndex() {
        int lastIndex = 0;

        for (int index : this.waypoints.keySet()) {
            if (index > lastIndex) lastIndex = index;
        }

        return lastIndex;
    }

    public Integer getNextIndex(int index) {
        int nextIndex = -1;
        for (Integer key : this.waypoints.keySet()) {
            if (key > index) {
                nextIndex = key;
                break;
            }
        }

        if (nextIndex == -1) {
            nextIndex = getFirstIndex();
        }

        return nextIndex;
    }
}
