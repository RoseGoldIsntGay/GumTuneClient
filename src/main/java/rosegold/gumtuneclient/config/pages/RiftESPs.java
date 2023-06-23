package rosegold.gumtuneclient.config.pages;

import cc.polyfrost.oneconfig.config.annotations.Info;
import cc.polyfrost.oneconfig.config.annotations.Switch;
import cc.polyfrost.oneconfig.config.data.InfoType;

public class RiftESPs {

    @Info(
            text = "Enemy ESPs",
            type = InfoType.INFO,
            size = 2
    )
    public static boolean enemyEspsIgnored;

        @Switch(
                name = "Shy ESP"
        )
        public static boolean shyESP = false;

        @Switch(
                name = "Shadow ESP"
        )
        public static boolean shadowESP = false;

    @Info(
            text = "Passive ESPs",
            type = InfoType.INFO,
            size = 2
    )
    public static boolean passivesEspsIgnored;

        @Switch(
                name = "NPC ESP"
        )
        public static boolean NPCESP = false;

        @Switch(
                name = "Rabbit ESP"
        )
        public static boolean rabbitESP = false;

    @Info(
            text = "Collectibles ESPs",
            type = InfoType.INFO,
            size = 2
    )
    public static boolean collectiblesEspsIgnored;

        @Switch(
                name = "Enigma Soul ESP"
        )
        public static boolean enigmaSoulESP = false;

        @Switch(
                name = "Larvae ESP"
        )
        public static boolean larvaeESP = false;

        @Switch(
                name = "Odonata ESP"
        )
        public static boolean odonataESP = false;

        @Switch(
                name = "Montezuma ESP"
        )
        public static boolean montezumaESP = false;

        @Switch(
                name = "Glyph Chest ESP"
        )
        public static boolean glyphChestESP = false;
}
