package rosegold.gumtuneclient.modules.dev;

import net.minecraft.entity.Entity;
import net.minecraft.network.handshake.client.C00Handshake;
import net.minecraft.network.play.client.*;
import net.minecraft.network.play.server.*;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import rosegold.gumtuneclient.GumTuneClient;
import rosegold.gumtuneclient.config.GumTuneClientConfig;
import rosegold.gumtuneclient.events.PacketReceivedEvent;
import rosegold.gumtuneclient.events.PacketSentEvent;
import rosegold.gumtuneclient.utils.ModUtils;

import java.util.ArrayList;

public class PacketLogger {

    public static final ArrayList<Class> clientPackets = new ArrayList<Class>() {{
        add(C0APacketAnimation.class);
        add(C0BPacketEntityAction.class);
        add(C0CPacketInput.class);
        add(C0DPacketCloseWindow.class);
        add(C0EPacketClickWindow.class);
        add(C0FPacketConfirmTransaction.class);
        add(C00PacketKeepAlive.class);
        add(C01PacketChatMessage.class);
        add(C02PacketUseEntity.class);
        add(C03PacketPlayer.class);
        add(C07PacketPlayerDigging.class);
        add(C08PacketPlayerBlockPlacement.class);
        add(C09PacketHeldItemChange.class);
        add(C10PacketCreativeInventoryAction.class);
        add(C11PacketEnchantItem.class);
        add(C12PacketUpdateSign.class);
        add(C13PacketPlayerAbilities.class);
        add(C14PacketTabComplete.class);
        add(C15PacketClientSettings.class);
        add(C16PacketClientStatus.class);
        add(C17PacketCustomPayload.class);
        add(C18PacketSpectate.class);
        add(C19PacketResourcePackStatus.class);
    }};

    public static final ArrayList<Class> serverPackets = new ArrayList<Class>() {{
        add(S0APacketUseBed.class);
        add(S0BPacketAnimation.class);
        add(S0CPacketSpawnPlayer.class);
        add(S0DPacketCollectItem.class);
        add(S0EPacketSpawnObject.class);
        add(S0FPacketSpawnMob.class);
        add(S00PacketKeepAlive.class);
        add(S1BPacketEntityAttach.class);
        add(S1CPacketEntityMetadata.class);
        add(S1DPacketEntityEffect.class);
        add(S1EPacketRemoveEntityEffect.class);
        add(S1FPacketSetExperience.class);
        add(S01PacketJoinGame.class);
        add(S2APacketParticles.class);
        add(S2BPacketChangeGameState.class);
        add(S2CPacketSpawnGlobalEntity.class);
        add(S2DPacketOpenWindow.class);
        add(S2EPacketCloseWindow.class);
        add(S2FPacketSetSlot.class);
        add(S02PacketChat.class);
        add(S3APacketTabComplete.class);
        add(S3BPacketScoreboardObjective.class);
        add(S3CPacketUpdateScore.class);
        add(S3DPacketDisplayScoreboard.class);
        add(S3EPacketTeams.class);
        add(S3FPacketCustomPayload.class);
        add(S03PacketTimeUpdate.class);
        add(S04PacketEntityEquipment.class);
        add(S05PacketSpawnPosition.class);
        add(S06PacketUpdateHealth.class);
        add(S07PacketRespawn.class);
        add(S08PacketPlayerPosLook.class);
        add(S09PacketHeldItemChange.class);
        add(S10PacketSpawnPainting.class);
        add(S11PacketSpawnExperienceOrb.class);
        add(S12PacketEntityVelocity.class);
        add(S13PacketDestroyEntities.class);
        add(S14PacketEntity.class);
        add(S18PacketEntityTeleport.class);
        add(S19PacketEntityHeadLook.class);
        add(S19PacketEntityStatus.class);
        add(S20PacketEntityProperties.class);
        add(S21PacketChunkData.class);
        add(S22PacketMultiBlockChange.class);
        add(S23PacketBlockChange.class);
        add(S24PacketBlockAction.class);
        add(S25PacketBlockBreakAnim.class);
        add(S26PacketMapChunkBulk.class);
        add(S27PacketExplosion.class);
        add(S28PacketEffect.class);
        add(S29PacketSoundEffect.class);
        add(S30PacketWindowItems.class);
        add(S31PacketWindowProperty.class);
        add(S32PacketConfirmTransaction.class);
        add(S33PacketUpdateSign.class);
        add(S34PacketMaps.class);
        add(S35PacketUpdateTileEntity.class);
        add(S36PacketSignEditorOpen.class);
        add(S37PacketStatistics.class);
        add(S38PacketPlayerListItem.class);
        add(S39PacketPlayerAbilities.class);
        add(S40PacketDisconnect.class);
        add(S41PacketServerDifficulty.class);
        add(S42PacketCombatEvent.class);
        add(S43PacketCamera.class);
        add(S44PacketWorldBorder.class);
        add(S45PacketTitle.class);
        add(S46PacketSetCompressionLevel.class);
        add(S47PacketPlayerListHeaderFooter.class);
        add(S48PacketResourcePackSend.class);
        add(S49PacketUpdateEntityNBT.class);
    }};

