package ru.timeconqueror.tcneiadditions.nei;

import codechicken.nei.api.API;
import codechicken.nei.api.IConfigureNEI;
import com.djgiannuzz.thaumcraftneiplugin.nei.recipehandler.ArcaneShapedRecipeHandler;
import com.djgiannuzz.thaumcraftneiplugin.nei.recipehandler.AspectRecipeHandler;
import ru.timeconqueror.tcneiadditions.StuffRemovingThread;
import ru.timeconqueror.tcneiadditions.TCNEIAdditions;

public class NEIConfig implements IConfigureNEI {

    @Override
    public void loadConfig() {
        StuffRemovingThread.removeRecipeHandler(AspectRecipeHandler.class);
        StuffRemovingThread.removeRecipeHandler(ArcaneShapedRecipeHandler.class);

        StuffRemovingThread.removeUsageHandler(AspectRecipeHandler.class);

        API.registerRecipeHandler(new AspectFromItemStackHandler());
        API.registerRecipeHandler(new AspectCombinationHandler());
        API.registerRecipeHandler(new ArcaneCraftingShapedHandler());

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