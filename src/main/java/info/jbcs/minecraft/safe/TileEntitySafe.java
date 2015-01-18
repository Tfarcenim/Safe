package info.jbcs.minecraft.safe;

import info.jbcs.minecraft.utilities.InventoryStatic;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.Packet132TileEntityData;
import net.minecraft.tileentity.TileEntity;

public class TileEntitySafe extends TileEntity implements IInventory, ISidedInventory {
	public String ownerName = "";
	public int userCount;
	public long lastCrackDate;
	public int cracks;

	public float lidAngle;
    public float prevLidAngle;
	public boolean turnedToEntity;
    
    static float lidSpeed = 0.1F;

	private static final int[] side0 = new int[] { };

	InventoryStatic inventory = new InventoryStatic(36) {
		@Override
		public String getInvName() {
			return "Safe";
		}
		
		@Override
		public boolean isUseableByPlayer(EntityPlayer entityplayer) {
			if (worldObj.getTileEntity(xCoord, yCoord, zCoord) != TileEntitySafe.this) {
				return false;
			} else {
				return entityplayer.getDistanceSq(xCoord + 0.5D, yCoord + 0.5D, zCoord + 0.5D) <= 64D;
			}
		}
	};
	
    @Override
	public void updateEntity(){
        super.updateEntity();

        prevLidAngle = lidAngle;
        double d0;

        if (userCount > 0 && lidAngle == 0.0F){
            worldObj.playSoundEffect(xCoord + 0.5D, yCoord + 0.5D, zCoord + 0.5D, "safe:safe-open", 0.5F, worldObj.rand.nextFloat() * 0.1F + 0.9F);
        }

        if (userCount == 0 && lidAngle > 0.0F || userCount > 0 && lidAngle < 1.0F)
        {
            float startingLidAngle = lidAngle;

            if (userCount > 0){
                lidAngle += lidSpeed;
            } else {
                lidAngle -= lidSpeed;
            }

            if (lidAngle > 1.0F) {
                lidAngle = 1.0F;
            } else if(lidAngle < 0.2F && startingLidAngle >= 0.2F){
                worldObj.playSoundEffect(xCoord + 0.5D, yCoord + 0.5D, zCoord + 0.5D, "safe:safe-close", 0.5F, worldObj.rand.nextFloat() * 0.1F + 0.9F);
            } else if(lidAngle < 0.0F){
                lidAngle = 0.0F;
            }
        }
    }


	@Override
	public int getSizeInventory() {
		return inventory.getSizeInventory();
	}

	@Override
	public ItemStack getStackInSlot(int i) {
		return inventory.getStackInSlot(i);
	}

	@Override
	public ItemStack decrStackSize(int i, int j) {
		return inventory.decrStackSize(i, j);
	}

	@Override
	public void setInventorySlotContents(int i, ItemStack itemstack) {
		inventory.setInventorySlotContents(i, itemstack);
	}

	@Override
	public String getInvName() {
		return inventory.getInvName();
	}

	@Override
	public int getInventoryStackLimit() {
		return inventory.getInventoryStackLimit();
	}

	@Override
	public boolean isUseableByPlayer(EntityPlayer entityplayer) {
		return inventory.isUseableByPlayer(entityplayer);
	}

	@Override
	public void readFromNBT(NBTTagCompound nbttagcompound) {
		super.readFromNBT(nbttagcompound);
		
		if(nbttagcompound.hasKey("items")){
			inventory.clear();
			inventory.readFromNBT(nbttagcompound);
		}
		
		ownerName = nbttagcompound.getString("owner");
		userCount = nbttagcompound.getInteger("usersCount");
		cracks = nbttagcompound.getInteger("cracks");
		lastCrackDate=nbttagcompound.getLong("lastcrack");
	}

	@Override
	public void writeToNBT(NBTTagCompound nbttagcompound) {
		super.writeToNBT(nbttagcompound);
		inventory.writeToNBT(nbttagcompound);
		nbttagcompound.setString("owner", ownerName);
		nbttagcompound.setInteger("usersCount", userCount);
		nbttagcompound.setInteger("cracks", cracks);
		nbttagcompound.setLong("lastcrack", lastCrackDate);
	}

	@Override
	public Packet getDescriptionPacket() {
		NBTTagCompound var1 = new NBTTagCompound();
		writeToNBT(var1);
		var1.removeTag("items");
		var1.removeTag("lastcrack");
		return new Packet132TileEntityData(xCoord, yCoord, zCoord, 1, var1);
	}

	@Override
	public void onDataPacket(INetworkManager net, Packet132TileEntityData pkt) {
		readFromNBT(pkt.data);
	}

	@Override
	public ItemStack getStackInSlotOnClosing(int var1) {
		return null;
	}

	@Override
	public void openChest() {
		++userCount;
		worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
	}

	@Override
	public void closeChest() {
		--userCount;
		worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
	}

	@Override
	public boolean isInvNameLocalized() {
		return false;
	}

	@Override
	public boolean isItemValidForSlot(int i, ItemStack itemstack) {
		return true;
	}

	@Override
	public boolean canInsertItem(int index, ItemStack stack, int par3) {
		return isItemValidForSlot(index, stack);
	}

	@Override
	public boolean canExtractItem(int index, ItemStack stack, int side) {
		return false;
	}

	@Override
	public int[] getAccessibleSlotsFromSide(int side) {
		return side0;
	}
}


