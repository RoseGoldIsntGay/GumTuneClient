package rosegold.gumtuneclient.utils;

import com.google.common.base.Predicates;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.EntitySelectors;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;

import java.util.List;

public class RaytracingUtils {
    private static final Minecraft mc = Minecraft.getMinecraft();

    public static MovingObjectPosition raytraceToBlock(float yaw, float pitch, double reach) {
        Vec3 vec3 = mc.thePlayer.getPositionEyes(1f);
        Vec3 vec31 = getVectorForRotation(pitch, yaw);
        Vec3 vec32 = vec3.addVector(vec31.xCoord * reach, vec31.yCoord * reach, vec31.zCoord * reach);
        return mc.thePlayer.worldObj.rayTraceBlocks(vec3, vec32, false, true, false);
    }

    public static MovingObjectPosition raytrace(float yaw, float pitch, double reach) {
        MovingObjectPosition ray = raytraceToBlock(yaw, pitch, reach);
        double d1 = reach;
        Vec3 vec3 = mc.thePlayer.getPositionEyes(1f);
        if (ray != null) d1 = ray.hitVec.distanceTo(vec3);
        Vec3 vec31 = getVectorForRotation(yaw, pitch);
        Vec3 vec32 = vec3.addVector(vec31.xCoord * reach, vec31.yCoord * reach, vec31.zCoord * reach);
        Entity pointedEntity = null;
        Vec3 vec33 = null;
        float f = 1.0F;
        List<Entity> list = mc
                .theWorld
                .getEntitiesInAABBexcluding(
                        mc.thePlayer,
                        mc.thePlayer.getEntityBoundingBox().addCoord(vec31.xCoord * reach, vec31.yCoord * reach, vec31.zCoord * reach).expand(f, f, f),
                        Predicates.and(EntitySelectors.NOT_SPECTATING, Entity::canBeCollidedWith)
                );
        double d2 = d1;

        for (Entity entity : list) {
            float f1 = entity.getCollisionBorderSize();
            AxisAlignedBB axisalignedbb = entity.getEntityBoundingBox().expand(f1, f1, f1);
            MovingObjectPosition movingobjectposition = axisalignedbb.calculateIntercept(vec3, vec32);
            if (axisalignedbb.isVecInside(vec3)) {
                if (d2 >= 0.0) {
                    pointedEntity = entity;
                    vec33 = movingobjectposition == null ? vec3 : movingobjectposition.hitVec;
                    d2 = 0.0;
                }
            } else if (movingobjectposition != null) {
                double d3 = vec3.distanceTo(movingobjectposition.hitVec);
                if (d3 < d2 || d2 == 0.0) {
                    if (entity != entity.ridingEntity || entity.canRiderInteract()) {
                        pointedEntity = entity;
                        vec33 = movingobjectposition.hitVec;
                        d2 = d3;
                    } else if (d2 == 0.0) {
                        pointedEntity = entity;
                        vec33 = movingobjectposition.hitVec;
                    }
                }
            }
        }

        return pointedEntity != null && (d2 < d1 || ray == null) ? new MovingObjectPosition(pointedEntity, vec33) : ray;
    }

    public static Vec3 getVectorForRotation(float yaw, float pitch) {
        double f = Math.cos(-yaw * (Math.PI / 180.0) - (Math.PI));
        double f1 = Math.sin(-yaw * (Math.PI / 180.0) - Math.PI);
        double f2 = -Math.cos(-pitch * (Math.PI / 180.0));
        double f3 = Math.sin(-pitch * (Math.PI / 180.0));
        return new Vec3(f1 * f2, f3, f * f2);
    }
}
