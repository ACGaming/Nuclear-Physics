package org.halvors.nuclearphysics.client.jei;

import java.util.ArrayList;
import java.util.List;

public final class RecipeMakerSolderer {
    public static List<RecipeWrapperSolderer> getRecipes() {
        List<RecipeWrapperSolderer> recipes = new ArrayList<>();

        /*
        for (ISoldererRecipe recipe : API.instance().getSoldererRegistry().getRecipes()) {
            List<ItemStack> inputs = new ArrayList<>();

            inputs.add(recipe.getRow(0));
            inputs.add(recipe.getRow(1));
            inputs.add(recipe.getRow(2));

            ItemStack output = recipe.getResult();

            recipes.add(new RecipeWrapperSolderer(inputs, output));
        }
        */

        return recipes;
    }
}
