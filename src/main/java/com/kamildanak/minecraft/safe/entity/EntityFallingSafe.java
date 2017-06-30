package com.kamildanak.minecraft.safe.entity;

import com.kamildanak.minecraft.safe.init.SafeSoundEvents;
import com.kamildanak.minecraft.safe.network.PacketDispatcher;
import com.kamildanak.minecraft.safe.network.client.MessageSafeFall;
import io.netty.buffer.ByteBuf;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;

public class EntityFallingSafe extends EntityFallingBlockExtended {
    public int cracks;

    public EntityFallingSafe(World par1World) {
        super(par1World);
        setHurtEntities(true);
        cracks = 0;
    }

    public EntityFallingSafe(World world, BlockPos pos, IBlockState blockState) {
        super(world, pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5, blockState, world.getTileEntity(pos), pos);
        TileEntity te = world.getTileEntity(pos);
        cracks = 0;
        if (te instanceof TileEntitySafe)
            cracks = ((TileEntitySafe) te).cracks;
        setHurtEntities(true);
    }

    public static void createFallParticles(World world, BlockPos pos, int distance) {
        PacketDispatcher.sendToAllAround(new MessageSafeFall(pos, distance), world.provider.getDimension(),
                pos.getX(), pos.getY(), pos.getZ(), 20);
    }

    @Override
    public void writeSpawnData(ByteBuf byteBuf) {
        super.writeSpawnData(byteBuf);
        byteBuf.writeInt(cracks);
        byteBuf.writeByte(super.fallTime);
    }

    @Override
    public void readSpawnData(ByteBuf byteBuf) {
        super.readSpawnData(byteBuf);
        cracks = byteBuf.readInt();
        super.fallTime = byteBuf.readByte();
    }

    @Override
    protected boolean tryBreakingBlockAt(BlockPos blockPos) {
        double explosionForce = (force * force) * 6.0;
        IBlockState blockState = world.getBlockState(blockPos);
        Explosion explosion = new Explosion(world, this, blockPos.getX(), blockPos.getY(), blockPos.getZ(),
                (float) explosionForce, false, true);
        double resistance = blockState.getBlock().getExplosionResistance(world, blockPos, this, explosion);
        if (resistance < 1.0f) {
            resistance += getBlockAddedResitance(world, blockPos.east(), explosion);
            resistance += getBlockAddedResitance(world, blockPos.west(), explosion);
            resistance += getBlockAddedResitance(world, blockPos.north(), explosion);
            resistance += getBlockAddedResitance(world, blockPos.south(), explosion);
        }

        if (resistance > explosionForce)
            return false;
        if (!blockState.getBlock().canDropFromExplosion(explosion))
            return false;
        int distance = Math.abs(getPosition().getY() - start.getY());
        fallSound(world, getPosition(), distance);
        createFallParticles(world, getPosition(), distance);
        force = motionY = force * (explosionForce - resistance * 0.5) / explosionForce;
        world.destroyBlock(blockPos, true);
        start = new BlockPos(blockPos);
        return true;
    }

    public double getBlockAddedResitance(World world, BlockPos pos, Explosion explosion) {
        IBlockState blockState = world.getBlockState(pos);
        double res = blockState.getBlock().getExplosionResistance(world, pos, this, explosion);

        if (res > 1.0) return 1.0;
        return res;
    }

    public void fallSound(World world, BlockPos pos, int distance) {
        IBlockState block = world.getBlockState(pos.down());
        SoundEvent sound;
        Material material = block.getMaterial();

        if (material == Material.AIR) {
            return;
        } else if (material == Material.ANVIL || material == Material.GLASS || material == Material.ICE ||
                material == Material.IRON) {
            sound = SafeSoundEvents.FALL_METAL;
        } else {
            sound = SafeSoundEvents.FALL_WOOD;
        }
        playSound(sound, 0.5F + 0.1F * distance, world.rand.nextFloat() * 0.1F + 0.9F);
    }
}
