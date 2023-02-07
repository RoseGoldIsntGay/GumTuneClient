package rosegold.gumtuneclient.modules.mining;

import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import rosegold.gumtuneclient.GumTuneClient;

public class MobxDrill {

    public static boolean isHittingPosition(BlockPos blockPos, ItemStack currentItemHittingBlock, BlockPos currentBlock) {
        boolean flag;
        ItemStack itemstack = GumTuneClient.mc.thePlayer.getHeldItem();
        flag = currentItemHittingBlock == null && itemstack == null;
        if (currentItemHittingBlock != null && itemstack != null) {
            if (itemstack.getTagCompound() != null) {
                String lore = itemstack.getTagCompound().getCompoundTag("display").getTagList("Lore", 8).toString();
                if (lore.contains("GAUNTLET") || lore.contains("DRILL") || lore.contains("PICKAXE")) {
                    return blockPos.equals(currentBlock) &&
                            itemstack.getItem() == currentItemHittingBlock.getItem();
                }
            }
            flag = itemstack.getItem() == currentItemHittingBlock.getItem() && ItemStack.areItemStackTagsEqual(itemstack, currentItemHittingBlock) && (itemstack.isItemStackDamageable() || itemstack.getMetadata() == currentItemHittingBlock.getMetadata());
        }
        return blockPos.equals(currentBlock) && flag;
    }
}
