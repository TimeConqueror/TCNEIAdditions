package ru.timeconqueror.tcneiadditions.nei;

import codechicken.nei.api.API;
import codechicken.nei.api.IConfigureNEI;
import ru.timeconqueror.tcneiadditions.TCNEIAdditions;

public class NEIConfig implements IConfigureNEI {

    @Override
    public void loadConfig() {
        API.registerRecipeHandler(new AspectFromItemStackHandler());
        API.registerRecipeHandler(new AspectCombinationHandler());
        API.registerUsageHandler(new AspectCombinationHandler());
    }

    @Override
    public String getName() {
        return TCNEIAdditions.NAME;
    }

    @Override
    public String getVersion() {
        return TCNEIAdditions.VERSION;
    }
}