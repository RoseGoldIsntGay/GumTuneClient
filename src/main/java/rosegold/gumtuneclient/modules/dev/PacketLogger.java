package rosegold.gumtuneclient.modules.dev;

import net.minecraft.network.play.server.S08PacketPlayerPosLook;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import rosegold.gumtuneclient.GumTuneClient;
import rosegold.gumtuneclient.config.GumTuneClientConfig;
import rosegold.gumtuneclient.events.PacketReceivedEvent;
import rosegold.gumtuneclient.events.PacketSentEvent;
import rosegold.gumtuneclient.utils.ModUtils;

public class PacketLogger {
    @SubscribeEvent
    public void onPacketSent(PacketSentEvent event) {
        if (!GumTuneClientConfig.packetLogger) return;
        if (GumTuneClient.mc.thePlayer == null) return;
    }

    @SubscribeEvent
    public void onPacketReceived(PacketReceivedEvent event) {
        if (!GumTuneClientConfig.packetLogger) return;
        if (GumTuneClient.mc.thePlayer == null) return;
        switch (GumTuneClientConfig.packetLoggerType) {
            case 0:
                if (event.packet instanceof S08PacketPlayerPosLook) {
                    S08PacketPlayerPosLook packetPlayerPosLook = (S08PacketPlayerPosLook) event.packet;
                    ModUtils.sendMessage(String.format("x: %f y: %f z: %f yaw: %f pitch: %f", packetPlayerPosLook.getX(), packetPlayerPosLook.getY(), packetPlayerPosLook.getZ(), packetPlayerPosLook.getYaw(), packetPlayerPosLook.getPitch()));
                }
                break;
        }
    }
}
