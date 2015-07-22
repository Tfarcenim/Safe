package info.jbcs.minecraft.safe;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry;
import info.jbcs.minecraft.safe.block.BlockSafe;
import info.jbcs.minecraft.safe.entity.EntityFallingSafe;
import info.jbcs.minecraft.safe.gui.GuiHandler;
import info.jbcs.minecraft.safe.gui.GuiSafe;
import info.jbcs.minecraft.safe.inventory.ContainerSafe;
import info.jbcs.minecraft.safe.item.ItemSafe;
import info.jbcs.minecraft.safe.network.MessagePipeline;
import info.jbcs.minecraft.safe.proxy.CommonProxy;
import info.jbcs.minecraft.safe.tileentity.TileEntitySafe;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.config.Configuration;

import java.io.File;


@Mod(modid = "Safe", name = "Safe", version = "1.3.1") //dependencies = "required-after:Autoutils"
public class Safe {
	public static BlockSafe blockSafe;
	public static GuiHandler guiSafe;

	static Configuration config;
	public MessagePipeline messagePipeline;
	
	@Instance("Safe")
	public static Safe instance;

	@SidedProxy(clientSide = "info.jbcs.minecraft.safe.proxy.ClientProxy", serverSide = "info.jbcs.minecraft.safe.proxy.CommonProxy")
	public static CommonProxy commonProxy;
	public static int	crackDelay;
	public static int	crackCount;
	public static int	crackChance;

	public Safe(){
		messagePipeline = new MessagePipeline();
	}

	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		File configFile=event.getSuggestedConfigurationFile();
		config = new Configuration(configFile);
		config.load();

		commonProxy.preInit();
 	}

	@EventHandler
	public void init(FMLInitializationEvent event) {
		commonProxy.registerPackets(messagePipeline);
		crackDelay=config.get("general", "crack delay", 86400, "The amount of seconds that must pass before safe block can get another crack").getInt();
		crackCount=config.get("general", "crack count", 6, "The amount of cracks that will cause the safe to break.").getInt();
		crackChance=config.get("general", "crack chance", 100, "Chance, in percent, that a safe will receive a crack from an explosion").getInt();

		
		blockSafe = (BlockSafe) new BlockSafe().setCreativeTab(CreativeTabs.tabDecorations);
		GameRegistry.registerBlock(blockSafe, ItemSafe.class, "safe");
		commonProxy.registerCraftingRecipes();


		guiSafe=new GuiHandler("safe"){
			@Override
			public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
		        TileEntity tileEntity = world.getTileEntity(x, y, z);

		        if(! (tileEntity instanceof TileEntitySafe))
		        	return null;
		        
		        TileEntitySafe e=(TileEntitySafe) tileEntity;
		        
		        return new ContainerSafe(player.inventory, e);
			}

			@Override
			public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
                TileEntity tileEntity = world.getTileEntity(x, y, z);

		        if(! (tileEntity instanceof TileEntitySafe))
		        	return null;
		        
		        TileEntitySafe e=(TileEntitySafe) tileEntity;
		        
                return new GuiSafe(player.inventory, e);
			}
		};
		
        GameRegistry.registerTileEntity(TileEntitySafe.class, "containerSafe");
		EntityRegistry.registerModEntity(EntityFallingSafe.class, "FallingSafe", 1, this, 40, 9999, false);
		
		GuiHandler.register(this);

		commonProxy.init();
				
        config.save();
	}

	@EventHandler
	public void postInit(FMLPostInitializationEvent event) {
	}
}
