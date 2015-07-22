package info.jbcs.minecraft.safe.network;

import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;

public class MessagePipeline extends SimpleNetworkWrapper{
    public MessagePipeline(){
        super("Safe");
    }
}