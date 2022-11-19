package rosegold.gumtuneaddons.command;

import rosegold.gumtuneaddons.GumTuneAddons;
import cc.polyfrost.oneconfig.utils.commands.annotations.Command;
import cc.polyfrost.oneconfig.utils.commands.annotations.Main;

@Command(value = GumTuneAddons.MODID, description = "Access the " + GumTuneAddons.NAME + " GUI.", aliases = {"gta"})
public class MainCommand {
    @Main
    private static void main() {
        GumTuneAddons.INSTANCE.config.openGui();
    }
}