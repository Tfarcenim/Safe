package com.kamildanak.minecraft.safe.network.client;

import com.kamildanak.minecraft.safe.network.AbstractMessage;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.relauncher.Side;

import java.io.IOException;

public class MessageSafeFall extends AbstractMessage.AbstractClientMessage<MessageSafeFall> {
    private BlockPos pos;
    private int distance;


    @SuppressWarnings("unused")
    public MessageSafeFall() {
    }

    public MessageSafeFall(BlockPos pos, int distance) {
        this.pos = pos;
        this.distance = distance;
    }

    @Override
    protected void read(PacketBuffer buffer) throws IOException {
        pos = new BlockPos(buffer.readInt(), buffer.readInt(), buffer.readInt());
        distance = buffer.readInt();
    }

    @Override
    protected void write(PacketBuffer buffer) throws IOException {
        buffer.writeInt(pos.getX());
        buffer.writeInt(pos.getY());
        buffer.writeInt(pos.getZ());
        buffer.writeInt(distance);
    }

    @Override
    public void process(EntityPlayer player, Side side) {
        IBlockState iblockstate = player.getEntityWorld().getBlockState(pos.down());
        double ml = Math.log(distance) / 2;
        for (int i = 0; i < 90; i++) {
            player.getEntityWorld().spawnParticle(EnumParticleTypes.BLOCK_DUST,
                    pos.getX() + ((double) player.getEntityWorld().rand.nextFloat()),
                    pos.getY() + 0.1D,
                    pos.getZ() + ((double) player.getEntityWorld().rand.nextFloat()),
                    ml * (player.getEntityWorld().rand.nextFloat() / 5 - 0.1),
                    ml * (0.2),
                    ml * (player.getEntityWorld().rand.nextFloat() / 5 - 0.1), Block.getStateId(iblockstate));
        }
    }
}
