package ru.timeconqueror.tcneiadditions.nei;

import codechicken.lib.gui.GuiDraw;
import codechicken.nei.PositionedStack;
import codechicken.nei.recipe.TemplateRecipeHandler;
import com.djgiannuzz.thaumcraftneiplugin.ModItems;
import com.djgiannuzz.thaumcraftneiplugin.items.ItemAspect;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import org.apache.commons.lang3.ArrayUtils;
import ru.timeconqueror.tcneiadditions.client.DrawUtils;
import ru.timeconqueror.tcneiadditions.client.TCNAClient;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.common.Thaumcraft;

import java.util.ArrayList;
import java.util.List;

public class AspectCombinationHandler extends TemplateRecipeHandler {
    private static final int SPACE_DECREASE = -25;

    private String userName = Minecraft.getMinecraft().getSession().getUsername();

    @Override
    public String getGuiTexture() {
        return null;
    }

    @Override
    public int recipiesPerPage() {
        return 5;
    }

    @Override
    public String getRecipeName() {
        return I18n.format("tcneiadditions.aspect_combination.title");
    }

    @Override
    public void loadCraftingRecipes(ItemStack result) {
        if (result.getItem() instanceof ItemAspect) {
            Aspect aspect = ItemAspect.getAspects(result).getAspects()[0];
            if (Thaumcraft.proxy.playerKnowledge.hasDiscoveredAspect(userName, aspect)) {
                new AspectCombinationRecipe(result);
            }
        }
    }

    @Override
    public void loadUsageRecipes(ItemStack ingredient) {
        if (ingredient.getItem() instanceof ItemAspect) {
            Aspect aspect = ItemAspect.getAspects(ingredient).getAspects()[0];

            if (Thaumcraft.proxy.playerKnowledge.hasDiscoveredAspect(Minecraft.getMinecraft().getSession().getUsername(), aspect)) {
                for (Aspect compoundAspect : Aspect.getCompoundAspects()) {
                    if (ArrayUtils.contains(compoundAspect.getComponents(), aspect) && Thaumcraft.proxy.playerKnowledge.hasDiscoveredAspect(userName, compoundAspect)) {
                        ItemStack result = new ItemStack(ModItems.itemAspect);
                        ItemAspect.setAspect(result, compoundAspect);

                        new AspectCombinationRecipe(result);
                    }
                }
            }
        }
    }

    @Override
    public void drawBackground(int recipe) {
        AspectCombinationRecipe cachedRecipe = (AspectCombinationRecipe) arecipes.get(recipe);
        if (cachedRecipe.getIngredients().isEmpty()) {
            int startY = calculateOffsetY(recipe, SPACE_DECREASE) + 25;
            GuiDraw.drawStringC(I18n.format("tc.aspect.primal"), TCNAClient.NEI_GUI_WIDTH / 2, startY, TCNAClient.NEI_TEXT_COLOR, false);
        } else {
            int spaceX = 16;
            int startX = TCNAClient.NEI_GUI_WIDTH / 2 - (16 + (16 + spaceX) * 2) / 2;
            int startY = calculateOffsetY(recipe, SPACE_DECREASE) + 6;
            DrawUtils.drawXYCenteredString("=", startX + 24, startY + 8, TCNAClient.NEI_TEXT_COLOR, false);
            DrawUtils.drawXYCenteredString("+", startX + 56, startY + 8, TCNAClient.NEI_TEXT_COLOR, false);
        }
    }

    @Override
    public void drawForeground(int recipe) {

    }

    private int calculateOffsetY(int recipeIndex, int spaceIncrease) {
        return -(TCNAClient.NEI_RECIPE_HEIGHT + spaceIncrease) * (recipeIndex % recipiesPerPage());
    }

    private class AspectCombinationRecipe extends CachedRecipe {
        private List<PositionedStack> ingredients = new ArrayList<>();
        private PositionedStack result;

        public AspectCombinationRecipe(ItemStack aspectStack) {
            int recipeIndex = arecipes.size();
            arecipes.add(this);

            int startY = calculateOffsetY(recipeIndex, SPACE_DECREASE);// in case of NEI uses not changeable recipe height (=65), so this allows to the space between recipes to be reduced visually.

            Aspect aspect = ItemAspect.getAspects(aspectStack).getAspects()[0];
            aspectStack = new ItemStack(ModItems.itemAspect);
            ItemAspect.setAspect(aspectStack, aspect);

            if (aspect.isPrimal()) {
                this.result = new PositionedStack(aspectStack, TCNAClient.NEI_GUI_WIDTH / 2 - 16 / 2, startY + 6);
            } else {
                int spaceX = 16;
                int startX = TCNAClient.NEI_GUI_WIDTH / 2 - (16 + (16 + spaceX) * 2) / 2;

                this.result = new PositionedStack(aspectStack, startX, startY + 6);

                Aspect[] components = aspect.getComponents();

                ItemStack firstIngred = new ItemStack(ModItems.itemAspect);
                ItemAspect.setAspect(firstIngred, components[0]);
                ItemStack secondIngred = new ItemStack(ModItems.itemAspect);
                ItemAspect.setAspect(secondIngred, components[1]);

                ingredients.add(new PositionedStack(firstIngred, startX + (spaceX + 16), startY + 6));
                ingredients.add(new PositionedStack(secondIngred, startX + (spaceX + 16) * 2, startY + 6));
            }
        }

        @Override
        public PositionedStack getResult() {
            return result;
        }

        @Override
        public List<PositionedStack> getIngredients() {
            return ingredients;
        }
    }
}
