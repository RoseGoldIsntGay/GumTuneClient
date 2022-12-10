package rosegold.gumtuneclient.events;

import net.minecraft.client.model.ModelBase;
import net.minecraft.entity.EntityLivingBase;
import net.minecraftforge.fml.common.eventhandler.Cancelable;
import net.minecraftforge.fml.common.eventhandler.Event;

@Cancelable
public class RenderLivingEntityEvent extends Event {

    public EntityLivingBase entity;
    public float limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scaleFactor;
    public ModelBase modelBase;

    public RenderLivingEntityEvent(EntityLivingBase entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor, ModelBase modelBase) {
        this.entity = entity;
        this.limbSwing = limbSwing;
        this.limbSwingAmount = limbSwingAmount;
        this.ageInTicks = ageInTicks;
        this.netHeadYaw = netHeadYaw;
        this.headPitch = headPitch;
        this.scaleFactor = scaleFactor;
        this.modelBase = modelBase;
    }
}