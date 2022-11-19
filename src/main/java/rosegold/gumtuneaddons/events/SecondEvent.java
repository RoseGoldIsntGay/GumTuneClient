package rosegold.gumtuneaddons.events;

import net.minecraftforge.fml.common.eventhandler.Event;

import java.time.LocalDateTime;

public class SecondEvent extends Event {
    public LocalDateTime dateTime;

    public SecondEvent() {
        dateTime = LocalDateTime.now();
    }
}