package info.jbcs.minecraft.safe.proxy;


import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import info.jbcs.minecraft.safe.entity.EntityFallingSafe;
import info.jbcs.minecraft.safe.gui.OwnerHintGui;
import info.jbcs.minecraft.safe.renderer.BlockSafeRenderer;
import info.jbcs.minecraft.safe.renderer.RenderFallingSafe;
import info.jbcs.minecraft.safe.renderer.TileEntitySafeRenderer;
import info.jbcs.minecraft.safe.tileentity.TileEntitySafe;
import net.minecraft.client.Minecraft;
import net.minecraftforge.common.MinecraftForge;

public class ClientProxy extends CommonProxy {
	private Minecraft mc;
	
	@Override
	public void preInit() {
	}

	@Override
	public void init() {
		mc = FMLClientHandler.instance().getClient();

		ClientRegistry.bindTileEntitySpecialRenderer(TileEntitySafe.class, new TileEntitySafeRenderer());
		RenderingRegistry.registerBlockHandler(new BlockSafeRenderer(RenderingRegistry.getNextAvailableRenderId()));

		RenderingRegistry.registerEntityRenderingHandler(EntityFallingSafe.class, new RenderFallingSafe());

        MinecraftForge.EVENT_BUS.register(new OwnerHintGui(Minecraft.getMinecraft()));
	}
}
