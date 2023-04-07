package rosegold.gumtuneclient.modules.farming;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.BlockPos;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import rosegold.gumtuneclient.GumTuneClient;

import java.util.AbstractMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class AvoidBreakingCrops {

    private static final ConcurrentHashMap<AbstractMap.SimpleEntry<BlockPos, IBlockState>, Integer> queue = new ConcurrentHashMap<>();

    @SubscribeEvent
    public void onTick(TickEvent.ClientTickEvent event) {
        if (GumTuneClient.mc.thePlayer == null || GumTuneClient.mc.theWorld == null) return;
        queue.entrySet().removeIf(entry -> {
            if (GumTuneClient.mc.thePlayer.ticksExisted > entry.getValue() + 8) {
                GumTuneClient.mc.theWorld.setBlockState(entry.getKey().getKey(), entry.getKey().getValue());
                return true;
            }
            return false;
        });
    }

    public static void addBlock(BlockPos blockPos, IBlockState blockState) {
        queue.put(new AbstractMap.SimpleEntry<>(blockPos, blockState), GumTuneClient.mc.thePlayer.ticksExisted);
    }
}
