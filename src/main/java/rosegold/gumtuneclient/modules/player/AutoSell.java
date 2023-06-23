package rosegold.gumtuneclient.modules.player;

import cc.polyfrost.oneconfig.gui.OneConfigGui;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.lwjgl.input.Keyboard;
import rosegold.gumtuneclient.GumTuneClient;
import rosegold.gumtuneclient.config.GumTuneClientConfig;
import rosegold.gumtuneclient.events.SecondEvent;
import rosegold.gumtuneclient.utils.*;
import rosegold.gumtuneclient.utils.objects.WaypointList;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;

public class AutoSell {

    private enum AutoSellState {
        IDLE,
        BZ_INSTASELL,
        TRADES_SELL
    }

    private static AutoSellState autoSellState = AutoSellState.IDLE;
    private static HashSet<String> itemsToSell = new HashSet<>();
    private static long timestamp = System.currentTimeMillis();
    private static long forceStopTimestamp = System.currentTimeMillis();
    private static int timesFullInARow = 0;
    private boolean soldSomething = false;

    @SubscribeEvent
    public void onOverlayRender(RenderGameOverlayEvent.Post event) {
        if (GumTuneClient.mc.thePlayer == null) return;
        if (!GumTuneClientConfig.autoSell) return;
        if (!LocationUtils.onSkyblock) return;
        if (event.type == RenderGameOverlayEvent.ElementType.ALL) {
            FontUtils.drawScaledString("Auto Sell State: " + autoSellState, 1, 40, 40, true);
            FontUtils.drawScaledString("Times Sold In A Row: " + timesFullInARow, 1, 40, 50, true);
        }
    }

    @SubscribeEvent
    public void onSecond(SecondEvent event) {
        if (GumTuneClient.mc.thePlayer == null) return;
        if (!GumTuneClientConfig.autoSell) return;
        if (!LocationUtils.onSkyblock) return;
        if (GumTuneClientConfig.autoSellPassiveMode) {
            autoSellState = AutoSellState.TRADES_SELL;
            timestamp = System.currentTimeMillis();
        } else {
            if (InventoryUtils.getFilledSlotCount() / 36f * 100 >= GumTuneClientConfig.autoSellInventoryFullness && autoSellState == AutoSellState.IDLE && System.currentTimeMillis() - forceStopTimestamp > 15000) {
                if (timesFullInARow > 1) {
                    ModUtils.sendMessage("Detected inventory full even after selling, enabling 15 second cooldown until next sell can be triggered");
                    forceStopTimestamp = System.currentTimeMillis();
                    return;
                }
                autoSell();
                timesFullInARow++;
            } else {
                timesFullInARow = 0;
            }
        }

        if (GumTuneClient.mc.currentScreen instanceof OneConfigGui) {
            itemsToSell.clear();
            for (String item : GumTuneClientConfig.autoSellItemFilter.split(", ")) {
                if (!item.equals("")) itemsToSell.add(item);
            }
        } else {
            GumTuneClientConfig.autoSellItemFilter = "";

            for (String item : itemsToSell) {
                GumTuneClientConfig.autoSellItemFilter += item + ", ";
            }

            GumTuneClientConfig.autoSellItemFilter = GumTuneClientConfig.autoSellItemFilter.replaceAll(", $", "");
        }

        saveConfig();
    }

    @SubscribeEvent
    public void onKey(InputEvent.KeyInputEvent event) {
        if (!GumTuneClientConfig.autoSell) return;
        if (GumTuneClient.mc.currentScreen != null) return;
        if (Keyboard.getEventKeyState()) return;
        int eventKey = Keyboard.getEventKey();

        ArrayList<Integer> keyBindsExecuteAutoSell = GumTuneClientConfig.executeAutoSellKeybind.getKeyBinds();
        if (keyBindsExecuteAutoSell.size() > 0 && keyBindsExecuteAutoSell.get(0) == eventKey) {
            autoSell();
        }
    }

