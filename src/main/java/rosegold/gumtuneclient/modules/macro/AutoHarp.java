package rosegold.gumtuneclient.modules.macro;

import net.minecraft.init.Blocks;
import net.minecraft.inventory.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import rosegold.gumtuneclient.annotations.Module;
import rosegold.gumtuneclient.config.GumTuneClientConfig;
import rosegold.gumtuneclient.utils.GuiUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static rosegold.gumtuneclient.GumTuneClient.mc;

@Module
public class AutoHarp {
    private int wait;
    private boolean inHarp;
    private final List<Item> lastInventory = new ArrayList<>();

    @SubscribeEvent
    public final void onGuiOpen(@NotNull GuiOpenEvent event) {
        String name = GuiUtils.getInventoryName(event.gui);

        if (name.startsWith("Harp -")) {
            this.lastInventory.clear();
            this.inHarp = true;
        }
    }

    @SubscribeEvent
    public final void onTick(@Nullable TickEvent.ClientTickEvent event) {
        // shadyaddons harp macro fixed feat. delay
        if (this.wait != 0) {
            wait += 1;

            if (this.wait != GumTuneClientConfig.harpMacroDelay + 1 /* wait = 1 */) {
                return;
            }

            this.wait = 0;
        }

        if (!inHarp || !GumTuneClientConfig.harpMacro || mc.thePlayer == null) return;
        if (!GuiUtils.getOpenInventoryName().startsWith("Harp -")) inHarp = false;

        List<Item> thisInventory = new ArrayList<Item>() {{
            for (Slot slot : mc.thePlayer.openContainer.inventorySlots) {
                if (slot.getStack() != null) add(slot.getStack().getItem());
            }
        }};

        if (!Objects.equals(lastInventory.toString(), thisInventory.toString())) {
            for (Slot slot : mc.thePlayer.openContainer.inventorySlots) {
                if (slot.getStack() != null
                        && slot.getStack().getItem() instanceof ItemBlock
                        && ((ItemBlock) slot.getStack().getItem()).getBlock() == Blocks.quartz_block) {

                    mc.playerController.windowClick(
                            mc.thePlayer.openContainer.windowId,
                            slot.slotNumber,
                            2,
                            3,
                            mc.thePlayer
                    );

                    wait = 1;
                    break;
                }
            }
        }

        lastInventory.clear();
        lastInventory.addAll(thisInventory);
    }
}