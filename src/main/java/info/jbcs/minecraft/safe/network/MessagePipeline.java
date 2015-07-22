package info.jbcs.minecraft.safe.network;

import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;

public class MessagePipeline extends SimpleNetworkWrapper{
    public MessagePipeline(){
        super("Safe");
    }
}