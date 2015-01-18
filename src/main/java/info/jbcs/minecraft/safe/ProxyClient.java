package info.jbcs.minecraft.safe;

import info.jbcs.minecraft.utilities.General;
import info.jbcs.minecraft.utilities.Sounds;

import java.util.EnumSet;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumMovingObjectType;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.ITickHandler;
import cpw.mods.fml.common.TickType;
import cpw.mods.fml.common.registry.TickRegistry;
import cpw.mods.fml.relauncher.Side;

public class ProxyClient extends Proxy  implements ITickHandler{
	private Minecraft mc;
	
	static boolean hoveringBlock;
	static int hoverX;
	static int hoverY;
	static int hoverZ;
	

	@Override
	public void preInit() {
		MinecraftForge.EVENT_BUS.register(new Sounds() {
			@Override
			public void addSounds() {
				addSound("safe:safe-open.ogg");
				addSound("safe:safe-close.ogg");
				addSound("safe:safe-locked.ogg");
				
				addSound("safe:fall-metal.ogg");
				addSound("safe:fall-wood.ogg");
			}
		});
	}

	@Override
	public void init() {
		mc = FMLClientHandler.instance().getClient();
		TickRegistry.registerTickHandler(this, Side.CLIENT);
		
		TileEntityRenderer.instance.specialRendererMap.put(TileEntitySafe.class, new TileEntitySafeRenderer());
		RenderingRegistry.registerBlockHandler(new BlockSafeRenderer(RenderingRegistry.getNextAvailableRenderId()));
		
		RenderingRegistry.registerEntityRenderingHandler(EntityFallingSafe.class, new RenderFallingSafe());

	}

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

	@Override
	public String getLabel() {
		return "Safe looking to display GUI";
	}
}
