package rosegold.gumtuneclient.utils.objects;

import com.google.gson.annotations.Expose;
import net.minecraft.block.state.IBlockState;

import java.awt.*;

public class ColorBlockState {
    @Expose
    public IBlockState blockState;
    @Expose
    public Color color;

    public ColorBlockState(IBlockState blockState, Color color) {
        this.blockState = blockState;
        this.color = color;
    }
}
