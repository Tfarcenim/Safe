package info.jbcs.minecraft.safe;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.world.IBlockAccess;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;

public class BlockSafeRenderer implements ISimpleBlockRenderingHandler {
	static int id;

	public BlockSafeRenderer() {

	}

	@Override
	public void renderInventoryBlock(Block block, int meta, int modelID, RenderBlocks renderer) {
        GL11.glScaled(16.0/14.0, 16.0/14.0, 16.0/14.0);
        GL11.glTranslatef(-0.5F, -0.5F+1.0f/16, -0.5F);
        TileEntitySafeRenderer.render(0, 0, 0, 0, 0, meta, 0, 0);
	}

	@Override
	public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block block, int modelId, RenderBlocks renderer) {
		return false;
	}

	@Override
	public boolean shouldRender3DInInventory(int i) {
		return true;
	}

	@Override
	public int getRenderId() {
		return id;
	}
}
