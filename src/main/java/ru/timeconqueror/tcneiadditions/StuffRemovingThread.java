package ru.timeconqueror.tcneiadditions;

import codechicken.nei.NEIClientConfig;
import codechicken.nei.recipe.GuiCraftingRecipe;
import codechicken.nei.recipe.GuiUsageRecipe;
import codechicken.nei.recipe.ICraftingHandler;
import codechicken.nei.recipe.IUsageHandler;
import com.djgiannuzz.thaumcraftneiplugin.nei.recipehandler.AspectRecipeHandler;
import net.minecraft.util.Timer;
import ru.timeconqueror.tcneiadditions.client.TCNAClient;

import java.lang.reflect.Field;
import java.util.Iterator;

public class StuffRemovingThread extends Thread {
    private static final Field configLoadedField;

    static {
        Field configLoaded = null;
        try {
            configLoaded = NEIClientConfig.class.getDeclaredField("configLoaded");
            configLoaded.setAccessible(true);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }

        configLoadedField = configLoaded;
    }

    private boolean stop = false;
    private Timer timer = new Timer(1);

    public StuffRemovingThread() {
        setDaemon(true);
    }

    @Override
    public void run() {
        while (!stop) {
            timer.updateTimer();

            for (int i = 0; i < timer.elapsedTicks; i++) {
                onTick();
            }
        }
    }

    private void onTick() {
        TCNAClient.getInstance().addScheduledTask(() -> {
            try {

                boolean configLoaded = (boolean) configLoadedField.get(null);
                if (configLoaded) {

                    Iterator<ICraftingHandler> craftingIterator = GuiCraftingRecipe.craftinghandlers.iterator();
                    while (craftingIterator.hasNext()) {
                        ICraftingHandler next = craftingIterator.next();
                        if (next instanceof AspectRecipeHandler) {
                            craftingIterator.remove();
                            TCNEIAdditions.LOGGER.info("Crafting Recipes: found and removed standard " + AspectRecipeHandler.class.getSimpleName() + " from Thaumcraft NEI Plugin");
                        }
                    }

                    Iterator<IUsageHandler> usageIterator = GuiUsageRecipe.usagehandlers.iterator();
                    while (usageIterator.hasNext()) {
                        IUsageHandler next = usageIterator.next();
                        if (next instanceof AspectRecipeHandler) {
                            usageIterator.remove();
                            TCNEIAdditions.LOGGER.info("Usage Recipes: found and removed standard " + AspectRecipeHandler.class.getSimpleName() + " from Thaumcraft NEI Plugin");
                        }
                    }

                    stop = true;
                }
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        });
    }
}
