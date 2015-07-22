package info.jbcs.minecraft.safe.network;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ReadOnlyByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.tileentity.TileEntity;
import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;

public class MsgLand extends Message {
    private int				x, z, sy, ey;

    public MsgLand() { }

    public MsgLand(int x, int z, int sy, int ey)
    {
        this.x = x;
        this.z = z;
        this.sy = sy;
        this.ey = ey;
    }

    @Override
    public void fromBytes(ByteBuf buf)
    {
        x = buf.readInt();
        z = buf.readInt();
        sy = buf.readInt();
        ey = buf.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf)
    {
        buf.writeInt(x);
        buf.writeInt(z);
        buf.writeInt(sy);
        buf.writeInt(ey);
    }

    public static class Handler implements IMessageHandler<MsgLand, IMessage> {

        @Override
        public IMessage onMessage(MsgLand message, MessageContext ctx) {
            EntityPlayerMP player = ctx.getServerHandler().playerEntity;

            int x=message.x;
            int z=message.z;
            int sy=message.sy;
            int ey=message.ey;

            for(int y=sy;y>=ey;y--) {
                player.worldObj.markBlockForUpdate(x, y, z);
            }
            return null;
        }
    }
}