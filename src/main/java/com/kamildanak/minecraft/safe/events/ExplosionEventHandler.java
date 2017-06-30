package com.kamildanak.minecraft.safe.events;

import com.kamildanak.minecraft.safe.Safe;
import com.kamildanak.minecraft.safe.init.SafeBlocks;
import net.minecraft.block.Block;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.event.world.ExplosionEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.HashSet;
import java.util.Set;

@Mod.EventBusSubscriber(modid = Safe.MOD_ID)
public class ExplosionEventHandler {

    @SubscribeEvent
    public static void onExplosionDetonate(ExplosionEvent event) {
        World world = event.getWorld();
        Set<BlockPos> safe_set = new HashSet<>();
        for (BlockPos pos : event.getExplosion().getAffectedBlockPositions()) {
            Block block = world.getBlockState(pos).getBlock();
            if (block == SafeBlocks.SAFE) {
                safe_set.add(pos);
                SafeBlocks.SAFE.crack(world, pos, event.getExplosion().getExplosivePlacedBy());
            }
        }
        event.getExplosion().getAffectedBlockPositions().removeAll(safe_set);
    }
}
