package rosegold.gumtuneclient.modules.world;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.init.Blocks;
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
import rosegold.gumtuneclient.utils.*;

import java.awt.*;
import java.util.ArrayList;

public class Nuker {
    private boolean enabled;
    private final ArrayList<BlockPos> broken = new ArrayList<>();
    private BlockPos blockPos;
    private long lastBroken = 0;
    private BlockPos current;
    private final ArrayList<BlockPos> blocksInRange = new ArrayList<>();

    @SubscribeEvent
    public void onKey(InputEvent.KeyInputEvent event) {
        if (Keyboard.getEventKeyState()) return;
        int eventKey = Keyboard.getEventKey();
        ArrayList<Integer> keyBinds = GumTuneClientConfig.nukerKeyBind.getKeyBinds();
        if (keyBinds.size() > 0 && keyBinds.get(0) == eventKey) {
            enabled = !enabled;
            ModUtils.sendMessage((enabled ? "Enabled" : "Disabled") + " Nuker");
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
                            mineBlock(blockPos);
                        }
                    } else {
                        pinglessMineBlock(blockPos);
                        current = null;
                    }
                    return;
                }
            }

            blockPos = BlockUtils.getClosestBlock(4, GumTuneClientConfig.nukerHeight, GumTuneClientConfig.nukerDepth, this::canMine);

            if (blockPos != null) {
                if (current != null && current.compareTo(blockPos) != 0) {
                    current = null;
                }
                if (isSlow(getBlockState(blockPos))) {
                    if (current == null) {
                        mineBlock(blockPos);
                    }
                } else {
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
        if (GumTuneClientConfig.nukerPreview) blocksInRange.forEach(bp -> RenderUtils.renderEspBox(bp, event.partialTicks, Color.CYAN.getRGB()));
    }

    @SubscribeEvent(priority = EventPriority.NORMAL)
    public void onUpdatePre(PlayerMoveEvent.Pre pre) {
        if (!isEnabled()) return;
        if (!GumTuneClientConfig.serverSideNukerRotations) return;
        if (blockPos != null) {
            RotationUtils.serverLook(RotationUtils.getRotationToBlock(blockPos));
        } else {
            RotationUtils.resetServerLook();
        }
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

    private boolean isEnabled() {
        return enabled && GumTuneClientConfig.nuker && GumTuneClient.mc.thePlayer != null && GumTuneClient.mc.theWorld != null;
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
        if (canMineBlockType(getBlockState(blockPos)) && !broken.contains(blockPos) && blocksInRange.contains(blockPos)) {
            if (GumTuneClientConfig.nukerShape == 1) {
                EntityPlayerSP player = GumTuneClient.mc.thePlayer;
                EnumFacing axis = player.getHorizontalFacing();
                Vec3i ray = new Vec3i((int) Math.floor(player.posX), 0, (int) Math.floor(player.posZ));
                for (int i = 0; i < 5; i++) {
                    ray = addVector(ray, axis.getDirectionVec());
                    if (ray.getX() == blockPos.getX() && ray.getZ() == blockPos.getZ()) {
                        return true;
                    }
                }
                return false;
            }
            return true;
        }
        return false;
    }

    private boolean canMineBlockType(IBlockState blockState) {
        Block block = blockState.getBlock();
        if (LocationUtils.currentIsland == LocationUtils.Island.CRYSTAL_HOLLOWS &&
                NukerBlockFilter.nukerBlockFilterHardstone && block == Blocks.stone) return true;
        if (LocationUtils.currentIsland == LocationUtils.Island.CRYSTAL_HOLLOWS &&
                NukerBlockFilter.nukerBlockFilterGemstones && (block == Blocks.stained_glass_pane ||
                block == Blocks.stained_glass)) return true;
        if ((LocationUtils.currentIsland == LocationUtils.Island.CRYSTAL_HOLLOWS ||
                LocationUtils.currentIsland == LocationUtils.Island.DWARVEN_MINES) &&
                NukerBlockFilter.nukerBlockFilterMithril && (block == Blocks.prismarine || block == Blocks.wool ||
                block == Blocks.stained_hardened_clay || block == Blocks.gold_block ||
                block == Blocks.stained_glass_pane || block == Blocks.stained_glass)) return true;

        if (LocationUtils.currentIsland == LocationUtils.Island.CRIMSON_ISLE &&
                NukerBlockFilter.nukerBlockFilterExcavatables && (block == Blocks.sand || block == Blocks.mycelium)) return true;
        if (NukerBlockFilter.nukerBlockFilterGold && block == Blocks.gold_block) return true;
        if (NukerBlockFilter.nukerBlockFilterStone && block == Blocks.stone) return true;
        if (NukerBlockFilter.nukerBlockFilterOres && (block == Blocks.coal_ore || block == Blocks.lapis_ore ||
                block == Blocks.iron_ore || block == Blocks.gold_ore || block == Blocks.redstone_ore ||
                block == Blocks.lit_redstone_ore || block == Blocks.diamond_ore || block == Blocks.emerald_ore ||
                block == Blocks.quartz_ore)) return true;
        if (NukerBlockFilter.nukerBlockFilterSand && NukerBlockFilter.nukerBlockFilterWood && block == Blocks.chest) return true;
        if (NukerBlockFilter.nukerBlockFilterCrops && (block == Blocks.carrots || block == Blocks.potatoes ||
                block == Blocks.reeds || block == Blocks.cocoa || block == Blocks.melon_block ||
                block == Blocks.pumpkin || block == Blocks.cactus || block == Blocks.brown_mushroom ||
                block == Blocks.red_mushroom || block == Blocks.nether_wart || block == Blocks.wheat)) return true;
        if (NukerBlockFilter.nukerBlockFilterWood && block == Blocks.log ||
                NukerBlockFilter.nukerBlockFilterWood && block == Blocks.log2) return true;
        if (NukerBlockFilter.nukerBlockFilterSand && block == Blocks.sand) return true;
        if (NukerBlockFilter.nukerBlockFilterGlowstone && block == Blocks.glowstone) return true;
        return NukerBlockFilter.nukerBlockFilterNetherrack && block == Blocks.netherrack;
    }

    private boolean isSlow(IBlockState blockState) {
        Block block = blockState.getBlock();
        return block == Blocks.prismarine || block == Blocks.wool || block == Blocks.stained_hardened_clay ||
                block == Blocks.gold_block || block == Blocks.stained_glass_pane || block == Blocks.stained_glass ||
                block == Blocks.glowstone || block == Blocks.chest;
    }

    private Vec3i addVector(Vec3i vec1, Vec3i vec2) {
        return new Vec3i(vec1.getX() + vec2.getX(), vec1.getY() + vec2.getY(), vec1.getZ() + vec2.getZ());
    }

    private IBlockState getBlockState(BlockPos blockPos) {
        return GumTuneClient.mc.theWorld.getBlockState(blockPos);
    }
}
