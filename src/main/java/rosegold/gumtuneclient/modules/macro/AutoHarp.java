package rosegold.gumtuneclient.modules.macro;

import cc.polyfrost.oneconfig.utils.Multithreading;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.server.S2FPacketSetSlot;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.jetbrains.annotations.NotNull;
import rosegold.gumtuneclient.config.GumTuneClientConfig;
import rosegold.gumtuneclient.events.ReceivePacketEvent;
import rosegold.gumtuneclient.events.ScreenClosedEvent;
import rosegold.gumtuneclient.utils.GuiUtils;
import rosegold.gumtuneclient.utils.ModUtils;
import rosegold.gumtuneclient.utils.ReflectionUtils;

import java.awt.*;
import java.util.concurrent.TimeUnit;

import static rosegold.gumtuneclient.GumTuneClient.mc;

public class AutoHarp {
    private boolean inHarp;
    private Slot slot;
    private long timestamp;
    private int guiLeft;
    private int guiTop;

    @SubscribeEvent
    public final void onGuiOpen(@NotNull GuiOpenEvent event) {
        this.inHarp = GuiUtils.getInventoryName(event.gui).startsWith("Harp -");
    }

    @SubscribeEvent
    public void onGuiClose(ScreenClosedEvent event) {
        this.inHarp = false;
    }

    @SubscribeEvent
    public void onGuiRender(GuiScreenEvent.DrawScreenEvent.Post event) {
        if (!this.inHarp) return;
        if (this.slot != null && System.currentTimeMillis() - timestamp < GumTuneClientConfig.harpMacroDelay * 0.75) {
            GlStateManager.disableLighting();
            GlStateManager.disableDepth();
            GlStateManager.disableBlend();
            mc.fontRendererObj.drawStringWithShadow(
                    "Click",
                    (event.gui.width - 176) / 2f + slot.xDisplayPosition + 8 - mc.fontRendererObj.getStringWidth("Click") / 2f,
                    (event.gui.height - 222) / 2f + slot.yDisplayPosition + 24,
                    Color.GREEN.getRGB()
            );
            GlStateManager.enableLighting();
            GlStateManager.enableDepth();
        }
    }

    @SubscribeEvent
    public void onPacket(ReceivePacketEvent.Post event) throws InterruptedException {
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