package info.jbcs.minecraft.safe;

import info.jbcs.minecraft.utilities.packets.PacketHandler;

import java.io.DataInputStream;
import java.io.IOException;

import net.minecraft.entity.player.EntityPlayer;

public class Packets {

	static PacketHandler safeCheck = new PacketHandler("Ask server to confirm block info after safe falls donn") {
		@Override
		public void onData(DataInputStream stream, EntityPlayer player) throws IOException {
			int x=stream.readInt();
			int z=stream.readInt();
			int sy=stream.readInt();
			int ey=stream.readInt();
			
			for(int y=sy;y>=ey;y--){
				player.worldObj.markBlockForUpdate(x, y, z);
			}
		}
	};

}
