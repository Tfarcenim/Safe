package info.jbcs.minecraft.safe;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.EffectRenderer;
import net.minecraft.client.particle.EntityDiggingFX;
import net.minecraft.world.World;

public class GeneralSafeClient {
	public static Random rand = new Random();

	public static EntityDiggingFX addBlockHitEffects(World world, int x, int y, int z, int side) {
		int i1 = world.getBlockId(x, y, z);
		if (i1 == 0)
			return null;

		EffectRenderer renderer = Minecraft.getMinecraft().effectRenderer;

		Block block = Block.blocksList[i1];
		float f = 0.1F;
		double d0 = x + rand.nextDouble() * (block.getBlockBoundsMaxX() - block.getBlockBoundsMinX() - f * 2.0F) + f + block.getBlockBoundsMinX();
		double d1 = y + rand.nextDouble() * (block.getBlockBoundsMaxY() - block.getBlockBoundsMinY() - f * 2.0F) + f + block.getBlockBoundsMinY();
		double d2 = z + rand.nextDouble() * (block.getBlockBoundsMaxZ() - block.getBlockBoundsMinZ() - f * 2.0F) + f + block.getBlockBoundsMinZ();

		switch (side) {
		case 0:
			d1 = y + block.getBlockBoundsMinY() - f;
			break;
		case 1:
			d1 = y + block.getBlockBoundsMaxY() + f;
			break;
		case 2:
			d2 = z + block.getBlockBoundsMinZ() - f;
			break;
		case 3:
			d2 = z + block.getBlockBoundsMaxZ() + f;
			break;
		case 4:
			d0 = x + block.getBlockBoundsMinX() - f;
			break;
		case 5:
			d0 = x + block.getBlockBoundsMaxX() + f;
			break;
		}

		EntityDiggingFX fx = new EntityDiggingFX(world, d0, d1, d2, 0.0D, 0.0D, 0.0D, block, world.getBlockMetadata(x, y, z), side);
		fx.motionX = d0 - (x + 0.5);
		fx.motionY = d1 - (y + 0.5);
		fx.motionZ = d2 - (z + 0.5);

		renderer.addEffect(fx);

		fx.multipleParticleScaleBy(0.25f + 0.5f * rand.nextFloat());
		fx.multiplyVelocity(0.2f * rand.nextFloat());
		
		
		return fx;
	}
	
	public static void spawnSafeFallEffect(World world, int x, int y, int z) {
		for (int j = 0; j < 16; j++) {
			if(world.getBlockId(x, y, z-1)==0){
				addBlockHitEffects(world, x, y, z, 2);
				addBlockHitEffects(world, x, y-1, z-1, 1);
			}
			if(world.getBlockId(x, y, z+1)==0){
				addBlockHitEffects(world, x, y, z, 3);
				addBlockHitEffects(world, x, y-1, z+1, 1);
			}
			if(world.getBlockId(x-1, y, z)==0){
				addBlockHitEffects(world, x, y, z, 4);
				addBlockHitEffects(world, x-1, y-1, z, 1);
			}
			if(world.getBlockId(x+1, y, z)==0){
				addBlockHitEffects(world, x, y, z, 5);
				addBlockHitEffects(world, x+1, y-1, z, 1);
			}
			
			if(world.getBlockId(x, y-1, z-1)==0)
				addBlockHitEffects(world, x, y-1, z, 2);
			if(world.getBlockId(x, y-1, z+1)==0)
				addBlockHitEffects(world, x, y-1, z, 3);
			if(world.getBlockId(x-1, y-1, z)==0)
				addBlockHitEffects(world, x, y-1, z, 4);
			if(world.getBlockId(x+1, y-1, z)==0)
				addBlockHitEffects(world, x, y-1, z, 5);
		}
	}
	
}
