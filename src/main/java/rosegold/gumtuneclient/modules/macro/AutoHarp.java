package rosegold.gumtuneclient.modules.macro;

import cc.polyfrost.oneconfig.utils.Multithreading;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.server.S2FPacketSetSlot;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.jetbrains.annotations.NotNull;
import rosegold.gumtuneclient.config.GumTuneClientConfig;
import rosegold.gumtuneclient.events.PacketReceivedEvent;
import rosegold.gumtuneclient.events.ScreenClosedEvent;
import rosegold.gumtuneclient.utils.GuiUtils;
import rosegold.gumtuneclient.utils.ModUtils;

import java.awt.Color;
import java.util.ArrayList;

import static rosegold.gumtuneclient.GumTuneClient.mc;

public class AutoHarp {
    private boolean inHarp;
    private Slot slot;
    private long timestamp;
    private long startedSongTimestamp;
    private int updates;
    private final ArrayList<ItemStack> currentInventory = new ArrayList<>();

    @SubscribeEvent
    public final void onGuiOpen(@NotNull GuiOpenEvent event) {
        this.inHarp = GuiUtils.getInventoryName(event.gui).startsWith("Harp -");
        this.updates = 0;
    }

    @SubscribeEvent
    public void onGuiClose(ScreenClosedEvent event) {
        this.inHarp = false;
    }

    @SubscribeEvent
    public void onTick(TickEvent.ClientTickEvent event) {
        if (!this.inHarp) return;
        if (event.phase == TickEvent.Phase.START) return;
        if (mc.thePlayer.openContainer.inventorySlots.size() != this.currentInventory.size()) {
            for (Slot slot : mc.thePlayer.openContainer.inventorySlots) {
                this.currentInventory.add(slot.getStack());
            }
            this.updates++;
            return;
        }
        boolean flag = false;
        for (int i = 0; i < mc.thePlayer.openContainer.inventorySlots.size(); i++) {
            ItemStack itemStack1 = mc.thePlayer.openContainer.inventorySlots.get(i).getStack();
            ItemStack itemStack2 = this.currentInventory.get(i);
            if (!ItemStack.areItemStacksEqual(itemStack1, itemStack2)) {
                if (this.updates < 2) {
                    ModUtils.sendMessage(this.updates);
                    this.startedSongTimestamp = System.currentTimeMillis();
                }
                this.currentInventory.set(i, itemStack1);
                flag = true;
            }
        }

        if (flag) {
            this.updates++;
            ModUtils.sendMessage(updates);
        }
    }

    @SubscribeEvent
    public void onGuiRender(GuiScreenEvent.DrawScreenEvent.Post event) {
        if (!this.inHarp) return;
        GlStateManager.disableLighting();
        GlStateManager.disableDepth();
        GlStateManager.disableBlend();
        if (this.updates != 0) {
            mc.fontRendererObj.drawStringWithShadow(
                    (System.currentTimeMillis() - this.startedSongTimestamp) / this.updates + "ms",
                    100,
                    100,
                    Color.GREEN.getRGB()
            );
        }
        if (this.slot != null && System.currentTimeMillis() - timestamp < GumTuneClientConfig.harpMacroDelay/* * 0.75*/) {
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

    @SubscribeEvent
    public void onPacket(PacketReceivedEvent.Post event) {
        if (!GumTuneClientConfig.harpMacro) return;
        if (!this.inHarp) return;
        if (event.packet instanceof S2FPacketSetSlot) {
            S2FPacketSetSlot packetSetSlot = (S2FPacketSetSlot) event.packet;
            ItemStack itemStack = packetSetSlot.func_149174_e();
            int windowId = packetSetSlot.func_149175_c();
            if (itemStack != null) {
                int slotNumber = packetSetSlot.func_149173_d();
                if (slotNumber > 26 && slotNumber < 36 &&
                        itemStack.getItem() instanceof ItemBlock &&
                        ((ItemBlock) itemStack.getItem()).getBlock() == Blocks.wool) {
                    Multithreading.runAsync(() -> {
                        try {
                            Thread.sleep(50L);
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                        int clicksNeeded = 0;
//                        System.out.println(itemStack.getItem());
//                        mc.thePlayer.openContainer.inventorySlots.forEach(slot1 -> {
//                            if (slot1.getStack() != null && slot1.getStack().getItem() instanceof ItemBlock) {
//                                System.out.println(slot1.slotNumber + " " + ((ItemBlock) slot1.getStack().getItem()).getBlock());
//                            }
//                        });
                        for (int i = slotNumber; i >= 0; i -= 9) {
                            ItemStack stackInSlot = mc.thePlayer.openContainer.inventorySlots.get(i).getStack();
                            if (((ItemBlock) stackInSlot.getItem()).getBlock() == ((ItemBlock) itemStack.getItem()).getBlock()) {
                                clicksNeeded++;
                            } else {
                                break;
                            }
                        }

                        ModUtils.sendMessage("needed: " + clicksNeeded);
                        for (int i = 1; i <= clicksNeeded; i++) {
                            try {
                                Thread.sleep(GumTuneClientConfig.harpMacroDelay);
                            } catch (InterruptedException e) {
                                throw new RuntimeException(e);
                            }
                            this.slot = mc.thePlayer.openContainer.inventorySlots.get(slotNumber);
                            this.timestamp = System.currentTimeMillis();
                            ModUtils.sendMessage("clicked at " + this.timestamp);
                            mc.playerController.windowClick(
                                    windowId,
                                    slotNumber + 9,
                                    2,
                                    3,
                                    mc.thePlayer
                            );
                        }
                    });
                }
            }
        }
    }
}