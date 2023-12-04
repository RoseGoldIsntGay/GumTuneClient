package rosegold.gumtuneclient.modules.macro;

import cc.polyfrost.oneconfig.utils.Multithreading;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.jetbrains.annotations.NotNull;
import rosegold.gumtuneclient.GumTuneClient;
import rosegold.gumtuneclient.config.GumTuneClientConfig;
import rosegold.gumtuneclient.events.ScreenClosedEvent;
import rosegold.gumtuneclient.utils.GuiUtils;
import rosegold.gumtuneclient.utils.ModUtils;

import java.awt.*;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import static rosegold.gumtuneclient.GumTuneClient.mc;

public class AutoHarp {
    private boolean inHarp;
    private Slot slot;
    private long timestamp;
    private long startedSongTimestamp;
    private int updates;
    private final ArrayList<ItemStack> currentInventory = new ArrayList<>();
    private long lastContainerUpdate;

    @SubscribeEvent
    public final void onGuiOpen(@NotNull GuiOpenEvent event) {
        inHarp = GuiUtils.getInventoryName(event.gui).startsWith("Harp -");
        updates = 0;
        currentInventory.clear();
    }

    @SubscribeEvent
    public void onGuiClose(ScreenClosedEvent event) {
        inHarp = false;
        updates = 0;
        currentInventory.clear();
    }

    @SubscribeEvent
    public void onBackgroundDraw(GuiScreenEvent.BackgroundDrawnEvent event) {
        if (!inHarp) return;
        if (mc.thePlayer.openContainer.inventorySlots.size() != currentInventory.size()) {
            for (Slot slot : mc.thePlayer.openContainer.inventorySlots) {
                currentInventory.add(slot.getStack());
            }
            return;
        }

        boolean updated = false;

        if (System.currentTimeMillis() - lastContainerUpdate > 175) {
            for (int i = 0; i < mc.thePlayer.openContainer.inventorySlots.size(); i++) {
                ItemStack itemStack1 = mc.thePlayer.openContainer.inventorySlots.get(i).getStack();
                ItemStack itemStack2 = currentInventory.get(i);
                if (!ItemStack.areItemStacksEqual(itemStack1, itemStack2)) {
                    if (updates < 3) {
                        startedSongTimestamp = System.currentTimeMillis();
                    }

                    lastContainerUpdate = System.currentTimeMillis();
                    currentInventory.set(i, itemStack1);
                    updated = true;
                }
            }
        }

        if (updated) {
            updates++;
            for (int slotNumber = 0; slotNumber < currentInventory.size(); slotNumber++) {
                if (slotNumber > 26 && slotNumber < 36) {
                    ItemStack itemStack = currentInventory.get(slotNumber);
                    if (itemStack != null && itemStack.getItem() instanceof ItemBlock && ((ItemBlock) itemStack.getItem()).getBlock() == Blocks.wool) {
                        int finalSlotNumber = slotNumber;
                        Multithreading.schedule(() -> {
                            slot = mc.thePlayer.openContainer.inventorySlots.get(finalSlotNumber);
                            timestamp = System.currentTimeMillis();
                            ModUtils.sendMessage("clicked slot " + (slot.slotNumber + 9) + " at " + (timestamp - startedSongTimestamp));
                            mc.playerController.windowClick(
                                    GumTuneClient.mc.thePlayer.openContainer.windowId,
                                    finalSlotNumber + 9,
                                    2,
                                    3,
                                    mc.thePlayer
                            );
                        }, GumTuneClientConfig.harpMacroDelay, TimeUnit.MILLISECONDS);
                        break;
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public void onGuiRender(GuiScreenEvent.DrawScreenEvent.Post event) {
        if (!inHarp) return;
        GlStateManager.disableLighting();
        GlStateManager.disableDepth();
        GlStateManager.disableBlend();
        if (updates != 0) {
            mc.fontRendererObj.drawStringWithShadow(
                    "song speed: " + (System.currentTimeMillis() - startedSongTimestamp) / updates + "ms",
                    100,
                    100,
                    Color.GREEN.getRGB()
            );
            mc.fontRendererObj.drawStringWithShadow(
                    "gui updates: " + updates,
                    100,
                    110,
                    Color.GREEN.getRGB()
            );
            mc.fontRendererObj.drawStringWithShadow(
                    "ms since song started : " + (System.currentTimeMillis() - startedSongTimestamp),
                    100,
                    120,
                    Color.GREEN.getRGB()
            );
        }
        if (slot != null && System.currentTimeMillis() - timestamp < GumTuneClientConfig.harpMacroDelay/* * 0.75*/) {
            mc.fontRendererObj.drawStringWithShadow(
                    "Click",
                    (event.gui.width - 176) / 2f + slot.xDisplayPosition + 8 - mc.fontRendererObj.getStringWidth("Click") / 2f,
                    (event.gui.height - 222) / 2f + slot.yDisplayPosition + 24,
                    Color.GREEN.getRGB()
            );
        }
        GlStateManager.enableLighting();
        GlStateManager.enableDepth();
    }
}