package ru.timeconqueror.thaumcraftneifix.nei;

import codechicken.nei.api.IConfigureNEI;
import codechicken.nei.recipe.GuiUsageRecipe;
import codechicken.nei.recipe.IUsageHandler;
import ru.timeconqueror.thaumcraftneifix.TCNEIPluginFix;

public class NEIConfig
        implements IConfigureNEI {

    @Override
    public void loadConfig() {
//        API.registerRecipeHandler();
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