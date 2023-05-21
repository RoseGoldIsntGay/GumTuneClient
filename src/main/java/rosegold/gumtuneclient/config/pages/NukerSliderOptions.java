package rosegold.gumtuneclient.config.pages;

import cc.polyfrost.oneconfig.config.annotations.Slider;

public class NukerSliderOptions {
    @Slider(
            name = "Speed",
            description = "Blocks per second",
            min = 0, max = 80
    )
    public static int nukerSpeed = 20;

    @Slider(
            name = "Range",
            description = "Range in blocks",
            min = 1, max = 5,
            step = 1
    )
    public static int nukerRange = 5;

    @Slider(
            name = "Height",
            description = "Blocks above your head",
            min = 0, max = 5
    )
    public static int nukerHeight = 0;

    @Slider(
            name = "Depth",
            description = "Blocks below your head",
            min = 0, max = 4
    )
    public static int nukerDepth = 1;

    @Slider(
            name = "Field of View",
            description = "Change fov of sphere shape nuker",
            min = 0, max = 361, // bruh moment
            step = 20
    )
    public static int nukerFieldOfView = 180;

    @Slider(
            name = "Sideways Offset",
            description = "For facing axis mode (positive - offset to the right)",
            min = -4, max = 4,
            step = 1
    )
    public static int nukerSidewaysOffset = 0;

    @Slider(
            name = "Forwards-Backwards Offset",
            description = "For facing axis mode (positive - forwards)",
            min = -4, max = 4,
            step = 1
    )
    public static int nukerForwardsOffset = 0;

    @Slider(
            name = "Pingless reset cutoff",
            description = "Mess with this slider and see if it makes nuker faster",
            min = 0f, max = 20,
            step = 1
    )
    public static int nukerPinglessCutoff = 10;
}
