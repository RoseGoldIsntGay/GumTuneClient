package rosegold.gumtuneclient.modules.world;

import net.minecraft.block.Block;
import net.minecraft.block.BlockColored;
import net.minecraft.block.BlockStone;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.init.Blocks;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.util.*;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.lwjgl.input.Keyboard;
import rosegold.gumtuneclient.GumTuneClient;
import rosegold.gumtuneclient.config.GumTuneClientConfig;
import rosegold.gumtuneclient.config.pages.NukerBlockFilter;
import rosegold.gumtuneclient.events.MillisecondEvent;
import rosegold.gumtuneclient.events.PlayerMoveEvent;
import rosegold.gumtuneclient.events.SecondEvent;
import rosegold.gumtuneclient.modules.render.ESPs;
import rosegold.gumtuneclient.utils.*;

import java.awt.*;
import java.util.ArrayList;

public class Nuker {
    public static boolean enabled;
    private final ArrayList<BlockPos> broken = new ArrayList<>();
    public static BlockPos blockPos;
    private long lastBroken = 0;
    private BlockPos current;
    private final ArrayList<BlockPos> blocksInRange = new ArrayList<>();

    @SubscribeEvent
    public void onKey(InputEvent.KeyInputEvent event) {
        if (!GumTuneClientConfig.nuker) return;
        if (Keyboard.getEventKeyState()) return;
        int eventKey = Keyboard.getEventKey();
        ArrayList<Integer> keyBinds = GumTuneClientConfig.nukerKeyBind.getKeyBinds();
        if (keyBinds.size() > 0 && keyBinds.get(0) == eventKey) {
            enabled = !enabled;
            ModUtils.sendMessage((enabled ? "Enabled" : "Disabled") + " Nuker");
            if (enabled) {
                if (NukerBlockFilter.nukerBlockFilterFrozenTreasure) {
                    if (GumTuneClientConfig.frozenTreasureESP) {
                        ModUtils.sendMessage("&cIf nuker doesn't work for frozen treasures, try disabling entity culling in patcher!");
                    } else {
                        ModUtils.sendMessage("&cEnable frozen treasure ESP for frozen treasure nuker to work!");
                    }
                }
                if (GumTuneClientConfig.nukerShape == 2 && (NukerBlockFilter.nukerBlockFilterHardstone || NukerBlockFilter.nukerBlockFilterStone) && !GumTuneClientConfig.phaseCameraThroughBlocks) {
                    ModUtils.sendMessage("&cRecommended to turn on Phase Camera Through Blocks when using tunnel shape and hardstone filter!");
                }
            }
        }
    }

    @SubscribeEvent
    public void onSecond(SecondEvent event) {
        if (!isEnabled()) return;
        if (broken.size() > 0) broken.clear();
    }

    @SubscribeEvent
    public void onTick(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.END) return;
        if (!isEnabled()) {
            if (current != null && GumTuneClient.mc.thePlayer != null) {
                GumTuneClient.mc.thePlayer.sendQueue.addToSendQueue(new C07PacketPlayerDigging(
                        C07PacketPlayerDigging.Action.STOP_DESTROY_BLOCK,
                        blockPos,
                        EnumFacing.DOWN)
                );
            }
            current = null;
            return;
        }
        blocksInRange.clear();
        EntityPlayerSP player =  GumTuneClient.mc.thePlayer;
        BlockPos playerPos = new BlockPos((int) Math.floor(player.posX), (int) Math.floor(player.posY) + 1, (int) Math.floor(player.posZ));
        Vec3i vec3Top = new Vec3i(4, GumTuneClientConfig.nukerHeight, 4);
        Vec3i vec3Bottom = new Vec3i(4, GumTuneClientConfig.nukerDepth, 4);

