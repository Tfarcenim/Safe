package com.kamildanak.minecraft.safe.proxy;

import com.kamildanak.minecraft.safe.network.PacketDispatcher;
import com.kamildanak.minecraft.safe.network.client.MessageSafeFall;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.IThreadListener;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class CommonProxy {
    public void preInit() {
    }

    public void init() {
    }

    public EntityPlayer getPlayerEntity(MessageContext ctx) {
        return ctx.getServerHandler().player;
    }

    public IThreadListener getThreadFromContext(MessageContext ctx) {
        return ctx.getServerHandler().player.getServerWorld();
    }

    public void registerPackets() {
        PacketDispatcher.registerMessage(MessageSafeFall.class);
    }
}
