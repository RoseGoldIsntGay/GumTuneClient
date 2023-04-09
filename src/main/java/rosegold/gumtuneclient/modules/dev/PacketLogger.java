package rosegold.gumtuneclient.modules.dev;

import net.minecraft.network.handshake.client.C00Handshake;
import net.minecraft.network.play.client.*;
import net.minecraft.network.play.server.*;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import rosegold.gumtuneclient.GumTuneClient;
import rosegold.gumtuneclient.config.GumTuneClientConfig;
import rosegold.gumtuneclient.events.PacketReceivedEvent;
import rosegold.gumtuneclient.events.PacketSentEvent;
import rosegold.gumtuneclient.utils.ModUtils;

import java.util.ArrayList;

public class PacketLogger {

    private static final ArrayList<Class> clientPackets = new ArrayList<Class>() {{
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

    @SubscribeEvent
    public void onPacketSent(PacketSentEvent event) {
        if (!GumTuneClientConfig.packetLogger) return;
        if (GumTuneClient.mc.thePlayer == null) return;
        if (event.packet.getClass().equals(clientPackets.get(GumTuneClientConfig.packetLoggerType))) {
            ModUtils.sendMessage(event.packet);
        }
    }

    @SubscribeEvent
    public void onPacketReceived(PacketReceivedEvent event) {
        if (!GumTuneClientConfig.packetLogger) return;
        if (GumTuneClient.mc.thePlayer == null) return;
    }
}
