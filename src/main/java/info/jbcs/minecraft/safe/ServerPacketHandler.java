package info.jbcs.minecraft.safe;

import cpw.mods.fml.common.network.ByteBufUtils;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.ReadOnlyByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.Container;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.NetHandlerPlayServer;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.network.FMLNetworkEvent.ServerCustomPacketEvent;
import net.minecraft.tileentity.TileEntity;

import java.io.IOException;

public class ServerPacketHandler {
    @SubscribeEvent
    public void onServerPacket(ServerCustomPacketEvent event) {
        EntityPlayerMP player = ((NetHandlerPlayServer)event.handler).playerEntity;
        ByteBuf bbis = new ReadOnlyByteBuf(event.packet.payload());

        int x=bbis.readInt();
        int z=bbis.readInt();
        int sy=bbis.readInt();
        int ey=bbis.readInt();

        for(int y=sy;y>=ey;y--) {
            player.worldObj.markBlockForUpdate(x, y, z);
        }
    };
}
