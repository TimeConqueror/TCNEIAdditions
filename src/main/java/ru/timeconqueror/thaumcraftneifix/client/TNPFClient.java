package ru.timeconqueror.thaumcraftneifix.client;

import com.google.common.collect.Queues;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListenableFutureTask;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import net.minecraft.client.Minecraft;
import org.jetbrains.annotations.NotNull;

import java.util.Queue;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;

public class TNPFClient {
    private static final TNPFClient instance = new TNPFClient();
    private final Queue<FutureTask<?>> tasks = Queues.newArrayDeque();

    public static TNPFClient getInstance() {
        return instance;
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
                return Futures.immediateFailedCheckedFuture(exception);
            }
        }
    }

    public boolean isMainThread() {
        return Minecraft.getMinecraft().func_152345_ab();
    }
}
