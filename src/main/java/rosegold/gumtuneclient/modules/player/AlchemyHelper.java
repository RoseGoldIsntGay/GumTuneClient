    package rosegold.gumtuneclient.modules.player;

import net.minecraft.inventory.Slot;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import rosegold.gumtuneclient.GumTuneClient;
import rosegold.gumtuneclient.config.GumTuneClientConfig;
import rosegold.gumtuneclient.utils.GuiUtils;
import rosegold.gumtuneclient.utils.InventoryUtils;
import rosegold.gumtuneclient.utils.LocationUtils;
import rosegold.gumtuneclient.utils.StringUtils;

import java.util.List;
import java.util.Objects;

public class AlchemyHelper {

    private static final String WATER_BOTTLE = "Water Bottle";
    private static final String AWKWARD_POTION = "Awkward Potion";
    private static final String ENCHANTED_GLOWSTONE_DUST = "ENCHANTED_GLOWSTONE_DUST";
    private enum AlchemyState {
        PICK_UP,
        PLACE_IN_BREWER,
        EXTRACT_POTION_A,
        EXTRACT_POTION_B,
        EXTRACT_POTION_C
    }
    private static AlchemyState alchemyState = AlchemyState.PICK_UP;
    private static long timestamp = System.currentTimeMillis();

    @SubscribeEvent
    public void onTick(TickEvent.ClientTickEvent event) {
        if (GumTuneClient.mc.thePlayer == null) return;
        if (!GumTuneClientConfig.alchemyHelper) return;
        if (!LocationUtils.onSkyblock) return;
        if (event.phase == TickEvent.Phase.START) return;
        String chestName = GuiUtils.getOpenInventoryName();
        if (chestName != null && StringUtils.removeFormatting(chestName).equals("Brewing Stand")) {
            switch (alchemyState) {
                case PICK_UP:
                    if (System.currentTimeMillis() - timestamp > GumTuneClientConfig.alchemyHelperActionDelay * 2L) {
                        List<Slot> slotList = GumTuneClient.mc.thePlayer.openContainer.inventorySlots;

                        if (!slotList.get(13).getHasStack() && slotList.get(38).getHasStack()) {
                            String slotAName = StringUtils.removeFormatting(slotList.get(38).getStack().getDisplayName());

                            if (GumTuneClientConfig.alchemyHelperAutoNetherWart && slotAName.equals(WATER_BOTTLE)) {
                                Slot netherWartSlot = getItemFromInventory("NETHER_STALK");
                                if (netherWartSlot != null) {
                                    clickSlot(netherWartSlot.slotNumber, 0, 0);
                                    alchemyState = AlchemyState.PLACE_IN_BREWER;
                                    timestamp = System.currentTimeMillis();
                                }

                                return;
                            }

                            if (slotList.get(40).getHasStack() && slotList.get(42).getHasStack()) {
                                String slotBName = StringUtils.removeFormatting(slotList.get(40).getStack().getDisplayName());
                                String slotCName = StringUtils.removeFormatting(slotList.get(42).getStack().getDisplayName());

                                if (GumTuneClientConfig.alchemyHelperAutoMainIngredient && slotAName.equals(AWKWARD_POTION) && slotBName.equals(AWKWARD_POTION) && slotCName.equals(AWKWARD_POTION)) {
                                    Slot netherWartSlot = getItemFromInventory(GumTuneClientConfig.alchemyHelperMainIngredientId);
                                    if (netherWartSlot != null) {
                                        clickSlot(netherWartSlot.slotNumber, 0, 0);
                                        alchemyState = AlchemyState.PLACE_IN_BREWER;
                                        timestamp = System.currentTimeMillis();
                                    }

                                    return;
                                }

                                if (GumTuneClientConfig.alchemyHelperAutoGlowstone && !GumTuneClientConfig.alchemyHelperBasePotionLevel.equals("")) {
                                    if (slotAName.endsWith(" " + GumTuneClientConfig.alchemyHelperBasePotionLevel + " Potion") && slotBName.endsWith(" " + GumTuneClientConfig.alchemyHelperBasePotionLevel + " Potion") && slotCName.endsWith(" " + GumTuneClientConfig.alchemyHelperBasePotionLevel + " Potion")) {
                                        Slot glowstoneSlot = getItemFromInventory(ENCHANTED_GLOWSTONE_DUST);
                                        if (glowstoneSlot != null) {
                                            clickSlot(glowstoneSlot.slotNumber, 0, 0);
                                            alchemyState = AlchemyState.PLACE_IN_BREWER;
                                            timestamp = System.currentTimeMillis();
                                        }

                                        return;
                                    }
                                }

                                if (GumTuneClientConfig.alchemyHelperAutoExtractPotions) {
                                    alchemyState = AlchemyState.EXTRACT_POTION_A;
                                    timestamp = System.currentTimeMillis();
                                }
                            }
                        }
                    }
                    break;
                case PLACE_IN_BREWER:
                    if (System.currentTimeMillis() - timestamp > GumTuneClientConfig.alchemyHelperActionDelay) {
                        clickSlot(13, 1, 0);
                        alchemyState = AlchemyState.PICK_UP;
                        timestamp = System.currentTimeMillis();
                        if (GumTuneClientConfig.alchemyHelperCloseBrewer) {
                            GumTuneClient.mc.thePlayer.closeScreen();
                        }
                    }
                    break;
                case EXTRACT_POTION_A:
                    if (System.currentTimeMillis() - timestamp > GumTuneClientConfig.alchemyHelperActionDelay) {
                        clickSlot(38, 0, 1);
                        alchemyState = AlchemyState.EXTRACT_POTION_B;
                        timestamp = System.currentTimeMillis();
                    }
                    break;
                case EXTRACT_POTION_B:
                    if (System.currentTimeMillis() - timestamp > GumTuneClientConfig.alchemyHelperActionDelay) {
                        clickSlot(40, 0, 1);
                        alchemyState = AlchemyState.EXTRACT_POTION_C;
                        timestamp = System.currentTimeMillis();
                    }
                    break;
                case EXTRACT_POTION_C:
                    if (System.currentTimeMillis() - timestamp > GumTuneClientConfig.alchemyHelperActionDelay) {
                        clickSlot(42, 0, 1);
                        alchemyState = AlchemyState.PICK_UP;
                        timestamp = System.currentTimeMillis();
                    }
                    break;

            }
        }
    }

    private void clickSlot(int slot, int mouseButton, int clickMode) {
        GumTuneClient.mc.playerController.windowClick(
                GumTuneClient.mc.thePlayer.openContainer.windowId,
                slot,
                mouseButton,
                clickMode,
                GumTuneClient.mc.thePlayer
        );
    }

    private Slot getItemFromInventory(String skyblockId) {
        for (Slot slot : GumTuneClient.mc.thePlayer.openContainer.inventorySlots) {
            if (slot.getHasStack()) {
                String itemId = InventoryUtils.getSkyBlockItemId(slot.getStack());
                if (itemId != null && itemId.equals(skyblockId)) {
                    return slot;
                }
            }
        }

        return null;
    }

}
