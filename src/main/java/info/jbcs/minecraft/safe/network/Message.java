package info.jbcs.minecraft.safe.network;

import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import io.netty.buffer.ByteBuf;

public abstract class Message implements IMessage
{
    public abstract void fromBytes(ByteBuf buffer);

    public abstract void toBytes(ByteBuf buffer);
}