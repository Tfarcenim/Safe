package info.jbcs.minecraft.safe.network;

import io.netty.buffer.ByteBuf;
import cpw.mods.fml.common.network.simpleimpl.IMessage;

public abstract class Message implements IMessage
{
    public abstract void fromBytes(ByteBuf buffer);

    public abstract void toBytes(ByteBuf buffer);
}