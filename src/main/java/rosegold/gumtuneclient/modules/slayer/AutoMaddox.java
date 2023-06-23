package rosegold.gumtuneclient.modules.slayer;

import cc.polyfrost.oneconfig.utils.Multithreading;
import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IChatComponent;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import rosegold.gumtuneclient.GumTuneClient;
import rosegold.gumtuneclient.config.GumTuneClientConfig;
import rosegold.gumtuneclient.utils.*;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class AutoMaddox {

    private enum MaddoxState {
        IDLE,
        CALL_MADDOX,
        WAIT_FOR_PICK_UP,
        OPEN_BATPHONE,
        RESTART,
        WAITING_TO_IDLE
    }

    private static String lastMaddoxCommand = "/cb placeholder";
    private static MaddoxState maddoxState = MaddoxState.IDLE;
    private static long timestamp = System.currentTimeMillis();

    @SubscribeEvent
    public void chat(ClientChatReceivedEvent event) {
        if (!GumTuneClientConfig.autoMaddoxBatphone) return;
        String message = StringUtils.removeFormatting(event.message.getUnformattedText());
        if (message.contains(":")) return;
        if (maddoxState == MaddoxState.WAIT_FOR_PICK_UP) {
            if (message.contains("[OPEN MENU]")) {
                List<IChatComponent> siblings = event.message.getSiblings();
                for (IChatComponent sibling : siblings) {
                    if (sibling.getUnformattedText().contains("[OPEN MENU]")) {
                        lastMaddoxCommand = sibling.getChatStyle().getChatClickEvent().getValue();
                        maddoxState = MaddoxState.OPEN_BATPHONE;
                    }
                }
            }
        }
        if (maddoxState == MaddoxState.CALL_MADDOX) {
            if (message.contains("You can't use this menu while in combat!")) {
                Multithreading.schedule(() -> maddoxState = MaddoxState.IDLE, 500, TimeUnit.MILLISECONDS);
                event.setCanceled(true);
            }
        }
    }

    @SubscribeEvent
    public void onTick(TickEvent.ClientTickEvent event) {
        if (GumTuneClient.mc.thePlayer == null || GumTuneClient.mc.theWorld == null) return;
        if (event.phase == TickEvent.Phase.START) return;
        if (!GumTuneClientConfig.autoMaddoxBatphone) return;

        switch (maddoxState) {
            case CALL_MADDOX:
                if (GumTuneClient.mc.currentScreen == null && System.currentTimeMillis() - timestamp > 500) {
                    ModUtils.sendMessage("Failed to open abiphone, retrying");
                    maddoxState = MaddoxState.IDLE;
                }
                break;
            case OPEN_BATPHONE:
                GumTuneClient.mc.thePlayer.sendChatMessage(lastMaddoxCommand);
                maddoxState = MaddoxState.RESTART;
                break;
            case WAITING_TO_IDLE:
                if (System.currentTimeMillis() - timestamp > 3000) {
                    // Detect if restarting quest failed then bounce back
                    // probably not needed
                    maddoxState = MaddoxState.IDLE;
                }
                break;
            case IDLE:
                if (ScoreboardUtils.scoreboardContains("Boss slain!")) {
                    int maddox = InventoryUtils.findItemInHotbar("Batphone");
                    int abiphone = InventoryUtils.findItemInHotbar("Abiphone");

                    if (maddox != -1) {
                        ItemStack item = GumTuneClient.mc.thePlayer.inventory.getStackInSlot(maddox);
                        int save = GumTuneClient.mc.thePlayer.inventory.currentItem;
                        GumTuneClient.mc.thePlayer.inventory.currentItem = maddox;
                        GumTuneClient.mc.playerController.sendUseItem(GumTuneClient.mc.thePlayer, GumTuneClient.mc.theWorld, item);
                        GumTuneClient.mc.thePlayer.inventory.currentItem = save;
                        timestamp = System.currentTimeMillis();
                        maddoxState = MaddoxState.WAIT_FOR_PICK_UP;
                    } else if (abiphone != -1) {
                        ItemStack item = GumTuneClient.mc.thePlayer.inventory.getStackInSlot(abiphone);
                        int save = GumTuneClient.mc.thePlayer.inventory.currentItem;
                        GumTuneClient.mc.thePlayer.inventory.currentItem = abiphone;
                        GumTuneClient.mc.playerController.sendUseItem(GumTuneClient.mc.thePlayer, GumTuneClient.mc.theWorld, item);
                        GumTuneClient.mc.thePlayer.inventory.currentItem = save;
                        maddoxState = MaddoxState.CALL_MADDOX;
                        timestamp = System.currentTimeMillis();
                    }
                }
                break;
            case WAIT_FOR_PICK_UP:
                if (GumTuneClient.mc.currentScreen == null && System.currentTimeMillis() - timestamp > 6000) {
                    ModUtils.sendMessage("Seems like calling maddox failed for some reason. switching back to idle");

                    timestamp = System.currentTimeMillis();
                    maddoxState = MaddoxState.WAITING_TO_IDLE;
                }
                break;
        }
    }

    @SubscribeEvent
    public void onBackgroundDrawn(GuiScreenEvent.BackgroundDrawnEvent event) {
        if (GumTuneClient.mc.thePlayer == null || GumTuneClient.mc.theWorld == null) return;
        if (!GumTuneClientConfig.autoMaddoxBatphone) return;

        switch (maddoxState) {
            case WAIT_FOR_PICK_UP:
                if (GuiUtils.getInventoryName(event.gui).contains("Abiphone") && System.currentTimeMillis() - timestamp > 1000) {
                    ModUtils.sendMessage("Detected failed clicking in abiphone menu, trying again");
                    maddoxState = MaddoxState.CALL_MADDOX;
                }
                break;
            case CALL_MADDOX:
                if (GuiUtils.getInventoryName(event.gui).contains("Abiphone") && System.currentTimeMillis() - timestamp > 500) {
                    List<Slot> chestInventory = ((GuiChest) GumTuneClient.mc.currentScreen).inventorySlots.inventorySlots;

                    for (Slot slot : chestInventory) {
                        if (slot.getHasStack() && slot.getStack().getDisplayName().contains("Maddox")) {
                            clickSlot(slot.slotNumber, 0);
                            maddoxState = MaddoxState.WAIT_FOR_PICK_UP;
                            timestamp = System.currentTimeMillis();
                            return;
                        }
                    }

                    ModUtils.sendMessage("Unable to find maddox in your abiphone, disabling Auto Slayer");
                    GumTuneClientConfig.autoMaddoxBatphone = false;
                }
                break;
            case RESTART:
                if (GuiUtils.getInventoryName(event.gui).contains("Slayer")) {
                    List<Slot> chestInventory = ((GuiChest) GumTuneClient.mc.currentScreen).inventorySlots.inventorySlots;
                    if (!chestInventory.get(13).getHasStack()) return;

                    String displayName = chestInventory.get(13).getStack().getDisplayName();
                    if (displayName.contains("Complete") || displayName.contains("Failed")) {
                        clickSlot(13, 0);
                        clickSlot(10 + GumTuneClientConfig.autoMaddoxBossType, 1);
                        clickSlot(11 + GumTuneClientConfig.autoMaddoxBossLevel, 2);
                        clickSlot(11, 3); //confirm
                    } else {
                        clickSlot(10 + GumTuneClientConfig.autoMaddoxBossType, 0);
                        clickSlot(11 + GumTuneClientConfig.autoMaddoxBossLevel, 1);
                        clickSlot(11, 2); //confirm
                    }

                    timestamp = System.currentTimeMillis();
                    maddoxState = MaddoxState.WAITING_TO_IDLE;
                }
                break;
        }
    }

    @SubscribeEvent
    public void onOverlayRender(RenderGameOverlayEvent.Post event) {
        if (!GumTuneClientConfig.autoMaddoxBatphone) return;
        if (event.type == RenderGameOverlayEvent.ElementType.ALL) {
            FontUtils.drawScaledString("Maddox State: " + maddoxState, 1, 300, 100, true);
            FontUtils.drawScaledString("Debounce: " + (System.currentTimeMillis() - timestamp), 1, 300, 110, true);
        }
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
