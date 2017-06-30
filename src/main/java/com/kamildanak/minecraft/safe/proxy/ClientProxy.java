package com.kamildanak.minecraft.safe.proxy;


import com.kamildanak.minecraft.safe.block.BlockSafe;
import com.kamildanak.minecraft.safe.entity.EntityFallingSafe;
import com.kamildanak.minecraft.safe.entity.TileEntitySafe;
import com.kamildanak.minecraft.safe.init.SafeBlocks;
import com.kamildanak.minecraft.safe.renderer.RenderFallingSafe;
import com.kamildanak.minecraft.safe.renderer.TileEntitySafeRenderer;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.statemap.StateMap;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IThreadListener;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class ClientProxy extends CommonProxy {
    private Minecraft mc;

    @Override
    public void preInit() {
        super.preInit();
        RenderingRegistry.registerEntityRenderingHandler(EntityFallingSafe.class, RenderFallingSafe::new);
    }

    @Override
    public void init() {
        super.init();
        mc = Minecraft.getMinecraft();
        registerTESRRender(SafeBlocks.SAFE, new TileEntitySafeRenderer(), TileEntitySafe.class);
    }

    private void registerTESRRender(Block block, TileEntitySpecialRenderer renderer, Class<? extends TileEntity> te) {
        ClientRegistry.bindTileEntitySpecialRenderer(te, renderer);
        Item item = Item.getItemFromBlock(block);
        ForgeHooksClient.registerTESRItemStack(item, 0, te);
        ModelLoader.setCustomStateMapper(block, new StateMap.Builder().ignore(BlockSafe.FACING).build());
    }

    @Override
    public EntityPlayer getPlayerEntity(MessageContext ctx) {
        return (ctx.side.isClient() ? mc.player : super.getPlayerEntity(ctx));
    }

    @Override
    public IThreadListener getThreadFromContext(MessageContext ctx) {
        return (ctx.side.isClient() ? mc : super.getThreadFromContext(ctx));
    }
}
