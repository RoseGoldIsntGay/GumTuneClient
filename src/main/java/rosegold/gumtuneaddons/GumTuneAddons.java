package rosegold.gumtuneaddons;

import cc.polyfrost.oneconfig.utils.commands.CommandManager;
import net.minecraft.client.Minecraft;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import org.reflections.Reflections;
import rosegold.gumtuneaddons.annotations.Module;
import rosegold.gumtuneaddons.command.MainCommand;
import rosegold.gumtuneaddons.config.GumTuneAddonsConfig;
import rosegold.gumtuneaddons.events.MillisecondEvent;
import rosegold.gumtuneaddons.events.SecondEvent;

import java.lang.reflect.InvocationTargetException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Mod(modid = GumTuneAddons.MODID, name = GumTuneAddons.NAME, version = GumTuneAddons.VERSION, clientSideOnly = true)
public class GumTuneAddons {
    public static final String MODID = "@ID@";
    public static final String NAME = "@NAME@";
    public static final String VERSION = "@VER@";
    public static final String PACKAGE = "rosegold." + MODID;
    @Mod.Instance(MODID)
    public static GumTuneAddons INSTANCE;
    public GumTuneAddonsConfig config;
    public static Minecraft mc = Minecraft.getMinecraft();

    private final List<Object> modules = new ArrayList<>();

    public GumTuneAddons() throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        for (Class<?> clz : new Reflections(PACKAGE).getTypesAnnotatedWith(Module.class)) {
            modules.add(clz.getDeclaredConstructor().newInstance());
        }
    }

    @Mod.EventHandler
    public void preFMLInitialization(FMLPreInitializationEvent event) {}

    @Mod.EventHandler
    public void onFMLInitialization(FMLInitializationEvent event) {
        config = new GumTuneAddonsConfig();
        registerCommand(new MainCommand());
        registerModule(this);
        modules.forEach(this::registerModule);
    }

    @Mod.EventHandler
    public void postFMLInitialization(FMLPostInitializationEvent event) {
        LocalDateTime now = LocalDateTime.now();
        Duration initialDelay = Duration.between(now, now);
        long initialDelaySeconds = initialDelay.getSeconds();

        Executors.newScheduledThreadPool(1).scheduleAtFixedRate(() -> MinecraftForge.EVENT_BUS.post(new SecondEvent()), initialDelaySeconds, 1, TimeUnit.SECONDS);
        Executors.newScheduledThreadPool(1).scheduleAtFixedRate(() -> MinecraftForge.EVENT_BUS.post(new MillisecondEvent()), initialDelaySeconds, 1, TimeUnit.MILLISECONDS);
    }

    private void registerModule(Object obj) {
        MinecraftForge.EVENT_BUS.register(obj);
    }

    private void registerCommand(Object obj) {
        CommandManager.INSTANCE.registerCommand(obj);
    }
}
