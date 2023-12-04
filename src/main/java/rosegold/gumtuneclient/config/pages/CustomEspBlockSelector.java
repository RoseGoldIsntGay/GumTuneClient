package rosegold.gumtuneclient.config.pages;

import cc.polyfrost.oneconfig.gui.elements.text.TextInputField;
import cc.polyfrost.oneconfig.gui.pages.Page;
import cc.polyfrost.oneconfig.renderer.NanoVGHelper;
import cc.polyfrost.oneconfig.renderer.font.Fonts;
import cc.polyfrost.oneconfig.utils.InputHandler;
import cc.polyfrost.oneconfig.utils.color.ColorPalette;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemColored;
import net.minecraft.item.ItemMultiTexture;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.registry.GameData;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class CustomEspBlockSelector extends Page {
    private TextInputField searchField = new TextInputField(1024, 48, true, "Search");
    //    private BasicButton
    public static List<ItemStack> blockItems = new ArrayList<>();
    private final int WIDTH = 1024;
    private final int HEIGHT = 696;
    private final int ITEM_GAP = 8;
    private final int ITEM_SIZE = 48;
    private final int ITEMS_PER_ROW = WIDTH / (ITEM_SIZE + ITEM_GAP);

    public CustomEspBlockSelector() {
        super("Add a block");
    }

    //17 25 94
    public static void loadItems() {
        GameData.getItemRegistry().forEach(it -> {
            if (it instanceof ItemMultiTexture) {
                ItemMultiTexture itemMultiTexture = (ItemMultiTexture) it;
                itemMultiTexture.getSubItems(it, null, blockItems);
            } else if (it instanceof ItemColored) {
                ItemColored itemColored = (ItemColored) it;
                itemColored.getSubItems(it, null, blockItems);
            } else if (it instanceof ItemBlock) {
                blockItems.add(new ItemStack(it));
            }
        });
        blockItems = blockItems.stream().filter(it -> it != null && it.getItem() != null).collect(Collectors.toList());
        ArrayList<ItemStack> tmpList = new ArrayList<>();
        blockItems.forEach(it -> {
            if (!tmpList.contains(it) && tmpList.stream().noneMatch(it2 -> Objects.equals(it.serializeNBT(), it2.serializeNBT()))) {
                tmpList.add(it);
            }
        });
        blockItems = tmpList;
    }

    public void drawAll(long vg, int x, int y, InputHandler inputHandler) {
        int startX = x + 16;
        int startY = y + 16;
        int gridX = x + 16;
        int gridY = y + 16 + 48 + 16;
        int items = blockItems.size();
        int rows = items / ITEMS_PER_ROW;
        int itemsInLastRow = items % ITEMS_PER_ROW;
        int itemsInLastRowWidth = itemsInLastRow * ITEM_SIZE + (itemsInLastRow - 1) * ITEM_GAP;
        int itemsInLastRowX = gridX + (WIDTH - itemsInLastRowWidth) / 2;
        int itemX = gridX + ITEM_GAP;
        int itemY = gridY + ITEM_GAP;
        int gridHeight = rows * (ITEM_SIZE + ITEM_GAP) + ITEM_GAP;

        NanoVGHelper.INSTANCE.drawRoundedRect(vg, gridX, gridY, WIDTH, gridHeight, ColorPalette.SECONDARY.getHoveredColor(), 8);

        for (int i = 0; i < items; i++) {
            ItemStack stack = blockItems.get(i);
            int itemRenderX = itemX + (i % ITEMS_PER_ROW) * (ITEM_SIZE + ITEM_GAP);
            int itemRenderY = itemY + (i / ITEMS_PER_ROW) * (ITEM_SIZE + ITEM_GAP);

            NanoVGHelper.INSTANCE.drawRoundedRect(vg, itemRenderX, itemRenderY, ITEM_SIZE, ITEM_SIZE, ColorPalette.TERTIARY.getNormalColor(), 8);
            int fontSize = stack.getDisplayName().length() > 5 ? 12 - (stack.getDisplayName().length() / 3) : 16;
            NanoVGHelper.INSTANCE.drawText(vg, stack.getDisplayName(), itemRenderX, itemRenderY + ((float) ITEM_SIZE / 2), ColorPalette.PRIMARY.getNormalColor(), fontSize, Fonts.REGULAR);

            //temp removed because it is bugged
            //ScaledResolution scaledResolution = new ScaledResolution(GumTuneClient.mc);
            //GumTuneClient.mc.getRenderItem().renderItemAndEffectIntoGUI(stack, itemRenderX / scaledResolution.getScaleFactor(), itemRenderY / scaledResolution.getScaleFactor());
        }
    }


    @Override
    public void draw(long vg, int x, int y, InputHandler inputHandler) {
        drawAll(vg, x, y, inputHandler);
    }

    @Override
    public int drawStatic(long vg, int x, int y, InputHandler inputHandler) {
        searchField.draw(vg, x + 16, y + 16, inputHandler);
        return 48;
    }

    @Override
    public void keyTyped(char key, int keyCode) {
        searchField.keyTyped(key, keyCode);
    }

    @Override
    public int getMaxScrollHeight() {
        return (blockItems.size() / ITEMS_PER_ROW) * (ITEM_SIZE + ITEM_GAP);
    }
}
