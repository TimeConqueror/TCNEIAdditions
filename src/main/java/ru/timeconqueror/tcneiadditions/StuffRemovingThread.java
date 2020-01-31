package ru.timeconqueror.tcneiadditions;

import codechicken.nei.NEIClientConfig;
import codechicken.nei.recipe.GuiUsageRecipe;
import codechicken.nei.recipe.IUsageHandler;
import com.djgiannuzz.thaumcraftneiplugin.nei.recipehandler.AspectRecipeHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Timer;

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
        Minecraft.getMinecraft().func_152344_a(() -> {
            try {

                boolean configLoaded = (boolean) configLoadedField.get(null);
                if (configLoaded) {

                    Iterator<IUsageHandler> iterator = GuiUsageRecipe.usagehandlers.iterator();

                    while (iterator.hasNext()) {
                        IUsageHandler next = iterator.next();
                        if (next instanceof AspectRecipeHandler) {

                            iterator.remove();
                            TCNEIAdditions.LOGGER.info("Found and removed standard Thaumcraft NEI Plugin Aspect Usage GuiRecipe.");
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
