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

        MinecraftForge.EVENT_BUS.register(new OwnerHintGui(Minecraft.getMinecraft()));
	}
}
