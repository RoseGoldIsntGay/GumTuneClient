package rosegold.gumtuneclient.utils;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.*;
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

    public static void renderEntityModel(Entity entity, Vec3 vec, float partialTicks, float opacity) {
        RenderManagerAccessor rm = (RenderManagerAccessor) mc.getRenderManager();

        double x = vec.xCoord - rm.getRenderPosX();
        double y = vec.yCoord - rm.getRenderPosY();
        double z = vec.zCoord - rm.getRenderPosZ();

        float yaw = entity.prevRotationYaw + (entity.rotationYaw - entity.prevRotationYaw) * partialTicks;

        int i = entity.getBrightnessForRender(partialTicks);
        if (entity.isBurning()) {
            i = 0xF000F0;
        }
        int j = i % 65536;
        int k = i / 65536;
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, (float) j, (float) k);

        GlStateManager.color(1, 1, 1, 1 * opacity);

        mc.getRenderManager().doRenderEntity(entity, x, y, z, yaw, partialTicks, false);
    }

    public static void renderTracer(double posX, double posY, double posZ, double height, Color color, float partialTicks) {
        Entity render = mc.getRenderViewEntity();
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldRenderer = tessellator.getWorldRenderer();

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
        GL11.glLineWidth(2f);
        GlStateManager.disableDepth();
        GlStateManager.depthMask(false);
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
        worldRenderer.begin(3, DefaultVertexFormats.POSITION_COLOR);

        worldRenderer.pos(realX, realY + render.getEyeHeight(), realZ).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
        worldRenderer.pos(posX, posY, posZ).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();

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

    public static void renderTracer(Entity entity, Color color, float partialTicks) {
        renderTracer(entity.posX, entity.posY, entity.posZ, entity.height, color, partialTicks);
    }

    public static void renderTracer(BlockPos blockPos, Color color, float partialTicks) {
        renderTracer(blockPos.getX() + 0.5, blockPos.getY() + 0.5, blockPos.getZ() + 0.5, 0.5, color, partialTicks);
    }

    public static void renderBeacon(BlockPos blockPos, Color color, float partialTicks) {
        renderBeacon(blockPos.getX(), blockPos.getY(), blockPos.getZ(), color, partialTicks);
    }

    public static void renderBeacon(Vec3 location, Color color, float partialTicks) {
        renderBeacon(location.xCoord, location.yCoord, location.zCoord, color, partialTicks);
    }

    public static void renderBeacon(double x, double y, double z, Color color, float partialTicks) {
        int height = 300;
        int bOffset = 0;
        int tOffset = bOffset + height;

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
        double o = 0.5 + Math.cos(d2 + 2.356194490192345) * 0.2;
        double p = 0.5 + Math.sin(d2 + 2.356194490192345) * 0.2;
        double q = 0.5 + Math.cos(d2 + (Math.PI / 4)) * 0.2;
        double r = 0.5 + Math.sin(d2 + (Math.PI / 4)) * 0.2;
        double s = 0.5 + Math.cos(d2 + 3.9269908169872414) * 0.2;
        double t = 0.5 + Math.sin(d2 + 3.9269908169872414) * 0.2;
        double u = 0.5 + Math.cos(d2 + 5.497787143782138) * 0.2;
        double v = 0.5 + Math.sin(d2 + 5.497787143782138) * 0.2;
        double d14 = -1 + d1;
        double d15 = height * 2.5 + d14;

        float red = color.getRed() / 256f;
        float green = color.getGreen() / 256f;
        float blue = color.getBlue() / 256f;

        worldrenderer.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
        worldrenderer.pos(x + o, y + tOffset, z + p).tex(1.0D, d15).color(red, green, blue, 1.0f).endVertex();
        worldrenderer.pos(x + o, y + bOffset, z + p).tex(1.0D, d14).color(red, green, blue, 1.0f).endVertex();
        worldrenderer.pos(x + q, y + bOffset, z + r).tex(0.0D, d14).color(red, green, blue, 1.0f).endVertex();
        worldrenderer.pos(x + q, y + tOffset, z + r).tex(0.0D, d15).color(red, green, blue, 1.0f).endVertex();
        worldrenderer.pos(x + u, y + tOffset, z + v).tex(1.0D, d15).color(red, green, blue, 1.0f).endVertex();
        worldrenderer.pos(x + u, y + bOffset, z + v).tex(1.0D, d14).color(red, green, blue, 1.0f).endVertex();
        worldrenderer.pos(x + s, y + bOffset, z + t).tex(0.0D, d14).color(red, green, blue, 1.0f).endVertex();
        worldrenderer.pos(x + s, y + tOffset, z + t).tex(0.0D, d15).color(red, green, blue, 1.0f).endVertex();
        worldrenderer.pos(x + q, y + tOffset, z + r).tex(1.0D, d15).color(red, green, blue, 1.0f).endVertex();
        worldrenderer.pos(x + q, y + bOffset, z + r).tex(1.0D, d14).color(red, green, blue, 1.0f).endVertex();
        worldrenderer.pos(x + u, y + bOffset, z + v).tex(0.0D, d14).color(red, green, blue, 1.0f).endVertex();
        worldrenderer.pos(x + u, y + tOffset, z + v).tex(0.0D, d15).color(red, green, blue, 1.0f).endVertex();
        worldrenderer.pos(x + s, y + tOffset, z + t).tex(1.0D, d15).color(red, green, blue, 1.0f).endVertex();
        worldrenderer.pos(x + s, y + bOffset, z + t).tex(1.0D, d14).color(red, green, blue, 1.0f).endVertex();
        worldrenderer.pos(x + o, y + bOffset, z + p).tex(0.0D, d14).color(red, green, blue, 1.0f).endVertex();
        worldrenderer.pos(x + o, y + tOffset, z + p).tex(0.0D, d15).color(red, green, blue, 1.0f).endVertex();
        tessellator.draw();

        GlStateManager.disableCull();
        double d12 = -1.0D + d1;
        double d13 = height + d12;

        worldrenderer.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
        worldrenderer.pos(x + 0.2D, y + tOffset, z + 0.2D).tex(1.0D, d13).color(red, green, blue, 0.125f).endVertex();
        worldrenderer.pos(x + 0.2D, y + bOffset, z + 0.2D).tex(1.0D, d12).color(red, green, blue, 0.125f).endVertex();
        worldrenderer.pos(x + 0.8D, y + bOffset, z + 0.2D).tex(0.0D, d12).color(red, green, blue, 0.125f).endVertex();
        worldrenderer.pos(x + 0.8D, y + tOffset, z + 0.2D).tex(0.0D, d13).color(red, green, blue, 0.125f).endVertex();
        worldrenderer.pos(x + 0.8D, y + tOffset, z + 0.8D).tex(1.0D, d13).color(red, green, blue, 0.125f).endVertex();
        worldrenderer.pos(x + 0.8D, y + bOffset, z + 0.8D).tex(1.0D, d12).color(red, green, blue, 0.125f).endVertex();
        worldrenderer.pos(x + 0.2D, y + bOffset, z + 0.8D).tex(0.0D, d12).color(red, green, blue, 0.125f).endVertex();
        worldrenderer.pos(x + 0.2D, y + tOffset, z + 0.8D).tex(0.0D, d13).color(red, green, blue, 0.125f).endVertex();
        worldrenderer.pos(x + 0.8D, y + tOffset, z + 0.2D).tex(1.0D, d13).color(red, green, blue, 0.125f).endVertex();
        worldrenderer.pos(x + 0.8D, y + bOffset, z + 0.2D).tex(1.0D, d12).color(red, green, blue, 0.125f).endVertex();
        worldrenderer.pos(x + 0.8D, y + bOffset, z + 0.8D).tex(0.0D, d12).color(red, green, blue, 0.125f).endVertex();
        worldrenderer.pos(x + 0.8D, y + tOffset, z + 0.8D).tex(0.0D, d13).color(red, green, blue, 0.125f).endVertex();
        worldrenderer.pos(x + 0.2D, y + tOffset, z + 0.8D).tex(1.0D, d13).color(red, green, blue, 0.125f).endVertex();
        worldrenderer.pos(x + 0.2D, y + bOffset, z + 0.8D).tex(1.0D, d12).color(red, green, blue, 0.125f).endVertex();
        worldrenderer.pos(x + 0.2D, y + bOffset, z + 0.2D).tex(0.0D, d12).color(red, green, blue, 0.125f).endVertex();
        worldrenderer.pos(x + 0.2D, y + tOffset, z + 0.2D).tex(0.0D, d13).color(red, green, blue, 0.125f).endVertex();
        tessellator.draw();

        GlStateManager.disableLighting();
        GlStateManager.enableTexture2D();

        GlStateManager.enableDepth();

        GlStateManager.popMatrix();
    }

    public static void renderWaypointText(String str, BlockPos blockPos, float partialTicks) {
        renderWaypointText(str, blockPos.getX() + 0.5, blockPos.getY() + 0.5, blockPos.getZ() + 0.5, partialTicks, true);
    }

    public static void renderWaypointText(String str, Vec3 vec3, float partialTicks) {
        renderWaypointText(str, vec3.xCoord, vec3.yCoord, vec3.zCoord, partialTicks, true);
    }

    public static void renderWaypointText(String str, double X, double Y, double Z, float partialTicks) {
        renderWaypointText(str, X, Y, Z, partialTicks, true);
    }

    public static void renderWaypointText(String str, double X, double Y, double Z, float partialTicks, boolean showDist) {
        GlStateManager.alphaFunc(516, 0.1F);

        GlStateManager.pushMatrix();

        Entity viewer = GumTuneClient.mc.getRenderViewEntity();
        RenderManagerAccessor rm = (RenderManagerAccessor) mc.getRenderManager();

        double x = X - rm.getRenderPosX();
        double y = Y - rm.getRenderPosY();
        double z = Z - rm.getRenderPosZ();

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
            drawNametag("§e" + Math.round(dist * 10) / 10 + " blocks");
        }

        GlStateManager.popMatrix();

        GlStateManager.disableLighting();
    }

    public static void drawNametag(String str) {
        FontRenderer fontrenderer = mc.fontRendererObj;
        float f1 = 0.0266666688f;
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
        renderBoundingBox(entity, partialTicks, color, 0.5f, new Vec3(0, 0, 0));
    }

    public static void renderBoundingBox(Entity entity, float partialTicks, int color, Vec3 offset) {
        renderBoundingBox(entity, partialTicks, color, 0.5f, offset);
    }

    public static void renderBoundingBox(Entity entity, float partialTicks, int color, float opacity, Vec3 offset) {
        RenderManagerAccessor rm = (RenderManagerAccessor) mc.getRenderManager();

        double renderPosX = rm.getRenderPosX();
        double renderPosY = rm.getRenderPosY();
        double renderPosZ = rm.getRenderPosZ();

        double x = entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * partialTicks - renderPosX;
        double y = entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * partialTicks - renderPosY;
        double z = entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * partialTicks - renderPosZ;

        AxisAlignedBB bbox = entity.getEntityBoundingBox();
        if (bbox.maxX - bbox.minX == 0 && bbox.maxY - bbox.minY == 0 && bbox.maxZ - bbox.minZ == 0) {
            bbox = new AxisAlignedBB(entity.posX - 0.3, entity.posY, entity.posZ - 0.3, entity.posX + 0.3, entity.posY + 1.62, entity.posZ + 0.3);
        }
        AxisAlignedBB aabb = new AxisAlignedBB(
                bbox.minX - entity.posX + x + offset.xCoord,
                bbox.minY - entity.posY + y + offset.yCoord,
                bbox.minZ - entity.posZ + z + offset.zCoord,
                bbox.maxX - entity.posX + x + offset.xCoord,
                bbox.maxY - entity.posY + y + offset.yCoord,
                bbox.maxZ - entity.posZ + z + + offset.zCoord
        );

        drawFilledBoundingBox(aabb, color, opacity);
    }

    public static void renderBoundingBox(AxisAlignedBB axisAlignedBB, int color, float opacity) {
        RenderManagerAccessor rm = (RenderManagerAccessor) mc.getRenderManager();

        double renderPosX = rm.getRenderPosX();
        double renderPosY = rm.getRenderPosY();
        double renderPosZ = rm.getRenderPosZ();

        AxisAlignedBB aabb = new AxisAlignedBB(
                axisAlignedBB.minX - renderPosX,
                axisAlignedBB.minY - renderPosY,
                axisAlignedBB.minZ - renderPosZ,
                axisAlignedBB.maxX - renderPosX,
                axisAlignedBB.maxY - renderPosY,
                axisAlignedBB.maxZ - renderPosZ
        );

        drawFilledBoundingBox(aabb, color, opacity);
    }

    public static void drawLine(Vec3 from, Vec3 to, final float thickness, final float partialTicks) {
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

        final int i = ColorUtils.getChroma(1000, 0);
        bufferBuilder.pos(from.xCoord, from.yCoord, from.zCoord).color((i >> 16 & 0xFF) / 255.0f, (i >> 8 & 0xFF) / 255.0f, (i & 0xFF) / 255.0f, (i >> 24 & 0xFF) / 255.0f).endVertex();
        bufferBuilder.pos(to.xCoord, to.yCoord, to.zCoord).color((i >> 16 & 0xFF) / 255.0f, (i >> 8 & 0xFF) / 255.0f, (i & 0xFF) / 255.0f, (i >> 24 & 0xFF) / 255.0f).endVertex();

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

    public static void renderAxisSquare(BlockPos blockPos, EnumFacing enumFacing, float partialTicks, int color) {
        renderAxisSquare(blockPos, enumFacing, partialTicks, color, 0.5f);
    }

    public static void renderAxisSquare(BlockPos blockPos, EnumFacing enumFacing, float partialTicks, int color, float opacity) {
        if (blockPos != null) {
            IBlockState blockState = mc.theWorld.getBlockState(blockPos);

            if (blockState != null) {
                Block block = blockState.getBlock();
                block.setBlockBoundsBasedOnState(mc.theWorld, blockPos);
                double d0 = mc.thePlayer.lastTickPosX + (mc.thePlayer.posX - mc.thePlayer.lastTickPosX) * (double) partialTicks;
                double d1 = mc.thePlayer.lastTickPosY + (mc.thePlayer.posY - mc.thePlayer.lastTickPosY) * (double) partialTicks;
                double d2 = mc.thePlayer.lastTickPosZ + (mc.thePlayer.posZ - mc.thePlayer.lastTickPosZ) * (double) partialTicks;
                drawFilledAxisSquare(block.getSelectedBoundingBox(mc.theWorld, blockPos).expand(0.002D, 0.002D, 0.002D).offset(-d0, -d1, -d2), enumFacing, color, opacity);
            }
        }
    }

    public static void drawFilledAxisSquare(AxisAlignedBB aabb, EnumFacing enumFacing, int color, float opacity) {
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

        switch (enumFacing) {
            case UP:
                worldrenderer.begin(7, DefaultVertexFormats.POSITION);
                worldrenderer.pos(aabb.minX, aabb.maxY, aabb.maxZ).endVertex();
                worldrenderer.pos(aabb.maxX, aabb.maxY, aabb.maxZ).endVertex();
                worldrenderer.pos(aabb.maxX, aabb.maxY, aabb.minZ).endVertex();
                worldrenderer.pos(aabb.minX, aabb.maxY, aabb.minZ).endVertex();
                tessellator.draw();
                break;
            case DOWN:
                worldrenderer.begin(7, DefaultVertexFormats.POSITION);
                worldrenderer.pos(aabb.minX, aabb.minY, aabb.maxZ).endVertex();
                worldrenderer.pos(aabb.maxX, aabb.minY, aabb.maxZ).endVertex();
                worldrenderer.pos(aabb.maxX, aabb.minY, aabb.minZ).endVertex();
                worldrenderer.pos(aabb.minX, aabb.minY, aabb.minZ).endVertex();
                tessellator.draw();
                break;
            case EAST:
                worldrenderer.begin(7, DefaultVertexFormats.POSITION);
                worldrenderer.pos(aabb.maxX, aabb.maxY, aabb.maxZ).endVertex();
                worldrenderer.pos(aabb.maxX, aabb.minY, aabb.maxZ).endVertex();
                worldrenderer.pos(aabb.maxX, aabb.minY, aabb.minZ).endVertex();
                worldrenderer.pos(aabb.maxX, aabb.maxY, aabb.minZ).endVertex();
                tessellator.draw();
                break;
            case WEST:
                worldrenderer.begin(7, DefaultVertexFormats.POSITION);
                worldrenderer.pos(aabb.minX, aabb.minY, aabb.maxZ).endVertex();
                worldrenderer.pos(aabb.minX, aabb.maxY, aabb.maxZ).endVertex();
                worldrenderer.pos(aabb.minX, aabb.maxY, aabb.minZ).endVertex();
                worldrenderer.pos(aabb.minX, aabb.minY, aabb.minZ).endVertex();
                tessellator.draw();
                break;
            case NORTH:
                worldrenderer.begin(7, DefaultVertexFormats.POSITION);
                worldrenderer.pos(aabb.minX, aabb.maxY, aabb.minZ).endVertex();
                worldrenderer.pos(aabb.maxX, aabb.maxY, aabb.minZ).endVertex();
                worldrenderer.pos(aabb.maxX, aabb.minY, aabb.minZ).endVertex();
                worldrenderer.pos(aabb.minX, aabb.minY, aabb.minZ).endVertex();
                tessellator.draw();
                break;
            case SOUTH:
                worldrenderer.begin(7, DefaultVertexFormats.POSITION);
                worldrenderer.pos(aabb.minX, aabb.minY, aabb.maxZ).endVertex();
                worldrenderer.pos(aabb.maxX, aabb.minY, aabb.maxZ).endVertex();
                worldrenderer.pos(aabb.maxX, aabb.maxY, aabb.maxZ).endVertex();
                worldrenderer.pos(aabb.minX, aabb.maxY, aabb.maxZ).endVertex();
                tessellator.draw();
                break;
        }

        GlStateManager.color(r, g, b, a);
//        RenderGlobal.drawSelectionBoundingBox(aabb);
        GlStateManager.enableTexture2D();
        GlStateManager.enableDepth();
        GlStateManager.disableBlend();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
    }
}
