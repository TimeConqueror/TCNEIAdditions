package ru.timeconqueror.tcneiadditions.proxy;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import ru.timeconqueror.tcneiadditions.StuffRemovingThread;
import ru.timeconqueror.tcneiadditions.client.TCNAClient;

public class ClientProxy extends CommonProxy {
    @Override
    public void preInit(FMLPreInitializationEvent event) {
        FMLCommonHandler.instance().bus().register(TCNAClient.getInstance());
        super.preInit(event);
    }

    @Override
    public void postInit(FMLPostInitializationEvent event) {
        super.postInit(event);

        new StuffRemovingThread().start();
    }
}

