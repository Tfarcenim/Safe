package com.kamildanak.minecraft.safe.renderer;

import com.kamildanak.minecraft.safe.entity.EntityFallingSafe;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;

public class RenderFallingSafe extends Render {
	TileEntitySafeRenderer renderer=new TileEntitySafeRenderer();

	public RenderFallingSafe(RenderManager renderManager) {
		super(renderManager);
	}

	@Override
	public void doRender(Entity entity, double x, double y, double z, float a, float frame) {
		if(! (entity instanceof EntityFallingSafe)) return;
		EntityFallingSafe fallingSafe=(EntityFallingSafe) entity;

		int meta = fallingSafe.getMeta();
		int cracks = fallingSafe.cracks;
		TileEntitySafeRenderer.render(cracks, x - 0.5, y, z - 0.5, frame, meta, 0, 0);
	}

	@Override
	protected ResourceLocation getEntityTexture(Entity entity) {
		return TileEntitySafeRenderer.textures[0];
	}
	
}