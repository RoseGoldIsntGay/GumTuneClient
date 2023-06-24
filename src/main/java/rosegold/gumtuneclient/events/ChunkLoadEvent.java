package rosegold.gumtuneclient.events;

import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.fml.common.eventhandler.Event;

public class ChunkLoadEvent extends Event {
    private final Chunk chunk;

    public ChunkLoadEvent(Chunk chunk) {
        this.chunk = chunk;
    }

    public Chunk getChunk() {
        return this.chunk;
    }
}
