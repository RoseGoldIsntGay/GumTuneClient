package rosegold.gumtuneclient;

import cc.polyfrost.oneconfig.events.EventManager;
import cc.polyfrost.oneconfig.utils.commands.CommandManager;
import net.minecraft.client.Minecraft;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import rosegold.gumtuneclient.command.MainCommand;
import rosegold.gumtuneclient.config.GumTuneClientConfig;
import rosegold.gumtuneclient.events.MillisecondEvent;
import rosegold.gumtuneclient.events.SecondEvent;
import rosegold.gumtuneclient.modules.macro.AutoHarp;
import rosegold.gumtuneclient.modules.macro.FarmingMacro;
import rosegold.gumtuneclient.modules.macro.MobMacro;
import rosegold.gumtuneclient.modules.mining.MetalDetectorSolver;
import rosegold.gumtuneclient.modules.player.AutoSell;
import rosegold.gumtuneclient.modules.player.AvoidBreakingCrops;
import rosegold.gumtuneclient.modules.player.PathFinding;
import rosegold.gumtuneclient.modules.render.ESPs;
import rosegold.gumtuneclient.modules.singleplayer.skyblockitems.AspectOfTheVoid;
import rosegold.gumtuneclient.modules.world.CropPlacer;
import rosegold.gumtuneclient.modules.mining.Nuker;
import rosegold.gumtuneclient.modules.mining.PowderChestSolver;
import rosegold.gumtuneclient.modules.world.WorldScanner;
import rosegold.gumtuneclient.utils.LocationUtils;
import rosegold.gumtuneclient.utils.RotationUtils;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Mod(modid = GumTuneClient.MODID, name = GumTuneClient.NAME, version = GumTuneClient.VERSION, clientSideOnly = true)
public class GumTuneClient {
    public static final String MODID = "@ID@";
    public static final String NAME = "@NAME@";
    public static final String VERSION = "@VER@";

    @Mod.Instance(MODID)
    public static GumTuneClient INSTANCE;
    public GumTuneClientConfig config;
    public static Minecraft mc = Minecraft.getMinecraft();

    private final List<Object> modules = new ArrayList<>();
    private boolean login = false;
    public static boolean debug = false;

    public GumTuneClient() {
        modules.add(new PowderChestSolver());
        modules.add(new AutoHarp());
        modules.add(new ESPs());
        modules.add(new CropPlacer());
        modules.add(new Nuker());
        modules.add(new LocationUtils());
        modules.add(new RotationUtils());
        modules.add(new MobMacro());
        modules.add(new WorldScanner());
        modules.add(new PathFinding());
        modules.add(new AspectOfTheVoid());
        modules.add(new MetalDetectorSolver());
        modules.add(new AvoidBreakingCrops());
        modules.add(new AutoSell());
        modules.add(new FarmingMacro());
    }

    @Mod.EventHandler
    public void preFMLInitialization(FMLPreInitializationEvent event) {
    }

    @Mod.EventHandler
    public void onFMLInitialization(FMLInitializationEvent event) {
        config = new GumTuneClientConfig();
        registerCommand(new MainCommand());
        registerModule(this);
        modules.forEach(this::registerModule);
    }

    @Mod.EventHandler
    public void postFMLInitialization(FMLPostInitializationEvent event) {
        LocalDateTime now = LocalDateTime.now();
        Duration initialDelay = Duration.between(now, now);
        long initialDelaySeconds = initialDelay.getSeconds();

        ScheduledExecutorService threadPool = Executors.newScheduledThreadPool(1);
        threadPool.scheduleAtFixedRate(() -> MinecraftForge.EVENT_BUS.post(new SecondEvent()), initialDelaySeconds, 1, TimeUnit.SECONDS);
        threadPool.scheduleAtFixedRate(() -> MinecraftForge.EVENT_BUS.post(new MillisecondEvent()), initialDelaySeconds, 1, TimeUnit.MILLISECONDS);
    }

    @SubscribeEvent
    public void onTick(TickEvent.ClientTickEvent event) {
        if (mc.thePlayer == null || mc.theWorld == null || login) return;
        login = true;
        // Disabled notification since I fxed locraw
        //if (bladecore) ModUtils.sendMessage("§c[WARNING]: §fBladecore currently prevents some features that require your current skyblock island type from working!");
    }

    private void registerModule(Object obj) {
        MinecraftForge.EVENT_BUS.register(obj);
        EventManager.INSTANCE.register(obj);
    }

    private void registerCommand(Object obj) {
        CommandManager.INSTANCE.registerCommand(obj);
    }
}