    @SubscribeEvent
    public void onKeyInput(GuiScreenEvent.KeyboardInputEvent.Pre event) {
        if (!GumTuneClientConfig.autoSell) return;
        if (GumTuneClient.mc.thePlayer == null) return;
        int eventKey = Keyboard.getEventKey();
        if (!Keyboard.isKeyDown(eventKey)) return;
        ArrayList<Integer> keyBindsAddItemToAutoSellFilter = GumTuneClientConfig.addItemToAutoSellFilter.getKeyBinds();
        if (keyBindsAddItemToAutoSellFilter.size() > 0 && keyBindsAddItemToAutoSellFilter.get(0) == eventKey) {
            GuiScreen currentScreen = event.gui;

            if (GuiContainer.class.isAssignableFrom(currentScreen.getClass())) {
                Slot currentSlot = ((GuiContainer) currentScreen).getSlotUnderMouse();

                if (currentSlot != null && currentSlot.getHasStack()) {
                    String itemId = InventoryUtils.getSkyBlockItemId(currentSlot.getStack());
                    if (itemId != null) {
                        if (itemsToSell.contains(itemId)) {
                            itemsToSell.remove(itemId);
                            ModUtils.sendMessage("Removed " + itemId + " from Auto Sell filter");
                        } else {
                            itemsToSell.add(itemId);
                            ModUtils.sendMessage("Added " + itemId + " to Auto Sell filter");
                        }
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public void onTick(TickEvent.ClientTickEvent event) {
        if (GumTuneClient.mc.thePlayer == null) return;
        if (!GumTuneClientConfig.autoSell) return;
        if (!LocationUtils.onSkyblock) return;
        if (event.phase == TickEvent.Phase.START) return;
        if (autoSellState == AutoSellState.IDLE) return;
        String chestName = GuiUtils.getOpenInventoryName();
        if (chestName != null) {
            switch (autoSellState) {
                case BZ_INSTASELL:
                    if (chestName.equals("Result") && System.currentTimeMillis() - timestamp > 4000) {
                        if (GumTuneClientConfig.autoSellMode == 2) {
                            autoSellState = AutoSellState.TRADES_SELL;
                            timestamp = System.currentTimeMillis();
                            GumTuneClient.mc.thePlayer.sendChatMessage("/trades");
                        } else {
                            autoSellState = AutoSellState.IDLE;
                            timestamp = System.currentTimeMillis();
                            GumTuneClient.mc.thePlayer.closeScreen();
                        }
                    } else if (chestName.startsWith("Bazaar ➜") && System.currentTimeMillis() - timestamp > 500) {
                        for (Slot slot : GumTuneClient.mc.thePlayer.openContainer.inventorySlots) {
                            if (!slot.getHasStack()) continue;
                            if (slot.getStack().getDisplayName().contains("Sell Inventory")) {
                                String lore = InventoryUtils.getItemLore(slot.getStack(), 4);
                                if (lore != null && StringUtils.removeFormatting(lore).contains("You don't have anything to")) {
                                    if (GumTuneClientConfig.autoSellMode == 2) {
                                        autoSellState = AutoSellState.TRADES_SELL;
                                        timestamp = System.currentTimeMillis();
                                        GumTuneClient.mc.thePlayer.sendChatMessage("/trades");
                                    } else {
                                        autoSellState = AutoSellState.IDLE;
                                        timestamp = System.currentTimeMillis();
                                        GumTuneClient.mc.thePlayer.closeScreen();
                                    }
                                } else {
                                    clickSlot(slot.slotNumber);
                                    timestamp = System.currentTimeMillis();
                                    return;
                                }
                            }
                        }
                    }

                    if (chestName.equals("Are you sure?")) {
                        clickSlot(11);
                        GumTuneClient.mc.thePlayer.closeScreen();

                        if (GumTuneClientConfig.autoSellMode == 2) {
                            autoSellState = AutoSellState.TRADES_SELL;
                            timestamp = System.currentTimeMillis();
                            GumTuneClient.mc.thePlayer.sendChatMessage("/trades");
                        } else {
                            autoSellState = AutoSellState.IDLE;
                            timestamp = System.currentTimeMillis();
                            GumTuneClient.mc.thePlayer.closeScreen();
                        }
                    }
                    break;
                case TRADES_SELL:
                    if (!GumTuneClientConfig.autoSellPassiveMode && chestName.equals("Result") && System.currentTimeMillis() - timestamp > 4000) {
                        autoSellState = AutoSellState.IDLE;
                        timestamp = System.currentTimeMillis();
                        GumTuneClient.mc.thePlayer.closeScreen();
                        soldSomething = false;
                    } else if (chestName.equals("Trades") && System.currentTimeMillis() - timestamp > GumTuneClientConfig.autoSellClickDelay) {
                        for (Slot slot : GumTuneClient.mc.thePlayer.openContainer.inventorySlots) {
                            if (slot.slotNumber < 54 || !slot.getHasStack()) continue;
                            String itemId = InventoryUtils.getSkyBlockItemId(slot.getStack());
                            if (itemId != null) {
                                if (itemsToSell.contains(itemId)) {
                                    clickSlot(slot.slotNumber);
                                    soldSomething = true;
                                    timestamp = System.currentTimeMillis();
                                    return;
                                }
                            }
                        }

                        autoSellState = AutoSellState.IDLE;

                        if (!GumTuneClientConfig.autoSellPassiveMode || GumTuneClientConfig.autoSellPassiveModeCloseTrades && soldSomething) {
                            GumTuneClient.mc.thePlayer.closeScreen();
                        }
                        timestamp = System.currentTimeMillis();
                        soldSomething = false;
                    }
                    break;
            }
        }
    }

    public static void loadConfig() {
        try {
            Path path = Paths.get("./config/" + GumTuneClient.MODID + "/autoSellFilters.json");
            if (new File(path.toUri()).exists()) {
                itemsToSell = new Gson().fromJson(new String(Files.readAllBytes(path)), new TypeToken<HashSet<String>>() {
                }.getType());
            }
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
    }

    private void saveConfig() {
        try {
            Files.write(Paths.get("./config/" + GumTuneClient.MODID + "/autoSellFilters.json"), new Gson().toJson(itemsToSell).getBytes(StandardCharsets.UTF_8));
        } catch (IOException ioException) {
            ioException.printStackTrace();
            ModUtils.sendMessage("Failed saving Auto Sell filters!");
        }
    }

    public static void autoSell() {
        timestamp = System.currentTimeMillis();

        if (GumTuneClientConfig.autoSellMode == 1) {
            autoSellState = AutoSellState.TRADES_SELL;
            GumTuneClient.mc.thePlayer.sendChatMessage("/trades");
        } else {
            autoSellState = AutoSellState.BZ_INSTASELL;
            GumTuneClient.mc.thePlayer.sendChatMessage("/bz");
        }
    }

    private void clickSlot(int slot) {
        GumTuneClient.mc.playerController.windowClick(
                GumTuneClient.mc.thePlayer.openContainer.windowId,
                slot,
                0,
                0,
                GumTuneClient.mc.thePlayer
        );
    }


}
