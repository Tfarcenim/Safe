package com.kamildanak.minecraft.safe.block;

import com.kamildanak.minecraft.safe.Safe;
import com.kamildanak.minecraft.safe.entity.EntityFallingBlockExtended;
import com.kamildanak.minecraft.safe.entity.EntityFallingSafe;
import com.kamildanak.minecraft.safe.entity.TileEntitySafe;
import com.kamildanak.minecraft.safe.init.SafeSoundEvents;
import com.kamildanak.minecraft.safe.stats.ModStats;
import net.minecraft.block.BlockHorizontal;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.util.*;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.ILockableContainer;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;

public class BlockSafe extends BlockContainerFalling {
    public static final PropertyDirection FACING = BlockHorizontal.FACING;
    protected static final AxisAlignedBB BOUNDS =
            new AxisAlignedBB(0.0625F, 0.0F, 0.0625F, 0.9375F, 0.875F, 0.9375F);
    static boolean fallInstantly = false;

    public BlockSafe(String name, Material material) {
        super(material);
        setRegistryName(name);
        setUnlocalizedName(name);
        setSoundType(SoundType.METAL);

        this.setDefaultState(this.blockState.getBaseState()); //.withProperty(FACING, EnumFacing.NORTH)
        setHardness(0.3F);
        setResistance(6000000.0F);
        setBlockUnbreakable();

        setCreativeTab(CreativeTabs.DECORATIONS);
    }

    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }

    public boolean isFullCube(IBlockState state) {
        return false;
    }

    @SideOnly(Side.CLIENT)
    public boolean hasCustomBreakingProgress(IBlockState state) {
        return true;
    }

    public EnumBlockRenderType getRenderType(IBlockState state) {
        return EnumBlockRenderType.ENTITYBLOCK_ANIMATED;
    }

    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
        return BOUNDS;
    }

    public IBlockState getStateForPlacement(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
        return this.getDefaultState().withProperty(FACING, placer.getHorizontalFacing());
    }

    public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer,
                                ItemStack stack) {
        EnumFacing enumfacing = EnumFacing.getHorizontal(
                MathHelper.floor((double) (placer.rotationYaw * 4.0F / 360.0F) + 0.5D) & 3).getOpposite();
        state = state.withProperty(FACING, enumfacing);
        worldIn.setBlockState(pos, state, 3);

        TileEntity tileentity = worldIn.getTileEntity(pos);
        if (placer instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) placer;
            if (tileentity instanceof TileEntitySafe) {
                ((TileEntitySafe) tileentity).setOwner(player);
            }
        }
    }

    public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
        TileEntity tileentity = worldIn.getTileEntity(pos);

        if (tileentity instanceof IInventory) {
            InventoryHelper.dropInventoryItems(worldIn, pos, (IInventory) tileentity);
            worldIn.updateComparatorOutputLevel(pos, this);
        }

        super.breakBlock(worldIn, pos, state);
    }

    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn,
                                    EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        if (worldIn.isRemote) {
            return true;
        } else {
            ILockableContainer ilockablecontainer = this.getLockableContainer(worldIn, pos);

            if (ilockablecontainer != null) {
                TileEntity tileentity = worldIn.getTileEntity(pos);
                if (!(tileentity instanceof TileEntitySafe)) return true;
                TileEntitySafe tileEntitySafe = (TileEntitySafe) tileentity;
                if (playerIn.getUniqueID().equals(tileEntitySafe.owner) || tileEntitySafe.numPlayersUsing > 0 ||
                        playerIn.capabilities.isCreativeMode) {
                    if (!playerIn.capabilities.isCreativeMode && !playerIn.getUniqueID().equals(tileEntitySafe.owner)) {
                        playerIn.addStat(ModStats.SAFE_PEEKED_INTO_OPENED_SAFE);
                    } else {
                        playerIn.addStat(ModStats.SAFE_OPENED);
                    }
                    playerIn.displayGUIChest(ilockablecontainer);
                    return true;
                }
                worldIn.playSound(null, pos, SafeSoundEvents.SAFE_LOCKED, SoundCategory.BLOCKS,
                        0.5F, 0.5F);
                playerIn.addStat(ModStats.SAFE_FAILED_UNLOCK);
                return true;

            }

            return true;
        }
    }

    @Nullable
    public ILockableContainer getLockableContainer(World worldIn, BlockPos pos) {
        return this.getContainer(worldIn, pos, false);
    }

    @Nullable
    public ILockableContainer getContainer(World worldIn, BlockPos pos, boolean allowBlocking) {
        TileEntity tileentity = worldIn.getTileEntity(pos);

        if (!(tileentity instanceof TileEntityChest)) {
            return null;
        } else {
            ILockableContainer ilockablecontainer = (TileEntityChest) tileentity;

            if (!allowBlocking && this.isBlocked(worldIn, pos)) {
                return null;
            }
            return ilockablecontainer;
        }
    }

    private boolean isBlocked(World worldIn, BlockPos pos) {
        IBlockState blockSafeState = worldIn.getBlockState(pos);
        return !worldIn.getBlockState(pos.offset(blockSafeState.getValue(FACING))).getMaterial().isReplaceable();
    }

    public boolean hasComparatorInputOverride(IBlockState state) {
        return true;
    }

    public int getComparatorInputOverride(IBlockState blockState, World worldIn, BlockPos pos) {
        return Container.calcRedstoneFromInventory(this.getLockableContainer(worldIn, pos));
    }

    public IBlockState getStateFromMeta(int meta) {
        EnumFacing enumfacing = EnumFacing.getFront(meta);

        if (enumfacing.getAxis() == EnumFacing.Axis.Y) {
            enumfacing = EnumFacing.NORTH;
        }

        return this.getDefaultState().withProperty(FACING, enumfacing);
    }

    public int getMetaFromState(IBlockState state) {
        return state.getValue(FACING).getIndex();
    }

    public IBlockState withRotation(IBlockState state, Rotation rot) {
        return state.withProperty(FACING, rot.rotate(state.getValue(FACING)));
    }

    public IBlockState withMirror(IBlockState state, Mirror mirrorIn) {
        return state.withRotation(mirrorIn.toRotation(state.getValue(FACING)));
    }

    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, FACING);
    }

    public BlockFaceShape getBlockFaceShape(IBlockAccess p_193383_1_, IBlockState p_193383_2_, BlockPos p_193383_3_, EnumFacing p_193383_4_) {
        return BlockFaceShape.UNDEFINED;
    }

    @Override
    public void onBlockClicked(World worldIn, BlockPos pos, EntityPlayer playerIn) {
        if (worldIn.isRemote) return;

        TileEntity tileEntity = worldIn.getTileEntity(pos);
        if (tileEntity == null) return;
        TileEntitySafe safe = (TileEntitySafe) tileEntity;
        boolean empty = safe.isEmpty();
        boolean creative = playerIn.capabilities.isCreativeMode;

        if (empty) {
            destroy(worldIn, pos);
            return;
        }

        if (playerIn.getUniqueID() != safe.owner && !creative) {
            worldIn.playSound(null, pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D,
                    SafeSoundEvents.SAFE_LOCKED, SoundCategory.BLOCKS, 0.5F,
                    worldIn.rand.nextFloat() * 0.1F + 0.9F);
            return;
        }

        if (creative || empty)
            destroy(worldIn, pos);
    }

    @Override
    public void getDrops(NonNullList<ItemStack> drops, IBlockAccess world, BlockPos pos, IBlockState state, int fortune) {
        drops.add(new ItemStack(this, 1, 0));
    }

    @Override
    public void onBeforeFall(World worldIn, BlockPos pos) {
        TileEntity tileEntity = worldIn.getTileEntity(pos);
        if (tileEntity == null || !(tileEntity instanceof TileEntitySafe)) return;
        ((TileEntitySafe) tileEntity).numPlayersUsing = 0;
        if (tileEntity instanceof IInventory) {
            ((IInventory) tileEntity).clear();
            worldIn.updateComparatorOutputLevel(pos, this);
        }
    }

    @Override

    public void onEndFalling(World world, BlockPos start, BlockPos finish, IBlockState fallTile,
                             IBlockState iblockstate, EntityFallingBlockExtended entityFalling) {
        int distance = Math.abs(finish.getY() - start.getY());
        if (entityFalling instanceof EntityFallingSafe) {
            ((EntityFallingSafe) entityFalling).fallSound(world, finish, distance);
        }
        EntityFallingSafe.createFallParticles(world, finish, distance);
    }

    @Override
    public float getExplosionResistance(World world, BlockPos pos, @Nullable Entity exploder, Explosion explosion) {
        return 0;
    }

    @Override
    public boolean canDropFromExplosion(Explosion par1Explosion) {
        return false;
    }

    public void crack(World world, BlockPos pos, EntityLivingBase explosivePlacedBy) {
        TileEntitySafe safe = (TileEntitySafe) world.getTileEntity(pos);
        if (safe == null) return;

        if (world.rand.nextInt(100) > Safe.crackChance)
            return;

        long timestamp = System.currentTimeMillis() / 1000L;
        if (timestamp <= safe.lastCrackDate + Safe.crackDelay)
            // crack is still on cooldown
            return;

        safe.lastCrackDate = timestamp;
        safe.cracks++;
        if (explosivePlacedBy instanceof EntityPlayer) {
            EntityPlayer playerIn = (EntityPlayer) explosivePlacedBy;
            playerIn.addStat(ModStats.CRACKED_SAFE);
        }

        if (safe.cracks >= Safe.crackCount) {
            destroy(world, pos);
        } else {
            IBlockState blockState = world.getBlockState(pos);
            world.notifyBlockUpdate(pos, blockState, blockState, 2);
        }
    }

    public void destroy(World world, BlockPos pos) {
        dropBlockAsItem(world, pos, world.getBlockState(pos), 0);
        world.setBlockToAir(pos);
    }

    @Override
    public TileEntity createNewTileEntity(World world, int i) {
        return new TileEntitySafe();
    }
}
