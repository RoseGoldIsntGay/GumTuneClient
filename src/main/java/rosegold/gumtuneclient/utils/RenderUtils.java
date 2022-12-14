package rosegold.gumtuneclient.utils;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.util.*;
import org.lwjgl.opengl.GL11;
import rosegold.gumtuneclient.GumTuneClient;
import rosegold.gumtuneclient.mixin.accessors.RenderManagerAccessor;

import java.awt.*;
import java.util.ArrayList;

import static rosegold.gumtuneclient.GumTuneClient.mc;

public class RenderUtils {

    private static final ResourceLocation beaconBeam = new ResourceLocation("textures/entity/beacon_beam.png");

    public static void renderBeacon(Vec3 location, Color color, float partialTicks) {
        int height = 300;
        int bottomOffset = 0;
        int topOffset = bottomOffset + height;

        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldrenderer = tessellator.getWorldRenderer();

        GlStateManager.pushMatrix();
        GlStateManager.disableDepth();
        GlStateManager.translate(-mc.getRenderManager().viewerPosX, -mc.getRenderManager().viewerPosY, -mc.getRenderManager().viewerPosZ);
        mc.getTextureManager().bindTexture(beaconBeam);

        GL11.glTexParameterf(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL11.GL_REPEAT);
        GL11.glTexParameterf(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL11.GL_REPEAT);
        GlStateManager.disableLighting();
        GlStateManager.enableCull();
        GlStateManager.enableTexture2D();
        GlStateManager.tryBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ONE, GL11.GL_ZERO);
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ZERO);

        double time = mc.theWorld.getWorldTime() + partialTicks;

        double d1 = MathHelper.func_181162_h(-time * 0.2 - MathHelper.floor_double(-time * 0.1));

        double d2 = time * 0.025 * -1.5;
        double d4 = 0.5 + Math.cos(d2 + 2.356194490192345) * 0.2;
        double d5 = 0.5 + Math.sin(d2 + 2.356194490192345) * 0.2;
        double d6 = 0.5 + Math.cos(d2 + (Math.PI / 4)) * 0.2;
        double d7 = 0.5 + Math.sin(d2 + (Math.PI / 4)) * 0.2;
        double d8 = 0.5 + Math.cos(d2 + 3.9269908169872414) * 0.2;
        double d9 = 0.5 + Math.sin(d2 + 3.9269908169872414) * 0.2;
        double d10 = 0.5 + Math.cos(d2 + 5.497787143782138) * 0.2;
        double d11 = 0.5 + Math.sin(d2 + 5.497787143782138) * 0.2;
        double d14 = -1 + d1;
        double d15 = height * 2.5 + d14;

        double x = location.xCoord;
        double y = location.yCoord;
        double z = location.zCoord;

        worldrenderer.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
        worldrenderer.pos(x + d4, y + topOffset, z + d5).tex(1.0D, d15).color(color.getRed(), color.getGreen(), color.getBlue(),1.0F * color.getAlpha()).endVertex();
        worldrenderer.pos(x + d4, y + bottomOffset, z + d5).tex(1.0D, d14).color(color.getRed(), color.getGreen(), color.getBlue(), 1.0F).endVertex();
        worldrenderer.pos(x + d6, y + bottomOffset, z + d7).tex(0.0D, d14).color(color.getRed(), color.getGreen(), color.getBlue(), 1.0F).endVertex();
        worldrenderer.pos(x + d6, y + topOffset, z + d7).tex(0.0D, d15).color(color.getRed(), color.getGreen(), color.getBlue(), 1.0F * color.getAlpha()).endVertex();
        worldrenderer.pos(x + d10, y + topOffset, z + d11).tex(1.0D, d15).color(color.getRed(), color.getGreen(), color.getBlue(), 1.0F * color.getAlpha()).endVertex();
        worldrenderer.pos(x + d10, y + bottomOffset, z + d11).tex(1.0D, d14).color(color.getRed(), color.getGreen(), color.getBlue(), 1.0F).endVertex();
        worldrenderer.pos(x + d8, y + bottomOffset, z + d9).tex(0.0D, d14).color(color.getRed(), color.getGreen(), color.getBlue(), 1.0F).endVertex();
        worldrenderer.pos(x + d8, y + topOffset, z + d9).tex(0.0D, d15).color(color.getRed(), color.getGreen(), color.getBlue(), 1.0F * color.getAlpha()).endVertex();
        worldrenderer.pos(x + d6, y + topOffset, z + d7).tex(1.0D, d15).color(color.getRed(), color.getGreen(), color.getBlue(), 1.0F * color.getAlpha()).endVertex();
        worldrenderer.pos(x + d6, y + bottomOffset, z + d7).tex(1.0D, d14).color(color.getRed(), color.getGreen(), color.getBlue(), 1.0F).endVertex();
        worldrenderer.pos(x + d10, y + bottomOffset, z + d11).tex(0.0D, d14).color(color.getRed(), color.getGreen(), color.getBlue(), 1.0F).endVertex();
        worldrenderer.pos(x + d10, y + topOffset, z + d11).tex(0.0D, d15).color(color.getRed(), color.getGreen(), color.getBlue(), 1.0F * color.getAlpha()).endVertex();
        worldrenderer.pos(x + d8, y + topOffset, z + d9).tex(1.0D, d15).color(color.getRed(), color.getGreen(), color.getBlue(), 1.0F * color.getAlpha()).endVertex();
        worldrenderer.pos(x + d8, y + bottomOffset, z + d9).tex(1.0D, d14).color(color.getRed(), color.getGreen(), color.getBlue(), 1.0F).endVertex();
        worldrenderer.pos(x + d4, y + bottomOffset, z + d5).tex(0.0D, d14).color(color.getRed(), color.getGreen(), color.getBlue(), 1.0F).endVertex();
        worldrenderer.pos(x + d4, y + topOffset, z + d5).tex(0.0D, d15).color(color.getRed(), color.getGreen(), color.getBlue(), 1.0F * color.getAlpha()).endVertex();
        tessellator.draw();

        GlStateManager.disableCull();
        double d12 = -1.0D + d1;
        double d13 = height + d12;

        worldrenderer.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
        worldrenderer.pos(x + 0.2D, y + topOffset, z + 0.2D).tex(1.0D, d13).color(color.getRed(), color.getGreen(), color.getBlue(), 0.25F * color.getAlpha()).endVertex();
        worldrenderer.pos(x + 0.2D, y + bottomOffset, z + 0.2D).tex(1.0D, d12).color(color.getRed(), color.getGreen(), color.getBlue(), 0.25F).endVertex();
        worldrenderer.pos(x + 0.8D, y + bottomOffset, z + 0.2D).tex(0.0D, d12).color(color.getRed(), color.getGreen(), color.getBlue(), 0.25F).endVertex();
        worldrenderer.pos(x + 0.8D, y + topOffset, z + 0.2D).tex(0.0D, d13).color(color.getRed(), color.getGreen(), color.getBlue(), 0.25F * color.getAlpha()).endVertex();
        worldrenderer.pos(x + 0.8D, y + topOffset, z + 0.8D).tex(1.0D, d13).color(color.getRed(), color.getGreen(), color.getBlue(), 0.25F * color.getAlpha()).endVertex();
        worldrenderer.pos(x + 0.8D, y + bottomOffset, z + 0.8D).tex(1.0D, d12).color(color.getRed(), color.getGreen(), color.getBlue(), 0.25F).endVertex();
        worldrenderer.pos(x + 0.2D, y + bottomOffset, z + 0.8D).tex(0.0D, d12).color(color.getRed(), color.getGreen(), color.getBlue(), 0.25F).endVertex();
        worldrenderer.pos(x + 0.2D, y + topOffset, z + 0.8D).tex(0.0D, d13).color(color.getRed(), color.getGreen(), color.getBlue(), 0.25F * color.getAlpha()).endVertex();
        worldrenderer.pos(x + 0.8D, y + topOffset, z + 0.2D).tex(1.0D, d13).color(color.getRed(), color.getGreen(), color.getBlue(), 0.25F * color.getAlpha()).endVertex();
        worldrenderer.pos(x + 0.8D, y + bottomOffset, z + 0.2D).tex(1.0D, d12).color(color.getRed(), color.getGreen(), color.getBlue(), 0.25F).endVertex();
        worldrenderer.pos(x + 0.8D, y + bottomOffset, z + 0.8D).tex(0.0D, d12).color(color.getRed(), color.getGreen(), color.getBlue(), 0.25F).endVertex();
        worldrenderer.pos(x + 0.8D, y + topOffset, z + 0.8D).tex(0.0D, d13).color(color.getRed(), color.getGreen(), color.getBlue(), 0.25F * color.getAlpha()).endVertex();
        worldrenderer.pos(x + 0.2D, y + topOffset, z + 0.8D).tex(1.0D, d13).color(color.getRed(), color.getGreen(), color.getBlue(), 0.25F * color.getAlpha()).endVertex();
        worldrenderer.pos(x + 0.2D, y + bottomOffset, z + 0.8D).tex(1.0D, d12).color(color.getRed(), color.getGreen(), color.getBlue(), 0.25F).endVertex();
        worldrenderer.pos(x + 0.2D, y + bottomOffset, z + 0.2D).tex(0.0D, d12).color(color.getRed(), color.getGreen(), color.getBlue(), 0.25F).endVertex();
        worldrenderer.pos(x + 0.2D, y + topOffset, z + 0.2D).tex(0.0D, d13).color(color.getRed(), color.getGreen(), color.getBlue(), 0.25F * color.getAlpha()).endVertex();
        tessellator.draw();

        GlStateManager.disableLighting();
        GlStateManager.enableTexture2D();

        GlStateManager.enableDepth();

        GlStateManager.popMatrix();
    }

    public static void renderWaypointText(String str, double X, double Y, double Z, float partialTicks) {
        renderWaypointText(str, X, Y, Z, partialTicks, true);
    }

    public static void renderWaypointText(String str, double X, double Y, double Z, float partialTicks, boolean showDist) {
        GlStateManager.alphaFunc(516, 0.1F);

        GlStateManager.pushMatrix();

        Entity viewer = GumTuneClient.mc.getRenderViewEntity();
        double viewerX = viewer.lastTickPosX + (viewer.posX - viewer.lastTickPosX) * partialTicks;
        double viewerY = viewer.lastTickPosY + (viewer.posY - viewer.lastTickPosY) * partialTicks;
        double viewerZ = viewer.lastTickPosZ + (viewer.posZ - viewer.lastTickPosZ) * partialTicks;

        double x = X - viewerX;
        double y = Y - viewerY - viewer.getEyeHeight();
        double z = Z - viewerZ;

        double distSq = x * x + y * y + z * z;
        double dist = Math.sqrt(distSq);
        if (distSq > 144) {
            x *= 12 / dist;
            y *= 12 / dist;
            z *= 12 / dist;
        }
        GlStateManager.translate(x, y, z);
        GlStateManager.translate(0, viewer.getEyeHeight(), 0);

        drawNametag(str);

        GlStateManager.rotate(-GumTuneClient.mc.getRenderManager().playerViewY, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(GumTuneClient.mc.getRenderManager().playerViewX, 1.0F, 0.0F, 0.0F);
        GlStateManager.translate(0, -0.25f, 0);
        GlStateManager.rotate(-GumTuneClient.mc.getRenderManager().playerViewX, 1.0F, 0.0F, 0.0F);
        GlStateManager.rotate(GumTuneClient.mc.getRenderManager().playerViewY, 0.0F, 1.0F, 0.0F);

        if (showDist) {
            drawNametag("??e" + Math.round(dist * 10) / 10 + " blocks");
        }

        GlStateManager.popMatrix();

        GlStateManager.disableLighting();
    }

    public static void drawNametag(String str) {
        FontRenderer fontrenderer = mc.fontRendererObj;
        float f = 1.6F;
        float f1 = 0.016666668F * f;
        GlStateManager.pushMatrix();
        GL11.glNormal3f(0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(-GumTuneClient.mc.getRenderManager().playerViewY, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(GumTuneClient.mc.getRenderManager().playerViewX, 1.0F, 0.0F, 0.0F);
        GlStateManager.scale(-f1, -f1, f1);
        GlStateManager.disableLighting();
        GlStateManager.depthMask(false);
        GlStateManager.disableDepth();
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer bufferBuilder = tessellator.getWorldRenderer();
        int i = 0;

        int j = fontrenderer.getStringWidth(str) / 2;
        GlStateManager.disableTexture2D();
        bufferBuilder.begin(7, DefaultVertexFormats.POSITION_COLOR);
        bufferBuilder.pos(-j - 1, -1 + i, 0.0D).color(0.0F, 0.0F, 0.0F, 0.25F).endVertex();
        bufferBuilder.pos(-j - 1, 8 + i, 0.0D).color(0.0F, 0.0F, 0.0F, 0.25F).endVertex();
        bufferBuilder.pos(j + 1, 8 + i, 0.0D).color(0.0F, 0.0F, 0.0F, 0.25F).endVertex();
        bufferBuilder.pos(j + 1, -1 + i, 0.0D).color(0.0F, 0.0F, 0.0F, 0.25F).endVertex();
        tessellator.draw();
        GlStateManager.enableTexture2D();
        fontrenderer.drawString(str, -fontrenderer.getStringWidth(str) / 2, i, 553648127);
        GlStateManager.depthMask(true);

        fontrenderer.drawString(str, -fontrenderer.getStringWidth(str) / 2, i, -1);

        GlStateManager.enableDepth();
        GlStateManager.enableBlend();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.popMatrix();
    }

    public static void renderBoundingBox(Entity entity, float partialTicks, int color) {
        renderBoundingBox(entity, partialTicks, color, 0.5f);
    }

    public static void renderBoundingBox(Entity entity, float partialTicks, int color, float opacity) {
        RenderManagerAccessor rm = (RenderManagerAccessor) mc.getRenderManager();

        double renderPosX = rm.getRenderPosX();
        double renderPosY = rm.getRenderPosY();
        double renderPosZ = rm.getRenderPosZ();

        double x = entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * partialTicks - renderPosX;
        double y = entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * partialTicks - renderPosY;
        double z = entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * partialTicks - renderPosZ;

        AxisAlignedBB bbox = entity.getEntityBoundingBox();
        AxisAlignedBB aabb = new AxisAlignedBB(
                bbox.minX - entity.posX + x,
                bbox.minY - entity.posY + y,
                bbox.minZ - entity.posZ + z,
                bbox.maxX - entity.posX + x,
                bbox.maxY - entity.posY + y,
                bbox.maxZ - entity.posZ + z
        );

        drawFilledBoundingBox(aabb, color, opacity);
    }

    public static void drawLines(ArrayList<Vec3> poses, final float thickness, final float partialTicks) {
        final Entity render = mc.getRenderViewEntity();
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer bufferBuilder = tessellator.getWorldRenderer();
        final double realX = render.lastTickPosX + (render.posX - render.lastTickPosX) * partialTicks;
        final double realY = render.lastTickPosY + (render.posY - render.lastTickPosY) * partialTicks;
        final double realZ = render.lastTickPosZ + (render.posZ - render.lastTickPosZ) * partialTicks;
        GlStateManager.pushMatrix();
        GlStateManager.translate(-realX, -realY, -realZ);
        GlStateManager.disableTexture2D();
        GlStateManager.disableLighting();
        GL11.glDisable(3553);
        GlStateManager.enableBlend();
        GlStateManager.disableAlpha();
        GL11.glLineWidth(thickness);
        GlStateManager.disableDepth();
        GlStateManager.depthMask(false);
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
        bufferBuilder.begin(3, DefaultVertexFormats.POSITION_COLOR);
        int num = 0;
        for (final Vec3 pos : poses) {
            final int i = ColorUtils.getChroma(2500.0f, num++ * 5);
            bufferBuilder.pos(pos.xCoord + 0.5, pos.yCoord + 0.5, pos.zCoord + 0.5).color((i >> 16 & 0xFF) / 255.0f, (i >> 8 & 0xFF) / 255.0f, (i & 0xFF) / 255.0f, (i >> 24 & 0xFF) / 255.0f).endVertex();
        }
        Tessellator.getInstance().draw();
        GlStateManager.translate(realX, realY, realZ);
        GlStateManager.disableBlend();
        GlStateManager.enableAlpha();
        GlStateManager.enableTexture2D();
        GlStateManager.enableDepth();
        GlStateManager.depthMask(true);
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
        GlStateManager.popMatrix();
    }

    public static void renderEspBox(BlockPos blockPos, float partialTicks, int color) {
        renderEspBox(blockPos, partialTicks, color, 0.5f);
    }

    public static void renderEspBox(BlockPos blockPos, float partialTicks, int color, float opacity) {
        if (blockPos != null) {
            IBlockState blockState = mc.theWorld.getBlockState(blockPos);

            if (blockState != null) {
                Block block = blockState.getBlock();
                block.setBlockBoundsBasedOnState(mc.theWorld, blockPos);
                double d0 = mc.thePlayer.lastTickPosX + (mc.thePlayer.posX - mc.thePlayer.lastTickPosX) * (double) partialTicks;
                double d1 = mc.thePlayer.lastTickPosY + (mc.thePlayer.posY - mc.thePlayer.lastTickPosY) * (double) partialTicks;
                double d2 = mc.thePlayer.lastTickPosZ + (mc.thePlayer.posZ - mc.thePlayer.lastTickPosZ) * (double) partialTicks;
                drawFilledBoundingBox(block.getSelectedBoundingBox(mc.theWorld, blockPos).expand(0.002D, 0.002D, 0.002D).offset(-d0, -d1, -d2), color, opacity);
            }
        }
    }

    public static void renderSmallBox(Vec3 vec, int color) {
        renderSmallBox(vec, color, 0.5f);
    }
    public static void renderSmallBox(Vec3 vec, int color, float opacity) {
        RenderManagerAccessor renderManager = (RenderManagerAccessor) mc.getRenderManager();

        double renderPosX = renderManager.getRenderPosX();
        double renderPosY = renderManager.getRenderPosY();
        double renderPosZ = renderManager.getRenderPosZ();

        double x = vec.xCoord - renderPosX;
        double y = vec.yCoord - renderPosY;
        double z = vec.zCoord - renderPosZ;

        AxisAlignedBB aabb = new AxisAlignedBB(
                x - 0.05,
                y - 0.05,
                z - 0.05,
                x + 0.05,
                y + 0.05,
                z + 0.05
        );

        drawFilledBoundingBox(aabb, color, opacity);
    }

    public static void drawFilledBoundingBox(AxisAlignedBB aabb, int color, float opacity) {
        GlStateManager.enableBlend();
        GlStateManager.disableDepth();
        GlStateManager.disableLighting();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        GlStateManager.disableTexture2D();
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldrenderer = tessellator.getWorldRenderer();

        float a = (color >> 24 & 0xFF) / 255.0F;
        float r = (color >> 16 & 0xFF) / 255.0F;
        float g = (color >> 8 & 0xFF) / 255.0F;
        float b = (color & 0xFF) / 255.0F;

        GlStateManager.color(r, g, b, a * opacity);
        worldrenderer.begin(7, DefaultVertexFormats.POSITION);
        worldrenderer.pos(aabb.minX, aabb.minY, aabb.minZ).endVertex();
        worldrenderer.pos(aabb.maxX, aabb.minY, aabb.minZ).endVertex();
        worldrenderer.pos(aabb.maxX, aabb.minY, aabb.maxZ).endVertex();
        worldrenderer.pos(aabb.minX, aabb.minY, aabb.maxZ).endVertex();
        tessellator.draw();
        worldrenderer.begin(7, DefaultVertexFormats.POSITION);
        worldrenderer.pos(aabb.minX, aabb.maxY, aabb.maxZ).endVertex();
        worldrenderer.pos(aabb.maxX, aabb.maxY, aabb.maxZ).endVertex();
        worldrenderer.pos(aabb.maxX, aabb.maxY, aabb.minZ).endVertex();
        worldrenderer.pos(aabb.minX, aabb.maxY, aabb.minZ).endVertex();
        tessellator.draw();
        GlStateManager.color(r, g, b, a * opacity);
        worldrenderer.begin(7, DefaultVertexFormats.POSITION);
        worldrenderer.pos(aabb.minX, aabb.minY, aabb.maxZ).endVertex();
        worldrenderer.pos(aabb.minX, aabb.maxY, aabb.maxZ).endVertex();
        worldrenderer.pos(aabb.minX, aabb.maxY, aabb.minZ).endVertex();
        worldrenderer.pos(aabb.minX, aabb.minY, aabb.minZ).endVertex();
        tessellator.draw();
        worldrenderer.begin(7, DefaultVertexFormats.POSITION);
        worldrenderer.pos(aabb.maxX, aabb.minY, aabb.minZ).endVertex();
        worldrenderer.pos(aabb.maxX, aabb.maxY, aabb.minZ).endVertex();
        worldrenderer.pos(aabb.maxX, aabb.maxY, aabb.maxZ).endVertex();
        worldrenderer.pos(aabb.maxX, aabb.minY, aabb.maxZ).endVertex();
        tessellator.draw();
        GlStateManager.color(r, g, b, a * opacity);
        worldrenderer.begin(7, DefaultVertexFormats.POSITION);
        worldrenderer.pos(aabb.minX, aabb.maxY, aabb.minZ).endVertex();
        worldrenderer.pos(aabb.maxX, aabb.maxY, aabb.minZ).endVertex();
        worldrenderer.pos(aabb.maxX, aabb.minY, aabb.minZ).endVertex();
        worldrenderer.pos(aabb.minX, aabb.minY, aabb.minZ).endVertex();
        tessellator.draw();
        worldrenderer.begin(7, DefaultVertexFormats.POSITION);
        worldrenderer.pos(aabb.minX, aabb.minY, aabb.maxZ).endVertex();
        worldrenderer.pos(aabb.maxX, aabb.minY, aabb.maxZ).endVertex();
        worldrenderer.pos(aabb.maxX, aabb.maxY, aabb.maxZ).endVertex();
        worldrenderer.pos(aabb.minX, aabb.maxY, aabb.maxZ).endVertex();
        tessellator.draw();
        GlStateManager.color(r, g, b, a);
        RenderGlobal.drawSelectionBoundingBox(aabb);
        GlStateManager.enableTexture2D();
        GlStateManager.enableDepth();
        GlStateManager.disableBlend();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
    }

}
