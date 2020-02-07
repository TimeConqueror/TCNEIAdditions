package ru.timeconqueror.tcneiadditions.nei;

import codechicken.nei.NEIServerUtils;
import codechicken.nei.PositionedStack;
import codechicken.nei.recipe.ShapedRecipeHandler;
import com.djgiannuzz.thaumcraftneiplugin.items.ItemAspect;
import com.djgiannuzz.thaumcraftneiplugin.nei.NEIHelper;
import com.djgiannuzz.thaumcraftneiplugin.nei.recipehandler.ArcaneShapedRecipeHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import ru.timeconqueror.tcneiadditions.client.TCNAClient;
import thaumcraft.api.ThaumcraftApi;
import thaumcraft.api.ThaumcraftApiHelper;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.crafting.ShapedArcaneRecipe;
import thaumcraft.api.wands.WandCap;
import thaumcraft.api.wands.WandRod;
import thaumcraft.common.items.wands.ItemWandCasting;

import java.util.Collection;
import java.util.List;

public class ArcaneCraftingShapedHandler extends ArcaneShapedRecipeHandler {
    private String userName = Minecraft.getMinecraft().getSession().getUsername();

    public void loadCraftingRecipes(ItemStack result) {
        if (result.getItem() instanceof ItemWandCasting) {
            ItemWandCasting wand = (ItemWandCasting) result.getItem();
            WandRod rod = wand.getRod(result);
            WandCap cap = wand.getCap(result);
            if (!wand.isSceptre(result) || ThaumcraftApiHelper.isResearchComplete(userName, "SCEPTRE")) {
                if (ThaumcraftApiHelper.isResearchComplete(userName, cap.getResearch()) && ThaumcraftApiHelper.isResearchComplete(userName, rod.getResearch())) {
                    if (!TCNAClient.getInstance().areWandRecipesDeleted()) {
                        ArcaneWandCachedRecipe recipe = new ArcaneWandCachedRecipe(rod, cap, result, wand.isSceptre(result));
                        recipe.computeVisuals();
                        this.arecipes.add(recipe);
                        this.aspectsAmount.add(NEIHelper.getWandAspectsWandCost(result));
                    }

                    loadShapedRecipesForWands(result);
                }
            }
        } else {
            for (Object o : ThaumcraftApi.getCraftingRecipes()) {
                if (o instanceof ShapedArcaneRecipe) {
                    ShapedArcaneRecipe shapedArcaneRecipe = (ShapedArcaneRecipe) o;

                    ArcaneShapedCachedRecipe recipe = new ArcaneShapedCachedRecipe(shapedArcaneRecipe);

                    if (recipe.isValid() && ThaumcraftApiHelper.isResearchComplete(userName, shapedArcaneRecipe.getResearch()) && (NEIServerUtils.areStacksSameTypeCrafting(shapedArcaneRecipe.getRecipeOutput(), result))) {
                        recipe.computeVisuals();
                        this.arecipes.add(recipe);
                        this.aspectsAmount.add(getAmounts(shapedArcaneRecipe));
                    }
                }
            }
        }
    }

    @SuppressWarnings("unchecked")
    public void loadShapedRecipesForWands(ItemStack wandStack) {
        if (!(wandStack.getItem() instanceof ItemWandCasting)) {
            throw new RuntimeException("This method works only for Thaumcraft Wands! Provided: " + wandStack);
        }

        ItemWandCasting wand = ((ItemWandCasting) wandStack.getItem());
        WandRod rod = wand.getRod(wandStack);
        WandCap cap = wand.getCap(wandStack);
        boolean isSceptre = wand.isSceptre(wandStack);

        ((List<Object>) ThaumcraftApi.getCraftingRecipes()).stream()
                .filter(o -> o instanceof ShapedArcaneRecipe && ThaumcraftApiHelper.isResearchComplete(userName, ((ShapedArcaneRecipe) o).getResearch()))
                .filter(r -> {
                    ItemStack output = ((ShapedArcaneRecipe) r).output;
                    if (!(output.getItem() instanceof ItemWandCasting)) return false;

                    if (isSceptre != wand.isSceptre(output)) return false;

                    if (output.getItem().getClass() != wandStack.getItem().getClass()) return false;

                    WandRod outputRod = wand.getRod(output);
                    WandCap outputCap = wand.getCap(output);

                    return outputRod.getTag().equals(rod.getTag()) && outputCap.getTag().equals(cap.getTag());
                }).forEach(o -> {
            ShapedArcaneRecipe arcaneRecipe = (ShapedArcaneRecipe) o;
            ArcaneShapedCachedRecipe recipe = new ArcaneShapedCachedRecipe(arcaneRecipe);
            recipe.computeVisuals();
            this.arecipes.add(recipe);
            this.aspectsAmount.add(getAmounts(arcaneRecipe));
        });

    }

