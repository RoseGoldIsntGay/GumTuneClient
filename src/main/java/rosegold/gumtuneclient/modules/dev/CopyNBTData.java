package rosegold.gumtuneclient.modules.dev;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.inventory.Slot;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MovingObjectPosition;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import org.lwjgl.input.Keyboard;
import rosegold.gumtuneclient.GumTuneClient;
import rosegold.gumtuneclient.config.GumTuneClientConfig;
import rosegold.gumtuneclient.utils.DevUtils;
import rosegold.gumtuneclient.utils.ModUtils;

import java.util.ArrayList;

public class CopyNBTData {
    @SubscribeEvent
    public void onKeyInput(GuiScreenEvent.KeyboardInputEvent.Pre event) {
        if (!GumTuneClientConfig.copyNBTData) return;
        int eventKey = Keyboard.getEventKey();
        if (!Keyboard.isKeyDown(eventKey)) return;
        ArrayList<Integer> keyBinds = GumTuneClientConfig.copyNBTDataKeyBind.getKeyBinds();
        if (keyBinds.size() > 0 && keyBinds.get(0) == eventKey) {
            GuiScreen currentScreen = event.gui;

            if (GuiContainer.class.isAssignableFrom(currentScreen.getClass())) {
                Slot currentSlot = ((GuiContainer) currentScreen).getSlotUnderMouse();

                if (currentSlot != null && currentSlot.getHasStack()) {
                    DevUtils.copyNBTTagToClipboard(currentSlot.getStack().serializeNBT(), "&aItem data was copied to clipboard!");
                }
            }
        }
    }

    @SubscribeEvent
    public void keyPress(InputEvent.KeyInputEvent event) {
        if (!GumTuneClientConfig.copyNBTData) return;
        int eventKey = Keyboard.getEventKey();
        ArrayList<Integer> keyBinds = GumTuneClientConfig.copyNBTDataKeyBind.getKeyBinds();
        if (keyBinds.size() > 0 && keyBinds.get(0) == eventKey && GumTuneClient.mc.objectMouseOver != null) {
            if (GumTuneClient.mc.objectMouseOver.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK) {
                BlockPos blockPos = GumTuneClient.mc.objectMouseOver.getBlockPos();
                IBlockState iBlockState = GumTuneClient.mc.theWorld.getBlockState(blockPos);
                Block block = iBlockState.getBlock();
                if (block.hasTileEntity(iBlockState)) {
                    TileEntity tileEntity = GumTuneClient.mc.theWorld.getTileEntity(blockPos);
                    assert tileEntity != null;
                    DevUtils.copyStringToClipboard(DevUtils.getTileEntityData(tileEntity), "&aTile entity data was copied to clipboard!");
                } else {
                    ModUtils.sendMessage("&cBlock has no tile entity");
                }
            } else if (GumTuneClient.mc.objectMouseOver.typeOfHit == MovingObjectPosition.MovingObjectType.ENTITY) {
                DevUtils.copyStringToClipboard(DevUtils.getEntityData(GumTuneClient.mc.objectMouseOver.entityHit), "&aEntity data of " + GumTuneClient.mc.objectMouseOver.entityHit + " was copied to clipboard!");
            } else if (GumTuneClient.mc.objectMouseOver.typeOfHit == MovingObjectPosition.MovingObjectType.MISS) {
                DevUtils.copyStringToClipboard(DevUtils.getEntityData(GumTuneClient.mc.thePlayer), "&aEntity data of the player was copied to clipboard!");
            }
        }
    }
}
