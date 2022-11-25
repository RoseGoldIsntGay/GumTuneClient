package rosegold.gumtuneclient.events;

import net.minecraftforge.fml.common.eventhandler.Cancelable;
import net.minecraftforge.fml.common.eventhandler.Event;

@Cancelable
public class PlayerMoveEvent {
    @Cancelable
    public static class Pre extends Event {}

    public static class Post extends Event {}
}
