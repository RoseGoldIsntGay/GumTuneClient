package cc.polyfrost.example.command;

import cc.polyfrost.example.ExampleMod;
import cc.polyfrost.oneconfig.utils.commands.annotations.Command;
import cc.polyfrost.oneconfig.utils.commands.annotations.Main;

@Command(value = ExampleMod.MODID, description = "Access the " + ExampleMod.NAME + " GUI.")
public class ExampleCommand {

    @Main
    private static void main() {
        ExampleMod.INSTANCE.config.openGui();
    }
}