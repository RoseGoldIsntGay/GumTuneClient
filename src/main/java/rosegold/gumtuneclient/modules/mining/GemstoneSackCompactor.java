package rosegold.gumtuneclient.modules.mining;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import rosegold.gumtuneclient.GumTuneClient;
import rosegold.gumtuneclient.config.GumTuneClientConfig;
import rosegold.gumtuneclient.events.MillisecondEvent;
import rosegold.gumtuneclient.events.ScreenClosedEvent;
import rosegold.gumtuneclient.modules.player.AutoSell;
import rosegold.gumtuneclient.utils.*;

import java.awt.*;
import java.util.Arrays;
import java.util.Objects;

import static rosegold.gumtuneclient.GumTuneClient.mc;

public class GemstoneSackCompactor {
    private final boolean[] gemstoneToggles = {
        false, false, false, false, false, false, false
    };
    private final String[] gemstoneTypes = {
        "Jade", "Amber", "Topaz", "Sapphire", "Amethyst", "Jasper", "Ruby"
    };
    private long timestamp = 0;

    @SubscribeEvent
    public void onGuiRender(GuiScreenEvent.DrawScreenEvent.Post event) {
        if (!GumTuneClientConfig.gemstoneSackCompactor) return;
        GuiScreen screen = event.gui;
        if (screen instanceof GuiChest) {
            GuiChest guiChest = (GuiChest) screen;
            String chestName = guiChest.inventorySlots.inventorySlots.get(0).inventory.getName();
            if (chestName.equals("Gemstones Sack")) {
                mc.fontRendererObj.drawStringWithShadow(
                        Arrays.toString(gemstoneToggles),
                        100,
                        100,
                        Color.GREEN.getRGB()
                );
            }
        }
    }

    @SubscribeEvent
    public void onGuiInitialized(GuiScreenEvent.InitGuiEvent.Post event) {
        if (!GumTuneClientConfig.gemstoneSackCompactor) return;
        GuiScreen screen = event.gui;
        if (screen instanceof GuiChest) {
            GuiChest guiChest = (GuiChest) screen;
            String chestName = guiChest.inventorySlots.inventorySlots.get(0).inventory.getName();
            if (chestName.equals("Gemstones Sack")) {
                ScaledResolution scaledResolution = new ScaledResolution(GumTuneClient.mc);
                for (int i = 0; i < gemstoneTypes.length; i++) {
                    event.buttonList.add(new GuiButton(
                            i + 110,
                            (scaledResolution.getScaledWidth() - 50) / 2 - 180 + 60 * i,
                            (scaledResolution.getScaledHeight() - 20) / 2 - 105,
                            50,
                            20,
                            "§" + (gemstoneToggles[i] ? "a" : "c") + gemstoneTypes[i]
                    ));
                }
            }
        }
    }

    @SubscribeEvent
    public void onMillisecond(MillisecondEvent event) {
        if (GumTuneClient.mc.thePlayer == null) return;
        if (!GumTuneClientConfig.gemstoneSackCompactor) return;
        if (System.currentTimeMillis() - timestamp > GumTuneClientConfig.gemstoneSackCompactorClickDelay) {
            timestamp = System.currentTimeMillis();
            String chestName = GuiUtils.getOpenInventoryName();
            if ("Gemstones Sack".equals(chestName)) {
                for (int i = 0; i < gemstoneToggles.length; i++) {
                    if (gemstoneToggles[i]) {
                        ItemStack itemStack = GumTuneClient.mc.thePlayer.openContainer.inventorySlots.get(i + 10).getStack();
                        if (itemStack != null) {
                            String stored = StringUtils.removeFormatting(Objects.requireNonNull(InventoryUtils.getItemLore(itemStack, 7))).replaceAll("[A-z,: ]", "");
                            if (!stored.startsWith("0")) {
                                clickSlot(i + 10);
                                break;
                            }
                        }
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public void onActionPerformedGui(final GuiScreenEvent.ActionPerformedEvent.Post event) {
        if (!GumTuneClientConfig.gemstoneSackCompactor) return;
        GuiScreen screen = event.gui;
        if (screen instanceof GuiChest) {
            GuiChest guiChest = (GuiChest) screen;
            String chestName = guiChest.inventorySlots.inventorySlots.get(0).inventory.getName();
            if (chestName.equals("Gemstones Sack")) {
                int buttonId = event.button.id;
                gemstoneToggles[buttonId - 110] = !gemstoneToggles[buttonId - 110];
                event.button.displayString = "§" + (gemstoneToggles[buttonId - 110] ? "a" : "c") + gemstoneTypes[buttonId - 110];
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
}
