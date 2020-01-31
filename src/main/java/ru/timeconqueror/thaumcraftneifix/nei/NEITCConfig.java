package ru.timeconqueror.thaumcraftneifix.nei;

import codechicken.nei.api.IConfigureNEI;
import codechicken.nei.recipe.GuiUsageRecipe;
import codechicken.nei.recipe.IUsageHandler;
import ru.timeconqueror.thaumcraftneifix.TCNEIPluginFix;

public class NEITCConfig
        implements IConfigureNEI {

    @Override
    public void loadConfig() {
//        API.registerRecipeHandler();
        new Exception().printStackTrace();
        for (IUsageHandler usagehandler : GuiUsageRecipe.usagehandlers) {
            System.out.println(usagehandler.getClass());
        }
    }

    @Override
    public String getName() {
        return TCNEIPluginFix.NAME;
    }

    @Override
    public String getVersion() {
        return TCNEIPluginFix.VERSION;
    }
}