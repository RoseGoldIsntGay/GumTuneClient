package rosegold.gumtuneclient.modules.player;

import cc.polyfrost.oneconfig.events.event.ChatReceiveEvent;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityArmorStand;
import net.minecraft.entity.item.EntityItemFrame;
import net.minecraft.init.Blocks;
import net.minecraft.network.play.server.S29PacketSoundEffect;
import net.minecraft.network.play.server.S45PacketTitle;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Vec3;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import rosegold.gumtuneclient.GumTuneClient;
import rosegold.gumtuneclient.config.GumTuneClientConfig;
import rosegold.gumtuneclient.events.PacketReceivedEvent;
import rosegold.gumtuneclient.utils.*;
import rosegold.gumtuneclient.utils.objects.MirrorverseRoom;

import java.awt.*;
import java.util.*;

public class MirrorverseHelpers {
    private static final HashSet<Entity> neededEntities = new HashSet<>();
    private static final ArrayList<MirrorverseRoom> mirrorverseRooms = new ArrayList<MirrorverseRoom>() {{
        add(new MirrorverseRoom(0, new AxisAlignedBB(new BlockPos(-85, 63, -121), new BlockPos(-80, 69, -113)), EnumFacing.WEST));
        add(new MirrorverseRoom(1, new AxisAlignedBB(new BlockPos(-105, 51, -117), new BlockPos(-81, 61, -106)), EnumFacing.SOUTH));
        add(new MirrorverseRoom(2, new AxisAlignedBB(new BlockPos(-117, 51, -116), new BlockPos(-108, 58, -105)), EnumFacing.NORTH));
        add(new MirrorverseRoom(3, new AxisAlignedBB(new BlockPos(-223, 35, -126), new BlockPos(-122, 46, -91)), EnumFacing.UP));
        add(new MirrorverseRoom(4, new AxisAlignedBB(new BlockPos(-267, 32, -110), new BlockPos(-261, 41, -104)), EnumFacing.UP));
        add(new MirrorverseRoom(5, new AxisAlignedBB(new BlockPos(-310, 0, -113), new BlockPos(-298, 56, -101)), EnumFacing.WEST));

    }};
    private static final BlockPos[] danceBlocks = {
            new BlockPos(-265, 32, -108),
            new BlockPos(-265, 32, -106),
            new BlockPos(-263, 32, -106),
            new BlockPos(-263, 32, -108)
    };
    private static final HashMap<String, ArrayList<KeyBinding>> danceMoves = new HashMap<String, ArrayList<KeyBinding>>() {{
        put("Move!", new ArrayList<>());
        put("Stand!",  new ArrayList<>());
        put("Sneak!",  new ArrayList<KeyBinding>(){{ add(GumTuneClient.mc.gameSettings.keyBindSneak); }});
        put("Sneak! and Jump!",new ArrayList<KeyBinding>(){{ add(GumTuneClient.mc.gameSettings.keyBindSneak); add(GumTuneClient.mc.gameSettings.keyBindJump); }});
    }};

    enum DanceState {
        WAITING_FOR_DANCE_MOVE,
        MOVING_TO_NEXT_POSITION,
        DANCING
    }

    private static DanceState danceState = DanceState.WAITING_FOR_DANCE_MOVE;
    private static ArrayList<KeyBinding> nextDanceMoves;
    private static BlockPos nextBlockPos;
    private static long timestamp;
    private static int currentRoom = -1;

    @SubscribeEvent
    public void onPacketReceived(PacketReceivedEvent event) {
        if (!GumTuneClientConfig.mirrorverseHelpers) return;
        if (LocationUtils.currentIsland != LocationUtils.Island.THE_RIFT) return;
//        if (currentRoom != 4) return;
//        if (event.packet instanceof S45PacketTitle) {
//            if (((S45PacketTitle) event.packet).getMessage() == null) return;
//            String action = StringUtils.removeFormatting(((S45PacketTitle) event.packet).getMessage().getUnformattedText());
//            if (!action.equals("")) {
//                if (danceMoves.get(action) != null) {
//                    nextDanceMoves = danceMoves.get(action);
//                } else {
//                    nextDanceMoves = null;
//                    ModUtils.sendMessage("Unrecognized dance move: " + action + ", please report this!");
//                }
//            }
//        } else if (event.packet instanceof S29PacketSoundEffect) {
//            S29PacketSoundEffect packetSoundEffect = (S29PacketSoundEffect) event.packet;
//            if (packetSoundEffect.getSoundName().equals("note.bassattack") && packetSoundEffect.getPitch() < 0.7) {
//                ModUtils.sendMessage(packetSoundEffect.getPitch() + " " + packetSoundEffect.getX() + " " + packetSoundEffect.getZ() + " playerPos: " + GumTuneClient.mc.thePlayer.getPositionVector());
//            }
//        }
    }

    @SubscribeEvent
    public void onOverlayRender(RenderGameOverlayEvent.Post event) {
        if (!GumTuneClientConfig.mirrorverseHelpers) return;
        if (LocationUtils.currentIsland != LocationUtils.Island.THE_RIFT) return;
        if (event.type == RenderGameOverlayEvent.ElementType.ALL && currentRoom != -1) {
            FontUtils.drawScaledString("Current Room: " + currentRoom, 1, 300, 100, true);
//            if (currentRoom == 4) {
//                FontUtils.drawScaledString("Dance State: " + danceState, 1, 300, 110, true);
//            }
        }
    }