    private class ArcaneShapedCachedRecipe
            extends ShapedRecipeHandler.CachedShapedRecipe {
        protected AspectList aspects;
        protected Object[] overlay;
        protected int width;
        protected int height;

        public ArcaneShapedCachedRecipe(ShapedArcaneRecipe recipe) {
            super(recipe.width, recipe.height, recipe.getInput(), recipe.getRecipeOutput());
            this.result = new PositionedStack(recipe.getRecipeOutput(), 74, 2);
            this.aspects = recipe.getAspects();
            this.overlay = recipe.getInput();
            this.width = recipe.width;
            this.height = recipe.height;
            NEIHelper.addAspectsToIngredients(this.aspects, this.ingredients, 0);
        }

        public boolean isValid() {
            return !this.ingredients.isEmpty() && this.result != null;
        }

        public void setIngredients(int width, int height, Object[] items) {
            if (items != null && items.length > 0) {
                int y;
                int x;
                int[][][] positions2 = new int[width][height][2];
                int shiftX = 0;
                int shiftY = 0;
                for (x = 0; x < width && x < 3; ++x) {
                    for (y = 0; y < height && y < 3; ++y) {
                        positions2[x][y][0] = positions[y][x][0];
                        positions2[x][y][1] = positions[y][x][1];
                    }
                }
                for (x = 0; x < width && x < 3; ++x) {
                    for (y = 0; y < height && y < 3; ++y) {
                        if (items[y * width + x] == null || !(items[y * width + x] instanceof ItemStack) && !(items[y * width + x] instanceof ItemStack[]) && !(items[y * width + x] instanceof String) && !(items[y * width + x] instanceof List) || items[y * width + x] instanceof List && ((List) items[y * width + x]).isEmpty())
                            continue;
                        PositionedStack stack = new PositionedStack(items[y * width + x], positions2[x][y][0] + shiftX, positions2[x][y][1] + shiftY, false);
                        stack.setMaxSize(1);
                        this.ingredients.add(stack);
                    }
                }
            }
        }

        public boolean contains(Collection<PositionedStack> ingredients, ItemStack ingredient) {
            if (ingredient.getItem() instanceof ItemAspect) {
                return false;
            }
            return super.contains(ingredients, ingredient);
        }
    }

    private class ArcaneWandCachedRecipe
            extends ShapedRecipeHandler.CachedShapedRecipe {
        protected AspectList aspects;
        protected Object[] overlay;

        public ArcaneWandCachedRecipe(WandRod rod, WandCap cap, ItemStack result, boolean isScepter) {
            super(3, 3, isScepter ? NEIHelper.buildScepterInput(rod, cap) : NEIHelper.buildWandInput(rod, cap), result);
            this.overlay = isScepter ? NEIHelper.buildScepterInput(rod, cap) : NEIHelper.buildWandInput(rod, cap);
            this.result = new PositionedStack(result, 74, 2);
            this.aspects = NEIHelper.getPrimalAspectListFromAmounts(NEIHelper.getWandAspectsWandCost(result));
            NEIHelper.addAspectsToIngredients(this.aspects, this.ingredients, 0);
        }

        public void setIngredients(int width, int height, Object[] items) {
            if (items != null && items.length > 0) {
                int[][] positions = new int[][]{{48, 32}, {75, 33}, {103, 33}, {49, 60}, {76, 60}, {103, 60}, {49, 87}, {76, 87}, {103, 87}};
                int[][] positions2 = new int[][]{{48, 32}, {75, 33}, {49, 60}, {76, 60}};
                int shiftX = 0;
                int shiftY = 0;
                for (int x = 0; x < width; ++x) {
                    for (int y = 0; y < height; ++y) {
                        if (items[y * width + x] == null || !(items[y * width + x] instanceof ItemStack) && !(items[y * width + x] instanceof ItemStack[]) && !(items[y * width + x] instanceof String) && !(items[y * width + x] instanceof List) || items[y * width + x] instanceof List && ((List) items[y * width + x]).isEmpty())
                            continue;
                        if (width == 2 && height == 2) {
                            positions = positions2;
                        }
                        PositionedStack stack = new PositionedStack(items[y * width + x], positions[y * width + x][0] + shiftX, positions[y * width + x][1] + shiftY, false);
                        stack.setMaxSize(1);
                        this.ingredients.add(stack);
                    }
                }
            }
        }

        public boolean contains(Collection<PositionedStack> ingredients, ItemStack ingredient) {
            if (ingredient.getItem() instanceof ItemAspect) {
                return false;
            }
            return super.contains(ingredients, ingredient);
        }
    }
}
