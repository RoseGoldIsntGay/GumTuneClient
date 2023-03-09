package rosegold.gumtuneclient.utils.objects;

import net.minecraft.util.BlockPos;

public class HighlightBlock {
    private final BlockPos blockPos;
    private final String name;

    public HighlightBlock(BlockPos blockPos, String name) {
        this.blockPos = blockPos;
        this.name = name;
    }

    public BlockPos getBlockPos() {
        return blockPos;
    }

    public String getName() {
        return name;
    }
}