    @SubscribeEvent
    public void onRenderWorld(RenderWorldLastEvent event) {
        if (!GumTuneClientConfig.mirrorverseHelpers) return;
        if (LocationUtils.currentIsland != LocationUtils.Island.THE_RIFT) return;
        if (GumTuneClient.mc.thePlayer == null || GumTuneClient.mc.theWorld == null) return;
        if (currentRoom == -1) return;
        MirrorverseRoom mirrorverseRoom = mirrorverseRooms.get(currentRoom);

        RenderUtils.renderBoundingBox(mirrorverseRoom.getRoomBoundingBox(), new Color(0, 0, 255).getRGB(), 0.3f);
        if (currentRoom != 4) {
            RenderUtils.renderBoundingBox(mirrorverseRoom.getMirroredRoomBoundingBox(), new Color(0, 255, 0).getRGB(), 0.3f);
        }

        Vec3 mirrorAnchor = mirrorverseRoom.getMirrorAnchor().add(VectorUtils.scaleVec(mirrorverseRoom.getMirrorEnumFacing().getDirectionVec(), 0.5f));
        GumTuneClient.mc.theWorld.loadedEntityList.forEach(entity -> {
            if (neededEntities.contains(entity)) {
                RenderUtils.renderEntityModel(
                        entity,
                        new Vec3(
                                entity.posX + (mirrorAnchor.xCoord - entity.posX) * 2 * Math.abs(mirrorverseRoom.getMirrorEnumFacing().getDirectionVec().getX()),
                                entity.posY,
                                entity.posZ + (mirrorAnchor.zCoord - entity.posZ) * 2 * Math.abs(mirrorverseRoom.getMirrorEnumFacing().getDirectionVec().getZ())
                        ),
                        event.partialTicks,
                        0.3f
                );
            }
        });
    }

    @SubscribeEvent
    public void onTick(TickEvent.ClientTickEvent event) {
        if (!GumTuneClientConfig.mirrorverseHelpers) return;
        if (event.phase == TickEvent.Phase.END) return;
        if (GumTuneClient.mc.thePlayer == null) return;
        if (LocationUtils.currentIsland != LocationUtils.Island.THE_RIFT) return;
        currentRoom = -1;
        mirrorverseRooms.forEach(mirrorverseRoom -> {
            if (mirrorverseRoom.getRoomBoundingBox().isVecInside(GumTuneClient.mc.thePlayer.getPositionEyes(1f))) {
                currentRoom = mirrorverseRoom.getIndex();
            }
        });

        if (currentRoom != -1) {
            MirrorverseRoom mirrorverseRoom = mirrorverseRooms.get(currentRoom);
            switch (currentRoom) {
                case 1:
                case 3:
                case 5:
                    for (BlockPos blockPos : BlockPos.getAllInBox(
                            new BlockPos(
                                    mirrorverseRoom.getRoomBoundingBox().minX,
                                    mirrorverseRoom.getRoomBoundingBox().minY,
                                    mirrorverseRoom.getRoomBoundingBox().minZ
                            ),
                            new BlockPos(
                                    mirrorverseRoom.getRoomBoundingBox().maxX,
                                    mirrorverseRoom.getRoomBoundingBox().maxY,
                                    mirrorverseRoom.getRoomBoundingBox().maxZ
                            )
                    )) {
                        BlockPos mirroredBlockPos = mirrorverseRoom.getMirroredBlock(blockPos);
                        if (GumTuneClient.mc.theWorld.getBlockState(mirroredBlockPos).getBlock() != Blocks.iron_block) {
                            GumTuneClient.mc.theWorld.setBlockState(blockPos, GumTuneClient.mc.theWorld.getBlockState(mirroredBlockPos));
                        }
                    }
                    break;
                case 2:
                    neededEntities.clear();
                    GumTuneClient.mc.theWorld.loadedEntityList.forEach(entity -> {
                        if (mirrorverseRoom.getMirroredRoomBoundingBox().isVecInside(entity.getPositionEyes(1f))) {
                            if (!(entity instanceof EntityItemFrame) && !(entity instanceof EntityArmorStand) && !(entity instanceof EntityOtherPlayerMP)) {
                                neededEntities.add(entity);
                            }
                        }
                    });
                    break;
                case 4:
//                    switch (danceState) {
//                        case WAITING_FOR_DANCE_MOVE:
//                            if (nextBlockPos == null) {
//                                nextBlockPos = danceBlocks[0];
//                                timestamp = System.currentTimeMillis();
//                                danceState = DanceState.MOVING_TO_NEXT_POSITION;
//                            }
//                            break;
//                        case MOVING_TO_NEXT_POSITION:
//                            Vec3 nextVec = new Vec3(nextBlockPos.getX() + 0.5, nextBlockPos.getY() + 0.5, nextBlockPos.getZ() + 0.5);
//                            ArrayList<KeyBinding> neededPresses = VectorUtils.getNeededKeyPresses(GumTuneClient.mc.thePlayer.getPositionVector(), nextVec);
//                            if (VectorUtils.getHorizontalDistance(GumTuneClient.mc.thePlayer.getPositionVector(), nextVec) > 0.05) {
//                                neededPresses.forEach(v -> KeyBinding.setKeyBindState(v.getKeyCode(), true));
//                            } else {
//                                timestamp = System.currentTimeMillis();
//                                danceState = DanceState.DANCING;
//                            }
//                            break;
//                        case DANCING:
//                            if (nextDanceMoves != null) {
//                                nextDanceMoves.forEach(v -> KeyBinding.setKeyBindState(v.getKeyCode(), true));
//                            }
//                            break;
//                    }
                    break;
            }
        } else {
            nextBlockPos = null;
            nextDanceMoves = null;
            timestamp = System.currentTimeMillis();
        }
    }
}
