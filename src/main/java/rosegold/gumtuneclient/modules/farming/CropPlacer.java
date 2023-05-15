package rosegold.gumtuneclient.modules.farming;

import net.minecraft.block.Block;
import net.minecraft.block.BlockPlanks;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Vec3;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import rosegold.gumtuneclient.GumTuneClient;
import rosegold.gumtuneclient.config.GumTuneClientConfig;
import rosegold.gumtuneclient.events.MillisecondEvent;
import rosegold.gumtuneclient.events.PlayerMoveEvent;
import rosegold.gumtuneclient.utils.*;

import java.awt.*;

public class CropPlacer {

    public static Vec3 point;
    public static EnumFacing enumFacing;
    public static BlockPos blockPos;
    private static long lastPlanted = 0;

    @SubscribeEvent
    public void onMillisecond(MillisecondEvent event) {
        if (!GumTuneClientConfig.cropPlacer) return;
        if (GumTuneClient.mc.thePlayer == null || GumTuneClient.mc.theWorld == null || GumTuneClient.mc.playerController == null)
            return;
        if (event.timestamp - lastPlanted > 1000f / GumTuneClientConfig.cropPlacerSpeed) {
            lastPlanted = event.timestamp;
            point = null;
            enumFacing = null;
            EntityPlayerSP player = GumTuneClient.mc.thePlayer;
            blockPos = GumTuneClientConfig.cropPlacerFindingAlgorithm == 0 ?
                    BlockUtils.getClosestBlock(5, GumTuneClientConfig.cropPlacerCropType == 2 ? 5 : 0, 4, this::canPlantOnBlock) :
                    BlockUtils.getFurthestBlock(5, GumTuneClientConfig.cropPlacerCropType == 2 ? 5 : 0, 4, this::canPlantOnBlock);
            if (blockPos != null) {
                int slot;
                switch (GumTuneClientConfig.cropPlacerCropType) {
                    case 0:
                        slot = InventoryUtils.findItemInHotbar("Cane");
                        if (slot != -1) {
                            enumFacing = EnumFacing.UP;
                            player.inventory.currentItem = slot;
                            point = new Vec3(blockPos.getX() + 0.5, blockPos.getY() + 0.99, blockPos.getZ() + 0.5);
                        }
                        break;
                    case 1:
                        slot = InventoryUtils.findItemInHotbar("Cactus");
                        if (slot != -1) {
                            enumFacing = EnumFacing.UP;
                            player.inventory.currentItem = slot;
                            point = new Vec3(blockPos.getX() + 0.5, blockPos.getY() + 0.99, blockPos.getZ() + 0.5);
                        }
                        break;
                    case 2:
                        slot = InventoryUtils.findItemInHotbar("Cocoa");
                        enumFacing = getClosestEnumFacing(blockPos);
                        if (slot != -1 && enumFacing != null) {
                            player.inventory.currentItem = slot;
                            point = new Vec3(blockPos.getX() + 0.5, blockPos.getY() + 0.5, blockPos.getZ() + 0.5).add(VectorUtils.scaleVec(enumFacing.getDirectionVec(),0.49f));
                        }
                        break;
                    case 3:
                        slot = InventoryUtils.findItemInHotbar("Potato");
                        if (slot != -1) {
                            enumFacing = EnumFacing.UP;
                            player.inventory.currentItem = slot;
                            point = new Vec3(blockPos.getX() + 0.5, blockPos.getY() + 0.99, blockPos.getZ() + 0.5);
                        }
                        break;
                    case 4:
                        slot = InventoryUtils.findItemInHotbar("Carrot");
                        if (slot != -1) {
                            enumFacing = EnumFacing.UP;
                            player.inventory.currentItem = slot;
                            point = new Vec3(blockPos.getX() + 0.5, blockPos.getY() + 0.99, blockPos.getZ() + 0.5);
                        }
                        break;
                    case 5:
                        slot = InventoryUtils.findItemInHotbar("Seeds");
                        if (slot != -1) {
                            enumFacing = EnumFacing.UP;
                            player.inventory.currentItem = slot;
                            point = new Vec3(blockPos.getX() + 0.5, blockPos.getY() + 0.99, blockPos.getZ() + 0.5);
                        }
                        break;
                    case 6:
                        slot = InventoryUtils.findItemInHotbar("Pumpkin Seeds");
                        if (slot != -1) {
                            enumFacing = EnumFacing.UP;
                            player.inventory.currentItem = slot;
                            point = new Vec3(blockPos.getX() + 0.5, blockPos.getY() + 0.99, blockPos.getZ() + 0.5);
                        }
                        break;
                    case 7:
                        slot = InventoryUtils.findItemInHotbar("Melon Seeds");
                        if (slot != -1) {
                            enumFacing = EnumFacing.UP;
                            player.inventory.currentItem = slot;
                            point = new Vec3(blockPos.getX() + 0.5, blockPos.getY() + 0.99, blockPos.getZ() + 0.5);
                        }
                        break;
                    case 8:
                        slot = InventoryUtils.findItemInHotbar("Nether Wart");
                        if (slot != -1) {
                            enumFacing = EnumFacing.UP;
                            player.inventory.currentItem = slot;
                            point = new Vec3(blockPos.getX() + 0.5, blockPos.getY() + 0.99, blockPos.getZ() + 0.5);
                        }
                        break;
                    case 9:
                        slot = InventoryUtils.findItemInHotbar("Mushroom");
                        if (slot != -1) {
                            enumFacing = EnumFacing.UP;
                            player.inventory.currentItem = slot;
                            point = new Vec3(blockPos.getX() + 0.5, blockPos.getY() + 0.99, blockPos.getZ() + 0.5);
                        }
                        break;
                }

                if (enumFacing != null && point != null) {
                    GumTuneClient.mc.playerController.onPlayerRightClick(
                            player,
                            GumTuneClient.mc.theWorld,
                            player.inventory.getCurrentItem(),
                            blockPos,
                            enumFacing,
                            point
                    );
                }
            } else {
                enumFacing = null;
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.NORMAL)
    public void onUpdatePre(PlayerMoveEvent.Pre pre) {
        if (!GumTuneClientConfig.cropPlacer) return;
        if (point == null) return;
        RotationUtils.look(RotationUtils.getRotation(point));
    }

    @SubscribeEvent
    public void onRender(RenderWorldLastEvent event) {
        if (!GumTuneClientConfig.cropPlacer || enumFacing == null || blockPos == null) return;
        RenderUtils.renderAxisSquare(blockPos, enumFacing, event.partialTicks, Color.CYAN.getRGB());
    }

    private boolean canPlantOnBlock(BlockPos blockPos) {

        switch (GumTuneClientConfig.cropPlacerCropType) {
            case 0:
                if (!BlockUtils.canBlockBeSeen(blockPos, GumTuneClient.mc.playerController.getBlockReachDistance(), new Vec3(0, 0.49, 0), x -> false))
                    return false;
                return (getBlock(blockPos) == Blocks.grass || getBlock(blockPos) == Blocks.dirt || getBlock(blockPos) == Blocks.sand) && getBlock(blockPos.up()) == Blocks.air &&
                        blockPos.getY() < GumTuneClient.mc.thePlayer.getPositionEyes(1f).yCoord &&
                        (getBlock(blockPos.south()) == Blocks.water || getBlock(blockPos.south()) == Blocks.flowing_water ||
                                getBlock(blockPos.north()) == Blocks.water || getBlock(blockPos.north()) == Blocks.flowing_water ||
                                getBlock(blockPos.east()) == Blocks.water || getBlock(blockPos.east()) == Blocks.flowing_water ||
                                getBlock(blockPos.west()) == Blocks.water || getBlock(blockPos.west()) == Blocks.flowing_water);
            case 1:
                if (!BlockUtils.canBlockBeSeen(blockPos, GumTuneClient.mc.playerController.getBlockReachDistance(), new Vec3(0, 0.49, 0), x -> false))
                    return false;
                return getBlock(blockPos) == Blocks.sand && getBlock(blockPos.up()) == Blocks.air &&
                        getBlock(blockPos.up().south()) == Blocks.air &&
                        getBlock(blockPos.up().north()) == Blocks.air &&
                        getBlock(blockPos.up().east()) == Blocks.air &&
                        getBlock(blockPos.up().west()) == Blocks.air;
            case 2:
                if (!BlockUtils.canBlockBeSeen(blockPos, GumTuneClient.mc.playerController.getBlockReachDistance(), new Vec3(0, 0, 0), x -> x == Blocks.cocoa))
                    return false;
                EnumFacing enumFacing = getClosestEnumFacing(blockPos);
                return enumFacing != null && getBlock(blockPos) == Blocks.log &&
                        getBlockState(blockPos).getValue(BlockPlanks.VARIANT) == BlockPlanks.EnumType.JUNGLE &&
                        getBlock(blockPos.add(enumFacing.getDirectionVec())) == Blocks.air;
            case 3:
            case 4:
            case 5:
            case 6:
            case 7:
                return getBlock(blockPos) == Blocks.farmland && getBlock(blockPos.add(0, 1, 0)) == Blocks.air;
            case 8:
                return getBlock(blockPos) == Blocks.soul_sand && getBlock(blockPos.add(0, 1, 0)) == Blocks.air;
            case 9:
                return getBlock(blockPos) == Blocks.mycelium && getBlock(blockPos.add(0, 1, 0)) == Blocks.air;
        }
        return false;
    }

    private Block getBlock(BlockPos blockPos) {
        return GumTuneClient.mc.theWorld.getBlockState(blockPos).getBlock();
    }

    private IBlockState getBlockState(BlockPos blockPos) {
        return GumTuneClient.mc.theWorld.getBlockState(blockPos);
    }

    private EnumFacing getClosestEnumFacing(BlockPos blockPos) {
        Vec3 blockVec = new Vec3(blockPos.getX() + 0.5, blockPos.getY() + 0.5, blockPos.getZ() + 0.5);
        Vec3 blockDiff = GumTuneClient.mc.thePlayer.getPositionVector().subtract(blockVec);

        if (Math.abs(blockDiff.xCoord) > Math.abs(blockDiff.zCoord)) {
            if (blockDiff.xCoord > 0.5) {
                return EnumFacing.EAST;
            } else if (blockDiff.xCoord < -0.5) {
                return EnumFacing.WEST;
            }
        } else {
            if (blockDiff.zCoord > 0.5) {
                return EnumFacing.SOUTH;
            } else if (blockDiff.zCoord < -0.5) {
                return EnumFacing.NORTH;
            }
        }

        return null;
    }
}
