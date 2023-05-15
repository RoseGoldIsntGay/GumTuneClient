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

import java.util.HashMap;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

public class VisitorHelpers {

    private static long wasFull;
    public static String signText = "";
    private static String cropName = "";
    private static int cropAmount = -1;
    public static String visitorName = "";
    private static Entity visitor;
    private static boolean rejectOffer = false;
    private static final TimedSet<String> servedLately = new TimedSet<>(10, TimeUnit.SECONDS, true);
    private static long timestamp = System.currentTimeMillis();
    private static ConcurrentHashMap<String, Integer> cropsToBuy;
    private static final Pattern pattern = Pattern.compile("^ [\\w ]+", Pattern.CASE_INSENSITIVE);
    private static final HashMap<Integer, Character> chatType = new HashMap<Integer, Character>() {{
        put(0, 'c');
        put(1, 'a');
        put(2, 'p');
        put(3, 'g');
    }};

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
        if (!GumTuneClientConfig.visitorQuickBuyDebug) return;
        if (event.type == RenderGameOverlayEvent.ElementType.ALL) {
            FontUtils.drawScaledString("Bazaar Buy State: " + bazaarBuyState, 1, 40, 40, true);

            StringBuilder servedVisitors = new StringBuilder();
            for (String visitorName : servedLately) {
                servedVisitors.append(visitorName).append(", ");
            }
            if (servedVisitors.length() > 2) {
                servedVisitors.delete(servedVisitors.length() - 2, servedVisitors.length() - 1);
            }

            FontUtils.drawScaledString("Visitors served lately: " + servedVisitors, 1, 40, 50, true);

            if (cropsToBuy != null) {
                StringBuilder futureCrops = new StringBuilder();
                for (String crop : cropsToBuy.keySet()) {
                    futureCrops.append(cropsToBuy.get(crop)).append(" ").append(crop).append(", ");
                }
                if (futureCrops.length() > 2) {
                    futureCrops.delete(futureCrops.length() - 2, futureCrops.length() - 1);
                }

                FontUtils.drawScaledString("Crops to buy: " + futureCrops, 1, 40, 60, true);
            } else {
                FontUtils.drawScaledString("Crops to buy: ", 1, 40, 60, true);
            }
        }
    }

    @SubscribeEvent
    public void onTick(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.START) return;
        if (LocationUtils.currentIsland != LocationUtils.Island.GARDEN) return;
        if (GumTuneClient.mc.thePlayer == null) return;
        servedLately.iterator();

        if (GumTuneClientConfig.visitorQueueFullChatMessage) {
            if (System.currentTimeMillis() - wasFull > 300000 && TabListUtils.tabListContains("Queue Full")) {
                GumTuneClient.mc.thePlayer.sendChatMessage(String.format("/%cc visitor queue full", chatType.get(GumTuneClientConfig.visitorQueueFullChatType)));
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
                        if (cropsToBuy != null && !cropsToBuy.isEmpty() && System.currentTimeMillis() - timestamp > 500) {
                            for (String crops : cropsToBuy.keySet()) {
                                buyCrop(crops, cropsToBuy.get(crops));
                                cropsToBuy.remove(crops);
                                return;
                            }
                        }

                        if (servedLately.contains(chestName)) {
                            GumTuneClient.mc.thePlayer.closeScreen();
                            return;
                        }
                        if (GumTuneClientConfig.visitorQuickBuyMode == 1) {
                            for (Slot slot : GumTuneClient.mc.thePlayer.openContainer.inventorySlots) {
                                if (slot.getHasStack() && slot.getStack().getDisplayName().contains("Accept Offer")) {
                                    visitorName = chestName;

                                    ConcurrentHashMap<String, Integer> crops = new ConcurrentHashMap<>();

                                    for (int i = 1; i < 4; i++) {
                                        String lore = InventoryUtils.getItemLore(slot.getStack(), i);
                                        if (lore != null && pattern.matcher(StringUtils.removeFormatting(lore)).find()) {
                                            String cleanLore = StringUtils.removeFormatting(lore);
                                            if (cleanLore.contains("x")) {
                                                String[] split = cleanLore.split("x");
                                                crops.put(split[0].trim(), Integer.parseInt(split[1].replace(",", "")));
                                            } else {
                                                crops.put(cleanLore.trim(), 1);
                                            }
                                        }
                                    }

                                    VisitorHelpers.buyCrops(crops);
                                    return;
                                }
                            }
                        }
                        break;
                    case CLICK_SEARCH:
                        if (chestName.startsWith("Bazaar ➜")) {
                            clickSlot(45, 0);
                            bazaarBuyState = BazaarBuyState.CLICK_CROP;
                            timestamp = System.currentTimeMillis();
                        }
                        break;
                    case CLICK_CROP:
                        if (System.currentTimeMillis() - timestamp > 750 && chestName.startsWith("Bazaar ➜ \"")) {
                            for (Slot slot : GumTuneClient.mc.thePlayer.openContainer.inventorySlots) {
                                if (!slot.getHasStack()) continue;
                                if (StringUtils.removeFormatting(slot.getStack().getDisplayName()).equals(cropName)) {
                                    clickSlot(slot.slotNumber, 0);
                                    bazaarBuyState = BazaarBuyState.CLICK_SIGN;
                                    timestamp = System.currentTimeMillis();
                                    rejectOffer = false;
                                    return;
                                }
                            }

                            // finished iterating over slots, aka no such product in bazaar
                            timestamp = System.currentTimeMillis();
                            signText = "";
                            cropAmount = -1;
                            cropName = "";
                            rejectOffer = true;
                            bazaarBuyState = BazaarBuyState.SETUP_VISITOR_HAND_IN;
                        }
                        break;
                    case CLICK_SIGN:
                        if (System.currentTimeMillis() - timestamp > 500 && chestName.contains("➜") && !chestName.contains("Bazaar")) {
                            clickSlot(10, 0);
                            clickSlot(16, 1);

                            signText = String.valueOf(cropAmount);
                            bazaarBuyState = BazaarBuyState.CLICK_BUY;
                            timestamp = System.currentTimeMillis();
                        }
                        break;
                    case CLICK_BUY:
                        if (System.currentTimeMillis() - timestamp > 500 && chestName.equals("Confirm Instant Buy")) {
                            signText = "";
                            cropAmount = -1;
                            cropName = "";
                            if (!cropsToBuy.isEmpty()) {
                                bazaarBuyState = BazaarBuyState.IDLE;
                            } else {
                                bazaarBuyState = BazaarBuyState.SETUP_VISITOR_HAND_IN;
                            }
                            timestamp = System.currentTimeMillis();
                            clickSlot(13, 0);
                        }
                        break;
                    case SETUP_VISITOR_HAND_IN:
                        GumTuneClient.mc.theWorld.loadedEntityList.stream()
                                .filter(
                                        entity -> entity.hasCustomName() && StringUtils.removeFormatting(entity.getCustomNameTag()).equals(visitorName)
                                ).filter(
                                        entity -> entity.getDistanceToEntity(GumTuneClient.mc.thePlayer) < 4
                                ).findAny().ifPresent(visitorEntity -> {
                                    visitor = visitorEntity;

                                    bazaarBuyState = BazaarBuyState.OPEN_VISITOR_GUI;
                                    GumTuneClient.mc.thePlayer.closeScreen();
                                });
                        if (System.currentTimeMillis() - timestamp > 3000) {
                            ModUtils.sendMessage("Took longer than 3 seconds to find the visitor entity, did you move too far away from it?");
                            visitorName = "";
                            bazaarBuyState = BazaarBuyState.IDLE;
                        }
                        break;
                    case HAND_IN_CROPS:
                        if (System.currentTimeMillis() - timestamp > 500) {
                            for (Slot slot : GumTuneClient.mc.thePlayer.openContainer.inventorySlots) {
                                if (rejectOffer) {
                                    if (slot.getHasStack() && slot.getStack().getDisplayName().contains("Refuse Offer")) {
                                        clickSlot(slot.slotNumber, 0);
                                        servedLately.put(visitorName);
                                        visitorName = "";
                                        bazaarBuyState = BazaarBuyState.IDLE;
                                        visitor = null;
                                        break;
                                    }
                                } else {
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
                        }
                        break;
                }
            }
        }
    }

    private static void buyCrop(String name, int amount) {
        signText = name;
        cropName = name;
        cropAmount = amount;
        GumTuneClient.mc.thePlayer.sendChatMessage("/bz " + cropName);
        bazaarBuyState = BazaarBuyState.CLICK_CROP;
        timestamp = System.currentTimeMillis();
    }

    public static void buyCrops(ConcurrentHashMap<String, Integer> crops) {
        cropsToBuy = crops;
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
