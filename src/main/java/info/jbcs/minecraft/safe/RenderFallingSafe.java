package info.jbcs.minecraft.safe;

import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;

public class RenderFallingSafe extends Render {
	TileEntitySafeRenderer renderer=new TileEntitySafeRenderer();

    RenderBlocks sandRenderBlocks=new RenderBlocks();
    
	@Override
	public void doRender(Entity entity, double x, double y, double z, float a, float frame) {
		if(! (entity instanceof EntityFallingSafe)) return;
		EntityFallingSafe fallingSafe=(EntityFallingSafe) entity;

        int meta = fallingSafe.getMeta();
		int cracks = fallingSafe.cracks;
        TileEntitySafeRenderer.render(cracks,x-0.5, y-0.5, z-0.5, frame, meta, 0, 0);
	}

	@Override
	protected ResourceLocation getEntityTexture(Entity entity) {
		return TileEntitySafeRenderer.textures[0];
	}
	
}