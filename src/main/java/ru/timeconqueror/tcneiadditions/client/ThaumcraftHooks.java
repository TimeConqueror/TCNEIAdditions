package ru.timeconqueror.tcneiadditions.client;

import org.jetbrains.annotations.ApiStatus;

public class ThaumcraftHooks {
    private static int totalToLoad;
    private static int objectsLoaded;
    private static boolean allDataLoaded;

    /**
     * Internal. Do not use.
     */
    @ApiStatus.Internal
    public static void onItemStackAspectDataLoaded() {
        allDataLoaded = true;
    }

    /**
     * Internal. Do not use.
     */
    @ApiStatus.Internal
    public static void incrementLoadedObjects() {
        objectsLoaded++;
    }

    public static int getObjectsLoaded() {
        return objectsLoaded;
    }

    public static int getTotalToLoad() {
        return totalToLoad;
    }

    /**
     * Internal. Do not use.
     */
    @ApiStatus.Internal
    public static void setTotalToLoad(int totalToLoad) {
        ThaumcraftHooks.totalToLoad = totalToLoad;
    }

    public static boolean isDataLoaded() {
        return allDataLoaded;
    }
}
