package ru.timeconqueror.thaumcraftneifix;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import ru.timeconqueror.thaumcraftneifix.proxy.CommonProxy;

@Mod(modid = TCNEIPluginFix.MODID,
        name = TCNEIPluginFix.NAME,
        version = TCNEIPluginFix.VERSION,
        dependencies = "required-after:Thaumcraft;required-after:thaumcraftneiplugin")
public class TCNEIPluginFix {
    public static final String MODID = "thaumcraftneifix";
    public static final String NAME = "Thaumcraft NEI Plugin Fix";
    public static final String VERSION = "@VERSION@";

    @Mod.Instance(value = TCNEIPluginFix.MODID)
    public static TCNEIPluginFix instance;

    @SidedProxy(clientSide = "ru.timeconqueror.thaumcraftneifix.proxy.ClientProxy", serverSide = "ru.timeconqueror.thaumcraftneifix.proxy.ServerProxy")
    public static CommonProxy proxy;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        proxy.preInit(event);
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        proxy.init(event);
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        proxy.postInit(event);
    }
}