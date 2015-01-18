package info.jbcs.minecraft.safe;

import info.jbcs.minecraft.utilities.General;

import java.util.ArrayList;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Icon;
import net.minecraft.util.MathHelper;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;

public class BlockSafe extends BlockContainer implements ITileEntityProvider {
	static boolean fallInstantly=false;

	Icon iconSide,iconTop;
	
	public BlockSafe(int id) {
		super(id,Material.glass);	

		setStepSound(soundMetalFootstep);
		
		setHardness(0.3F);
		setResistance(6000000.0F);
		setBlockUnbreakable();
		
        setBlockBounds(0.0625F, 0.0F, 0.0625F, 0.9375F, 0.875F, 0.9375F);
	}

	@Override
	public boolean onBlockActivated(World world, int i, int j, int k, EntityPlayer entityplayer, int a, float b, float x, float y) {
		TileEntitySafe tileEntity = (TileEntitySafe) world.getBlockTileEntity(i, j, k);
		if (tileEntity == null)
			return false;

		int ox=i,oy=j,oz=k;
		int meta=world.getBlockMetadata(i, j, k);
		switch(meta){
		case 2: oz--; break;
		case 3: oz++; break;
		case 4: ox--; break;
		case 5: ox++; break;
		}
		
		if(! world.isAirBlock(ox, oy, oz))
			return true;
		
		if (entityplayer.username.equals(tileEntity.ownerName) || tileEntity.userCount>0 || entityplayer.capabilities.isCreativeMode) {
			Safe.guiSafe.open(entityplayer, world, i, j, k);
			
			return true;
		}
		
        world.playSoundEffect(i + 0.5D, j + 0.5D, k + 0.5D, "safe:safe-locked", 0.5F, world.rand.nextFloat() * 0.1F + 0.9F);
		return true;
	}

	@Override
	public void onBlockPlacedBy(World world, int i, int j, int k, EntityLivingBase entityliving, ItemStack stack) {
		TileEntitySafe e = new TileEntitySafe();

		if (entityliving instanceof EntityPlayer) {
			EntityPlayer player = (EntityPlayer) entityliving;
			e.ownerName = player.username;
		}

		if(stack.stackTagCompound!=null)
			e.readFromNBT(stack.stackTagCompound);

		world.setBlockTileEntity(i, j, k, e);
		
		byte meta = 0;
		int facing = MathHelper.floor_double((entityliving.rotationYaw * 4.0F / 360.0F) + 0.5D) & 3;

		if (facing == 0) {
			meta = 2;
		} else if (facing == 1) {
			meta = 5;
		} else if (facing == 2) {
			meta = 3;
		} else if (facing == 3) {
			meta = 4;
		}

		world.setBlockMetadataWithNotify(i, j, k, meta, 2);
	}
	
	@Override
	public void onBlockClicked(World world, int i, int j, int k, EntityPlayer entityplayer) {
		if(world.isRemote) return;
		
		TileEntity tileEntity = world.getBlockTileEntity(i, j, k);
		if (tileEntity == null) return;
		TileEntitySafe safe=(TileEntitySafe) tileEntity;
		boolean empty=safe.inventory.isEmpty();
		boolean creative=entityplayer.capabilities.isCreativeMode;
				
		if(empty){
			destroy(world,i,j,k);
			return;
		}
		
		if(entityplayer.username!=safe.ownerName && !creative){
            world.playSoundEffect(i + 0.5D, j + 0.5D, k + 0.5D, "safe:safe-locked", 0.5F, world.rand.nextFloat() * 0.1F + 0.9F);
			return;
		}
		
		if(creative || empty)
			destroy(world,i,j,k);
	}
	
	public void destroy(World world, int i, int j, int k) {
		world.playAuxSFX(2001, i, j, k, blockID + (world.getBlockMetadata(i, j, k) << 12));
		dropBlockAsItem(world, i, j, k, world.getBlockMetadata(i, j, k), 0);
		world.setBlock(i, j, k, 0);
	}
	
	@Override
	public ArrayList<ItemStack> getBlockDropped(World world, int x, int y, int z, int metadata, int fortune) {
		ArrayList<ItemStack> ret = new ArrayList<ItemStack>();

		ret.add(new ItemStack(blockID, 1, 0));
		
		return ret;
	}



	@Override
	public int getRenderBlockPass() {
		return 0;
	}
	
    @Override
	public int getRenderType(){
        return BlockSafeRenderer.id;
    }

	@Override
	public TileEntity createNewTileEntity(World var1) {
		TileEntitySafe e=new TileEntitySafe();
		
		return e;
	}
	

	@Override
	public Icon getIcon(int side, int metadata) {
		return side<2?iconTop:iconSide;
	}

	@Override
	public void registerIcons(IconRegister register) {
		iconSide=register.registerIcon("safe:metal-side");
		iconTop=register.registerIcon("safe:metal-top");
	}

	@Override
	public boolean isOpaqueCube() {
		return false;
	}
	
	@Override
	public int tickRate(World par1World) {
		return 2;
	}

