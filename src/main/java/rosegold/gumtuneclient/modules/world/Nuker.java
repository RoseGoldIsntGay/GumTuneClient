package rosegold.gumtuneclient.modules.world;

import net.minecraft.block.Block;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.init.Blocks;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.util.*;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import org.lwjgl.input.Keyboard;
import rosegold.gumtuneclient.GumTuneClient;
import rosegold.gumtuneclient.annotations.Module;
import rosegold.gumtuneclient.config.GumTuneClientConfig;
import rosegold.gumtuneclient.events.MillisecondEvent;
import rosegold.gumtuneclient.events.SecondEvent;
import rosegold.gumtuneclient.utils.BlockUtils;
import rosegold.gumtuneclient.utils.ModUtils;
import rosegold.gumtuneclient.utils.PlayerUtils;
import rosegold.gumtuneclient.utils.RenderUtils;

import java.awt.*;
import java.util.ArrayList;

@Module
public class Nuker {

    // TEMPORARILY JUST A HARDSTONE NUKER UNTIL ONECONFIG WORKS AGAIN LMAO

    private boolean enabled;
    private final ArrayList<BlockPos> broken = new ArrayList<>();
    private BlockPos blockPos;
    private static long lastBroken = 0;

    @SubscribeEvent
    public void onKey(InputEvent.KeyInputEvent event) {
        if (Keyboard.getEventKeyState()) return;
        int eventKey = Keyboard.getEventKey();
        ArrayList<Integer> keyBinds = GumTuneClientConfig.nukerKeyBind.getKeyBinds();
        if (keyBinds.size() > 0 && keyBinds.get(0) == eventKey) {
            enabled = !enabled;
            ModUtils.sendMessage((enabled ? "Enabled" : "Disabled") + " Nuker");
        }
    }

    @SubscribeEvent
    public void onSecond(SecondEvent event) {
        broken.clear();
    }

    @SubscribeEvent
    public void onMillisecond(MillisecondEvent event) {
        if (!isEnabled()) {
            if (broken.size() > 0) broken.clear();
            return;
        }
        if (event.timestamp - lastBroken > 1000f / GumTuneClientConfig.nukerSpeed) {
            lastBroken = event.timestamp;
            blockPos = BlockUtils.getClosestBlock(4, GumTuneClientConfig.nukerHeight, GumTuneClientConfig.nukerDepth, this::canMine);
            if (blockPos != null) {
                MovingObjectPosition objectMouseOver = GumTuneClient.mc.objectMouseOver;
                objectMouseOver.hitVec = new Vec3(blockPos);
                if (objectMouseOver.sideHit != null && GumTuneClient.mc.thePlayer != null) {
                    GumTuneClient.mc.thePlayer.sendQueue.addToSendQueue(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.START_DESTROY_BLOCK, blockPos, objectMouseOver.sideHit));
                }
                PlayerUtils.swingHand(objectMouseOver);
                broken.add(blockPos);
            }
        }
    }

    @SubscribeEvent
    public void onRender(RenderWorldLastEvent event) {
        if (!isEnabled()) return;
        RenderUtils.renderEspBox(blockPos, event.partialTicks, Color.GRAY.hashCode());
    }

    private boolean isEnabled() {
        return enabled && GumTuneClientConfig.nuker;
    }

    private boolean canMine(BlockPos blockPos) {
        if (getBlock(blockPos) == Blocks.stone && !broken.contains(blockPos)) {
            if (GumTuneClientConfig.nukerShape == 1) {
                EntityPlayerSP player = GumTuneClient.mc.thePlayer;
                EnumFacing axis = player.getHorizontalFacing();
                Vec3i ray = new Vec3i((int) Math.floor(player.posX), 0, (int) Math.floor(player.posZ));
                for (int i = 0; i < 5; i++) {
                    ray = addVector(ray, axis.getDirectionVec());
                    if (ray.getX() == blockPos.getX() && ray.getZ() == blockPos.getZ()) {
                        return true;
                    }
                }
                return false;
            }
            return true;
        }
        return false;
    }

    private Vec3i addVector(Vec3i vec1, Vec3i vec2) {
        return new Vec3i(vec1.getX() + vec2.getX(), vec1.getY() + vec2.getY(), vec1.getZ() + vec2.getZ());
    }

    private Block getBlock(BlockPos blockPos) {
        return GumTuneClient.mc.theWorld.getBlockState(blockPos).getBlock();
    }
}
