package rosegold.gumtuneclient.modules.player;

import cc.polyfrost.oneconfig.gui.OneConfigGui;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.inventory.Slot;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.lwjgl.input.Keyboard;
import rosegold.gumtuneclient.GumTuneClient;
import rosegold.gumtuneclient.config.GumTuneClientConfig;
import rosegold.gumtuneclient.events.SecondEvent;
import rosegold.gumtuneclient.utils.GuiUtils;
import rosegold.gumtuneclient.utils.InventoryUtils;
import rosegold.gumtuneclient.utils.LocationUtils;
import rosegold.gumtuneclient.utils.ModUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;

public class AutoCraft {
    private enum AutoCraftState {
        IDLE,
        CRAFTING
    }

    private static AutoCraftState autoCraftState = AutoCraftState.IDLE;
    private static HashSet<String> itemsToCraft = new HashSet<>();
    private static long timestamp = System.currentTimeMillis();
    private static long openCraftingMenuTimestamp = System.currentTimeMillis();
    private static long forceStopTimestamp = System.currentTimeMillis();
    private static int timesFullInARow = 0;
    private static final HashSet<Integer> QUICK_CRAFT_SLOTS = new HashSet<Integer>(){{ add(16); add(25); add(34); }};
    private static String currentCraftedItem;

    @SubscribeEvent
    public void onSecond(SecondEvent event) {
        if (GumTuneClient.mc.thePlayer == null) return;
        if (!GumTuneClientConfig.autoCraft) return;
        if (!LocationUtils.onSkyblock) return;
        if (InventoryUtils.getFilledSlotCount() / 36f * 100 >= GumTuneClientConfig.autoCraftInventoryFullness && autoCraftState == AutoCraftState.IDLE && System.currentTimeMillis() - forceStopTimestamp > 15000) {
            if (timesFullInARow > 1) {
                ModUtils.sendMessage("Detected inventory full even after crafting, enabling 15 second cooldown until next craft can be triggered");
                forceStopTimestamp = System.currentTimeMillis();
                return;
            }
            timesFullInARow++;
            autoCraft();
        } else {
            timesFullInARow = 0;
        }

        if (GumTuneClient.mc.currentScreen instanceof OneConfigGui) {
            itemsToCraft.clear();
            for (String item : GumTuneClientConfig.autoCraftItemFilter.split(", ")) {
                if (!item.equals("")) itemsToCraft.add(item);
            }
        } else {
            GumTuneClientConfig.autoCraftItemFilter = "";

            for (String item : itemsToCraft) {
                GumTuneClientConfig.autoCraftItemFilter += item + ", ";
            }

            GumTuneClientConfig.autoCraftItemFilter = GumTuneClientConfig.autoCraftItemFilter.replaceAll(", $", "");
        }

        saveConfig();
    }

