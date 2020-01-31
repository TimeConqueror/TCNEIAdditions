package ru.timeconqueror.thaumcraftneifix.proxy;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import ru.timeconqueror.thaumcraftneifix.StuffRemovingThread;
import ru.timeconqueror.thaumcraftneifix.client.TNPFClient;

public class ClientProxy extends CommonProxy {
    @Override
    public void preInit(FMLPreInitializationEvent event) {
        FMLCommonHandler.instance().bus().register(TNPFClient.getInstance());
        super.preInit(event);
    }

    @Override
    public void postInit(FMLPostInitializationEvent event) {
        super.postInit(event);

        new StuffRemovingThread().start();
    }
}

