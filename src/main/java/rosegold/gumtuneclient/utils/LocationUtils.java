package rosegold.gumtuneclient.utils;

import cc.polyfrost.oneconfig.events.event.LocrawEvent;
import cc.polyfrost.oneconfig.libs.eventbus.Subscribe;
import rosegold.gumtuneclient.annotations.Module;

@Module
public class LocationUtils {

    public enum Island {
        PRIVATE_ISLAND("Private Island"),
        THE_HUB("Hub"),
        THE_PARK("The Park"),
        THE_FARMING_ISLANDS("The Farming Islands"),
        SPIDER_DEN("Spider's Den"),
        THE_END("The End"),
        CRIMSON_ISLE("Crimson Isle"),
        GOLD_MINE("Gold Mine"),
        DEEP_CAVERNS("Deep Caverns"),
        DWARVEN_MINES("Dwarven Mines"),
        CRYSTAL_HOLLOWS("Crystal Hollows"),
        JERRY_WORKSHOP("???"),
        DUNGEON_HUB("Dungeon Hub"),
        LIMBO("UNKNOWN"),
        LOBBY("PROTOTYPE");

        private final String name;

        public String getName() {
            return name;
        }

        Island(String name) {
            this.name = name;
        }
    }

    public static Island currentIsland;

    @Subscribe
    public void onLocraw(LocrawEvent event) {
        if (event.info.getGameMode().equals("lobby")) {
            if (event.info.getServerId().equals("limbo")) {
                currentIsland = Island.LIMBO;
            } else {
                currentIsland = Island.LOBBY;
            }
        } else {
            for (Island island : Island.values()) {
                if (event.info.getMapName().equals(island.getName())) {
                    currentIsland = island;
                }
            }
        }
    }
}
