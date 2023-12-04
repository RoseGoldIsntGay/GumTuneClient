package rosegold.gumtuneclient.modules.macro;

import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import net.minecraft.block.BlockColored;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.Entity;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.init.Blocks;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.client.C09PacketHeldItemChange;
import net.minecraft.network.play.server.S25PacketBlockBreakAnim;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;
import rosegold.gumtuneclient.GumTuneClient;
import rosegold.gumtuneclient.config.GumTuneClientConfig;
import rosegold.gumtuneclient.config.pages.GemstoneMacroAOTVRoutes;
import rosegold.gumtuneclient.config.pages.GemstoneTypeFilter;
import rosegold.gumtuneclient.events.PacketReceivedEvent;
import rosegold.gumtuneclient.utils.*;
import rosegold.gumtuneclient.utils.objects.TimedSet;
import rosegold.gumtuneclient.utils.objects.Waypoint;
import rosegold.gumtuneclient.utils.objects.WaypointList;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class GemstoneMacro {
    private static boolean enabled;
    public static HashSet<WaypointList> allPaths = new HashSet<>();

    private enum GemMacroState {
        AOTV_SETUP,
        AOTV_WALK,
        AOTV_ROTATE,
        AOTV_TELEPORT,
        SETUP_ROTATE_TO_BLOCK,
        ROTATE_TO_BLOCK,
        MINING,
        SPAWN_ARMADILLO,
        MOUNT_ARMADILLO,
        ROTATE_ARMADILLO,
        DISMOUNT_ARMADILLO,
        POST_DISMOUNT_ARMADILLO,
        SLEEP_2000
    }

    private static GemMacroState gemMacroState = GemMacroState.AOTV_SETUP;
    private static GemMacroState previousState;
    private static BlockPos current;
    private static int currentProgress;
    private static BlockPos lastPos;
    private static boolean mining = false;
    private static long timestamp = System.currentTimeMillis();
    private static long startTimestamp = System.currentTimeMillis();
    private static int currentIndex = -1;
    public static ArrayList<BlockPos> blocksInTheWay = new ArrayList<>();
    public static HashSet<BlockPos> extraBlocksInTheWay = new HashSet<>();
    private static int rotationIndex = 0;
    private static final Random random = new Random();
    private final TimedSet<BlockPos> broken = new TimedSet<>(10, TimeUnit.SECONDS);

    @SubscribeEvent
    public void onOverlayRender(RenderGameOverlayEvent.Post event) {
        if (!GumTuneClientConfig.aotvGemstoneMacro) return;
        if (!GumTuneClientConfig.aotvGemstoneMacroDebug) return;
        if (LocationUtils.currentIsland != LocationUtils.Island.CRYSTAL_HOLLOWS) return;
        if (!enabled) return;
        if (event.type == RenderGameOverlayEvent.ElementType.ALL) {
            FontUtils.drawScaledString("Gemstone Macro State: " + gemMacroState, 1, 80, 40, true);
            FontUtils.drawScaledString("Index: " + currentIndex, 1, 80, 50, true);
            FontUtils.drawScaledString("Uptime: " + StringUtils.millisecondFormatTime(System.currentTimeMillis() - startTimestamp), 1, 80, 60, true);
            FontUtils.drawScaledString("Current Break Progress: " + currentProgress, 1, 80, 70, true);
        }
    }

    @SubscribeEvent
    public void onKey(InputEvent.KeyInputEvent event) {
        if (!GumTuneClientConfig.aotvGemstoneMacro) return;
        if (GumTuneClient.mc.currentScreen != null) return;
        if (LocationUtils.currentIsland != LocationUtils.Island.CRYSTAL_HOLLOWS) return;
        if (Keyboard.getEventKeyState()) return;
        int eventKey = Keyboard.getEventKey();

        ArrayList<Integer> keyBindsToggle = GumTuneClientConfig.gemstoneMacroToggleKeyBind.getKeyBinds();
        if (keyBindsToggle.size() > 0 && keyBindsToggle.get(0) == eventKey) {
            enabled = !enabled;
            ModUtils.sendMessage((enabled ? "Enabled" : "Disabled") + " Gemstone Macro");
            if (!enabled) {
                ModUtils.sendMessage("Final Gemstone Macro Uptime: " + StringUtils.millisecondFormatTime(System.currentTimeMillis() - startTimestamp));

                if (current != null && GumTuneClient.mc.thePlayer != null) {
                    GumTuneClient.mc.thePlayer.sendQueue.addToSendQueue(new C07PacketPlayerDigging(
                            C07PacketPlayerDigging.Action.ABORT_DESTROY_BLOCK,
                            current,
                            EnumFacing.DOWN)
                    );
                }

                current = null;
                currentProgress = 0;
                KeyBinding.setKeyBindState(GumTuneClient.mc.gameSettings.keyBindSneak.getKeyCode(), false);
            } else {
                if (GumTuneClientConfig.aotvGemstoneMacroResetStateOnToggle) {
                    gemMacroState = GemMacroState.AOTV_SETUP;
                    currentIndex = -1;
                }

                startTimestamp = System.currentTimeMillis();
            }
        }

        ArrayList<Integer> keyBindsAddToPath = GumTuneClientConfig.gemstoneMacroAddToPathKeyBind.getKeyBinds();
        if (keyBindsAddToPath.size() > 0 && keyBindsAddToPath.get(0) == eventKey) {
            if (GumTuneClient.mc.objectMouseOver != null && GumTuneClient.mc.objectMouseOver.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK) {
                addBlockToPath(GumTuneClient.mc.objectMouseOver.getBlockPos());

                GemstoneMacroAOTVRoutes.redrawRoutes();
                saveConfig();
            } else {
                ModUtils.sendMessage("Invalid block!");
            }
        }


    }

    @SubscribeEvent
    public void onRenderWorld(RenderWorldLastEvent event) {
        if (!GumTuneClientConfig.aotvGemstoneMacro) return;
        if (LocationUtils.currentIsland != LocationUtils.Island.CRYSTAL_HOLLOWS) return;
        if (current != null) {
            RenderUtils.renderEspBox(current, event.partialTicks, Color.BLUE.getRGB());
        }

        WaypointList activeList = getActiveWaypointList();
        if (activeList == null) return;

        activeList.waypoints.forEach((integer, waypoint) -> {
            if (GumTuneClientConfig.aotvGemstoneMacroWaypointRenderDistance == 0 || GumTuneClientConfig.aotvGemstoneMacroWaypointRenderDistance - currentIndex > Integer.parseInt(waypoint.name)) {
                RenderUtils.renderEspBox(new BlockPos(waypoint.x, waypoint.y, waypoint.z), event.partialTicks, Color.CYAN.getRGB(), 0.2f);
                RenderUtils.renderWaypointText(integer.toString(), waypoint.x + 0.5, waypoint.y + 0.5, waypoint.z + 0.5, event.partialTicks, false);

                int nextIndex = activeList.getNextIndex(integer);

                if (activeList.waypoints.get(nextIndex) != null) {
                    RenderUtils.drawLine(
                            new Vec3(waypoint.x + 0.5, waypoint.y + 2.62, waypoint.z + 0.5),
                            new Vec3(activeList.waypoints.get(nextIndex).x + 0.5, activeList.waypoints.get(nextIndex).y + 0.5, activeList.waypoints.get(nextIndex).z + 0.5),
                            1,
                            event.partialTicks
                    );
                }
            }
        });

        if (GumTuneClientConfig.aotvGemstoneShowBlocksBlockingPath) {
            if (blocksInTheWay.isEmpty()) return;

            GlStateManager.blendFunc(770, 771);
            GlStateManager.enableBlend();
            GlStateManager.disableTexture2D();
            GlStateManager.disableDepth();
            GlStateManager.disableAlpha();
            GlStateManager.depthMask(false);
            GlStateManager.disableLighting();

            GL11.glTranslated(-GumTuneClient.mc.getRenderManager().viewerPosX, -GumTuneClient.mc.getRenderManager().viewerPosY, -GumTuneClient.mc.getRenderManager().viewerPosZ);
            GlStateManager.color(1, 0, 0, 0.5f);
            for (List<BlockPos> blocks : Lists.partition(blocksInTheWay, 512)) {
                RenderUtils.renderEspBlocks(blocks);
            }

            GL11.glTranslated(GumTuneClient.mc.getRenderManager().viewerPosX, GumTuneClient.mc.getRenderManager().viewerPosY, GumTuneClient.mc.getRenderManager().viewerPosZ);

            GlStateManager.enableAlpha();
            GlStateManager.enableTexture2D();
            GlStateManager.enableDepth();
            GlStateManager.depthMask(true);
            GlStateManager.disableBlend();
            GlStateManager.enableLighting();
        }
    }

    @SubscribeEvent
    public void onTick(TickEvent.ClientTickEvent event) {
        if (!GumTuneClientConfig.aotvGemstoneMacro) return;
        if (GumTuneClient.mc.theWorld == null) return;
        if (LocationUtils.currentIsland != LocationUtils.Island.CRYSTAL_HOLLOWS) return;
        if (event.phase == TickEvent.Phase.START) return;

        WaypointList activeList = getActiveWaypointList();
        if (activeList == null) return;

        blocksInTheWay.clear();
        extraBlocksInTheWay.clear();

        if (GumTuneClientConfig.aotvGemstoneShowBlocksBlockingPath || GumTuneClientConfig.nukerShape == 4) {
            activeList.waypoints.forEach((integer, waypoint) -> {
                int nextIndex = activeList.getNextIndex(integer);

                if (activeList.waypoints.get(nextIndex) != null) {
                    ArrayList<BlockPos> blocks = BlockUtils.rayTraceBlockList(
                            new Vec3(activeList.waypoints.get(integer).x + 0.5, activeList.waypoints.get(integer).y + 2.62, activeList.waypoints.get(integer).z + 0.5),
                            new Vec3(activeList.waypoints.get(nextIndex).x + 0.5, activeList.waypoints.get(nextIndex).y + 0.5, activeList.waypoints.get(nextIndex).z + 0.5),
                            true,
                            blockPos -> GumTuneClient.mc.theWorld.getBlockState(blockPos).getBlock() == Blocks.chest ||
                                    GumTuneClient.mc.theWorld.getBlockState(blockPos).getBlock() == Blocks.stained_glass ||
                                    GumTuneClient.mc.theWorld.getBlockState(blockPos).getBlock() == Blocks.stained_glass_pane,
                            true
                    );

                    for (BlockPos blockPos : blocks) {
                        blocksInTheWay.add(blockPos);
                        extraBlocksInTheWay.add(blockPos);
                        extraBlocksInTheWay.add(blockPos.add(0, -1, 0));
                    }
                }
            });
        }

        if (!enabled) return;
        if (GumTuneClient.mc.currentScreen != null && !(GumTuneClient.mc.currentScreen instanceof GuiChat)) {
            KeyBinding.setKeyBindState(GumTuneClient.mc.gameSettings.keyBindSneak.getKeyCode(), false);
            return;
        }

        broken.releaseEntries();

        int rodSlot = InventoryUtils.findItemInHotbar("Rod");
        Waypoint nextWaypoint = activeList.waypoints.get(activeList.getNextIndex(currentIndex));

        switch (gemMacroState) {
            case AOTV_SETUP:
                if (nextWaypoint != null) {
                    BlockPos nextBlockPos = new BlockPos(nextWaypoint.x, nextWaypoint.y, nextWaypoint.z);
                    Vec3 viablePointOnBlock = BlockUtils.getViablePointsOnBlock(nextBlockPos, null, 60, true, true).stream().findAny().orElse(null);
                    if (viablePointOnBlock != null) {
                        int aotvSlot = InventoryUtils.findItemInHotbarSkyblockId("ASPECT_OF_THE_VOID");
                        if (aotvSlot != -1) {
                            GumTuneClient.mc.thePlayer.inventory.currentItem = aotvSlot;
                            GumTuneClient.mc.getNetHandler().addToSendQueue(new C09PacketHeldItemChange(GumTuneClient.mc.thePlayer.inventory.currentItem));

                            RotationUtils.smoothLook(RotationUtils.getRotation(viablePointOnBlock), GumTuneClientConfig.aotvGemstoneMacroRotationSpeed);
                            gemMacroState = GemMacroState.AOTV_WALK;
                            timestamp = System.currentTimeMillis();
                            lastPos = GumTuneClient.mc.thePlayer.getPosition();
                            KeyBinding.setKeyBindState(GumTuneClient.mc.gameSettings.keyBindSneak.getKeyCode(), true);
                        }
                        break;
                    } else {
                        MovingObjectPosition movingObjectPosition = BlockUtils.rayTraceBlocks(
                                new Vec3(GumTuneClient.mc.thePlayer.posX, GumTuneClient.mc.thePlayer.posY + 1.54, GumTuneClient.mc.thePlayer.posZ),
                                new Vec3(nextWaypoint.x + 0.5, nextWaypoint.y + 0.5, nextWaypoint.z + 0.5),
                                true,
                                true,
                                false,
                                blockPos -> false,
                                false,
                                true
                        );

                        if (movingObjectPosition != null && movingObjectPosition.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK) {
                            BlockPos blockPos = movingObjectPosition.getBlockPos();
                            if (GumTuneClient.mc.thePlayer.getDistance(blockPos.getX() + 0.5, blockPos.getY() + 0.5, blockPos.getZ() + 0.5) < 4.5) {
                                if (equipPickaxe()) {
                                    ModUtils.sendMessage("Breaking block in the way");
                                    breakBlock(blockPos);
                                }
                            } else {
                                ModUtils.sendMessage("block in the way is too far away to break :(, pausing macro for 2 seconds");
                            }
                            timestamp = System.currentTimeMillis();
                            gemMacroState = GemMacroState.SLEEP_2000;
                        }
                    }
                }
                break;
            case AOTV_WALK:
                if (GumTuneClientConfig.aotvGemstoneMacroWalkForwardsWhileTeleporting) {
                    if (GumTuneClient.mc.thePlayer.getPosition().equals(lastPos)) {
                        ModUtils.sendMessage("still on same block");
                        KeyBinding.setKeyBindState(GumTuneClient.mc.gameSettings.keyBindForward.getKeyCode(), true);
                    } else {
                        ModUtils.sendMessage("no longer on same block");
                        KeyBinding.setKeyBindState(GumTuneClient.mc.gameSettings.keyBindForward.getKeyCode(), false);
                        gemMacroState = GemMacroState.AOTV_ROTATE;
                    }
                } else {
                    gemMacroState = GemMacroState.AOTV_ROTATE;
                }
                break;
            case AOTV_ROTATE:
                if (System.currentTimeMillis() - timestamp > GumTuneClientConfig.aotvGemstoneMacroRotationSpeed) {
                    KeyBinding.setKeyBindState(GumTuneClient.mc.gameSettings.keyBindForward.getKeyCode(), false);
                    gemMacroState = GemMacroState.AOTV_TELEPORT;
                    timestamp = System.currentTimeMillis();
                    GumTuneClient.mc.getNetHandler().addToSendQueue(new C08PacketPlayerBlockPlacement(GumTuneClient.mc.thePlayer.getHeldItem()));
                }
                break;
            case AOTV_TELEPORT:
                if (nextWaypoint != null) {
                    BlockPos blockPos = new BlockPos(nextWaypoint.x, nextWaypoint.y, nextWaypoint.z);

                    if (GumTuneClient.mc.theWorld.getBlockState(blockPos).getBlock() == Blocks.air) {
                        ModUtils.sendMessage("Next point is air!!!");
                        break;
                    }

                    if (!GumTuneClient.mc.thePlayer.getPosition().add(-1, -1, -1).equals(blockPos)) {
                        if (GumTuneClient.mc.thePlayer.getPosition().equals(lastPos)) {
                            break;
                        }
                        ModUtils.sendMessage("AOTV teleport missed! retrying");
                        timestamp = System.currentTimeMillis();
                        gemMacroState = GemMacroState.AOTV_SETUP;
                        break;
                    }

                    timestamp = System.currentTimeMillis();
                    gemMacroState = GumTuneClientConfig.aotvGemstoneMacroMiningMode == 2 ? GemMacroState.SPAWN_ARMADILLO : GemMacroState.SETUP_ROTATE_TO_BLOCK;
                    KeyBinding.setKeyBindState(GumTuneClient.mc.gameSettings.keyBindSneak.getKeyCode(), false);
                }
                break;
            case SETUP_ROTATE_TO_BLOCK:
                current = BlockUtils.getEasiestBlock(5, 5, 4, this::canMine);

                if (current != null && !GumTuneClientConfig.aotvGemstoneMacroMineBlocksBehindWalls) {
                    MovingObjectPosition movingObjectPosition = BlockUtils.rayTraceBlocks(
                            GumTuneClient.mc.thePlayer.getPositionEyes(1.0f),
                            new Vec3(current.getX() + 0.5, current.getY() + 0.5, current.getZ() + 0.5),
                            false,
                            true,
                            false,
                            blockPos -> blockPos.getX() == Math.floor(GumTuneClient.mc.thePlayer.posX) && blockPos.getZ() == Math.floor(GumTuneClient.mc.thePlayer.posZ) && blockPos.getY() < Math.floor(GumTuneClient.mc.thePlayer.posY)
                    );

                    if (movingObjectPosition != null && movingObjectPosition.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK) {
                        current = movingObjectPosition.getBlockPos();
                    }
                }

                currentProgress = 0;

                if (current != null) {
                    if (equipPickaxe()) {

                        RotationUtils.smoothLook(RotationUtils.getRotation(current), GumTuneClientConfig.aotvGemstoneMacroRotationSpeed);
                        gemMacroState = GumTuneClientConfig.aotvGemstoneMacroMiningMode == 1 ? GemMacroState.MINING : GemMacroState.ROTATE_TO_BLOCK;
                    }
                } else {
                    gemMacroState = GemMacroState.AOTV_SETUP;
                    currentIndex = activeList.getNextIndex(currentIndex);
                }

                timestamp = System.currentTimeMillis();
                break;
            case ROTATE_TO_BLOCK:
                if (System.currentTimeMillis() - timestamp > GumTuneClientConfig.aotvGemstoneMacroRotationSpeed) {
                    gemMacroState = GemMacroState.MINING;
                    timestamp = System.currentTimeMillis();
                }
                break;
            case MINING:
                if (current != null) {
                    if (mining && PlayerUtils.pickaxeAbilityReady) {
                        GumTuneClient.mc.playerController.sendUseItem(
                                GumTuneClient.mc.thePlayer,
                                GumTuneClient.mc.theWorld,
                                GumTuneClient.mc.thePlayer.getHeldItem()
                        );
                    }

                    if (!mining) {
                        breakBlock(current);
                        mining = true;
                    }

                    if (System.currentTimeMillis() - timestamp > 3000) {
                        ModUtils.sendMessage("Stuck mining block, it's bad!");
                        current = null;
                        currentProgress = 0;
                        mining = false;
                        gemMacroState = GemMacroState.SETUP_ROTATE_TO_BLOCK;
                        timestamp = System.currentTimeMillis();
                        break;
                    }

                    GumTuneClient.mc.thePlayer.swingItem();

                    if ((currentProgress != 10 && currentProgress >= GumTuneClientConfig.aotvGemstoneMacroBlockBreakProgress) || getBlockState(current).getBlock() == Blocks.air) {
                        broken.put(current);
                        current = null;
                        currentProgress = 0;
                        mining = false;
                        gemMacroState = GemMacroState.SETUP_ROTATE_TO_BLOCK;
                    }
                }
                break;
            case SPAWN_ARMADILLO:
                if (rodSlot != -1) {
                    RotationUtils.smoothLookRelative(new RotationUtils.Rotation(random.nextFloat() - 0.5f, 0), GumTuneClientConfig.aotvGemstoneMacroRotationSpeed, true);

                    GumTuneClient.mc.thePlayer.inventory.currentItem = rodSlot;

                    GumTuneClient.mc.playerController.sendUseItem(
                            GumTuneClient.mc.thePlayer,
                            GumTuneClient.mc.theWorld,
                            GumTuneClient.mc.thePlayer.getHeldItem()
                    );

                    gemMacroState = GemMacroState.MOUNT_ARMADILLO;
                }
                break;
            case MOUNT_ARMADILLO:
                if (System.currentTimeMillis() - timestamp > 200) {
                    Entity zombie = GumTuneClient.mc.theWorld.loadedEntityList.stream().filter(
                            entity -> entity instanceof EntityZombie && entity.isInvisible() && GumTuneClient.mc.thePlayer.getDistanceSqToEntity(entity) < 10
                    ).findAny().orElse(null);

                    if (zombie != null) {
                        GumTuneClient.mc.getNetHandler().addToSendQueue(new C02PacketUseEntity(zombie, C02PacketUseEntity.Action.INTERACT));

                        if (GumTuneClient.mc.thePlayer.isRiding()) {
                            if (equipPickaxe()) {

                                KeyBinding.setKeyBindState(GumTuneClient.mc.gameSettings.keyBindJump.getKeyCode(), true);
                                gemMacroState = GemMacroState.ROTATE_ARMADILLO;
                            }
                        }
                        timestamp = System.currentTimeMillis();
                    }
                }
                break;
            case ROTATE_ARMADILLO:
                if (Math.round(GumTuneClient.mc.thePlayer.posY % 1 * 10000) != 1125) {
                    KeyBinding.setKeyBindState(GumTuneClient.mc.gameSettings.keyBindJump.getKeyCode(), false);
                    RotationUtils.smoothLookRelative(new RotationUtils.Rotation(random.nextFloat() - 0.5f, 360 + random.nextFloat() - 0.5f), GumTuneClientConfig.aotvGemstoneMacroRotationSpeed, true);
                    timestamp = System.currentTimeMillis();
                    gemMacroState = GemMacroState.DISMOUNT_ARMADILLO;
                }
                break;
            case DISMOUNT_ARMADILLO:
                if (System.currentTimeMillis() - timestamp > GumTuneClientConfig.aotvGemstoneMacroRotationSpeed) {
                    switch (GumTuneClientConfig.aotvGemstoneMacroDismountArmadilloMode) {
                        case 0:
                            KeyBinding.setKeyBindState(GumTuneClient.mc.gameSettings.keyBindSneak.getKeyCode(), true);
                            break;
                        case 1:
                            if (rodSlot != -1) {
                                GumTuneClient.mc.thePlayer.inventory.currentItem = rodSlot;

                                GumTuneClient.mc.playerController.sendUseItem(
                                        GumTuneClient.mc.thePlayer,
                                        GumTuneClient.mc.theWorld,
                                        GumTuneClient.mc.thePlayer.getHeldItem()
                                );
                            }
                            break;
                    }

                    Waypoint waypoint = activeList.waypoints.get(activeList.getNextIndex(activeList.getNextIndex(currentIndex)));
                    if (waypoint != null) {
                        Vec3 viablePointOnNextBlock = BlockUtils.getViablePointsOnBlock(new BlockPos(waypoint.x, waypoint.y, waypoint.z), null, 60, true, true).stream().findAny().orElse(null);
                        if (viablePointOnNextBlock != null) {
                            RotationUtils.smoothLook(RotationUtils.getRotation(viablePointOnNextBlock), GumTuneClientConfig.aotvGemstoneMacroRotationSpeed);
                        }
                    }
                    gemMacroState = GemMacroState.POST_DISMOUNT_ARMADILLO;
                    timestamp = System.currentTimeMillis();
                }
                break;
            case POST_DISMOUNT_ARMADILLO:
                if (GumTuneClient.mc.thePlayer.onGround) {
                    ModUtils.sendMessage("landing took " + (System.currentTimeMillis() - timestamp));
                    timestamp = System.currentTimeMillis();
                    gemMacroState = GemMacroState.AOTV_SETUP;
                    currentIndex = activeList.getNextIndex(currentIndex);
                }
                break;
            case SLEEP_2000:
                if (System.currentTimeMillis() - timestamp > 2000) {
                    gemMacroState = GemMacroState.AOTV_SETUP;
                }
                break;
        }

        previousState = gemMacroState;
    }

    @SubscribeEvent
    public void onPacketReceived(PacketReceivedEvent event) {
        if (!GumTuneClientConfig.aotvGemstoneMacro) return;
        if (LocationUtils.currentIsland != LocationUtils.Island.CRYSTAL_HOLLOWS) return;
        if (!enabled) return;
        if (event.packet instanceof S25PacketBlockBreakAnim) {
            S25PacketBlockBreakAnim blockBreakAnim = (S25PacketBlockBreakAnim) event.packet;
            if (blockBreakAnim.getPosition().equals(current)) {
                currentProgress = blockBreakAnim.getProgress();
            }
        }
    }

    private boolean equipPickaxe() {
        int drillSlot = InventoryUtils.findItemInHotbar("Drill");
        if (drillSlot != -1) {
            GumTuneClient.mc.thePlayer.inventory.currentItem = drillSlot;
            GumTuneClient.mc.getNetHandler().addToSendQueue(new C09PacketHeldItemChange(drillSlot));
            return true;
        }

        int pickaxeSlot = InventoryUtils.findItemInHotbar("Pickaxe");
        if (pickaxeSlot != -1) {
            GumTuneClient.mc.thePlayer.inventory.currentItem = pickaxeSlot;
            GumTuneClient.mc.getNetHandler().addToSendQueue(new C09PacketHeldItemChange(pickaxeSlot));
            return true;
        }

        int gauntletSlot = InventoryUtils.findItemInHotbar("Gauntlet");
        if (gauntletSlot != -1) {
            GumTuneClient.mc.thePlayer.inventory.currentItem = gauntletSlot;
            GumTuneClient.mc.getNetHandler().addToSendQueue(new C09PacketHeldItemChange(gauntletSlot));
            return true;
        }

        return false;
    }

    private void breakBlock(BlockPos blockPos) {
        EnumFacing enumFacing = BlockUtils.calculateEnumfacing(new Vec3(blockPos).add(RandomUtils.randomVec()));
        if (enumFacing != null) {
            GumTuneClient.mc.thePlayer.sendQueue.addToSendQueue(new C07PacketPlayerDigging(
                    C07PacketPlayerDigging.Action.START_DESTROY_BLOCK,
                    blockPos,
                    enumFacing
            ));
        } else {
            GumTuneClient.mc.thePlayer.sendQueue.addToSendQueue(new C07PacketPlayerDigging(
                    C07PacketPlayerDigging.Action.START_DESTROY_BLOCK,
                    blockPos,
                    GumTuneClient.mc.thePlayer.getHorizontalFacing().getOpposite()
            ));
        }
    }

    public static void loadConfig() {
        try {
            Path path = Paths.get("./config/" + GumTuneClient.MODID + "/aotvGemstoneMacroPath.json");
            if (new File(path.toUri()).exists()) {
                allPaths = new Gson().fromJson(new String(Files.readAllBytes(path)), new TypeToken<HashSet<WaypointList>>() {
                }.getType());
            }
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
    }

    public static void saveConfig() {
        try {
            Files.write(Paths.get("./config/" + GumTuneClient.MODID + "/aotvGemstoneMacroPath.json"), new Gson().toJson(allPaths).getBytes(StandardCharsets.UTF_8));
        } catch (IOException ioException) {
            ioException.printStackTrace();
            ModUtils.sendMessage("Failed saving Gemstone Macro path!");
        }
    }

    private boolean canMine(BlockPos blockPos) {
        IBlockState blockState = getBlockState(blockPos);

        if (blockPos.getX() == Math.floor(GumTuneClient.mc.thePlayer.posX) && blockPos.getZ() == Math.floor(GumTuneClient.mc.thePlayer.posZ) && blockPos.getY() < Math.floor(GumTuneClient.mc.thePlayer.posY)) {
            return false;
        }

        if (
                ((
                        (GumTuneClientConfig.aotvGemstoneMinePanes && blockState.getBlock() == Blocks.stained_glass_pane) ||
                                blockState.getBlock() == Blocks.stained_glass
                ) && (
                        GemstoneTypeFilter.amber && blockState.getValue(BlockColored.COLOR) == EnumDyeColor.ORANGE ||
                                GemstoneTypeFilter.jade && blockState.getValue(BlockColored.COLOR) == EnumDyeColor.LIME ||
                                GemstoneTypeFilter.ruby && blockState.getValue(BlockColored.COLOR) == EnumDyeColor.RED ||
                                GemstoneTypeFilter.topaz && blockState.getValue(BlockColored.COLOR) == EnumDyeColor.YELLOW ||
                                GemstoneTypeFilter.sapphire && blockState.getValue(BlockColored.COLOR) == EnumDyeColor.LIGHT_BLUE ||
                                GemstoneTypeFilter.amethyst && blockState.getValue(BlockColored.COLOR) == EnumDyeColor.PURPLE ||
                                GemstoneTypeFilter.jasper && blockState.getValue(BlockColored.COLOR) == EnumDyeColor.MAGENTA
                )) ||
                        GemstoneTypeFilter.mithril && (
                                blockState.getBlock() == Blocks.prismarine ||
                                        blockState.getBlock() == Blocks.wool && blockState.getValue(BlockColored.COLOR) == EnumDyeColor.LIGHT_BLUE
                        )
        ) {
            return !broken.contains(blockPos) && GumTuneClient.mc.thePlayer.getPositionEyes(1f).distanceTo(new Vec3(blockPos.getX() + 0.5, blockPos.getY() + 0.5, blockPos.getZ() + 0.5)) < GumTuneClient.mc.playerController.getBlockReachDistance();
        }

        return false;
    }

    private static IBlockState getBlockState(BlockPos blockPos) {
        return GumTuneClient.mc.theWorld.getBlockState(blockPos);
    }

    public static void addBlockToPath(BlockPos blockPos) {
        WaypointList activeList = getActiveWaypointList();
        if (activeList == null) {
            ModUtils.sendMessage("No active waypoint route selected!");
            return;
        }

        if (activeList.containsValue(blockPos)) {
            activeList.waypoints.remove(Objects.requireNonNull(activeList.getKey(blockPos)));
        } else {
            int emptyIndex = activeList.getEmptyIndex();
            activeList.waypoints.put(emptyIndex, new Waypoint(emptyIndex + "", blockPos));
        }
    }

    private static WaypointList getActiveWaypointList() {
        for (WaypointList waypointList : allPaths) {
            if (waypointList.enabled) return waypointList;
        }

        return null;
    }

    public static void structureCheck() {

    }
}