package info.jbcs.minecraft.safe;


import info.jbcs.minecraft.utilities.GuiHandler;

import java.io.File;

import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.Configuration;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.Init;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.Mod.PostInit;
import cpw.mods.fml.common.Mod.PreInit;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkMod;
import cpw.mods.fml.common.registry.EntityRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.LanguageRegistry;


@Mod(modid = "Safe", name = "Safe", version = "1.2.0", dependencies = "required-after:Autoutils")
@NetworkMod(clientSideRequired = true, serverSideRequired = true)
public class Safe {
	public static BlockSafe blockSafe;
	public static GuiHandler guiSafe;

	static Configuration config;
	
	@Instance("Safe")
	public static Safe instance;

	@SidedProxy(clientSide = "info.jbcs.minecraft.safe.ProxyClient", serverSide = "info.jbcs.minecraft.safe.Proxy")
	public static Proxy proxy;
	public static int	crackDelay;
	public static int	crackCount;
	public static int	crackChance;

	@PreInit
	public void preInit(FMLPreInitializationEvent event) {
		File configFile=event.getSuggestedConfigurationFile();
		config = new Configuration(configFile);
		config.load();
		
		proxy.preInit();
 	}
	
	int getBlock(String name,int id){
		return config.getBlock(name, id).getInt(id);
	}
	
	@Init
	public void init(FMLInitializationEvent event) {
		crackDelay=config.get("general", "crack delay", 86400, "The amount of seconds that must pass before safe block can get another crack").getInt();
		crackCount=config.get("general", "crack count", 6, "The amount of cracks that will cause the safe to break.").getInt();
		crackChance=config.get("general", "crack chance", 100, "Chance, in percent, that a safe will receive a crack from an explosion").getInt();

		
		blockSafe = (BlockSafe) new BlockSafe(getBlock("safe",2760)).setUnlocalizedName("safe").setCreativeTab(CreativeTabs.tabDecorations);
		LanguageRegistry.addName(blockSafe, "Safe");
		GameRegistry.registerBlock(blockSafe, ItemSafe.class, "safe");

		CraftingManager.getInstance().addRecipe(new ItemStack(blockSafe,1),
				new Object[] { "XYX", "Y Y", "XYX",
				'X', Block.blockIron,
				'Y', Item.ingotIron,
			});

		
		guiSafe=new GuiHandler("safe"){
			@Override
			public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
		        TileEntity tileEntity = world.getBlockTileEntity(x, y, z);

		        if(! (tileEntity instanceof TileEntitySafe))
		        	return null;
		        
		        TileEntitySafe e=(TileEntitySafe) tileEntity;
		        
		        return new ContainerSafe(player.inventory, e);
			}

			@Override
			public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
                TileEntity tileEntity = world.getBlockTileEntity(x, y, z);

		        if(! (tileEntity instanceof TileEntitySafe))
		        	return null;
		        
		        TileEntitySafe e=(TileEntitySafe) tileEntity;
		        
                return new GuiSafe(player.inventory, e);
			}
		};
		
        GameRegistry.registerTileEntity(TileEntitySafe.class, "containerSafe");
		EntityRegistry.registerModEntity(EntityFallingSafe.class, "FallingSafe", 1, this, 40, 9999, false);
		
		GuiHandler.register(this);
        
		proxy.init();
				
        config.save();
	}

	@PostInit
	public void postInit(FMLPostInitializationEvent event) {
	}
}
