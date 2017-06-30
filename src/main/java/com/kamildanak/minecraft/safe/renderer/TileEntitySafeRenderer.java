package com.kamildanak.minecraft.safe.renderer;

import com.kamildanak.minecraft.safe.entity.TileEntitySafe;
import com.kamildanak.minecraft.safe.model.ModelSafe;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

@SideOnly(Side.CLIENT)
public class TileEntitySafeRenderer extends TileEntitySpecialRenderer<TileEntitySafe> {
    public static final ResourceLocation textures[] = {
            new ResourceLocation("safe:textures/entity/safe/metal.png"),
            new ResourceLocation("safe:textures/entity/safe/metal-crack-1.png"),
            new ResourceLocation("safe:textures/entity/safe/metal-crack-2.png"),
            new ResourceLocation("safe:textures/entity/safe/metal-crack-3.png"),
            new ResourceLocation("safe:textures/entity/safe/metal-crack-4.png"),
            new ResourceLocation("safe:textures/entity/safe/metal-crack-5.png"),
    };
    public static ModelSafe model = new ModelSafe();

    public static void bind(ResourceLocation par1ResourceLocation) {
        TextureManager texturemanager = Minecraft.getMinecraft().renderEngine;

        if (texturemanager != null) {
            texturemanager.bindTexture(par1ResourceLocation);
        }
    }

    public static void render(int kind, double x, double y, double z,
                              float partialTicks, int meta, float prevAngle, float angle) {
        bind(textures[kind >= textures.length ? textures.length - 1 : kind]);

        GL11.glPushMatrix();
        GL11.glEnable(GL12.GL_RESCALE_NORMAL);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        GL11.glTranslatef((float) x, (float) y + 1.0F, (float) z + 1.0F);
        GL11.glScalef(1.0F, -1.0F, -1.0F);
        GL11.glTranslatef(0.5F, 0.5F, 0.5F);
        short short1 = 0;

        if (meta == 2) {
            short1 = 180;
        }

        if (meta == 3) {
            short1 = 0;
        }

        if (meta == 4) {
            short1 = 90;
        }

        if (meta == 5) {
            short1 = -90;
        }

        GL11.glRotatef(short1, 0.0F, 1.0F, 0.0F);
        GL11.glRotatef(-90, 0.0f, 0.0f, 1.0f);
        GL11.glRotatef(90, 1.0f, 0.0f, 0.0f);
        GL11.glTranslatef(-0.5F, -0.5F, -0.5F);
        float f1 = prevAngle + (angle - prevAngle) * partialTicks;
        float f2;

        f1 = 1.0F - f1;
        f1 = 1.0F - f1 * f1 * f1;
        model.chestLid.rotateAngleX = -(f1 * (float) Math.PI / 2.0F);
        model.renderAll();
        GL11.glDisable(GL12.GL_RESCALE_NORMAL);
        GL11.glPopMatrix();
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
    }

    public void render(TileEntitySafe te, double x, double y, double z,
                       float partialTicks, int destroyStage, float alpha) {
        if (te == null) {
            render(0, x, y, z, partialTicks, 0, 0, 0);
            return;
        }
        super.render(te, x, y, z, partialTicks, destroyStage, alpha);
        if (!te.hasWorld())
            return;

        Block block = te.getBlockType();
        int meta = te.getBlockMetadata();

        render(te.cracks, x, y, z, partialTicks, meta, te.prevLidAngle, te.lidAngle);
    }
}
