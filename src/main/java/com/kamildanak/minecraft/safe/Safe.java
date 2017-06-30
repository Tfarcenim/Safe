package com.kamildanak.minecraft.safe;

import com.kamildanak.minecraft.safe.entity.EntityFallingSafe;
import com.kamildanak.minecraft.safe.entity.TileEntitySafe;
import com.kamildanak.minecraft.safe.proxy.CommonProxy;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry;

import java.io.File;


@Mod(modid = Safe.MOD_ID, name = Safe.MOD_NAME, version = Safe.MOD_VERSION,
        acceptedMinecraftVersions = "{@mcVersion}") //dependencies = "required-after:Autoutils"
public class Safe {
    public static final String MOD_ID = "safe";
    public static final String MOD_NAME = "Safe";
    public static final String MOD_VERSION = "{@safeVersion}";
    @Mod.Instance("safe")
    public static Safe instance;
    @SidedProxy(clientSide = "com.kamildanak.minecraft.safe.proxy.ClientProxy",
            serverSide = "com.kamildanak.minecraft.safe.proxy.CommonProxy")
    public static CommonProxy proxy;

    public static int crackDelay;
    public static int crackCount;
    public static int crackChance;
    static Configuration config;

    public Safe() {
    }

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        File configFile = event.getSuggestedConfigurationFile();
        config = new Configuration(configFile);
        config.load();
        proxy.preInit();
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        crackDelay = config.get("general", "crack delay", 86400,
                "The amount of seconds that must pass before safe block can get another crack").getInt();
        crackCount = config.get("general", "crack count", 6,
                "The amount of cracks that will cause the safe to break.").getInt();
        crackChance = config.get("general", "crack chance", 100,
                "Chance, in percent, that a safe will receive a crack from an explosion").getInt();

        GameRegistry.registerTileEntity(TileEntitySafe.class, "containerSafe");
        EntityRegistry.registerModEntity(new ResourceLocation("safe:safe"),
                EntityFallingSafe.class, "FallingSafe", 1,
                this, 40, 9999, false);

        proxy.init();
        proxy.registerPackets();
        config.save();
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event) {
    }
}
