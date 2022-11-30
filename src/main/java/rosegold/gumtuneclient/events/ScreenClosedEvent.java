package rosegold.gumtuneclient.events;

import net.minecraft.inventory.Container;
import net.minecraftforge.fml.common.eventhandler.Cancelable;
import net.minecraftforge.fml.common.eventhandler.Event;

@Cancelable
public class ScreenClosedEvent extends Event {

    public Container container;

    public ScreenClosedEvent(Container container) {
        this.container = container;
    }

}