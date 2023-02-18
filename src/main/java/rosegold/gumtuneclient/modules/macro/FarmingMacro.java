package rosegold.gumtuneclient.modules.macro;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.lwjgl.input.Keyboard;
import rosegold.gumtuneclient.GumTuneClient;
import rosegold.gumtuneclient.config.GumTuneClientConfig;
import rosegold.gumtuneclient.utils.FontUtils;
import rosegold.gumtuneclient.utils.ModUtils;

import java.util.ArrayList;

public class FarmingMacro {
    enum State {
        LEFT,
        RIGHT,
        TO_LEFT,
        TO_RIGHT,
        NEXT_ROW

    }

    private State currentState;
    private int rowCount;
    private BlockPos previousPos;
    private BlockPos playerPos;
    private boolean enabled;

    @SubscribeEvent
    public void onKey(InputEvent.KeyInputEvent event) {
        if (!GumTuneClientConfig.farmingMacro) return;
        if (Keyboard.getEventKeyState()) return;
        int eventKey = Keyboard.getEventKey();
        ArrayList<Integer> keyBinds = GumTuneClientConfig.farmingMacroKeyBind.getKeyBinds();
        if (keyBinds.size() > 0 && keyBinds.get(0) == eventKey) {
            enabled = !enabled;
            ModUtils.sendMessage((enabled ? "Enabled" : "Disabled") + " Farming Macro");
        }
    }

    @SubscribeEvent
    public void onRenderGui(RenderGameOverlayEvent.Pre event) {
        if (!isEnabled()) return;
        if (event.type == RenderGameOverlayEvent.ElementType.ALL && currentState != null) {
            FontUtils.drawScaledString("State: " + currentState.name(), 2, 100, 100, true);
            FontUtils.drawScaledString("Pos: " + (playerPos == null ? "null" : playerPos), 2, 100, 100 + FontUtils.getLineHeight() * 2 + 1, true);
            FontUtils.drawScaledString("Previous Pos: " + (previousPos == null ? "null" : previousPos), 2, 100, 100 + FontUtils.getLineHeight() + 1, true);

        }
    }

    @SubscribeEvent
    public void onTick(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.END) return;
        if (!isEnabled()) return;

        EntityPlayerSP player = GumTuneClient.mc.thePlayer;
        playerPos = new BlockPos((int) Math.floor(player.posX), (int) Math.floor(player.posY) + 1, (int) Math.floor(player.posZ));

        switch (GumTuneClientConfig.farmingMacroMode) {
            case 0: // cocoa beans preset
                updateState(0);

                if (currentState == State.LEFT || currentState == State.RIGHT) {
                    //PlayerUtils.updateKeys(rowCount % 2 == 0, rowCount % 2 == 1, false, false, false);
                }

                if (currentState == State.TO_LEFT) {
                    //PlayerUtils.updateKeys(false, false, false, true, false);
                    if (!playerPos.equals(previousPos)) {

                    }
                }
                break;
        }

        previousPos = playerPos;
    }

    private void updateState(int mode) {
        State previousState = currentState;
        EntityPlayerSP player = GumTuneClient.mc.thePlayer;
        BlockPos playerPos = new BlockPos((int) Math.floor(player.posX), (int) Math.floor(player.posY) + 1, (int) Math.floor(player.posZ));
        EnumFacing horizontalFacing = player.getHorizontalFacing();

        switch (mode) {
            case 0:

                if (currentState == State.TO_LEFT || currentState == State.TO_RIGHT) {
                    return;
                }

                BlockPos rightBlock = playerPos.add(horizontalFacing.rotateY().getDirectionVec()).up();
                BlockPos leftBlock = playerPos.add(horizontalFacing.rotateYCCW().getDirectionVec()).up();
                if (getBlockState(rightBlock).getBlock() == Blocks.log) {
                    BlockPos nextBlock = rightBlock.add(horizontalFacing.getDirectionVec());
                    if (getBlockState(nextBlock).getBlock() == Blocks.log) {
                        currentState = State.RIGHT;
                    } else {
                        currentState = State.TO_LEFT;
                    }
                } else if (getBlockState(leftBlock).getBlock() == Blocks.log) {
                    BlockPos nextBlock = leftBlock.add(horizontalFacing.getDirectionVec());
                    if (getBlockState(nextBlock).getBlock() == Blocks.log) {
                        currentState = State.LEFT;
                    } else {
                        currentState = State.TO_RIGHT;
                    }
                } else {
                    ModUtils.sendMessage("No jungle logs were found next to the player, make sure you are rotated correctly when starting the macro!");
                    shutDown();
                }
                break;
        }
    }

    private void shutDown() {
        enabled = false;
        currentState = null;
    }

    private boolean isEnabled() {
        return GumTuneClientConfig.farmingMacro && enabled && GumTuneClient.mc.thePlayer != null && GumTuneClient.mc.theWorld != null;
    }

    private IBlockState getBlockState(BlockPos blockPos) {
        return GumTuneClient.mc.theWorld.getBlockState(blockPos);
    }
}
