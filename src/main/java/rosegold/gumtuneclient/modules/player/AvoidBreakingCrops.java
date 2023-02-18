package rosegold.gumtuneclient.modules.player;

import net.minecraft.block.BlockSand;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.BlockPos;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import rosegold.gumtuneclient.GumTuneClient;
import rosegold.gumtuneclient.utils.ModUtils;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class AvoidBreakingCrops {

    static final class Entry<K, V> implements Map.Entry<K, V> {
        private final K key;
        private V value;

        public Entry(K key, V value) {
            this.key = key;
            this.value = value;
        }

        @Override
        public K getKey() {
            return key;
        }

        @Override
        public V getValue() {
            return value;
        }

        @Override
        public V setValue(V value) {
            V old = this.value;
            this.value = value;
            return old;
        }
    }

    private static final ConcurrentHashMap<Entry<BlockPos, IBlockState>, Integer> queue = new ConcurrentHashMap<>();

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
        queue.put(new Entry<>(blockPos, blockState), GumTuneClient.mc.thePlayer.ticksExisted);
    }
}