    @SubscribeEvent
    public void onPacketSent(PacketSentEvent event) {
        if (!GumTuneClientConfig.clientPacketLogger) return;
        if (GumTuneClient.mc.thePlayer == null) return;
        if (GumTuneClientConfig.packetLoggerClientType1 != 0 && event.packet.getClass().equals(clientPackets.get(GumTuneClientConfig.packetLoggerClientType1 - 1)) ||
                GumTuneClientConfig.packetLoggerClientType2 != 0 &&event.packet.getClass().equals(clientPackets.get(GumTuneClientConfig.packetLoggerClientType2 + 10))) {
            if (event.packet instanceof C02PacketUseEntity) {
                C02PacketUseEntity packetUseEntity = (C02PacketUseEntity) event.packet;
                Entity entity = packetUseEntity.getEntityFromWorld(GumTuneClient.mc.theWorld);
                ModUtils.sendMessage(packetUseEntity.getAction() + " " + entity.getClass().getSimpleName() + " [" + entity.getName() + "] " + packetUseEntity.getHitVec());
            } else {
                ModUtils.sendMessage(event.packet);
            }
        }
    }

    @SubscribeEvent
    public void onPacketReceived(PacketReceivedEvent event) {
        if (!GumTuneClientConfig.serverPacketLogger) return;
        if (GumTuneClient.mc.thePlayer == null) return;
        if (GumTuneClientConfig.packetLoggerServerType1 != 0 && event.packet.getClass().equals(serverPackets.get(GumTuneClientConfig.packetLoggerServerType1 - 1)) ||
                GumTuneClientConfig.packetLoggerServerType2 != 0 && event.packet.getClass().equals(serverPackets.get(GumTuneClientConfig.packetLoggerServerType2 + 10)) ||
                GumTuneClientConfig.packetLoggerServerType3 != 0 && event.packet.getClass().equals(serverPackets.get(GumTuneClientConfig.packetLoggerServerType3 + 22)) ||
                GumTuneClientConfig.packetLoggerServerType4 != 0 && event.packet.getClass().equals(serverPackets.get(GumTuneClientConfig.packetLoggerServerType4 + 34)) ||
                GumTuneClientConfig.packetLoggerServerType5 != 0 && event.packet.getClass().equals(serverPackets.get(GumTuneClientConfig.packetLoggerServerType5 + 46)) ||
                GumTuneClientConfig.packetLoggerServerType6 != 0 && event.packet.getClass().equals(serverPackets.get(GumTuneClientConfig.packetLoggerServerType6 + 58))) {
            if (event.packet instanceof S2APacketParticles) {
                S2APacketParticles packetParticles = (S2APacketParticles) event.packet;
                ModUtils.sendMessage(packetParticles.getParticleType() + " " + packetParticles.getXCoordinate() + " " + packetParticles.getYCoordinate() + " " + packetParticles.getZCoordinate());
            } else if(event.packet instanceof S02PacketChat) {
                S02PacketChat packetChat = (S02PacketChat) event.packet;
                ModUtils.sendMessage(packetChat.getChatComponent() + " type: " + packetChat.getType());
            } else if(event.packet instanceof S45PacketTitle) {
                S45PacketTitle packetTitle = (S45PacketTitle) event.packet;
                ModUtils.sendMessage(packetTitle.getMessage() + " time: " + packetTitle.getDisplayTime());
            } else {
                ModUtils.sendMessage(event.packet);
            }
        }
    }
}