	@Override
	public void onBlockAdded(World par1World, int par2, int par3, int par4) {
		par1World.scheduleBlockUpdate(par2, par3, par4, this.blockID, this.tickRate(par1World));
	}

	@Override
	public void onNeighborBlockChange(World par1World, int par2, int par3, int par4, int par5) {
		par1World.scheduleBlockUpdate(par2, par3, par4, this.blockID, this.tickRate(par1World));
	}

	@Override
	public void updateTick(World world, int x, int y, int z, Random par5Random) {
		tryToFall(world, x, y, z);
	}

	private void tryToFall(World world, int x, int y, int z) {
		if (! canFallBelow(world, x, y - 1, z) && y >= 0) return;

		byte dist = 32;

		TileEntitySafe safe = (TileEntitySafe) world.getBlockTileEntity(x, y, z);
		if (safe == null) return;
		safe.userCount=0;
		
		int meta = world.getBlockMetadata(x, y, z);
		NBTTagCompound tag = new NBTTagCompound();
		safe.writeToNBT(tag);

		safe.turnedToEntity=true;
		
		if (!fallInstantly && world.checkChunksExist(x - dist, y - dist, z - dist, x + dist, y + dist, z + dist)) {			
			EntityFallingSafe entity = new EntityFallingSafe(world, x + 0.5F, y + 0.5F, z + 0.5F, blockID, meta);
			entity.fallingBlockTileEntityData = tag;
			world.spawnEntityInWorld(entity);
		} else {
			world.setBlockToAir(x, y, z);

			int startY=y;
			while (canFallBelow(world, x, y - 1, z) && y > 0) --y;
			if (y <= 0) return;

			finishFall(world,x,y,z,meta,tag,startY-y);
		}
	}

	public static boolean canFallBelow(World world, int x, int y, int z) {
		int l = world.getBlockId(x, y, z);

		if (world.isAirBlock(x, y, z)) {	
			return true;
		} else if (l == Block.fire.blockID) {
			return true;
		} else {
			Material material = Block.blocksList[l].blockMaterial;
			return material == Material.water ? true : material == Material.lava;
		}
	}
	
	static void fallSound(World world, int x, int y, int z,int distance){
 		Block block=General.getBlock(world.getBlockId(x, y-1, z));
		String sound=null;
		Material material;
		if(block!=null && block.blockMaterial!=null) material=block.blockMaterial;
		else material=Material.air;
		
		
		if(material==Material.air){
		} else if(material==Material.anvil || material==Material.glass || material==Material.ice || material==Material.iron){
			sound="safe:fall-metal";
		} else{
			sound="safe:fall-wood";
		}
		
		if(sound!=null)
			world.playSound(x + 0.5D, y , z + 0.5D, sound, 0.5f+0.1F*distance, world.rand.nextFloat() * 0.1F + 0.9F,false);
	}
	
	void finishFall(World world, int x, int y, int z, int meta, NBTTagCompound tag,int distance){
		if(world.isRemote){
			fallSound(world,x,y,z,distance);
			GeneralSafeClient.spawnSafeFallEffect(world,x,y,z);
			return;
		}
		
		world.setBlock(x, y, z, this.blockID, meta, 0);
		TileEntitySafe newSafe = (TileEntitySafe) world.getBlockTileEntity(x, y, z);
		if (newSafe == null) return;

		newSafe.readFromNBT(tag);
		newSafe.xCoord = x; newSafe.yCoord = y; newSafe.zCoord = z;
		world.markBlockForUpdate(x, y, z);
	}
	
    @Override
	public boolean canDropFromExplosion(Explosion par1Explosion){
        return false;
    }

    @Override
	public float getExplosionResistance(Entity par1Entity){
        return 0;
    }

    @Override
	public void onBlockExploded(World world, int x, int y, int z, Explosion explosion){
		crack(world,x,y,z);
    }
    
    void crack(World world, int x, int y, int z){
		TileEntitySafe safe = (TileEntitySafe) world.getBlockTileEntity(x, y, z);
		if (safe == null) return;
		
		if(world.rand.nextInt(100)>Safe.crackChance)
			return;			
		
		long timestamp=System.currentTimeMillis()/1000L;
		if(timestamp<=safe.lastCrackDate+Safe.crackDelay)
			/* crack is still on cooldown */
			return;

		safe.lastCrackDate=timestamp;
		safe.cracks++;
		
		if(safe.cracks>=Safe.crackCount){
			destroy(world,x,y,z);
		} else{
			world.markBlockForUpdate(x, y, z);
		}
	}
    

	@Override
	public void breakBlock(World world, int x, int y, int z, int id, int meta) {
		TileEntity tile = world.getBlockTileEntity(x, y, z);
		if(tile instanceof TileEntitySafe && !((TileEntitySafe) tile).turnedToEntity){
			((TileEntitySafe) tile).inventory.throwItems(world, x, y, z);
		}
		
		super.breakBlock(world, x, y, z, id, meta);
	}

}
