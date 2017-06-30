package com.kamildanak.minecraft.safe.init;

import com.kamildanak.minecraft.safe.block.BlockSafe;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;

public class SafeBlocks {
    public static final BlockSafe SAFE;
    static final Block[] BLOCKS;

    static {
        SAFE = new BlockSafe("safe", Material.GLASS);
        BLOCKS = new Block[]{SAFE};
    }
}
