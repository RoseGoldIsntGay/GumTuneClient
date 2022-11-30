package rosegold.gumtuneclient.events;

import net.minecraftforge.fml.common.eventhandler.Event;

import java.time.LocalDateTime;

public class SecondEvent extends Event {
    public LocalDateTime dateTime;
    public long timestamp;

    public SecondEvent() {
        dateTime = LocalDateTime.now();
        timestamp = System.currentTimeMillis();
    }
}