package rosegold.gumtuneclient.mixin;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiEditSign;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.network.play.client.C12PacketUpdateSign;
import net.minecraft.tileentity.TileEntitySign;
import net.minecraft.util.ChatComponentText;
import org.lwjgl.input.Keyboard;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import rosegold.gumtuneclient.modules.farming.VisitorHelpers;
import rosegold.gumtuneclient.utils.ModUtils;

@Mixin(GuiEditSign.class)
public class MixinGuiEditSign extends GuiScreen {

    @Shadow
    private TileEntitySign tileSign;

    public MixinGuiEditSign(TileEntitySign tileEntitySign) {
        tileSign = tileEntitySign;
    }

    @Inject(method = "initGui", at = @At("RETURN"))
    private void initGui(CallbackInfo ci) {
        if (VisitorHelpers.signText.equals("")) return;

        tileSign.signText[0] = new ChatComponentText(VisitorHelpers.signText);

        NetHandlerPlayClient netHandlerPlayClient = mc.getNetHandler();
        if (netHandlerPlayClient != null) {
            netHandlerPlayClient.addToSendQueue(new C12PacketUpdateSign(tileSign.getPos(), tileSign.signText));
            VisitorHelpers.signText = "";
        }
    }
}
