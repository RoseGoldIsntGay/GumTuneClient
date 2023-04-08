package rosegold.gumtuneclient.modules.farming;

import net.minecraft.entity.Entity;
import net.minecraft.inventory.Slot;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import rosegold.gumtuneclient.GumTuneClient;
import rosegold.gumtuneclient.config.GumTuneClientConfig;
import rosegold.gumtuneclient.utils.*;
import rosegold.gumtuneclient.utils.objects.TimedSet;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

public class VisitorHelpers {

    private static long wasFull;
    public static String signText = "";
    private static String cropName = "";
    private static int cropAmount = -1;
    private static String visitorName = "";
    private static Entity visitor;
    private static final TimedSet<String> servedLately = new TimedSet<>(10, TimeUnit.SECONDS, true);
    private static long timestamp = System.currentTimeMillis();

    private enum BazaarBuyState {
        IDLE,
        CLICK_SEARCH,
        CLICK_CROP,
        CLICK_SIGN,
        CLICK_BUY,
        SETUP_VISITOR_HAND_IN,
        OPEN_VISITOR_GUI,
        HAND_IN_CROPS
    }

    private static BazaarBuyState bazaarBuyState = BazaarBuyState.IDLE;

    @SubscribeEvent
    public void onOverlayRender(RenderGameOverlayEvent.Post event) {
        if (!GumTuneClientConfig.visitorQuickBuy) return;
        if (LocationUtils.currentIsland != LocationUtils.Island.GARDEN) return;
        if (event.type == RenderGameOverlayEvent.ElementType.ALL) {
            FontUtils.drawScaledString("Bazaar Buy State: " + bazaarBuyState, 1, 300, 100, true);
            FontUtils.drawScaledString("Crop: " + cropName, 1, 300, 110, true);
            FontUtils.drawScaledString("Amount: " + cropAmount, 1, 300, 120, true);
            if (visitor == null) {
                FontUtils.drawScaledString("Visitor: null", 1, 300, 130, true);
            } else {
                FontUtils.drawScaledString("Visitor: " + visitor.getCustomNameTag(), 1, 300, 130, true);
            }
        }
    }

    @SubscribeEvent
    public void onTick(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.START) return;
        if (LocationUtils.currentIsland != LocationUtils.Island.GARDEN) return;
        if (GumTuneClient.mc.thePlayer == null) return;

        if (GumTuneClientConfig.visitorQueueFullChatMessage) {
            if (System.currentTimeMillis() - wasFull > 300000 && TabListUtils.tabListContains("Queue Full")) {
                switch (GumTuneClientConfig.visitorQueueFullChatType) {
                    case 0:
                        GumTuneClient.mc.thePlayer.sendChatMessage("/cc visitor queue full");
                        break;
                    case 1:
                        GumTuneClient.mc.thePlayer.sendChatMessage("/ac visitor queue full");
                        break;
                    case 2:
                        GumTuneClient.mc.thePlayer.sendChatMessage("/pc visitor queue full");
                        break;
                    case 3:
                        GumTuneClient.mc.thePlayer.sendChatMessage("/gc visitor queue full");
                        break;

                }

                wasFull = System.currentTimeMillis();
            }
        }