        for (BlockPos blockPos : BlockPos.getAllInBox(playerPos.subtract(vec3Bottom), playerPos.add(vec3Top))) {
            Vec3 target = new Vec3(blockPos.getX() + 0.5, blockPos.getY() + 0.5, blockPos.getZ() + 0.5);
            if (Math.abs(RotationUtils.wrapAngleTo180(RotationUtils.fovToVec3(target) - RotationUtils.wrapAngleTo180(GumTuneClient.mc.thePlayer.rotationYaw))) < (float) GumTuneClientConfig.nukerFieldOfView / 2) blocksInRange.add(blockPos);
        }
        if (current != null) PlayerUtils.swingHand(null);
    }

    @SubscribeEvent
    public void onMillisecond(MillisecondEvent event) {
        if (!isEnabled()) {
            current = null;
            if (broken.size() > 0) broken.clear();
            return;
        }
        if (event.timestamp - lastBroken > 1000f / GumTuneClientConfig.nukerSpeed) {
            lastBroken = event.timestamp;
            if (GumTuneClientConfig.nukerShape == 1) {
                if (broken.size() > 6) broken.clear();
            } else {
                if (broken.size() > GumTuneClientConfig.nukerPinglessCutoff) broken.clear();
            }

            if (GumTuneClientConfig.mineBlocksInFront) {
                blockPos = blockInFront();

                if (blockPos != null) {
                    if (current != null && current.compareTo(blockPos) != 0) {
                        current = null;
                    }
                    if (isSlow(getBlockState(blockPos))) {
                        if (current == null) {
                            if (GumTuneClientConfig.smoothServerSideRotations && (GumTuneClientConfig.powderChestPauseNukerMode != 2 || PowderChestSolver.particle == null)) {
                                RotationUtils.serverSmoothLook(RotationUtils.getRotation(blockPos), 1000L / GumTuneClientConfig.nukerSpeed);
                            }
                            mineBlock(blockPos);
                        }
                    } else {
                        if (GumTuneClientConfig.smoothServerSideRotations && (GumTuneClientConfig.powderChestPauseNukerMode != 2 || PowderChestSolver.particle == null)) {
                            RotationUtils.serverSmoothLook(RotationUtils.getRotation(blockPos), 1000L / GumTuneClientConfig.nukerSpeed);
                        }
                        pinglessMineBlock(blockPos);
                        current = null;
                    }
                    return;
                }
            }

            switch (GumTuneClientConfig.nukerAlgorithm) {
                case 0:
                    blockPos = BlockUtils.getClosestBlock(4, GumTuneClientConfig.nukerHeight, GumTuneClientConfig.nukerDepth, this::canMine);
                    break;
                case 1:
                    blockPos = BlockUtils.getEasiestBlock(4, GumTuneClientConfig.nukerHeight, GumTuneClientConfig.nukerDepth, this::canMine);
                    break;
            }


            if (blockPos != null) {
                if (current != null && current.compareTo(blockPos) != 0) {
                    current = null;
                }
                if (isSlow(getBlockState(blockPos))) {
                    if (current == null) {
                        if (GumTuneClientConfig.smoothServerSideRotations && (GumTuneClientConfig.powderChestPauseNukerMode != 2 || PowderChestSolver.particle == null)) {
                            RotationUtils.serverSmoothLook(RotationUtils.getRotation(blockPos), 1000L / GumTuneClientConfig.nukerSpeed);
                        }
                        mineBlock(blockPos);
                    }
                } else {
                    if (GumTuneClientConfig.smoothServerSideRotations && (GumTuneClientConfig.powderChestPauseNukerMode != 2 || PowderChestSolver.particle == null)) {
                        RotationUtils.serverSmoothLook(RotationUtils.getRotation(blockPos), 1000L / GumTuneClientConfig.nukerSpeed);
                    }
                    pinglessMineBlock(blockPos);
                    current = null;
                }
            } else {
                current = null;
            }
        }
    }

    @SubscribeEvent
    public void onRender(RenderWorldLastEvent event) {
        if (!isEnabled()) return;
        RenderUtils.renderEspBox(blockPos, event.partialTicks, Color.GRAY.getRGB());
        if (GumTuneClientConfig.nukerPreview) blocksInRange.forEach(bp -> RenderUtils.renderEspBox(bp, event.partialTicks, Color.CYAN.getRGB(), 0.1f));
    }

    @SubscribeEvent(priority = EventPriority.NORMAL)
    public void onUpdatePre(PlayerMoveEvent.Pre pre) {
        if (!isEnabled()) return;
        if (!GumTuneClientConfig.serverSideNukerRotations) return;
        if (blockPos == null) return;
        if (GumTuneClientConfig.smoothServerSideRotations) return;
        if (GumTuneClientConfig.powderChestPauseNukerMode == 2 && PowderChestSolver.particle != null) return;
        RotationUtils.look(RotationUtils.getRotation(blockPos));
    }

    private void mineBlock(BlockPos blockPos) {
        breakBlock(blockPos);
        current = blockPos;
    }

    private void pinglessMineBlock(BlockPos blockPos) {
        PlayerUtils.swingHand(null);
        breakBlock(blockPos);
        broken.add(blockPos);
    }

    private void breakBlock(BlockPos blockPos) {
        MovingObjectPosition objectMouseOver = GumTuneClient.mc.objectMouseOver;
        objectMouseOver.hitVec = new Vec3(blockPos);
        if (objectMouseOver.sideHit != null) {
            GumTuneClient.mc.thePlayer.sendQueue.addToSendQueue(new C07PacketPlayerDigging(
                    C07PacketPlayerDigging.Action.START_DESTROY_BLOCK,
                    blockPos,
                    objectMouseOver.sideHit)
            );
        }
    }

    public static boolean isEnabled() {
        return (GumTuneClientConfig.powderChestPauseNukerMode != 1 || PowderChestSolver.particle == null) && enabled && GumTuneClientConfig.nuker && GumTuneClient.mc.thePlayer != null && GumTuneClient.mc.theWorld != null;
    }

    private BlockPos blockInFront() {
        EntityPlayerSP player = GumTuneClient.mc.thePlayer;
        BlockPos playerPos = new BlockPos((int) Math.floor(player.posX), (int) Math.floor(player.posY), (int) Math.floor(player.posZ));
        Vec3i axisVector = player.getHorizontalFacing().getDirectionVec();

        if (getBlockState(playerPos).getBlock() != Blocks.air && getBlockState(playerPos).getBlock() != Blocks.bedrock
                && !broken.contains(playerPos)) {
            return playerPos;
        }
        if (getBlockState(playerPos.add(new Vec3i(0, 1, 0))).getBlock() != Blocks.air &&
                getBlockState(playerPos).getBlock() != Blocks.bedrock && !broken.contains(playerPos.add(new Vec3i(0, 1, 0)))) {
            return playerPos.add(new Vec3i(0, 1, 0));
        }
        if (getBlockState(playerPos.add(axisVector)).getBlock() != Blocks.air && getBlockState(playerPos).getBlock() != Blocks.bedrock
                && !broken.contains(playerPos.add(axisVector))) {
            return playerPos.add(axisVector);
        }
        if (getBlockState(playerPos.add(axisVector).add(new Vec3i(0, 1, 0))).getBlock() != Blocks.air &&
                getBlockState(playerPos).getBlock() != Blocks.bedrock &&
                !broken.contains(playerPos.add(axisVector).add(new Vec3i(0, 1, 0)))) {
            return playerPos.add(axisVector).add(new Vec3i(0, 1, 0));
        }
        return null;
    }

    private boolean canMine(BlockPos blockPos) {
        if (canMineBlockType(blockPos) && !broken.contains(blockPos) && blocksInRange.contains(blockPos)) {
            EntityPlayerSP player = GumTuneClient.mc.thePlayer;
            EnumFacing axis = player.getHorizontalFacing();
            Vec3i ray = new Vec3i((int) Math.floor(player.posX), 0, (int) Math.floor(player.posZ));

            switch (GumTuneClientConfig.nukerShape) {
                case 1:
                    for (int i = 0; i < 5; i++) {
                        ray = VectorUtils.addVector(ray, axis.getDirectionVec());
                        if (ray.getX() == blockPos.getX() && ray.getZ() == blockPos.getZ()) {
                            return true;
                        }
                    }

                    return false;
                case 2:
                    for (int i = 0; i < 5; i++) {
                        ray = VectorUtils.addVector(ray, axis.getDirectionVec());
                        if (ray.getX() == blockPos.getX() && ray.getZ() == blockPos.getZ()) {
                            return true;
                        }
                        if (axis.getAxis() == EnumFacing.Axis.Z) {
                            if (ray.getX() + 2 == blockPos.getX() && ray.getZ() == blockPos.getZ()) {
                                return true;
                            }
                            if (ray.getX() - 2 == blockPos.getX() && ray.getZ() == blockPos.getZ()) {
                                return true;
                            }
                        } else if (axis.getAxis() == EnumFacing.Axis.X) {
                            if (ray.getX() == blockPos.getX() && ray.getZ() + 2 == blockPos.getZ()) {
                                return true;
                            }
                            if (ray.getX() == blockPos.getX() && ray.getZ() - 2 == blockPos.getZ()) {
                                return true;
                            }
                        }
                    }

                    return false;
            }

            return true;
        }

        return false;
    }

    private boolean canMineBlockType(BlockPos bp) {
        IBlockState blockState = getBlockState(bp);
        Block block = blockState.getBlock();
        if (LocationUtils.currentIsland == LocationUtils.Island.CRYSTAL_HOLLOWS &&
                NukerBlockFilter.nukerBlockFilterHardstone &&
                (block == Blocks.stone || block == Blocks.stained_hardened_clay)) return true;

        if (LocationUtils.currentIsland == LocationUtils.Island.CRYSTAL_HOLLOWS &&
                NukerBlockFilter.nukerBlockFilterGemstones &&
                (block == Blocks.stained_glass_pane ||
                block == Blocks.stained_glass)) return true;

        if (NukerBlockFilter.nukerBlockFilterMithril &&
                (
                        (LocationUtils.currentIsland == LocationUtils.Island.CRYSTAL_HOLLOWS &&
                                (block == Blocks.prismarine ||
                                (block == Blocks.wool && blockState.getValue(BlockColored.COLOR) == EnumDyeColor.CYAN))
                        ) || (LocationUtils.currentIsland == LocationUtils.Island.DWARVEN_MINES &&
                                (block == Blocks.prismarine ||
                                block == Blocks.wool ||
                                block == Blocks.stained_hardened_clay ||
                                (block == Blocks.stone && blockState.getValue(BlockStone.VARIANT) == BlockStone.EnumType.DIORITE_SMOOTH))
                        )
                )
        ) return true;

        if (LocationUtils.currentIsland == LocationUtils.Island.CRIMSON_ISLE &&
                NukerBlockFilter.nukerBlockFilterExcavatables &&
                (block == Blocks.sand ||
                block == Blocks.mycelium)) return true;

        if (NukerBlockFilter.nukerBlockFilterGold &&
                block == Blocks.gold_block) return true;

        if (NukerBlockFilter.nukerBlockFilterStone &&
                (block == Blocks.stone ||
                block == Blocks.cobblestone)) return true;

        if (NukerBlockFilter.nukerBlockFilterOres &&
                (block == Blocks.coal_ore ||
                block == Blocks.lapis_ore ||
                block == Blocks.iron_ore ||
                block == Blocks.gold_ore ||
                block == Blocks.redstone_ore ||
                block == Blocks.lit_redstone_ore ||
                block == Blocks.diamond_ore ||
                block == Blocks.emerald_ore ||
                block == Blocks.quartz_ore)) return true;

        if (NukerBlockFilter.nukerBlockFilterSand &&
                NukerBlockFilter.nukerBlockFilterWood &&
                block == Blocks.chest) return true;

        if (NukerBlockFilter.nukerBlockFilterCrops &&
                (block == Blocks.carrots ||
                block == Blocks.potatoes ||
                block == Blocks.reeds ||
                block == Blocks.cocoa ||
                block == Blocks.melon_block ||
                block == Blocks.pumpkin ||
                block == Blocks.cactus ||
                block == Blocks.brown_mushroom ||
                block == Blocks.red_mushroom ||
                block == Blocks.nether_wart ||
                block == Blocks.wheat)) return true;

        if (NukerBlockFilter.nukerBlockFilterWood &&
                (block == Blocks.log ||
                block == Blocks.log2)) return true;

        if (NukerBlockFilter.nukerBlockFilterSand &&
                block == Blocks.sand) return true;

        if (NukerBlockFilter.nukerBlockFilterGlowstone &&
                block == Blocks.glowstone) return true;

        if (NukerBlockFilter.nukerBlockFilterIce &&
                block == Blocks.ice) return true;

        if (NukerBlockFilter.nukerBlockFilterFrozenTreasure &&
                LocationUtils.currentIsland == LocationUtils.Island.JERRY_WORKSHOP &&
                ESPs.frozenTreasures.contains(bp)) {
            ESPs.frozenTreasures.remove(bp);
            ESPs.checked.clear(); // make nuker faster
            return true;
        }

        return NukerBlockFilter.nukerBlockFilterNetherrack &&
                block == Blocks.netherrack;
    }

    private boolean isSlow(IBlockState blockState) {
        Block block = blockState.getBlock();
        return block == Blocks.prismarine || block == Blocks.wool || block == Blocks.stained_hardened_clay ||
                block == Blocks.gold_block || block == Blocks.stained_glass_pane || block == Blocks.stained_glass ||
                block == Blocks.glowstone || block == Blocks.chest;
    }

    private IBlockState getBlockState(BlockPos blockPos) {
        return GumTuneClient.mc.theWorld.getBlockState(blockPos);
    }
}
