package com.kamildanak.minecraft.safe.entity;

import com.google.common.collect.Lists;
import com.kamildanak.minecraft.safe.block.BlockContainerFalling;
import com.kamildanak.minecraft.safe.block.BlockSafe;
import io.netty.buffer.ByteBuf;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.item.EntityFallingBlock;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData;

import java.util.List;

public abstract class EntityFallingBlockExtended extends EntityFallingBlock implements IEntityAdditionalSpawnData {
    public BlockPos start;
    public double force;
    private Block block;
    private int meta;
    private IBlockState fallTile;
    private boolean hurtEntities;
    private float fallHurtAmount = 2.0F;
    private int fallHurtMax = 40;

    public EntityFallingBlockExtended(World worldIn, double x, double y, double z, IBlockState fallingBlockState,
                                      TileEntity tileEntity, BlockPos start) {
        super(worldIn, x, y, z, fallingBlockState);
        fallTile = fallingBlockState;
        block = fallTile.getBlock();
        meta = block.getMetaFromState(fallingBlockState);
        tileEntityData = tileEntity.writeToNBT(new NBTTagCompound());
        shouldDropItem = false;
        hurtEntities = true;
        this.start = start;
        force = 0;
    }

    public EntityFallingBlockExtended(World world) {
        super(world);
    }

    @Override
    public void writeSpawnData(ByteBuf buffer) {
        buffer.writeInt(Block.getIdFromBlock(super.getBlock().getBlock()));
        buffer.writeInt(super.getBlock().getValue(BlockSafe.FACING).getIndex());
        buffer.writeInt(start.getX());
        buffer.writeInt(start.getY());
        buffer.writeInt(start.getZ());
        buffer.writeDouble(force);
    }

    @Override
    public void readSpawnData(ByteBuf additionalData) {
        block = Block.getBlockById(additionalData.readInt());
        meta = additionalData.readInt();
        fallTile = block.getStateFromMeta(meta);
        start = new BlockPos(additionalData.readInt(), additionalData.readInt(), additionalData.readInt());
        force = additionalData.readDouble();
    }

    @Override
    protected void writeEntityToNBT(NBTTagCompound compound) {
        compound.setInteger("extBlock", Block.getIdFromBlock(super.getBlock().getBlock()));
        compound.setInteger("extMeta", super.getBlock().getValue(BlockSafe.FACING).getIndex());
        compound.setInteger("startX", start.getX());
        compound.setInteger("startY", start.getY());
        compound.setInteger("startZ", start.getZ());
        compound.setDouble("force", force);
    }

    @Override
    protected void readEntityFromNBT(NBTTagCompound compound) {
        block = Block.getBlockById(compound.getInteger("extBlock"));
        meta = compound.getInteger("extMeta");
        fallTile = block.getStateFromMeta(meta);
        start = new BlockPos(compound.getInteger("startX"), compound.getInteger("startY"),
                compound.getInteger("startZ"));
        force = compound.getDouble("force");
    }

    public int getMeta() {
        return meta;
    }

