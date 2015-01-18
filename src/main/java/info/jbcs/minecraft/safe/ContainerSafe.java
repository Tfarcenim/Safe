package info.jbcs.minecraft.safe;

import info.jbcs.minecraft.utilities.ContainerTileEntity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;

public class ContainerSafe extends ContainerTileEntity<TileEntitySafe> {
	public ContainerSafe(IInventory playerInv, TileEntitySafe machine) {
		super(playerInv, machine, 8, 103);
		
		entity.openChest();
		
		int index=0;

		for (int y = 0; y < 4; y++) {
			for (int x = 0; x < 9; x++) {
				addSlotToContainer(new Slot(machine, index++, 8 + x * 18, 18 + y * 18));
			}
		}
	}
	
    @Override
	public void onContainerClosed(EntityPlayer par1EntityPlayer){
        super.onContainerClosed(par1EntityPlayer);
        
        entity.closeChest();
    }

}
