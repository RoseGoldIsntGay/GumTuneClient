package rosegold.gumtuneclient.modules.mining;

import net.minecraft.init.Blocks;
import net.minecraft.network.play.server.S2APacketParticles;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
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
import rosegold.gumtuneclient.events.PacketReceivedEvent;
import rosegold.gumtuneclient.utils.BlockUtils;
import rosegold.gumtuneclient.utils.LocationUtils;
import rosegold.gumtuneclient.utils.RenderUtils;
import rosegold.gumtuneclient.utils.RotationUtils;
import net.minecraft.util.MovingObjectPosition;
import rosegold.gumtuneclient.utils.ModUtils;

import java.awt.*;
import java.util.ArrayList;


public class PowderChestSolver {
    public static Vec3 particle;
    private static BlockPos closestChest;
    private final ArrayList<BlockPos> solved = new ArrayList<>();
    private boolean toggle = true;
    @SubscribeEvent
    public void onTick(TickEvent.ClientTickEvent event) {
        if (!isEnabled()) return;
        if (event.phase == TickEvent.Phase.END) return;
        if (closestChest == null) {
            particle = null;
            closestChest = BlockUtils.getClosestBlock(4, 4, 4, this::isPowderChest);
        }

        if(onChest()){
            toggle=false;
            if (GumTuneClient.mc.objectMouseOver.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK) {
                BlockPos blockPos = GumTuneClient.mc.objectMouseOver.getBlockPos();
                IBlockState blockState = GumTuneClient.mc.theWorld.getBlockState(blockPos);
                Block block = blockState.getBlock();
                String blockName = Block.blockRegistry.getNameForObject(block).toString().toLowerCase();
                if (blockName.contains("chest")) {
                    toggle=true;
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
            }
        }
        else
        {
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
    }

    @SubscribeEvent
    public void receivePacket(PacketReceivedEvent event) {
        if (!isEnabled() || !toggle) return;
        if (event.packet instanceof S2APacketParticles) {
            S2APacketParticles packet = (S2APacketParticles) event.packet;
            if (packet.getParticleType().equals(EnumParticleTypes.CRIT)) {
                Vec3 particlePos = new Vec3(packet.getXCoordinate(), packet.getYCoordinate(), packet.getZCoordinate());
                if (closestChest != null) {
                    if (particlePos.distanceTo(new Vec3(closestChest.getX() + 0.5, closestChest.getY() + 0.5, closestChest.getZ() + 0.5)) < 1) {
                        RotationUtils.serverSmoothLook(RotationUtils.getRotation(particlePos), GumTuneClientConfig.powderChestRotationTime);
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
        if (!isEnabled()||!toggle) return;
        if (particle != null) {
            RenderUtils.renderSmallBox(particle, Color.RED.getRGB());
        }
        if (closestChest != null) {
            RenderUtils.renderEspBox(closestChest, event.partialTicks, new Color(150, 75, 0).getRGB());
        }
    }

    @SubscribeEvent(priority = EventPriority.NORMAL)
    public void onUpdatePre(PlayerMoveEvent.Pre pre) {
        if (!isEnabled()||!toggle) return;
        if (particle == null) return;
        RotationUtils.updateServerLook();
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


    private boolean onChest() {
        return GumTuneClientConfig.powderChestOnLook;
    }

    private boolean isPowderChest(BlockPos blockPos) {
        return GumTuneClient.mc.theWorld.getBlockState(blockPos).getBlock() == Blocks.chest && !solved.contains(blockPos);
    }
}
