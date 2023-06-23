package rosegold.gumtuneclient.modules.mining;

import net.minecraft.block.BlockChest;
import net.minecraft.init.Blocks;
import net.minecraft.network.play.server.S2APacketParticles;
import net.minecraft.util.*;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import rosegold.gumtuneclient.GumTuneClient;
import rosegold.gumtuneclient.config.GumTuneClientConfig;
import rosegold.gumtuneclient.events.PlayerMoveEvent;
import rosegold.gumtuneclient.events.PacketReceivedEvent;
import rosegold.gumtuneclient.utils.*;

import java.awt.*;
import java.util.ArrayList;

public class PowderChestSolver {

    public static Vec3 particle;
    public static BlockPos closestChest;
    private static boolean rotatingBack = false;
    private static long timestamp = 0;
    public static final ArrayList<BlockPos> solved = new ArrayList<>();

    @SubscribeEvent
    public void onTick(TickEvent.ClientTickEvent event) {
        if (!isEnabled()) return;
        if (event.phase == TickEvent.Phase.END) return;
        if (closestChest == null) {
            particle = null;
            if (!GumTuneClientConfig.powderChestSolverLegitMode) {
                closestChest = BlockUtils.getClosestBlock(4, 4, 4, this::isPowderChest);
            } else if (GumTuneClient.mc.objectMouseOver != null && GumTuneClient.mc.objectMouseOver.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK) {
                BlockPos blockPos = GumTuneClient.mc.objectMouseOver.getBlockPos();
                if (GumTuneClient.mc.theWorld.getBlockState(blockPos).getBlock() == Blocks.chest) {
                    closestChest = blockPos;
                }
            }
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
    public void receivePacket(PacketReceivedEvent event) {
        if (!isEnabled()) return;
        if (event.packet instanceof S2APacketParticles) {
            S2APacketParticles packet = (S2APacketParticles) event.packet;
            if (packet.getParticleType().equals(EnumParticleTypes.CRIT)) {
                Vec3 particlePos = new Vec3(packet.getXCoordinate(), packet.getYCoordinate(), packet.getZCoordinate());
                if (closestChest != null) {
                    if (particlePos.distanceTo(new Vec3(closestChest.getX() + 0.5, closestChest.getY() + 0.5, closestChest.getZ() + 0.5).add(VectorUtils.scaleVec(GumTuneClient.mc.theWorld.getBlockState(closestChest).getValue(BlockChest.FACING).getDirectionVec(), 0.5f))) < 0.45 && Math.abs(closestChest.getY() + 0.5 - particlePos.yCoord) < 0.5) {
                        if (GumTuneClientConfig.powderChestSolverSmoothRotations) {
                            RotationUtils.serverSmoothLook(RotationUtils.getRotation(particlePos), GumTuneClientConfig.powderChestRotationTime);
                        }
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
            if (GumTuneClientConfig.powderChestSolverSmoothRotations) {
                RotationUtils.serverSmoothLook(new RotationUtils.Rotation(GumTuneClient.mc.thePlayer.rotationPitch, GumTuneClient.mc.thePlayer.rotationYaw), GumTuneClientConfig.powderChestRotationTime);
                rotatingBack = true;
                timestamp = System.currentTimeMillis();
                ModUtils.sendMessage("started rotating back");
            }
            closestChest = null;
            particle = null;
        }
    }

    @SubscribeEvent
    public void onRender(RenderWorldLastEvent event) {
        if (!isEnabled()) return;
        if (particle != null) {
            RenderUtils.renderSmallBox(particle, Color.RED.getRGB());
            RenderUtils.drawLine(GumTuneClient.mc.thePlayer.getPositionEyes(event.partialTicks), particle, 1, event.partialTicks);
        }
        if (closestChest != null) {
            RenderUtils.renderEspBox(closestChest, event.partialTicks, new Color(250, 150, 0).getRGB());
        }
    }

    @SubscribeEvent(priority = EventPriority.NORMAL)
    public void onUpdatePre(PlayerMoveEvent.Pre pre) {
        if (!isEnabled()) return;
        if (particle == null && !rotatingBack) return;
        if (GumTuneClientConfig.powderChestSolverSmoothRotations) {
            RotationUtils.updateServerLook();
            if (rotatingBack && System.currentTimeMillis() - timestamp > GumTuneClientConfig.powderChestRotationTime) {
                ModUtils.sendMessage("finished rotating back");
                rotatingBack = false;
            }
        } else {
            RotationUtils.look(RotationUtils.getRotation(particle));
        }
    }

    @SubscribeEvent
    public void clear(WorldEvent.Unload event) {
        solved.clear();
        particle = null;
        closestChest = null;
    }

    private boolean isEnabled() {
        return GumTuneClientConfig.powderChestSolver && GumTuneClient.mc.thePlayer != null && GumTuneClient.mc.theWorld != null && LocationUtils.currentIsland == LocationUtils.Island.CRYSTAL_HOLLOWS;
    }

    private boolean isPowderChest(BlockPos blockPos) {
        return GumTuneClient.mc.theWorld.getBlockState(blockPos).getBlock() == Blocks.chest && !solved.contains(blockPos);
    }
}
