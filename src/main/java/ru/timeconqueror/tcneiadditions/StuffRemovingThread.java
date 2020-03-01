package ru.timeconqueror.tcneiadditions;

import codechicken.nei.NEIClientConfig;
import codechicken.nei.recipe.GuiCraftingRecipe;
import codechicken.nei.recipe.GuiUsageRecipe;
import codechicken.nei.recipe.ICraftingHandler;
import codechicken.nei.recipe.IUsageHandler;
import net.minecraft.util.Timer;
import ru.timeconqueror.tcneiadditions.client.TCNAClient;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Iterator;

public class StuffRemovingThread extends Thread {
    private static final Field configLoadedField;
    private static ArrayList<Class<? extends ICraftingHandler>> recipeHandlersForRemoving = new ArrayList<>();
    private static ArrayList<Class<? extends IUsageHandler>> usageHandlersForRemoving = new ArrayList<>();

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

    public static void removeRecipeHandler(Class<? extends ICraftingHandler> handlerClass) {
        recipeHandlersForRemoving.add(handlerClass);
    }

    public static void removeUsageHandler(Class<? extends IUsageHandler> handlerClass) {
        usageHandlersForRemoving.add(handlerClass);
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
                        for (Class<? extends ICraftingHandler> craftingHandlerClass : recipeHandlersForRemoving) {
                            if (next.getClass() == craftingHandlerClass) {
                                craftingIterator.remove();
                                TCNEIAdditions.LOGGER.info("Crafting Recipes: found and removed standard " + craftingHandlerClass.getSimpleName() + " from Thaumcraft NEI Plugin");
                            }
                        }
                    }

                    Iterator<IUsageHandler> usageIterator = GuiUsageRecipe.usagehandlers.iterator();
                    while (usageIterator.hasNext()) {
                        IUsageHandler next = usageIterator.next();
                        for (Class<? extends IUsageHandler> usageHandlerClass : usageHandlersForRemoving) {
                            if (next.getClass() == usageHandlerClass) {
                                usageIterator.remove();
                                TCNEIAdditions.LOGGER.info("Usage Recipes: found and removed standard " + usageHandlerClass.getSimpleName() + " from Thaumcraft NEI Plugin");
                            }
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
