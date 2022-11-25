package rosegold.gumtuneclient.command;

import rosegold.gumtuneclient.GumTuneClient;
import cc.polyfrost.oneconfig.utils.commands.annotations.Command;
import cc.polyfrost.oneconfig.utils.commands.annotations.Main;

@Command(value = GumTuneClient.MODID, description = "Access the " + GumTuneClient.NAME + " GUI.", aliases = {"gtc"})
public class MainCommand {
    @Main
    private static void main() {
        GumTuneClient.INSTANCE.config.openGui();
    }
}