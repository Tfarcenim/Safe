package info.jbcs.minecraft.safe.proxy;

import cpw.mods.fml.relauncher.Side;
import info.jbcs.minecraft.safe.network.MessagePipeline;
import info.jbcs.minecraft.safe.network.MsgLand;

public class CommonProxy {
	public void preInit() {
	}
	public void init() {
	}
	public void registerPackets(MessagePipeline pipeline)
	{
		pipeline.registerMessage(MsgLand.Handler.class, MsgLand.class, 0, Side.SERVER);
	}

}
