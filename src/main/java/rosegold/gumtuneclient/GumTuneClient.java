package rosegold.gumtuneclient;

import cc.polyfrost.oneconfig.events.EventManager;
import cc.polyfrost.oneconfig.utils.commands.CommandManager;
import net.minecraft.client.Minecraft;
import net.minecraft.util.BlockPos;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import rosegold.gumtuneclient.command.MainCommand;
import rosegold.gumtuneclient.config.GumTuneClientConfig;
import rosegold.gumtuneclient.config.pages.CustomEspBlockSelector;
import rosegold.gumtuneclient.events.MillisecondEvent;
import rosegold.gumtuneclient.events.SecondEvent;
import rosegold.gumtuneclient.modules.combat.AntiScribe;
import rosegold.gumtuneclient.modules.combat.AntiShy;
import rosegold.gumtuneclient.modules.dev.CopyNBTData;
import rosegold.gumtuneclient.modules.dev.PacketLogger;
import rosegold.gumtuneclient.modules.farming.AvoidBreakingCrops;
import rosegold.gumtuneclient.modules.farming.CropPlacer;
import rosegold.gumtuneclient.modules.farming.PreventRenderingCrops;
import rosegold.gumtuneclient.modules.farming.VisitorHelpers;
import rosegold.gumtuneclient.modules.macro.AutoHarp;
import rosegold.gumtuneclient.modules.macro.GemstoneMacro;
import rosegold.gumtuneclient.modules.macro.MobMacro;
import rosegold.gumtuneclient.modules.mining.GemstoneSackCompactor;
import rosegold.gumtuneclient.modules.mining.MetalDetectorSolver;
import rosegold.gumtuneclient.modules.mining.Nuker;
import rosegold.gumtuneclient.modules.mining.PowderChestSolver;
import rosegold.gumtuneclient.modules.player.*;
import rosegold.gumtuneclient.modules.qol.Trackers;
import rosegold.gumtuneclient.modules.render.CustomBlockESP;
import rosegold.gumtuneclient.modules.render.ESPs;
import rosegold.gumtuneclient.modules.render.RevealHiddenMobs;
import rosegold.gumtuneclient.modules.singleplayer.skyblockitems.AspectOfTheVoid;
import rosegold.gumtuneclient.modules.slayer.AutoMaddox;
import rosegold.gumtuneclient.modules.slayer.HighlightSlayerBoss;
import rosegold.gumtuneclient.modules.slayer.SlayerHandler;
import rosegold.gumtuneclient.modules.world.WorldScanner;
import rosegold.gumtuneclient.utils.*;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
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
    public static GumTuneClientConfig config;
    public static Minecraft mc = Minecraft.getMinecraft();

    private final ArrayList<Object> modules = new ArrayList<>();
    private boolean login = false;
    public static boolean debug = false;
    private boolean failedCreatingConfig = false;

    public GumTuneClient() {
        Collections.addAll(modules,
                new Trackers(),
                new PowderChestSolver(),
                new AutoHarp(),
                new ESPs(),
                new CropPlacer(),
                new Nuker(),
                new LocationUtils(),
                new RotationUtils(),
                new MobMacro(),
                new WorldScanner(),
                new PathFinding(),
                new AspectOfTheVoid(),
                new MetalDetectorSolver(),
                new AvoidBreakingCrops(),
                new AutoSell(),
                new FairySoulAura(),
                new AutoMaddox(),
                new PacketLogger(),
                new PreventRenderingCrops(),
                new RevealHiddenMobs(),
                new CopyNBTData(),
                new SlayerHandler(),
                new HighlightSlayerBoss(),
                new VisitorHelpers(),
                new GemstoneMacro(),
                new AlchemyHelper(),
                new PlayerUtils(),
                new AutoCraft(),
                new AntiShy(),
                new MirrorverseHelpers(),
                new CustomBlockESP(),
                new AntiScribe(),
                new GemstoneSackCompactor()
        );
    }

    @Mod.EventHandler
    public void preFMLInitialization(FMLPreInitializationEvent event) {
        File configDirectory = new File(event.getModConfigurationDirectory(), MODID);
        if (!configDirectory.exists() && !configDirectory.mkdir()) {
            failedCreatingConfig = true;
        }

        AutoSell.loadConfig();
        AutoCraft.loadConfig();
        GemstoneMacro.loadConfig();
        CustomBlockESP.loadConfig();
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
    public void onRenderWorld(RenderWorldLastEvent event) {
        for (BlockPos blockPos : BlockUtils.blockPosConcurrentLinkedQueue) {
            RenderUtils.renderEspBox(blockPos, event.partialTicks, Color.RED.getRGB());
        }
        if (BlockUtils.destination != null) {
            RenderUtils.drawLine(BlockUtils.source, BlockUtils.destination, 1, event.partialTicks);
        }
    }

    @SubscribeEvent
    public void onTick(TickEvent.ClientTickEvent event) {
        if (mc.thePlayer == null || mc.theWorld == null || login) return;
        login = true;
        initialize();
    }

    @SubscribeEvent
    public void onWorldUnload(WorldEvent.Unload event) {
        BlockUtils.source = null;
        BlockUtils.destination = null;
        BlockUtils.blockPosConcurrentLinkedQueue.clear();
    }

    private void initialize() {
        if (failedCreatingConfig) {
            ModUtils.sendMessage("Failed creating a config directory, some configuration options will not persist!");
        }

        CustomEspBlockSelector.loadItems();
    }

    private void registerModule(Object obj) {
        MinecraftForge.EVENT_BUS.register(obj);
        EventManager.INSTANCE.register(obj);
    }

    private void registerCommand(Object obj) {
        CommandManager.INSTANCE.registerCommand(obj);
    }
}
