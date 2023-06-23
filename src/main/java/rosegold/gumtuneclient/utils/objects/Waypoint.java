package rosegold.gumtuneclient.utils.objects;

import com.google.gson.annotations.Expose;
import net.minecraft.util.BlockPos;

public class Waypoint {
    @Expose
    public String name;
    @Expose
    public int x;
    @Expose
    public int y;
    @Expose
    public int z;

    public Waypoint(String name, int x, int y, int z) {
        this.name = name;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Waypoint(String name, BlockPos pos) {
        this.name = name;
        this.x = pos.getX();
        this.y = pos.getY();
        this.z = pos.getZ();
    }

    public boolean equals(BlockPos blockPos) {
        return blockPos.getX() == this.x && blockPos.getY() == this.y && blockPos.getZ() == this.z;
    }
}