        if (GumTuneClientConfig.visitorQuickBuy) {
            if (bazaarBuyState == BazaarBuyState.OPEN_VISITOR_GUI && visitor != null && GumTuneClient.mc.theWorld.loadedEntityList.contains(visitor)) {
                GumTuneClient.mc.playerController.interactWithEntitySendPacket(GumTuneClient.mc.thePlayer, visitor);
                bazaarBuyState = BazaarBuyState.HAND_IN_CROPS;
                timestamp = System.currentTimeMillis();
                return;
            }

            String chestName = GuiUtils.getOpenInventoryName();
            if (chestName != null) {
                switch (bazaarBuyState) {
                    case IDLE:
                        if (GumTuneClientConfig.visitorQuickBuyMode == 1) {
                            if (servedLately.contains(chestName)) {
                                GumTuneClient.mc.thePlayer.closeScreen();
                                return;
                            }
                            for (Slot slot : GumTuneClient.mc.thePlayer.openContainer.inventorySlots) {
                                if (slot.getHasStack() && slot.getStack().getDisplayName().contains("Accept Offer")) {
                                    visitorName = chestName;
                                    ModUtils.sendMessage("Fulfilling request from visitor " + chestName);

                                    String requirementString = removeFormatting(InventoryUtils.getItemLore(slot.getStack(), 1));
                                    if (requirementString.contains("x")) {
                                        String[] split = requirementString.split("x");

                                        buyCrop(split[0].trim(), Integer.parseInt(split[1]));
                                    }
                                }
                            }
                        }
                        break;
                    case CLICK_SEARCH:
                        if (chestName.startsWith("Bazaar ➜")) {
                            clickSlot(45, 0);
                            bazaarBuyState = BazaarBuyState.CLICK_CROP;
                        }
                        break;
                    case CLICK_CROP:
                        if (chestName.startsWith("Bazaar ➜ \"")) {
                            for (Slot slot : GumTuneClient.mc.thePlayer.openContainer.inventorySlots) {
                                if (!slot.getHasStack()) continue;
                                if (removeFormatting(slot.getStack().getDisplayName()).equals(cropName)) {
                                    clickSlot(slot.slotNumber, 0);
                                    bazaarBuyState = BazaarBuyState.CLICK_SIGN;
                                    break;
                                }
                            }
                        }
                        break;
                    case CLICK_SIGN:
                        if (chestName.startsWith("Bazaar ➜")) {
                            clickSlot(10, 1);
                            clickSlot(16, 2);

                            signText = String.valueOf(cropAmount);
                            bazaarBuyState = BazaarBuyState.CLICK_BUY;
                        }
                        break;
                    case CLICK_BUY:
                        if (chestName.equals("Confirm Instant Buy")) {
                            signText = "";
                            cropAmount = -1;
                            cropName = "";
                            if (GumTuneClientConfig.visitorQuickBuyMode == 0) {
                                visitorName = "";
                                bazaarBuyState = BazaarBuyState.IDLE;
                            } else {
                                bazaarBuyState = BazaarBuyState.SETUP_VISITOR_HAND_IN;
                            }
                            clickSlot(13, 0);
                        }
                        break;
                    case SETUP_VISITOR_HAND_IN:
                        Optional<Entity> optional = GumTuneClient.mc.theWorld.loadedEntityList.stream()
                                .filter(
                                        entity -> entity.hasCustomName() && removeFormatting(entity.getCustomNameTag()).equals(visitorName)
                                ).filter(
                                        entity -> entity.getDistanceToEntity(GumTuneClient.mc.thePlayer) < 4
                                ).findAny();
                        if (optional.isPresent()) {
                            visitor = optional.get();

                            bazaarBuyState = BazaarBuyState.OPEN_VISITOR_GUI;
                            GumTuneClient.mc.thePlayer.closeScreen();
                        }
                        break;
                    case HAND_IN_CROPS:
                        if (System.currentTimeMillis() - timestamp > 1000) {
                            for (Slot slot : GumTuneClient.mc.thePlayer.openContainer.inventorySlots) {
                                if (slot.getHasStack() && slot.getStack().getDisplayName().contains("Accept Offer")) {
                                    clickSlot(slot.slotNumber, 0);
                                    servedLately.put(visitorName);
                                    visitorName = "";
                                    bazaarBuyState = BazaarBuyState.IDLE;
                                    visitor = null;
                                    break;
                                }
                            }
                        }
                        break;
                }
            }
        }
    }

    private String removeFormatting(String input) {
        return input.replaceAll("§[0-9a-fk-or]", "");
    }

    public static void buyCrop(String name, int amount) {
        signText = name;
        cropName = name;

        if (cropName.contains("Lantern")) {
            ModUtils.sendMessage("Cannot fulfill request for " + cropName + " as it is not purchasable from the bazaar");
            bazaarBuyState = BazaarBuyState.IDLE;
            return;
        }
        cropAmount = amount;
        GumTuneClient.mc.thePlayer.sendChatMessage("/bz");
        bazaarBuyState = BazaarBuyState.CLICK_SEARCH;
    }

    private void clickSlot(int slot, int windowAdd) {
        GumTuneClient.mc.playerController.windowClick(
                GumTuneClient.mc.thePlayer.openContainer.windowId + windowAdd,
                slot,
                0,
                0,
                GumTuneClient.mc.thePlayer
        );
    }
}
