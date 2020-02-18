package ru.timeconqueror.tcneiadditions.client;

import com.google.common.collect.Queues;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListenableFutureTask;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.common.network.FMLNetworkEvent;
import net.minecraft.client.Minecraft;
import org.jetbrains.annotations.NotNull;
import ru.timeconqueror.tcneiadditions.TCNEIAdditions;
import thaumcraft.api.ThaumcraftApi;
import thaumcraft.common.lib.crafting.ArcaneSceptreRecipe;
import thaumcraft.common.lib.crafting.ArcaneWandRecipe;

import java.util.Queue;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;

public class TCNAClient {
    public static final int NEI_TEXT_COLOR = 0x404040;
    public static final int NEI_RECIPE_HEIGHT = 65;
    public static final int NEI_GUI_WIDTH = 166;
    public static final int NEI_GUI_HEIGHT = 131;

    private static final TCNAClient instance = new TCNAClient();
    private final Queue<FutureTask<?>> tasks = Queues.newArrayDeque();
    /**
     * Detects if any mod turned off ArcaneWandRecipes by deleting them.
     */
    private Boolean wandRecipesDeleted = null;

    public static TCNAClient getInstance() {
        return instance;
    }

    @SubscribeEvent
    public void onPlayerEntered(FMLNetworkEvent.ClientConnectedToServerEvent event) {
        wandRecipesDeleted = true;

        for (Object craftingRecipe : ThaumcraftApi.getCraftingRecipes()) {
            if (craftingRecipe instanceof ArcaneWandRecipe || craftingRecipe instanceof ArcaneSceptreRecipe) {
                wandRecipesDeleted = false;
                break;
            }
        }

        if (wandRecipesDeleted) {
            TCNEIAdditions.LOGGER.info("Detected removing of ArcaneWandRecipe and ArcaneSceptreRecipe by another mod. Applying NEI Wand Recipe searching by ShapedArcaneRecipes...");
        }
    }

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.START) {
            synchronized (this.tasks) {
                while (!this.tasks.isEmpty()) {
                    FutureTask<?> task = this.tasks.poll();
                    task.run();
                    try {
                        task.get();
                    } catch (InterruptedException | ExecutionException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }
    }

    /**
     * {@link Minecraft#func_152344_a(Runnable)} which means addScheduledTask doesn't catch and rethrows exception,
     * so here's the right method to support so.
     */
    @SuppressWarnings("UnstableApiUsage")
    public ListenableFuture<Object> addScheduledTask(@NotNull Runnable runnable) {
        Callable<Object> callable = Executors.callable(runnable);

        if (!isMainThread()) {
            ListenableFutureTask<Object> futureTask = ListenableFutureTask.create(callable);

            synchronized (tasks) {
                this.tasks.add(futureTask);
                return futureTask;
            }
        } else {
            try {
                return Futures.immediateFuture(callable.call());
            } catch (Exception exception) {
                return Futures.immediateFailedFuture(exception);
            }
        }
    }

    public boolean isMainThread() {
        return Minecraft.getMinecraft().func_152345_ab();
    }

    public boolean areWandRecipesDeleted() {
        return wandRecipesDeleted;
    }
}
