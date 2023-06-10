package rosegold.gumtuneclient.config.pages;

import cc.polyfrost.oneconfig.gui.elements.text.TextInputField;
import cc.polyfrost.oneconfig.gui.pages.Page;
import cc.polyfrost.oneconfig.renderer.NanoVGHelper;
import cc.polyfrost.oneconfig.utils.InputHandler;
import cc.polyfrost.oneconfig.utils.color.ColorPalette;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import rosegold.gumtuneclient.GumTuneClient;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class CustomEspBlockSelector extends Page {
    private TextInputField searchField = new TextInputField(1024, 48, true, "Search");
    private List<ItemStack> blockItems = new ArrayList<>();

    public CustomEspBlockSelector() {
        super("Add a block");
        //add all blocks to the list
        Item.itemRegistry.forEach(item -> {
            ItemStack stack = new ItemStack(item);
            blockItems.add(stack);
        });
    }

    public void drawAll(long vg, int x, int y, InputHandler inputHandler) {
        searchField.draw(vg, x + 16, y + 16, inputHandler);
        //now I draw the main part of the window, in this window there will be clickable grid of all the blockitems
        //the main part should also have a scrollbar on the right side
        int gridX = x + 16;
        int gridY = y + 16 + 48 + 16;
        int gridWidth = 1024 - 32;
        int gridHeight = 696 - 32 - 48 - 16;

        //now i draw the background of the grid
        NanoVGHelper.INSTANCE.drawRoundedRect(vg, gridX, gridY, gridWidth, gridHeight, ColorPalette.TRANSPARENT.getHoveredColor(), 8);

        //now i draw the grid
        int items = blockItems.size();
        int itemGap = 8;
        int itemSize = 48;
        int itemsPerRow = gridWidth / (itemSize + itemGap);
        int rows = items / itemsPerRow;
        int itemsInLastRow = items % itemsPerRow;
        int itemsInLastRowWidth = itemsInLastRow * itemSize + (itemsInLastRow - 1) * itemGap;
        int itemsInLastRowX = gridX + (gridWidth - itemsInLastRowWidth) / 2;
        int itemX = gridX + itemGap;
        int itemY = gridY + itemGap;

        for (int i = 0; i < items; i++) {
            ItemStack stack = blockItems.get(i);
            int itemRenderX = itemX + (i % itemsPerRow) * (itemSize + itemGap);
            int itemRenderY = itemY + (i / itemsPerRow) * (itemSize + itemGap);

            NanoVGHelper.INSTANCE.drawRoundedRect(vg, itemRenderX, itemRenderY, itemSize, itemSize, ColorPalette.TERTIARY.getNormalColor(), 8);
            //draw item
//            GumTuneClient.mc.getRenderItem().renderItemIntoGUI(stack, itemRenderX, itemRenderY);
        }

        //now i draw the scrollbar
        int scrollBarWidth = 16;
        int scrollBarHeight = gridHeight;
        int scrollBarX = gridX + gridWidth - scrollBarWidth;
        int scrollBarY = gridY;
        int scrollBarThumbHeight = scrollBarHeight / rows;
        int scrollBarThumbY = scrollBarY + (scrollBarHeight - scrollBarThumbHeight) / 2;
        NanoVGHelper.INSTANCE.drawRoundedRect(vg, scrollBarX, scrollBarY, scrollBarWidth, scrollBarHeight, ColorPalette.SECONDARY.getNormalColor(), 8);
        NanoVGHelper.INSTANCE.drawRoundedRect(vg, scrollBarX, scrollBarThumbY, scrollBarWidth, scrollBarThumbHeight, ColorPalette.TERTIARY.getNormalColor(), 8);

        //debug lines
        NanoVGHelper.INSTANCE.drawLine(vg, gridX, gridY, gridX + gridWidth, gridY, ColorPalette.PRIMARY_DESTRUCTIVE.getNormalColor(), 2);
        NanoVGHelper.INSTANCE.drawLine(vg, gridX, gridY, gridX, gridY + gridHeight, ColorPalette.PRIMARY_DESTRUCTIVE.getNormalColor(), 2);
        NanoVGHelper.INSTANCE.drawLine(vg, gridX + gridWidth, gridY, gridX + gridWidth, gridY + gridHeight, ColorPalette.PRIMARY_DESTRUCTIVE.getNormalColor(), 2);
        NanoVGHelper.INSTANCE.drawLine(vg, gridX, gridY + gridHeight, gridX + gridWidth, gridY + gridHeight, ColorPalette.PRIMARY_DESTRUCTIVE.getNormalColor(), 2);
    }


    @Override
    public void draw(long vg, int x, int y, InputHandler inputHandler) {
    }

    @Override
    public void scrollWithDraw(long vg, int x, int y, InputHandler inputHandler) {
        super.scrollWithDraw(vg, x, y, inputHandler);
        drawAll(vg, x, y, inputHandler);
    }

    @Override
    public void keyTyped(char key, int keyCode) {
        searchField.keyTyped(key, keyCode);
    }
}
