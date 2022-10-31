package cc.polyfrost.example;

import cc.polyfrost.example.command.ExampleCommand;
import cc.polyfrost.example.config.TestConfig;
import cc.polyfrost.oneconfig.utils.commands.CommandManager;

@net.minecraftforge.fml.common.Mod(modid = ExampleMod.MODID, name = ExampleMod.NAME, version = ExampleMod.VERSION)
public class ExampleMod {
    public static final String MODID = "@ID@";
    public static final String NAME = "@NAME@";
    public static final String VERSION = "@VER@";
    @net.minecraftforge.fml.common.Mod.Instance(MODID)
    public static ExampleMod INSTANCE;
    public TestConfig config;

    @net.minecraftforge.fml.common.Mod.EventHandler
    public void onFMLInitialization(net.minecraftforge.fml.common.event.FMLInitializationEvent event) {
        config = new TestConfig();
        CommandManager.INSTANCE.registerCommand(new ExampleCommand());
    }
}
