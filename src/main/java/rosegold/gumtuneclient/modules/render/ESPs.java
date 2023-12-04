package rosegold.gumtuneclient.modules.render;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityArmorStand;
import net.minecraft.entity.monster.*;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemSkull;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.play.server.S2APacketParticles;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import rosegold.gumtuneclient.GumTuneClient;
import rosegold.gumtuneclient.config.GumTuneClientConfig;
import rosegold.gumtuneclient.config.pages.FrozenTreasureFilter;
import rosegold.gumtuneclient.config.pages.RiftESPs;
import rosegold.gumtuneclient.events.PacketReceivedEvent;
import rosegold.gumtuneclient.events.RenderLivingEntityEvent;
import rosegold.gumtuneclient.utils.LocationUtils;
import rosegold.gumtuneclient.utils.RenderUtils;
import rosegold.gumtuneclient.utils.StringUtils;
import rosegold.gumtuneclient.utils.objects.BlockPosition;
import rosegold.gumtuneclient.utils.objects.HighlightBlock;
import rosegold.gumtuneclient.utils.objects.HighlightEntity;

import java.awt.*;
import java.util.List;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class ESPs {

    private static final HashMap<Entity, HighlightEntity> highlightedEntities = new HashMap<>();
    private static final HashMap<Entity, HighlightBlock> highlightedEntityBlocks = new HashMap<>();
    public static final ArrayList<BlockPos> frozenTreasures = new ArrayList<>();
    public static final HashSet<Entity> checked = new HashSet<>();
    public static final ConcurrentHashMap<BlockPosition, Long> enderNodes = new ConcurrentHashMap<>();
    private static final String FAIRY_SOUL_TEXTURE = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYjk2OTIzYWQyNDczMTAwMDdmNmFlNWQzMjZkODQ3YWQ1Mzg2NGNmMTZjMzU2NWExODFkYzhlNmIyMGJlMjM4NyJ9fX0=";
    private static final String[] SHY_TEXTURES = {
            "ewogICJ0aW1lc3RhbXAiIDogMTY0NzU5MzA4NzU3OCwKICAicHJvZmlsZUlkIiA6ICI4N2RlZmVhMTQwMWQ0MzYxODFhNmNhOWI3ZGQ2ODg0MyIsCiAgInByb2ZpbGVOYW1lIiA6ICJCdm5ueUJ2biIsCiAgInNpZ25hdHVyZVJlcXVpcmVkIiA6IHRydWUsCiAgInRleHR1cmVzIiA6IHsKICAgICJTS0lOIiA6IHsKICAgICAgInVybCIgOiAiaHR0cDovL3RleHR1cmVzLm1pbmVjcmFmdC5uZXQvdGV4dHVyZS84Mjc3MWQ2ZWM0N2EyM2Y4MTZlNzhjNzgxMzBkYTVmNGZhYzQ1ZjlhODM0YTk4YzU1MWUzZGRiNDVkMzcyMWY2IiwKICAgICAgIm1ldGFkYXRhIiA6IHsKICAgICAgICAibW9kZWwiIDogInNsaW0iCiAgICAgIH0KICAgIH0KICB9Cn0=",
            "ewogICJ0aW1lc3RhbXAiIDogMTY0NzU4NzkyNDI4NywKICAicHJvZmlsZUlkIiA6ICJmMTYwZTMxMzJjYWM0YjRiOWM5OTk2NDQ1OGIxOWM0ZSIsCiAgInByb2ZpbGVOYW1lIiA6ICJUb255S0tLIiwKICAic2lnbmF0dXJlUmVxdWlyZWQiIDogdHJ1ZSwKICAidGV4dHVyZXMiIDogewogICAgIlNLSU4iIDogewogICAgICAidXJsIiA6ICJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlL2FjOWFkY2Q5NzVhOTE2OTE0YjVkMjlhZGFjZjVmNTJlNzk1MTQ3ODU4MzQ3NjU1MmE0ZjZmZThkOTRmNDhjMmEiLAogICAgICAibWV0YWRhdGEiIDogewogICAgICAgICJtb2RlbCIgOiAic2xpbSIKICAgICAgfQogICAgfQogIH0KfQ=="
    };
    private static final String ENIGMA_SOUL_TEXTURE = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOTMwZmU3NzFmY2MzZWNjMDUzMGVlOTU0NWFiMDc3OTc0MzdmOTVlMDlhMGVhYTliNTEyNDk3ZmU4OTJmNTJmYiJ9fX0=";
    private static final String LARVAE_TEXTURE = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOTgzYjMwZTlkMTM1YjA1MTkwZWVhMmMzYWM2MWUyYWI1NWEyZDgxZTFhNThkYmIyNjk4M2ExNDA4MjY2NCJ9fX0=";
    private static final String ODONATA_TEXTURE = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOWZkODA2ZGVmZGZkZjU5YjFmMjYwOWM4ZWUzNjQ2NjZkZTY2MTI3YTYyMzQxNWI1NDMwYzkzNThjNjAxZWY3YyJ9fX0=";
    private static final String MONTEZUMA_TEXTURE = "ewogICJ0aW1lc3RhbXAiIDogMTY0ODExMzgxMjE5OCwKICAicHJvZmlsZUlkIiA6ICIyYzEwNjRmY2Q5MTc0MjgyODRlM2JmN2ZhYTdlM2UxYSIsCiAgInByb2ZpbGVOYW1lIiA6ICJOYWVtZSIsCiAgInNpZ25hdHVyZVJlcXVpcmVkIiA6IHRydWUsCiAgInRleHR1cmVzIiA6IHsKICAgICJTS0lOIiA6IHsKICAgICAgInVybCIgOiAiaHR0cDovL3RleHR1cmVzLm1pbmVjcmFmdC5uZXQvdGV4dHVyZS9kZjY1NmMwNmU4YTVjYjQ2OTI1NjRlZTIxNzQ4YmRkZWM5ZDc4NWQxODM0Mjg0YWFhMTQzOTYwMWJiYTQ3ZDZiIiwKICAgICAgIm1ldGFkYXRhIiA6IHsKICAgICAgICAibW9kZWwiIDogInNsaW0iCiAgICAgIH0KICAgIH0KICB9Cn0=";
    private static final String SHADOW_TEXTURE = "ewogICJ0aW1lc3RhbXAiIDogMTY0OTIzMTczNTA1MiwKICAicHJvZmlsZUlkIiA6ICIwMGZiNTRiOWI4NDA0YTA0YTViMmJhMzBlYzBlYTAxMiIsCiAgInByb2ZpbGVOYW1lIiA6ICJrbGxveWQ3MCIsCiAgInNpZ25hdHVyZVJlcXVpcmVkIiA6IHRydWUsCiAgInRleHR1cmVzIiA6IHsKICAgICJTS0lOIiA6IHsKICAgICAgInVybCIgOiAiaHR0cDovL3RleHR1cmVzLm1pbmVjcmFmdC5uZXQvdGV4dHVyZS82MTMzYzA4MTEyZWE2NTJhOGY4YjhkZjQ1Y2I2MmYyZGJiNDA3MzdjZGQ0Nzg2NWYxMzVkODRmOTBjODA5ODNlIgogICAgfQogIH0KfQ";
    private static final String RABBIT_TEXTURE = "ewogICJ0aW1lc3RhbXAiIDogMTY3ODM4MDY1ODcxNSwKICAicHJvZmlsZUlkIiA6ICJmZWYyZDZjYzY5ZGI0ZWM5OWQzYzI5MzBmYzRmNTBhYSIsCiAgInByb2ZpbGVOYW1lIiA6ICJsb3Zlbm90d2FyIiwKICAic2lnbmF0dXJlUmVxdWlyZWQiIDogdHJ1ZSwKICAidGV4dHVyZXMiIDogewogICAgIlNLSU4iIDogewogICAgICAidXJsIiA6ICJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlLzhhYzg5ZWM5YzBiYWEwMDcwOWJhNmY0ZTZjMTg1NjBhNzNkZmM5NmJiOWY2YzcyYjQzNjgxNjU3MmZmYTY1ZmEiLAogICAgICAibWV0YWRhdGEiIDogewogICAgICAgICJtb2RlbCIgOiAic2xpbSIKICAgICAgfQogICAgfQogIH0KfQ==";
    private static final String GLYPH_CHEST_TEXTURE = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvM2Y2NmY3ZjAzMTI1Y2Y1NDczMzY5NmYzNjMyZjBkOWU2NDcwYmFhYjg0OTg0N2VhNWVhMmQ3OTE1NmFkMGY0MCJ9fX0=";
    private static final String VOLT_TEXTURE = "ewogICJ0aW1lc3RhbXAiIDogMTY3MzYzNzQ1OTAwOCwKICAicHJvZmlsZUlkIiA6ICJmMTA0NzMxZjljYTU0NmI0OTkzNjM4NTlkZWY5N2NjNiIsCiAgInByb2ZpbGVOYW1lIiA6ICJ6aWFkODciLAogICJzaWduYXR1cmVSZXF1aXJlZCIgOiB0cnVlLAogICJ0ZXh0dXJlcyIgOiB7CiAgICAiU0tJTiIgOiB7CiAgICAgICJ1cmwiIDogImh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvN2FlYTYyNTFlNThlM2QyMDU1MmEwMzVkNDI0NTYxZWFlZTA4M2ZlYWNkMWU2Y2IzYzJhNWNmOTQ1Y2U2ZDc2ZSIsCiAgICAgICJtZXRhZGF0YSIgOiB7CiAgICAgICAgIm1vZGVsIiA6ICJzbGltIgogICAgICB9CiAgICB9CiAgfQp9";
    private static final String SCRIBE_TEXTURE = "ewogICJ0aW1lc3RhbXAiIDogMTY3NjgwNjUwNjEyMCwKICAicHJvZmlsZUlkIiA6ICI0ZDBjMmViZmZkOGY0ZGQ2YTRmNzcxYjhmYzA1MDAzYiIsCiAgInByb2ZpbGVOYW1lIiA6ICJ4cnViaW9wcm94IiwKICAic2lnbmF0dXJlUmVxdWlyZWQiIDogdHJ1ZSwKICAidGV4dHVyZXMiIDogewogICAgIlNLSU4iIDogewogICAgICAidXJsIiA6ICJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlLzFhYTI2NmJlMThkNjUyYjY2ZGUxODk1MDhjN2QyYjc0MTlkNjk5MWQxNjYwYmU5ZjAxZWZiNTA1ZTU0ZTU0M2IiLAogICAgICAibWV0YWRhdGEiIDogewogICAgICAgICJtb2RlbCIgOiAic2xpbSIKICAgICAgfQogICAgfQogIH0KfQ==";
    private static final String KAT_FLY_TEXTURE = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZTMwYWMxZjljNjQ5Yzk5Y2Q2MGU0YmZhNTMzNmNjMTg1MGYyNzNlYWI5ZjViMGI3OTQwZDRkNGQ3ZGM4MjVkYyJ9fX0=";
    private static final String KAT_SPIDER_TEXTURES = "ewogICJ0aW1lc3RhbXAiIDogMTY1MDU1NjEzMTkxNywKICAicHJvZmlsZUlkIiA6ICI0ODI5MmJkMjI1OTc0YzUwOTZiMTZhNjEyOGFmMzY3NSIsCiAgInByb2ZpbGVOYW1lIiA6ICJLVVJPVE9ZVEIyOCIsCiAgInNpZ25hdHVyZVJlcXVpcmVkIiA6IHRydWUsCiAgInRleHR1cmVzIiA6IHsKICAgICJTS0lOIiA6IHsKICAgICAgInVybCIgOiAiaHR0cDovL3RleHR1cmVzLm1pbmVjcmFmdC5uZXQvdGV4dHVyZS84ZmRmNjJkNGUwM2NhNTk0YzhjZDIxZGQxNzUzMjdmMWNmNzdjNGJjMDU3YTA5NTk2MDNkODNhNjhiYTI3MDA4IgogICAgfQogIH0KfQ==";

    @SubscribeEvent(receiveCanceled = true)
    public void onChat(ClientChatReceivedEvent event) {
        if (LocationUtils.currentIsland != LocationUtils.Island.THE_END) return;
        if (!GumTuneClientConfig.ESPs) return;
        if (!GumTuneClientConfig.enderNodeESP) return;
        String message = StringUtils.removeFormatting(event.message.getUnformattedText());

        if (message.startsWith("ENDER NODE!") && GumTuneClient.mc.objectMouseOver != null && GumTuneClient.mc.objectMouseOver.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK) {
            enderNodes.remove(new BlockPosition(GumTuneClient.mc.objectMouseOver.getBlockPos()));
        }
    }

    @SubscribeEvent(receiveCanceled = true)
    public void onParticle(PacketReceivedEvent event) {
        if (LocationUtils.currentIsland != LocationUtils.Island.THE_END) return;
        if (!GumTuneClientConfig.ESPs) return;
        if (!GumTuneClientConfig.enderNodeESP) return;
        if (event.packet instanceof S2APacketParticles) {
            S2APacketParticles packet = (S2APacketParticles) event.packet;
            if (packet.getParticleType().equals(EnumParticleTypes.PORTAL)) {
                double x = packet.getXCoordinate();
                double y = packet.getYCoordinate();
                double z = packet.getZCoordinate();


                if (x % 1 == 0.25) {
                    x += 1;
                } else if (x % 1 == 0.75) {
                    x -= 1;
                }

                if (y % 1 == 0.25) {
                    y -= 1;
                } else if (y % 1 == 0.75) {
                    y += 1;
                }

                if (z % 1 == 0.25) {
                    z += 1;
                } else if (z % 1 == 0.75) {
                    z -= 1;
                }

                BlockPosition blockPosition = new BlockPosition((int) Math.floor(x), (int) Math.floor(y), (int) Math.floor(z));

                IBlockState blockState = GumTuneClient.mc.theWorld.getBlockState(blockPosition.toBlockPos());
                if (blockState.getBlock() != Blocks.air && blockState.getBlock() != Blocks.bedrock) {
                    enderNodes.put(blockPosition, System.currentTimeMillis());
                }
            }
        }
    }

    @SubscribeEvent
    public void onRenderEntityLiving(RenderLivingEntityEvent event) {
        if (LocationUtils.currentIsland == null) return;
        if (checked.contains(event.entity)) return;
        if (!GumTuneClientConfig.ESPs) return;
        if (event.entity instanceof EntityArmorStand) {
            switch (LocationUtils.currentIsland) {
                case SPIDER_DEN:
                    if (GumTuneClientConfig.arachneKeeperESP && event.entity.hasCustomName() && isArachneKeeper(event.entity)) {
                        List<Entity> possibleEntities = event.entity.getEntityWorld().getEntitiesInAABBexcluding(event.entity, event.entity.getEntityBoundingBox().offset(0, -1, 0), entity -> (!(entity instanceof EntityArmorStand) && entity != GumTuneClient.mc.thePlayer));
                        if (!possibleEntities.isEmpty()) {
                            highlightEntity(possibleEntities.get(0), event.entity.getCustomNameTag(), GumTuneClientConfig.espColor.getRGB());
                        } else {
                            highlightEntity(event.entity, event.entity.getCustomNameTag(), GumTuneClientConfig.espColor.getRGB());
                        }
                    }
                    break;
                case JERRY_WORKSHOP:
                    if (GumTuneClientConfig.frozenTreasureESP && isFrozenTreasure((EntityArmorStand) event.entity)) {
                        BlockPos blockPos = new BlockPos(event.entity.posX, event.entity.posY + 2, event.entity.posZ);
                        if (GumTuneClient.mc.theWorld.getBlockState(blockPos).getBlock() == Blocks.ice ||
                                GumTuneClient.mc.theWorld.getBlockState(blockPos).getBlock() == Blocks.packed_ice) {
                            frozenTreasures.add(blockPos);
                            highlightBlock(blockPos, event.entity, event.entity.getCurrentArmor(3).serializeNBT().getCompoundTag("tag").getCompoundTag("display").getTag("Name").toString().replace("\"", ""));
                        }
                    }
                    break;
                case THE_RIFT:
                    if (RiftESPs.shyESP && matchSkullTexture((EntityArmorStand) event.entity, SHY_TEXTURES)) {
                        highlightEntity(event.entity, "§2Shy", new Color(0, 140, 0).getRGB());
                    } else if (RiftESPs.shadowESP && matchSkullTexture((EntityArmorStand) event.entity, SHADOW_TEXTURE)) {
                        highlightEntity(event.entity, "§7Shadow", new Color(170, 170, 170).getRGB());
                    } else if (RiftESPs.voltESP && matchSkullTexture((EntityArmorStand) event.entity, VOLT_TEXTURE)) {
                        highlightEntity(event.entity, "§eVolt", new Color(170, 170, 0).getRGB());
                    } else if (RiftESPs.scribeESP && matchSkullTexture((EntityArmorStand) event.entity, SCRIBE_TEXTURE)) {
                        highlightEntity(event.entity, "§6Scribe", new Color(255, 170, 0).getRGB());
                    } else if (RiftESPs.NPCESP && event.entity.hasCustomName() && event.entity.getCustomNameTag().equals("§e§lCLICK")) {
                        List<Entity> possibleEntitiesNPCName = event.entity.getEntityWorld().getEntitiesInAABBexcluding(event.entity, event.entity.getEntityBoundingBox().expand(0, 1, 0), entity -> (entity instanceof EntityArmorStand) && entity != event.entity);
                        List<Entity> possibleEntitiesNPC = event.entity.getEntityWorld().getEntitiesInAABBexcluding(event.entity, event.entity.getEntityBoundingBox().offset(0, 1, 0), entity -> !(entity instanceof EntityArmorStand) && entity != GumTuneClient.mc.thePlayer);
                        if (!possibleEntitiesNPCName.isEmpty()) {
                            if (!possibleEntitiesNPC.isEmpty()) {
                                highlightEntity(possibleEntitiesNPC.get(0), possibleEntitiesNPCName.get(0).getCustomNameTag(), GumTuneClientConfig.espColor.getRGB());
                            } else {
                                highlightEntity(possibleEntitiesNPCName.get(0), possibleEntitiesNPCName.get(0).getCustomNameTag(), GumTuneClientConfig.espColor.getRGB());
                            }
                        } else {
                            highlightEntity(event.entity, "§eNPC", GumTuneClientConfig.espColor.getRGB());
                        }
                    } else if (RiftESPs.rabbitESP && matchSkullTexture((EntityArmorStand) event.entity, RABBIT_TEXTURE)) {
                        highlightEntity(event.entity, "§fRabbit", new Color(255, 255, 255).getRGB());
                    } else if (RiftESPs.enigmaSoulESP && matchSkullTexture((EntityArmorStand) event.entity, ENIGMA_SOUL_TEXTURE)) {
                        highlightEntity(event.entity, "§5Enigma Soul", new Color(170, 0, 170).getRGB());
                    } else if (RiftESPs.larvaeESP && matchSkullTexture((EntityArmorStand) event.entity, LARVAE_TEXTURE)) {
                        highlightEntity(event.entity, "§fLarvae", new Color(255, 255, 255).getRGB());
                    } else if (RiftESPs.odonataESP && matchHeldItemTexture((EntityArmorStand) event.entity, ODONATA_TEXTURE)) {
                        highlightEntity(event.entity, "§bOdonata", new Color(8, 176, 156).getRGB());
                    } else if (RiftESPs.montezumaESP && matchSkullTexture((EntityArmorStand) event.entity, MONTEZUMA_TEXTURE)) {
                        highlightEntity(event.entity, "§fMontezuma", Color.WHITE.getRGB());
                    } else if (RiftESPs.glyphChestESP && matchSkullTexture((EntityArmorStand) event.entity, GLYPH_CHEST_TEXTURE)) {
                        highlightEntity(event.entity, "§5Glyph Chest", new Color(100, 0, 160).getRGB());
                    } else if (RiftESPs.katFlyESP && matchSkullTexture((EntityArmorStand) event.entity, KAT_FLY_TEXTURE)) {
                        highlightEntity(event.entity, "§2Kat Fly", new Color(0, 120, 0).getRGB());
                    } else if (RiftESPs.katSpiderESP && matchSkullTexture((EntityArmorStand) event.entity, KAT_SPIDER_TEXTURES)) {
                        highlightEntity(event.entity, "§8Kat Spider", new Color(70, 70, 70).getRGB());
                    }
                    break;
            }

            if (GumTuneClientConfig.fairySoulESP && matchSkullTexture((EntityArmorStand) event.entity, FAIRY_SOUL_TEXTURE)) {
                highlightEntity(event.entity, "§dFairy Soul", Color.PINK.getRGB());
            }
        }

        if (LocationUtils.currentIsland == LocationUtils.Island.THE_RIFT) {
            if (RiftESPs.katSilverfishESP && event.entity instanceof EntitySilverfish) {
                highlightEntity(event.entity, "§7Kat Silverfish", new Color(120, 120, 120).getRGB());
            }
        }

        if (GumTuneClientConfig.crystalHollowsMobESP && LocationUtils.currentIsland == LocationUtils.Island.CRYSTAL_HOLLOWS) {
            if (event.entity instanceof EntityOtherPlayerMP) {
                String name = event.entity.getName();
                if (name.equals("Team Treasurite")) {
                    highlightEntity(event.entity, "Team Treasurite", Color.CYAN.getRGB());
                } else if (name.equals("Murderlover") || name.equals("Goblin") || name.equals("Weakling") || name.equals("Pitfighter")) {
                    highlightEntity(event.entity, "Goblin", new Color(0, 100, 0).getRGB());
                }
            } else if (event.entity instanceof EntityIronGolem) {
                highlightEntity(event.entity, "Automaton", Color.WHITE.getRGB());
            } else if (event.entity instanceof EntityMagmaCube) {
                highlightEntity(event.entity, "Yog", Color.YELLOW.getRGB());
            } else if (event.entity instanceof EntitySlime) {
                highlightEntity(event.entity, "Sludge", Color.GREEN.getRGB());
            } else if (event.entity instanceof EntityEndermite) {
                highlightEntity(event.entity, "Thyst", Color.MAGENTA.getRGB());
            }
        }

        checked.add(event.entity);
    }

    @SubscribeEvent
    public void onRenderWorld(RenderWorldLastEvent event) {
        if (!GumTuneClientConfig.ESPs) return;
        if (GumTuneClient.mc.thePlayer == null) return;
        GumTuneClient.mc.theWorld.loadedEntityList.forEach(entity -> {
            if (highlightedEntities.containsKey(entity)) {
                RenderUtils.renderBoundingBox(entity, event.partialTicks, highlightedEntities.get(entity).getColor());
                if (GumTuneClientConfig.entityRenderWaypoint) {
                    RenderUtils.renderWaypointText(highlightedEntities.get(entity).getName(), entity.posX, entity.posY + entity.height, entity.posZ, event.partialTicks);
                }
            }
            if (highlightedEntityBlocks.containsKey(entity)) {
                BlockPos blockPos = highlightedEntityBlocks.get(entity).getBlockPos();
                if (blockPos != null) {
                    RenderUtils.renderEspBox(blockPos, event.partialTicks, highlightedEntities.get(entity).getColor());
                    RenderUtils.renderWaypointText(highlightedEntityBlocks.get(entity).getName(), blockPos.getX() + 0.5, blockPos.getY() + 0.5, blockPos.getZ() + 0.5, event.partialTicks);
                }
            }
        });

        if (LocationUtils.currentIsland == LocationUtils.Island.THE_END) {
            HashSet<BlockPosition> toRemove = new HashSet<>();

            enderNodes.forEach((blockPosition, timestamp) -> {
                if (System.currentTimeMillis() - timestamp > 2000) {
                    toRemove.add(blockPosition);
                }
            });

            toRemove.forEach(enderNodes::remove);

            for (BlockPosition blockPosition : enderNodes.keySet()) {
                RenderUtils.renderEspBox(blockPosition.toBlockPos(), event.partialTicks, new Color(170, 0, 170).getRGB());
                RenderUtils.drawLine(GumTuneClient.mc.thePlayer.getPositionEyes(event.partialTicks), new Vec3(blockPosition.x + 0.5, blockPosition.y + 0.5, blockPosition.z + 0.5), 1, event.partialTicks);
            }
        }
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
        enderNodes.clear();
    }

    private void highlightEntity(Entity entity, String name, int color) {
        highlightedEntities.put(entity, new HighlightEntity(name, color));
    }

    private void highlightBlock(BlockPos blockPos, Entity entity, String name) {
        highlightedEntityBlocks.put(entity, new HighlightBlock(blockPos, name));
    }

    private boolean isArachneKeeper(Entity entity) {
        return entity.getCustomNameTag().contains("Keeper") && LocationUtils.currentIsland == LocationUtils.Island.SPIDER_DEN;
    }

    private boolean isFrozenTreasure(EntityArmorStand entity) {
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

    private boolean matchSkullTexture(EntityArmorStand entity, String... skullTextures) {
        ItemStack helmetItemStack = entity.getCurrentArmor(3);
        if (helmetItemStack != null && helmetItemStack.getItem() instanceof ItemSkull) {
            NBTTagList textures = helmetItemStack.serializeNBT().getCompoundTag("tag").getCompoundTag("SkullOwner").getCompoundTag("Properties").getTagList("textures", Constants.NBT.TAG_COMPOUND);
            for (int i = 0; i < textures.tagCount(); i++) {
                int finalI = i;
                if (Arrays.stream(skullTextures).anyMatch(s -> textures.getCompoundTagAt(finalI).getString("Value").equals(s))) {
                    return true;
                }
            }
        }

        return false;
    }

    private boolean matchHeldItemTexture(EntityArmorStand entity, String... skullTextures) {
        ItemStack heldItemStack = entity.getCurrentArmor(-1);
        if (heldItemStack != null && heldItemStack.getItem() instanceof ItemSkull) {
            NBTTagList textures = heldItemStack.serializeNBT().getCompoundTag("tag").getCompoundTag("SkullOwner").getCompoundTag("Properties").getTagList("textures", Constants.NBT.TAG_COMPOUND);
            for (int i = 0; i < textures.tagCount(); i++) {
                int finalI = i;
                if (Arrays.stream(skullTextures).anyMatch(s -> textures.getCompoundTagAt(finalI).getString("Value").equals(s))) {
                    return true;
                }
            }
        }

        return false;
    }
}
