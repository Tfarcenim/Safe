package com.kamildanak.minecraft.safe.entity;

import com.kamildanak.minecraft.safe.block.BlockSafe;
import com.kamildanak.minecraft.safe.init.SafeSoundEvents;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryLargeChest;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.UUID;

public class TileEntitySafe extends TileEntityChest {
    public int cracks = 0;
    public long lastCrackDate;
    public int numPlayersUsing;
    public TextComponentString ownerName = new TextComponentString("");
    public UUID owner = UUID.randomUUID();
    private int ticksSinceSync;

    public TileEntitySafe() {

    }

    public void checkForAdjacentChests() {
        this.adjacentChestChecked = false;
    }

    public ITextComponent getDisplayName() {
        return ownerName;
    }


    public void update() {
        int i = this.pos.getX();
        int j = this.pos.getY();
        int k = this.pos.getZ();
        ++this.ticksSinceSync;

        if (!this.world.isRemote && this.numPlayersUsing != 0 && (this.ticksSinceSync + i + j + k) % 200 == 0) {
            this.numPlayersUsing = 0;
            float f = 5.0F;

            for (EntityPlayer entityplayer :
                    this.world.getEntitiesWithinAABB(EntityPlayer.class,
                            new AxisAlignedBB((double) ((float) i - 5.0F),
                                    (double) ((float) j - 5.0F), (double) ((float) k - 5.0F),
                                    (double) ((float) (i + 1) + 5.0F),
                                    (double) ((float) (j + 1) + 5.0F),
                                    (double) ((float) (k + 1) + 5.0F)))) {
                if (entityplayer.openContainer instanceof ContainerChest) {
                    IInventory iinventory = ((ContainerChest) entityplayer.openContainer).getLowerChestInventory();

                    if (iinventory == this || iinventory instanceof InventoryLargeChest &&
                            ((InventoryLargeChest) iinventory).isPartOfLargeChest(this)) {
                        ++this.numPlayersUsing;
                    }
                }
            }
        }

        this.prevLidAngle = this.lidAngle;
        float f1 = 0.1F;

        if (this.numPlayersUsing > 0 && this.lidAngle == 0.0F) {
            double d1 = (double) i + 0.5D;
            double d2 = (double) k + 0.5D;
            this.world.playSound(null, d1, (double) j + 0.5D, d2,
                    SafeSoundEvents.SAFE_OPEN, SoundCategory.BLOCKS, 0.5F, this.world.rand.nextFloat() * 0.1F + 0.9F);
        }

        if (this.numPlayersUsing == 0 && this.lidAngle > 0.0F || this.numPlayersUsing > 0 && this.lidAngle < 1.0F) {
            float f2 = this.lidAngle;

            if (this.numPlayersUsing > 0) {
                this.lidAngle += 0.1F;
            } else {
                this.lidAngle -= 0.1F;
            }

            if (this.lidAngle > 1.0F) {
                this.lidAngle = 1.0F;
            }

            float f3 = 0.5F;

            if (this.lidAngle < 0.5F && f2 >= 0.5F) {
                double d3 = (double) i + 0.5D;
                double d0 = (double) k + 0.5D;

                if (this.adjacentChestZPos != null) {
                    d0 += 0.5D;
                }

                if (this.adjacentChestXPos != null) {
                    d3 += 0.5D;
                }

                this.world.playSound(null, d3, (double) j + 0.5D, d0,
                        SafeSoundEvents.SAFE_CLOSE,
                        SoundCategory.BLOCKS, 0.5F,
                        this.world.rand.nextFloat() * 0.1F + 0.9F);
            }

            if (this.lidAngle < 0.0F) {
                this.lidAngle = 0.0F;
            }
        }
    }

    public void openInventory(EntityPlayer player) {
        if (!player.isSpectator() && this.getBlockType() instanceof BlockSafe) {
            if (this.numPlayersUsing < 0) {
                this.numPlayersUsing = 0;
            }

            ++this.numPlayersUsing;
            this.world.addBlockEvent(this.pos, this.getBlockType(), 1, this.numPlayersUsing);
            this.world.notifyNeighborsOfStateChange(this.pos, this.getBlockType(), false);
            IBlockState blockState = world.getBlockState(pos);
            world.notifyBlockUpdate(pos, blockState, blockState, 2);
        }
    }

    public void closeInventory(EntityPlayer player) {
        if (!player.isSpectator() && this.getBlockType() instanceof BlockSafe) {
            --this.numPlayersUsing;
            this.world.addBlockEvent(this.pos, this.getBlockType(), 1, this.numPlayersUsing);
            this.world.notifyNeighborsOfStateChange(this.pos, this.getBlockType(), false);
            IBlockState blockState = world.getBlockState(pos);
            world.notifyBlockUpdate(pos, blockState, blockState, 2);
        }
    }

    @Override
    public void readFromNBT(NBTTagCompound nbttagcompound) {
        super.readFromNBT(nbttagcompound);
        if (nbttagcompound.hasKey("owner"))
            ownerName = new TextComponentString(nbttagcompound.getString("owner"));
        if (nbttagcompound.hasKey("cracks"))
            cracks = nbttagcompound.getInteger("cracks");
        if (nbttagcompound.hasKey("lastCrack"))
            lastCrackDate = nbttagcompound.getLong("lastCrack");
        if (nbttagcompound.hasKey("numPlayersUsing"))
            numPlayersUsing = nbttagcompound.getInteger("numPlayersUsing");
    }

    @Override
    @Nonnull
    public NBTTagCompound writeToNBT(@Nonnull NBTTagCompound nbttagcompound) {
        nbttagcompound.setString("owner", ownerName.getText());
        nbttagcompound.setInteger("cracks", cracks);
        nbttagcompound.setLong("lastCrack", lastCrackDate);
        nbttagcompound.setInteger("numPlayersUsing", numPlayersUsing);
        return super.writeToNBT(nbttagcompound);
    }

    @Nonnull
    public NBTTagCompound getUpdateTag() {
        NBTTagCompound updateTag = super.getUpdateTag();
        updateTag.removeTag("lastCrack");
        writeToNBT(updateTag);
        return updateTag;
    }

    @Override
    @Nullable
    public SPacketUpdateTileEntity getUpdatePacket() {
        NBTTagCompound var1 = new NBTTagCompound();
        var1 = writeToNBT(var1);
        var1.removeTag("Items");
        var1.removeTag("lastCrack");
        var1.removeTag("Lock");
        return new SPacketUpdateTileEntity(pos, 1, var1);
    }

    @Override
    public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {
        readFromNBT(pkt.getNbtCompound());
    }

    public void setOwner(EntityPlayer owner) {
        this.owner = owner.getUniqueID();
        this.ownerName = new TextComponentString(owner.getName());
    }
}


