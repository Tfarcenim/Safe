package info.jbcs.minecraft.safe.proxy;

import cpw.mods.fml.relauncher.Side;
import info.jbcs.minecraft.safe.Safe;
import info.jbcs.minecraft.safe.network.MessagePipeline;
import info.jbcs.minecraft.safe.network.MsgLand;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;

public class CommonProxy {
	public void preInit() {
	}
	public void init() {
	}
	public void registerPackets(MessagePipeline pipeline)
	{
		pipeline.registerMessage(MsgLand.Handler.class, MsgLand.class, 0, Side.SERVER);
	}
	public void registerCraftingRecipes(){
		CraftingManager.getInstance().addRecipe(new ItemStack(Safe.blockSafe,1),
				"XYX", "Y Y", "XYX",
				'X', Blocks.iron_block,
				'Y', Items.iron_ingot);
	}

}
