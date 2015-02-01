package info.jbcs.minecraft.safe;


import java.util.EnumSet;

import cpw.mods.fml.client.registry.ClientRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.client.registry.RenderingRegistry;

public class ProxyClient extends Proxy{
	private Minecraft mc;
	
	static boolean hoveringBlock;
	static int hoverX;
	static int hoverY;
	static int hoverZ;
	

	@Override
	public void preInit() {
	}

	@Override
	public void init() {
		mc = FMLClientHandler.instance().getClient();
		//TickRegistry.registerTickHandler(this, Side.CLIENT);

		ClientRegistry.bindTileEntitySpecialRenderer(TileEntitySafe.class, new TileEntitySafeRenderer());
		RenderingRegistry.registerBlockHandler(new BlockSafeRenderer(RenderingRegistry.getNextAvailableRenderId()));

		RenderingRegistry.registerEntityRenderingHandler(EntityFallingSafe.class, new RenderFallingSafe());
	}
/*
	@Override
	public void tickStart(EnumSet<TickType> type, Object... tickData) {
		hoveringBlock=false;
		
		if (mc == null || mc.thePlayer == null || mc.theWorld == null) {
			return;
		}

		EntityPlayer player = mc.thePlayer;
		World world = mc.theWorld;
		MovingObjectPosition mop = General.getMovingObjectPositionFromPlayer(world, player, false);

		if (mop == null) {
			return;
		}

		if (mop.typeOfHit != EnumMovingObjectType.TILE) {
			return;
		}
		
		hoverX=mop.blockX;
		hoverY=mop.blockY;
		hoverZ=mop.blockZ;
		hoveringBlock=true;
	}

	@Override
	public void tickEnd(EnumSet<TickType> type, Object... tickData) {
		
	}

	@Override
	public EnumSet<TickType> ticks() {
		return EnumSet.of(TickType.RENDER);
	}
*/

	public String getLabel() {
		return "Safe looking to display GUI";
	}
}