    @SubscribeEvent
    public void onKeyInput(GuiScreenEvent.KeyboardInputEvent.Pre event) {
        if (!GumTuneClientConfig.autoCraft) return;
        if (GumTuneClient.mc.thePlayer == null) return;
        int eventKey = Keyboard.getEventKey();
        if (!Keyboard.isKeyDown(eventKey)) return;
        ArrayList<Integer> keyBinds = GumTuneClientConfig.addItemToAutoCraftFilter.getKeyBinds();
        if (keyBinds.size() > 0 && keyBinds.get(0) == eventKey) {
            GuiScreen currentScreen = event.gui;

            if (GuiContainer.class.isAssignableFrom(currentScreen.getClass())) {
                Slot currentSlot = ((GuiContainer) currentScreen).getSlotUnderMouse();

                if (currentSlot != null && currentSlot.getHasStack()) {
                    String itemId = InventoryUtils.getSkyBlockItemId(currentSlot.getStack());
                    if (itemId != null) {
                        if (itemsToCraft.contains(itemId)) {
                            itemsToCraft.remove(itemId);
                            ModUtils.sendMessage("Removed " + itemId + " from Auto Craft filter");
                        } else {
                            itemsToCraft.add(itemId);
                            ModUtils.sendMessage("Added " + itemId + " to Auto Craft filter");
                        }
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public void onTick(TickEvent.ClientTickEvent event) {
        if (GumTuneClient.mc.thePlayer == null) return;
        if (!GumTuneClientConfig.autoCraft) return;
        if (!LocationUtils.onSkyblock) return;
        if (event.phase == TickEvent.Phase.START) return;
        if (autoCraftState == AutoCraftState.IDLE) return;
        String chestName = GuiUtils.getOpenInventoryName();
        if (chestName != null) {
            if (chestName.equals("Result") && System.currentTimeMillis() - timestamp > 4000) {
                autoCraftState = AutoCraftState.IDLE;
                timestamp = System.currentTimeMillis();
            } else if (chestName.equals("Craft Item") && System.currentTimeMillis() - timestamp > GumTuneClientConfig.autoCraftClickDelay && System.currentTimeMillis() - openCraftingMenuTimestamp > 1500) {
                if (System.currentTimeMillis() - timestamp > 8000) {
                    ModUtils.sendMessage("It took more than 8 seconds to craft, canceling!");
                    autoCraftState = AutoCraftState.IDLE;
                    GumTuneClient.mc.thePlayer.closeScreen();
                    timestamp = System.currentTimeMillis();
                }
                for (Slot slot : GumTuneClient.mc.thePlayer.openContainer.inventorySlots) {
                    if (!QUICK_CRAFT_SLOTS.contains(slot.slotNumber)) continue;
                    if (!slot.getHasStack()) return;
                    String itemId = InventoryUtils.getSkyBlockItemId(slot.getStack());
                    if (itemId != null) {
                        if (itemsToCraft.contains(itemId)) {
                            if (currentCraftedItem != null && !currentCraftedItem.equals(itemId)) {
                                for (Slot innerSlot : GumTuneClient.mc.thePlayer.openContainer.inventorySlots) {
                                    if (innerSlot.slotNumber < 54) continue;
                                    if (!innerSlot.getHasStack()) {
                                        clickSlot(innerSlot.slotNumber);
                                        // add an extra delay when switching types
                                        timestamp = System.currentTimeMillis() + 1000;
                                        currentCraftedItem = itemId;
                                        return;
                                    }
                                }
                            }
                            clickSlot(slot.slotNumber);
                            timestamp = System.currentTimeMillis();
                            currentCraftedItem = itemId;
                            return;
                        }
                    }
                }

                currentCraftedItem = null;
                autoCraftState = AutoCraftState.IDLE;
                GumTuneClient.mc.thePlayer.closeScreen();
                timestamp = System.currentTimeMillis();
            }
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

    public static void autoCraft() {
        timestamp = System.currentTimeMillis();
        openCraftingMenuTimestamp = System.currentTimeMillis();
        autoCraftState = AutoCraftState.CRAFTING;
        GumTuneClient.mc.thePlayer.sendChatMessage("/craft");
    }

    public static void loadConfig() {
        try {
            Path path = Paths.get("./config/" + GumTuneClient.MODID + "/autoCraftFilters.json");
            if (new File(path.toUri()).exists()) {
                itemsToCraft = new Gson().fromJson(new String(Files.readAllBytes(path)), new TypeToken<HashSet<String>>() {}.getType());
            }
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
    }

    private void saveConfig() {
        try {
            Files.write(Paths.get("./config/" + GumTuneClient.MODID + "/autoCraftFilters.json"), new Gson().toJson(itemsToCraft).getBytes(StandardCharsets.UTF_8));
        } catch (IOException ioException) {
            ioException.printStackTrace();
            ModUtils.sendMessage("Failed saving Auto Craft filters!");
        }
    }
}