    public void onUpdate() {
        if (this.fallTile == null) {
            this.setDead();
            return;
        }
        Block block = this.fallTile.getBlock();

        if (this.fallTile.getMaterial() == Material.AIR) {
            this.setDead();
            return;
        }

        this.prevPosX = this.posX;
        this.prevPosY = this.posY;
        this.prevPosZ = this.posZ;

        if (this.fallTime++ == 0) {
            BlockPos blockpos = new BlockPos(this);

            if (this.world.getBlockState(blockpos).getBlock() == block) {
                this.world.setBlockToAir(blockpos);
            } else if (!this.world.isRemote) {
                this.setDead();
                return;
            }
        }

        if (!this.hasNoGravity()) {
            this.motionY -= 0.08D;
        }

        this.move(MoverType.SELF, this.motionX, this.motionY, this.motionZ);

        if (!this.world.isRemote) {
            BlockPos blockpos1 = new BlockPos(this.posX, Math.ceil(this.posY), this.posZ);
            boolean flag = this.fallTile.getBlock() == Blocks.CONCRETE_POWDER;
            boolean flag1 = flag && this.world.getBlockState(blockpos1).getMaterial() == Material.WATER;
            double d0 = this.motionX * this.motionX + this.motionY * this.motionY + this.motionZ * this.motionZ;

            if (flag && d0 > 1.0D) {
                RayTraceResult raytraceresult =
                        this.world.rayTraceBlocks(new Vec3d(this.prevPosX, this.prevPosY, this.prevPosZ),
                                new Vec3d(this.posX, this.posY, this.posZ), true);

                if (raytraceresult != null && this.world.getBlockState(raytraceresult.getBlockPos()).getMaterial() == Material.WATER) {
                    blockpos1 = raytraceresult.getBlockPos();
                    flag1 = true;
                }
            }

            if (!this.onGround && !flag1) {
                force = motionY;
                if (this.fallTime > 100 && !this.world.isRemote && (blockpos1.getY() < 1 || blockpos1.getY() > 256) || this.fallTime > 600) {
                    if (this.shouldDropItem && this.world.getGameRules().getBoolean("doEntityDrops")) {
                        this.entityDropItem(new ItemStack(block, 1, block.damageDropped(this.fallTile)), 0.0F);
                    }

                    this.setDead();
                }
            } else {
                IBlockState iblockstate = this.world.getBlockState(blockpos1);

                if (this.world.isAirBlock(new BlockPos(this.posX, this.posY - 0.009999999776482582D, this.posZ))) //Forge: Don't indent below.
                    if (!flag1 && BlockContainerFalling.canFallThrough(this.world.getBlockState(new BlockPos(this.posX, this.posY - 0.009999999776482582D, this.posZ)))) {
                        this.onGround = false;
                        return;
                    }

                if (tryBreakingBlockAt(new BlockPos(this.posX, this.posY - 0.009999999776482582D, this.posZ))) {
                    this.onGround = false;
                    return;
                }

                this.motionX *= 0.699999988079071D;
                this.motionZ *= 0.699999988079071D;

                if (iblockstate.getBlock() != Blocks.PISTON_EXTENSION) {
                    this.setDead();

                    this.world.getBlockState(blockpos1).getBlock()
                            .dropBlockAsItem(world, blockpos1, this.world.getBlockState(blockpos1), 0);
                    IBlockState aaa = this.world.getBlockState(blockpos1);
                    IBlockState aaa2 = this.world.getBlockState(blockpos1);
                    if (this.world.setBlockState(blockpos1, this.fallTile, 3)) {
                        if (block instanceof BlockContainerFalling) {
                            ((BlockContainerFalling) block).onEndFalling(this.world, start, blockpos1,
                                    this.fallTile, iblockstate, this);
                        }

                        if (this.tileEntityData != null && block.hasTileEntity(this.fallTile)) {
                            TileEntity tileentity = this.world.getTileEntity(blockpos1);

                            if (tileentity != null) {
                                NBTTagCompound nbttagcompound = tileentity.writeToNBT(new NBTTagCompound());

                                for (String s : this.tileEntityData.getKeySet()) {
                                    NBTBase nbtbase = this.tileEntityData.getTag(s);

                                    if (!"x".equals(s) && !"y".equals(s) && !"z".equals(s)) {
                                        nbttagcompound.setTag(s, nbtbase.copy());
                                    }
                                }

                                tileentity.readFromNBT(nbttagcompound);
                                tileentity.markDirty();
                            }
                        }
                    }
                }
            }
        }

        this.motionX *= 0.9800000190734863D;
        this.motionY *= 0.9800000190734863D;
        this.motionZ *= 0.9800000190734863D;

    }

    protected abstract boolean tryBreakingBlockAt(BlockPos blockPos);


    public void fall(float distance, float damageMultiplier) {
        Block block = this.fallTile.getBlock();

        if (this.hurtEntities) {
            int i = MathHelper.ceil(distance - 1.0F);

            if (i > 0) {
                List<Entity> list = Lists.newArrayList(this.world.getEntitiesWithinAABBExcludingEntity(this, this.getEntityBoundingBox()));
                boolean flag = block == Blocks.ANVIL;
                DamageSource damagesource = flag ? DamageSource.ANVIL : DamageSource.FALLING_BLOCK;

                for (Entity entity : list) {
                    entity.attackEntityFrom(damagesource, (float) Math.min(MathHelper.floor((float) i * this.fallHurtAmount), this.fallHurtMax));
                }
            }
        }
    }
}
