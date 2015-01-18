package info.jbcs.minecraft.safe;

import info.jbcs.minecraft.utilities.General;
import io.netty.buffer.ByteBuf;
import net.minecraft.block.Block;
import net.minecraft.entity.item.EntityFallingBlock;
import net.minecraft.util.MathHelper;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;

import cpw.mods.fml.common.registry.IEntityAdditionalSpawnData;

public class EntityFallingSafe extends EntityFallingBlock implements IEntityAdditionalSpawnData{
    int startY=-1;
    int hitY=-1;
    double force=0;
    boolean alreadyDead=false;
	
	public EntityFallingSafe(World par1World) {
		super(par1World);
		
		setIsAnvil(true);
	}

	public EntityFallingSafe(World par1World, double x, double y, double z) {
		this(par1World, x, y, z, Safe.blockSafe.blockID, 0);
	}

    public EntityFallingSafe(World world, double x, double y, double z, int blockId){
        this(world, x, y, z, blockId, 0);
    }

    public EntityFallingSafe(World world, double x, double y, double z, int blockId, int meta){
        super(world,x,y,z,blockId,meta);
		
		setIsAnvil(true);
    }

	@Override
	public void writeSpawnData(ByteArrayDataOutput data) {
		data.writeShort(blockID);
		data.writeByte(metadata);	
	}

	@Override
	public void readSpawnData(ByteArrayDataInput data) {
		blockID=data.readShort();
		metadata=data.readByte();
	}

    /**
     * Called to update the entity's position/logic.
     */
    @Override
	public void onUpdate(){
        if (blockID == 0){
            setDead();
            return;
        }
        
        if(startY<0){
            startY=(int)posY;
            hitY=(int)posY;
        }
        
		prevPosX = posX;
		prevPosY = posY;
		prevPosZ = posZ;
		++fallTime;
		motionY -= 0.08D;
		force=motionY;	
		if(! onGround) moveEntity(motionX, motionY, motionZ);
		motionY=force;
		motionX *= 0.9800000190734863D;
		motionY *= 0.9800000190734863D;
		motionZ *= 0.9800000190734863D;
		
		if(posY<0){
			setDead();
			return;
		}
		
		final int x = MathHelper.floor_double(posX);
		int y = MathHelper.floor_double(posY);
		final int z = MathHelper.floor_double(posZ);

		if (fallTime == 1) {
			if (!worldObj.isRemote && worldObj.getBlockId(x, y, z) != blockID) {
				setDead();
				return;
			}

			worldObj.setBlockToAir(x, y, z);
		}

		if (onGround || !BlockSafe.canFallBelow(worldObj, x, y - 1, z)) {
			if(tryBreakingBlockAt(x,y-1,z))
				return;
			
			super.setDead();
			
			if(alreadyDead){
				y--;
			}
			
			worldObj.setBlock(x, y, z, blockID, metadata, 3);
			Block block=General.getBlock(blockID);
			if(block instanceof BlockSafe){
				((BlockSafe)block).finishFall(worldObj, x, y, z, metadata, fallingBlockTileEntityData,hitY-(int)posY);
			}
			
			fall(fallDistance);
			
			/*
			final int finaly=y;
			
			if(worldObj.isRemote){
				Packets.safeCheck.sendToServer(new PacketData(){
					@Override
					public void data(DataOutputStream stream) throws IOException {
						stream.writeInt(x);
						stream.writeInt(z);
						stream.writeInt(startY);
						stream.writeInt(finaly-3);
					}
				});
			}*/
		}
    }
    
    public double getBlockResitance(World world,int x,int y,int z){
		Block block=General.getBlock(worldObj.getBlockId(x, y, z));
		if(block==null) return 0;
		
		return block.getExplosionResistance(this, worldObj, x, y, z, posX, posY, posZ);
    }
    public double getBlockAddedResitance(World world,int x,int y,int z){
		double res=getBlockResitance(worldObj,x,y,z);
		
		if(res>1.0) return 1.0;
		return res;
    }

	public boolean tryBreakingBlockAt(int x,int y,int z) {
		boolean breakArea=false;
		
		double explosionForce=(force*force) * 6.0;
		Block block=General.getBlock(worldObj.getBlockId(x, y, z));
		if(block==null) return false;
		
		Explosion explosion=new Explosion(worldObj,this,x,y,z,(float)explosionForce);
		double resitance=getBlockResitance(worldObj, x, y, z);
		
		if(resitance<1.0f){
			resitance+=getBlockAddedResitance(worldObj, x+1, y, z);
			resitance+=getBlockAddedResitance(worldObj, x-1, y, z);
			resitance+=getBlockAddedResitance(worldObj, x, y, z+1);
			resitance+=getBlockAddedResitance(worldObj, x, y, z-1);
		}
		
		if(resitance>explosionForce)
			return false;
		
        if (! block.canDropFromExplosion(explosion))
        	return false;
        
    	BlockSafe.fallSound(worldObj, x, y, z, (int) (hitY-posY));
        
        block.dropBlockAsItemWithChance(this.worldObj, x, y, z, this.worldObj.getBlockMetadata(x, y, z), 1.0F / explosion.explosionSize, 0);
        worldObj.setBlock(x, y, z, 0, 0, 1);
        block.onBlockDestroyedByExplosion(worldObj, x, y, z, explosion);
                
        force=motionY=force*(explosionForce-resitance*0.5)/explosionForce;
        onGround=false;
        
        startY=(int) posY;
        
		return true;
	}

    @Override
	public void setDead(){
/*    	if(! isDead){
    		int x = MathHelper.floor_double(posX);
    		int y = MathHelper.floor_double(posY);
    		int z = MathHelper.floor_double(posZ);

        	BlockSafe.fallSound(worldObj, x, y, z, (int) (startY-posY));
    		worldObj.playAuxSFX(2001, x, y, z, worldObj.getBlockId(x, y, z) + (worldObj.getBlockMetadata(x, y, z) << 12));
    	}*/
    	
    	if(! worldObj.isRemote)
    		super.setDead();
    	else
    		alreadyDead=true;
    }

	@Override
	public void writeSpawnData(ByteBuf byteBuf) {

	}

	@Override
	public void readSpawnData(ByteBuf byteBuf) {

	}
}
