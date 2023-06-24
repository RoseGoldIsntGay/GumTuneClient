package rosegold.gumtuneclient.modules.combat;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Vec3;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import rosegold.gumtuneclient.GumTuneClient;
import rosegold.gumtuneclient.config.GumTuneClientConfig;
import rosegold.gumtuneclient.events.BlockChangeEvent;
import rosegold.gumtuneclient.events.PlayerMoveEvent;
import rosegold.gumtuneclient.utils.*;

import java.util.ArrayList;
import java.util.HashSet;

public class AntiScribe {
    public static final HashSet<BlockPos> coalBlocks = new HashSet<>();

    @SubscribeEvent
    public void onBlockChange(BlockChangeEvent event) {
        if (!GumTuneClientConfig.antiScribe) return;
        if (LocationUtils.currentIsland != LocationUtils.Island.THE_RIFT) return;
        BlockPos blockPos = event.pos;
        IBlockState blockState = event.update;

        coalBlocks.remove(blockPos);

        if (ScoreboardUtils.scoreboardContains("Village Plaza") || ScoreboardUtils.scoreboardContains("West Village")) {
            if (blockState.getBlock() == Blocks.coal_block) {
                coalBlocks.add(blockPos);
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.NORMAL)
    public void onUpdatePre(PlayerMoveEvent.Pre pre) {
        if (!GumTuneClientConfig.antiScribe) return;
        if (LocationUtils.currentIsland != LocationUtils.Island.THE_RIFT) return;
        if (coalBlocks.isEmpty()) return;

        BlockPos closestCoal = coalBlocks.stream().min((o1, o2) -> (int) (o1.distanceSq(GumTuneClient.mc.thePlayer.getPosition()) - o2.distanceSq(GumTuneClient.mc.thePlayer.getPosition()))).orElse(null);

        ArrayList<Vec3> possibleSpots = BlockUtils.getViablePointsOnBlock(closestCoal, EnumFacing.UP, 20, true, false);
        if (possibleSpots.size() > 0) {
            RotationUtils.look(RotationUtils.getRotation(possibleSpots.get(0)));
        }
    }
}
