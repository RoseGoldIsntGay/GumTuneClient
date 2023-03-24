package rosegold.gumtuneclient.modules.render;

import cc.polyfrost.oneconfig.utils.Multithreading;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityArmorStand;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemSkull;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.BlockPos;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.event.world.ChunkEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import rosegold.gumtuneclient.GumTuneClient;
import rosegold.gumtuneclient.config.GumTuneClientConfig;
import rosegold.gumtuneclient.config.pages.FrozenTreasureFilter;
import rosegold.gumtuneclient.events.BlockChangeEvent;
import rosegold.gumtuneclient.events.RenderLivingEntityEvent;
import rosegold.gumtuneclient.utils.LocationUtils;
import rosegold.gumtuneclient.utils.RenderUtils;
import rosegold.gumtuneclient.utils.objects.HighlightBlock;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class ESPs {

    private static final HashMap<Entity, String> highlightedEntities = new HashMap<>();
    private static final HashMap<Entity, HighlightBlock> highlightedEntityBlocks = new HashMap<>();
    private static final ConcurrentHashMap<BlockPos, Color> highlightedBlocks = new ConcurrentHashMap<>();
    public static final ArrayList<BlockPos> frozenTreasures = new ArrayList<>();
    public static final HashSet<Entity> checked = new HashSet<>();
    public static final ConcurrentHashMap<Block, Color> blockEsp = new ConcurrentHashMap<>();

    private static final String FAIRY_SOUL_TEXTURE = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYjk2OTIzYWQyNDczMTAwMDdmNmFlNWQzMjZkODQ3YWQ1Mzg2NGNmMTZjMzU2NWExODFkYzhlNmIyMGJlMjM4NyJ9fX0=";

    @SubscribeEvent
    public void onBlockChange(BlockChangeEvent event) {
        highlightedBlocks.remove(event.pos);

        if (blockEsp.containsKey(event.update.getBlock())) {
            highlightedBlocks.put(event.pos, blockEsp.get(event.update.getBlock()));
        }
    }

    @SubscribeEvent
    public void onChunkLoad(ChunkEvent.Load event) {
        Multithreading.runAsync(() -> handleChunkLoad(event.getChunk()));
    }

    @SubscribeEvent
    public void onChunkUnLoad(ChunkEvent.Unload event) {
        Multithreading.runAsync(() -> handleChunkUnload(event.getChunk()));
    }

    @SubscribeEvent
    public void onRenderEntityLiving(RenderLivingEntityEvent event) {
        if (checked.contains(event.entity)) return;
        if (!GumTuneClientConfig.ESPs) return;
        if (event.entity instanceof EntityArmorStand) {
            if (GumTuneClientConfig.arachneKeeperESP && event.entity.hasCustomName() && isArachneKeeper(event.entity)) {
                List<Entity> possibleEntities = event.entity.getEntityWorld().getEntitiesInAABBexcluding(event.entity, event.entity.getEntityBoundingBox().offset(0, -1, 0), entity -> (!(entity instanceof EntityArmorStand) && entity != GumTuneClient.mc.thePlayer));
                if (!possibleEntities.isEmpty()) {
                    highlightEntity(possibleEntities.get(0), event.entity.getCustomNameTag());
                } else {
                    highlightEntity(event.entity, event.entity.getCustomNameTag());
                }
            }

            if (GumTuneClientConfig.frozenTreasureESP && isFrozenTreasure((EntityArmorStand) event.entity)) {
                BlockPos blockPos = new BlockPos(event.entity.posX, event.entity.posY + 2, event.entity.posZ);
                if (GumTuneClient.mc.theWorld.getBlockState(blockPos).getBlock() == Blocks.ice ||
                        GumTuneClient.mc.theWorld.getBlockState(blockPos).getBlock() == Blocks.packed_ice) {
                    frozenTreasures.add(blockPos);
                    highlightBlock(blockPos, event.entity, event.entity.getCurrentArmor(3).serializeNBT().getCompoundTag("tag").getCompoundTag("display").getTag("Name").toString().replace("\"", ""));
                }
            }

            if (GumTuneClientConfig.fairySoulESP && isFairySoul((EntityArmorStand) event.entity)) {
                highlightEntity(event.entity, "§dFairy Soul");
            }

            checked.add(event.entity);
        }
    }

    @SubscribeEvent
    public void onRenderWorld(RenderWorldLastEvent event) {
        if (!GumTuneClientConfig.ESPs) return;
        GumTuneClient.mc.theWorld.loadedEntityList.forEach(entity -> {
            if (highlightedEntities.containsKey(entity)) {
                if (GumTuneClientConfig.espHighlight) {
                    RenderUtils.renderBoundingBox(entity, event.partialTicks, GumTuneClientConfig.espColor.getRGB());
                }
                if (GumTuneClientConfig.espWaypointText) {
                    RenderUtils.renderWaypointText(highlightedEntities.get(entity), entity.posX, entity.posY + entity.height, entity.posZ, event.partialTicks);
                }
            }
            if (highlightedEntityBlocks.containsKey(entity)) {
                BlockPos blockPos = highlightedEntityBlocks.get(entity).getBlockPos();
                if (GumTuneClientConfig.espHighlight) {
                    RenderUtils.renderEspBox(blockPos, event.partialTicks, GumTuneClientConfig.espColor.getRGB());
                }
                if (GumTuneClientConfig.espWaypointText) {
                    RenderUtils.renderWaypointText(highlightedEntityBlocks.get(entity).getName(), blockPos.getX() + 0.5, blockPos.getY() + 0.5, blockPos.getZ() + 0.5, event.partialTicks);
                }
            }
        });
        if (GumTuneClientConfig.customBlockESP) highlightedBlocks.forEach((blockPos, color) -> RenderUtils.renderEspBox(blockPos, event.partialTicks, color.getRGB()));
    }

    @SubscribeEvent
    public void onTick(TickEvent.ClientTickEvent event) {
        if (GumTuneClient.mc.thePlayer == null) return;
        if (!GumTuneClientConfig.ESPs) return;
        if (GumTuneClient.mc.thePlayer.ticksExisted % 40 == 0) {
            checked.clear();
        }
    }

    @SubscribeEvent
    public void onWorldLoad(WorldEvent.Load event) {
        highlightedEntities.clear();
        highlightedEntityBlocks.clear();
        frozenTreasures.clear();
        checked.clear();
    }

    public static void handleChunkLoad(Chunk chunk) {
        for (int x = 0; x < 16; x++) {
            for (int y = 0; y < 256; y++) {
                for (int z = 0; z < 16; z++) {
                    highlightedBlocks.remove(new BlockPos(chunk.xPosition * 16 + x, y, chunk.zPosition * 16 + z));

                    if (blockEsp.containsKey(chunk.getBlock(x, y, z))) {
                        highlightedBlocks.put(new BlockPos(chunk.xPosition * 16 + x, y, chunk.zPosition * 16 + z), blockEsp.get(chunk.getBlock(x, y, z)));
                    }
                }
            }
        }
    }

    public static void handleChunkUnload(Chunk chunk) {
        for (int x = 0; x < 16; x++) {
            for (int y = 0; y < 256; y++) {
                for (int z = 0; z < 16; z++) {
                    highlightedBlocks.remove(new BlockPos(chunk.xPosition * 16 + x, y, chunk.zPosition * 16 + z));
                }
            }
        }
    }

    private void highlightEntity(Entity entity, String name) {
        highlightedEntities.put(entity, name);
    }

    private void highlightBlock(BlockPos blockPos, Entity entity, String name) {
        highlightedEntityBlocks.put(entity, new HighlightBlock(blockPos, name));
    }

    private boolean isArachneKeeper(Entity entity) {
        return entity.getCustomNameTag().contains("Keeper") && LocationUtils.currentIsland == LocationUtils.Island.SPIDER_DEN;
    }

    private boolean isFrozenTreasure(EntityArmorStand entity) {
        if (LocationUtils.currentIsland != LocationUtils.Island.JERRY_WORKSHOP) return false;
        ItemStack itemStack = entity.getCurrentArmor(3);
        if (itemStack != null && itemStack.serializeNBT().getCompoundTag("tag") != null &&
                itemStack.serializeNBT().getCompoundTag("tag").getCompoundTag("display") != null &&
                itemStack.serializeNBT().getCompoundTag("tag").getCompoundTag("display").getTag("Name") != null) {
            String name = itemStack.serializeNBT().getCompoundTag("tag").getCompoundTag("display").getTag("Name").toString().replace("\"", "");
            return name.contains("Packed Ice") && FrozenTreasureFilter.frozenTreasurePackedIce ||
                    name.contains("Enchanted Ice") && FrozenTreasureFilter.frozenTreasureEnchantedIce ||
                    name.contains("Enchanted Packed Ice") && FrozenTreasureFilter.frozenTreasureEnchantedPackedIce ||
                    name.contains("Ice Bait") && FrozenTreasureFilter.frozenTreasureIceBait ||
                    name.contains("Glowy Chum Bait") && FrozenTreasureFilter.frozenTreasureGlowyChumBait ||
                    name.contains("Glacial Fragment") && FrozenTreasureFilter.frozenTreasureGlacialFragment ||
                    name.contains("White Gift") && FrozenTreasureFilter.frozenTreasureWhiteGift ||
                    name.contains("Green Gift") && FrozenTreasureFilter.frozenTreasureGreenGift ||
                    name.contains("Red Gift") && FrozenTreasureFilter.frozenTreasureRedGift ||
                    name.contains("Glacial Talisman") && FrozenTreasureFilter.frozenTreasureGlacialTalisman;
        }
        return false;
    }

    private boolean isFairySoul(EntityArmorStand entity) {
        ItemStack helmetItemStack = entity.getCurrentArmor(3);
        if (helmetItemStack != null && helmetItemStack.getItem() instanceof ItemSkull) {
            NBTTagList textures = helmetItemStack.serializeNBT().getCompoundTag("tag").getCompoundTag("SkullOwner").getCompoundTag("Properties").getTagList("textures", Constants.NBT.TAG_COMPOUND);
            for (int i = 0; i < textures.tagCount(); i++) {
                if (textures.getCompoundTagAt(i).getString("Value").equals(FAIRY_SOUL_TEXTURE)) {
                    return true;
                }
            }
        }

        return false;
    }

   /* private boolean checkName(String name) {
        String[] split = Main.configFile.rareMobESPFilter.split(",");
        Set<String> entityNames = Stream.of(split).collect(Collectors.toSet());
        return entityNames.stream().anyMatch(name::contains);
    }*/
}
