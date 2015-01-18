package info.jbcs.minecraft.safe;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

public class ItemSafe extends ItemBlock {

	public ItemSafe(int id) {
		super(id);
	}
	

    @Override
	public void addInformation(ItemStack stack, EntityPlayer player, List lines, boolean advancedTooltips) {
    	if(stack.stackTagCompound==null) return;
    	
    	String name=stack.stackTagCompound.getString("owner");
    	if(name==null || name.isEmpty()) return;

    	lines.add("Owned by "+(name.equals(player.username)?"you":name));
    }

}
