package rosegold.gumtuneclient.config.pages;

import cc.polyfrost.oneconfig.config.annotations.Switch;

public class NukerBooleanOptions {
    @Switch(
            name = "Mine ALL Blocks In Front",
            description = "Mine all blocks in the way of the player"
    )
    public static boolean mineBlocksInFront = false;

    @Switch(
            name = "On Ground Only",
            description = "Mine only while the player is grounded"
    )
    public static boolean onGroundOnly = false;

    @Switch(
            name = "Preview",
            description = "Show which blocks are going to be mined"
    )
    public static boolean preview = false;
}
