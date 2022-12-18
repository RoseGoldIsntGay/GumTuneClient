package rosegold.gumtuneclient.modules.world;

import net.minecraft.init.Blocks;
import net.minecraft.network.play.server.S2APacketParticles;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.Vec3;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import rosegold.gumtuneclient.GumTuneClient;
import rosegold.gumtuneclient.config.GumTuneClientConfig;
import rosegold.gumtuneclient.events.PlayerMoveEvent;
import rosegold.gumtuneclient.events.ReceivePacketEvent;
import rosegold.gumtuneclient.utils.BlockUtils;
import rosegold.gumtuneclient.utils.RenderUtils;
import rosegold.gumtuneclient.utils.RotationUtils;

import java.awt.*;
import java.util.ArrayList;

public class PowderChestSolver {

    private static Vec3 particle;
    private static BlockPos closestChest;
    private final ArrayList<BlockPos> solved = new ArrayList<>();

    @SubscribeEvent
    public void onTick(TickEvent.ClientTickEvent event) {
        if (!isEnabled()) return;
        if (event.phase == TickEvent.Phase.END) return;
        if (closestChest == null) {
            particle = null;
            closestChest = BlockUtils.getClosestBlock(4, 4, 4, this::isPowderChest);
        }

        if (closestChest != null) {
            if (GumTuneClient.mc.thePlayer.getPositionEyes(1f).distanceTo(new Vec3(
                    closestChest.getX() + 0.5,
                    closestChest.getY() + 0.5,
                    closestChest.getZ() + 0.5)
            ) >  4 || !isPowderChest(closestChest)) {
                closestChest = null;
                particle = null;
            }
        }
    }

    @SubscribeEvent
    public void receivePacket(ReceivePacketEvent event) {
        if (!isEnabled()) return;
        if (event.packet instanceof S2APacketParticles) {
            S2APacketParticles packet = (S2APacketParticles) event.packet;
            if (packet.getParticleType().equals(EnumParticleTypes.CRIT)) {
                Vec3 particlePos = new Vec3(packet.getXCoordinate(), packet.getYCoordinate(), packet.getZCoordinate());
                if (closestChest != null) {
                    if (particlePos.distanceTo(new Vec3(closestChest.getX() + 0.5, closestChest.getY() + 0.5, closestChest.getZ() + 0.5)) < 1) {
                        particle = particlePos;
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public void onChat(ClientChatReceivedEvent event) {
        String message = event.message.getUnformattedText();
        if (!message.contains(":") && message.contains("You have successfully picked the lock on this chest!")) {
            solved.add(closestChest);
            closestChest = null;
            particle = null;
        }
    }

    @SubscribeEvent
    public void onRender(RenderWorldLastEvent event) {
        if (!isEnabled()) return;
        if (particle != null) {
            RenderUtils.renderSmallBox(particle, Color.RED.getRGB());
        }
        if (closestChest != null) {
            RenderUtils.renderEspBox(closestChest, event.partialTicks, new Color(150, 75, 0).getRGB());
        }
    }

    @SubscribeEvent(priority = EventPriority.NORMAL)
    public void onUpdatePre(PlayerMoveEvent.Pre pre) {
        if (!isEnabled()) return;
        RotationUtils.resetServerLook();
        if (particle != null) RotationUtils.serverLook(RotationUtils.getRotationToVec(particle));
    }

    @SubscribeEvent
    public void clear(WorldEvent.Unload event) {
        solved.clear();
        particle = null;
        closestChest = null;
    }

    private boolean isEnabled() {
        return GumTuneClientConfig.powderChestSolver && GumTuneClient.mc.thePlayer != null && GumTuneClient.mc.theWorld != null;
    }

    private boolean isPowderChest(BlockPos blockPos) {
        return !solved.contains(blockPos) && GumTuneClient.mc.theWorld.getBlockState(blockPos).getBlock() == Blocks.chest;
    }
}
