package rosegold.gumtuneclient;

import cc.polyfrost.oneconfig.events.EventManager;
import cc.polyfrost.oneconfig.utils.commands.CommandManager;
import me.cephetir.communistscanner.CommunistScanners;
import me.cephetir.communistscanner.StructureCallBack;
import net.minecraft.client.Minecraft;
import net.minecraft.util.BlockPos;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.jetbrains.annotations.NotNull;
import rosegold.gumtuneclient.command.MainCommand;
import rosegold.gumtuneclient.config.GumTuneClientConfig;
import rosegold.gumtuneclient.events.MillisecondEvent;
import rosegold.gumtuneclient.events.SecondEvent;
import rosegold.gumtuneclient.modules.dev.CopyNBTData;
import rosegold.gumtuneclient.modules.dev.PacketLogger;
import rosegold.gumtuneclient.modules.farming.AvoidBreakingCrops;
import rosegold.gumtuneclient.modules.farming.PreventRenderingCrops;
import rosegold.gumtuneclient.modules.farming.VisitorHelpers;
import rosegold.gumtuneclient.modules.macro.AutoHarp;
import rosegold.gumtuneclient.modules.macro.MobMacro;
import rosegold.gumtuneclient.modules.mining.MetalDetectorSolver;
import rosegold.gumtuneclient.modules.mining.Nuker;
import rosegold.gumtuneclient.modules.mining.PowderChestSolver;
import rosegold.gumtuneclient.modules.player.AutoSell;
import rosegold.gumtuneclient.modules.player.FairySoulAura;
import rosegold.gumtuneclient.modules.player.PathFinding;
import rosegold.gumtuneclient.modules.render.ESPs;
import rosegold.gumtuneclient.modules.render.RevealHiddenMobs;
import rosegold.gumtuneclient.modules.singleplayer.skyblockitems.AspectOfTheVoid;
import rosegold.gumtuneclient.modules.slayer.AutoMaddox;
import rosegold.gumtuneclient.modules.slayer.HighlightSlayerBoss;
import rosegold.gumtuneclient.modules.slayer.SlayerHandler;
import rosegold.gumtuneclient.modules.world.CropPlacer;
import rosegold.gumtuneclient.modules.world.WorldScanner;
import rosegold.gumtuneclient.utils.*;
import rosegold.gumtuneclient.utils.objects.ModuleArrayList;

import java.awt.*;
import java.io.File;
import java.time.Duration;
import java.time.LocalDateTime;
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

    private final ModuleArrayList<Object> modules = new ModuleArrayList<>();
    private boolean login = false;
    public static boolean debug = false;
    private boolean failedCreatingConfig = false;

    public GumTuneClient() {
        modules.addAll(
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
                new VisitorHelpers()
        );
    }

    @Mod.EventHandler
    public void preFMLInitialization(FMLPreInitializationEvent event) {
        File configDirectory = new File(event.getModConfigurationDirectory(), MODID);
        if (!configDirectory.exists() && !configDirectory.mkdir()) {
            failedCreatingConfig = true;
        }

        AutoSell.loadConfig();
    }

    @Mod.EventHandler
    public void onFMLInitialization(FMLInitializationEvent event) {
        CommunistScanners.init(new StructureCallBack() {
            @Override
            public void newStructure(@NotNull String server, @NotNull String name, @NotNull BlockPos blockPos) {
                WorldScanner.World world = WorldScanner.worlds.get(server);
                if (world == null)
                    world = WorldScanner.worlds.put(server, new WorldScanner.World(server));
                world.addWaypoint(name, blockPos);
            }
        });

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
    }

    @SubscribeEvent
    public void onTick(TickEvent.ClientTickEvent event) {
        if (mc.thePlayer == null || mc.theWorld == null || login) return;
        login = true;
        initialize();
    }

    private void initialize() {
        if (failedCreatingConfig) {
            ModUtils.sendMessage("Failed creating a config directory, some configuration options will not persist!");
        }
    }

    private void registerModule(Object obj) {
        MinecraftForge.EVENT_BUS.register(obj);
        EventManager.INSTANCE.register(obj);
    }

    private void registerCommand(Object obj) {
        CommandManager.INSTANCE.registerCommand(obj);
    }
}
