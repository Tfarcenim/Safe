package com.kamildanak.minecraft.safe.init;

import com.kamildanak.minecraft.safe.Safe;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;

public class SafeSoundEvents {
    public static final SoundEvent SAFE_OPEN;
    public static final SoundEvent SAFE_CLOSE;
    public static final SoundEvent SAFE_LOCKED;
    public static final SoundEvent FALL_METAL;
    public static final SoundEvent FALL_WOOD;
    static final SoundEvent[] SOUNDS;

    static {
        ResourceLocation res_safe_open = new ResourceLocation(Safe.MOD_ID, "safe_open");
        ResourceLocation res_safe_close = new ResourceLocation(Safe.MOD_ID, "safe_close");
        ResourceLocation res_safe_locked = new ResourceLocation(Safe.MOD_ID, "safe_locked");
        ResourceLocation res_fall_metal = new ResourceLocation(Safe.MOD_ID, "fall_metal");
        ResourceLocation res_fall_wood = new ResourceLocation(Safe.MOD_ID, "fall_wood");
        SAFE_OPEN = new SoundEvent(res_safe_open).setRegistryName(res_safe_open);
        SAFE_CLOSE = new SoundEvent(res_safe_close).setRegistryName(res_safe_close);
        SAFE_LOCKED = new SoundEvent(res_safe_locked).setRegistryName(res_safe_locked);
        FALL_METAL = new SoundEvent(res_fall_metal).setRegistryName(res_fall_metal);
        FALL_WOOD = new SoundEvent(res_fall_wood).setRegistryName(res_fall_wood);
        SOUNDS = new SoundEvent[]{SAFE_OPEN, SAFE_CLOSE, SAFE_LOCKED, FALL_METAL, FALL_WOOD};
    }
}
