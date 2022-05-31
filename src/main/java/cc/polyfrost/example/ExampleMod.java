package cc.polyfrost.example;

import cc.polyfrost.example.command.ExampleCommand;
import cc.polyfrost.example.config.TestConfig;
import cc.polyfrost.oneconfig.utils.commands.CommandManager;

/**
 * The main class of OneConfig.
 */
@net.minecraftforge.fml.common.Mod(modid = ExampleMod.MODID, name = ExampleMod.NAME, version = ExampleMod.VERSION)
public class ExampleMod {
    @net.minecraftforge.fml.common.Mod.Instance("@ID@")
    public static ExampleMod INSTANCE;
    public static final String MODID = "@ID@";
    public static final String NAME = "@NAME@";
    public static final String VERSION = "@VER@";
    public TestConfig config;

    @net.minecraftforge.fml.common.Mod.EventHandler
    public void onPreFMLInit(net.minecraftforge.fml.common.event.FMLPreInitializationEvent event) {
        config = new TestConfig();
    }

    @net.minecraftforge.fml.common.Mod.EventHandler
    public void onFMLInitialization(net.minecraftforge.fml.common.event.FMLInitializationEvent event) {
        CommandManager.INSTANCE.registerCommand(new ExampleCommand());
    }
}
