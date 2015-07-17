package info.jbcs.minecraft.safe.renderer;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import info.jbcs.minecraft.safe.gui.OwnerHintGui;
import info.jbcs.minecraft.safe.model.ModelSafe;
import info.jbcs.minecraft.safe.tileentity.TileEntitySafe;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

@SideOnly(Side.CLIENT)
public class TileEntitySafeRenderer extends TileEntitySpecialRenderer {
	public static ModelSafe model	= new ModelSafe();

	public static final ResourceLocation	textures[] =  {
		new ResourceLocation("safe:textures/entity/safe/metal.png"),
		new ResourceLocation("safe:textures/entity/safe/metal-crack-1.png"),
		new ResourceLocation("safe:textures/entity/safe/metal-crack-2.png"),
		new ResourceLocation("safe:textures/entity/safe/metal-crack-3.png"),
		new ResourceLocation("safe:textures/entity/safe/metal-crack-4.png"),
		new ResourceLocation("safe:textures/entity/safe/metal-crack-5.png"),
	};

	public static void bind(ResourceLocation par1ResourceLocation) {
		TextureManager texturemanager = Minecraft.getMinecraft().renderEngine;

		if (texturemanager != null) {
			texturemanager.bindTexture(par1ResourceLocation);
		}
	}

	public static void render(int kind,double x, double y, double z, float frame, int meta, float prevAngle, float angle){
		bind(textures[kind>=textures.length?textures.length-1:kind]);

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
		GL11.glRotatef( 90, 1.0f, 0.0f, 0.0f);
		GL11.glTranslatef(-0.5F, -0.5F, -0.5F);
		float f1 = prevAngle + (angle - prevAngle) * frame;
		float f2;

		f1 = 1.0F - f1;
		f1 = 1.0F - f1 * f1 * f1;
		model.chestLid.rotateAngleX = -(f1 * (float) Math.PI / 2.0F);
		model.renderAll();
		GL11.glDisable(GL12.GL_RESCALE_NORMAL);
		GL11.glPopMatrix();
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
	}
	
	public void renderTileEntityChestAt(TileEntitySafe safe, double x, double y, double z, float frame) {
		if (!safe.hasWorldObj())
			return;
		
		Block block = safe.getBlockType();
		int meta = safe.getBlockMetadata();
		
		render(safe.cracks,x,y,z,frame,meta,safe.prevLidAngle,safe.lidAngle);
		if(
				OwnerHintGui.hoveringBlock &&
                OwnerHintGui.hoverX==safe.xCoord &&
                OwnerHintGui.hoverY==safe.yCoord &&
                OwnerHintGui.hoverZ==safe.zCoord &&
				safe.getWorldObj().getBlock(safe.xCoord, safe.yCoord+1, safe.zCoord)== Blocks.air
		){
			renderLivingLabel(safe.ownerName,x,y+1.0f,z);
		}
	}


    /**
     * Draws the debug or playername text above a living
     */
    protected void renderLivingLabel(String par2Str, double x, double y, double z){
    	RenderManager manager=RenderManager.instance;

		FontRenderer fontrenderer = Minecraft.getMinecraft().fontRenderer;
		float f = 1.6F;
		float f1 = 0.016666668F * f;
		GL11.glPushMatrix();
		GL11.glTranslatef((float) x + 0.5F, (float) y + 0.25F, (float) z + 0.5f);
		GL11.glNormal3f(0.0F, 1.0F, 0.0F);
		GL11.glRotatef(-manager.playerViewY, 0.0F, 1.0F, 0.0F);
		GL11.glRotatef(manager.playerViewX, 1.0F, 0.0F, 0.0F);
		GL11.glScalef(-f1, -f1, f1);
		GL11.glDisable(GL11.GL_LIGHTING);
		GL11.glDepthMask(false);
		GL11.glDisable(GL11.GL_DEPTH_TEST);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		Tessellator tessellator = Tessellator.instance;
		byte b0 = 0;

		GL11.glDisable(GL11.GL_TEXTURE_2D);
		tessellator.startDrawingQuads();
		int j = fontrenderer.getStringWidth(par2Str) / 2;
		tessellator.setColorRGBA_F(0.0F, 0.0F, 0.0F, 0.25F);
		tessellator.addVertex(-j - 1, -1 + b0, 0.0D);
		tessellator.addVertex(-j - 1, 8 + b0, 0.0D);
		tessellator.addVertex(j + 1, 8 + b0, 0.0D);
		tessellator.addVertex(j + 1, -1 + b0, 0.0D);
		tessellator.draw();
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		fontrenderer.drawString(par2Str, -fontrenderer.getStringWidth(par2Str) / 2, b0, 553648127);
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glDepthMask(true);
		fontrenderer.drawString(par2Str, -fontrenderer.getStringWidth(par2Str) / 2, b0, -1);
		GL11.glEnable(GL11.GL_LIGHTING);
		GL11.glDisable(GL11.GL_BLEND);
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		GL11.glPopMatrix();
    }

	
	@Override
	public void renderTileEntityAt(TileEntity par1TileEntity, double par2, double par4, double par6, float par8) {
		this.renderTileEntityChestAt((TileEntitySafe) par1TileEntity, par2, par4, par6, par8);
	}
}
