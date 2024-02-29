/*
 * Copyright (c) 2019-2023 TagnumElite
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package me.shepherd23333.projecteintegration.plugins.duplicate;

import gregtech.api.recipes.Recipe;
import gregtech.api.recipes.RecipeMap;
import gregtech.api.recipes.ingredients.GTRecipeInput;
import me.shepherd23333.projecteintegration.api.internal.sized.SizedObject;
import me.shepherd23333.projecteintegration.api.mappers.PEIMapper;
import me.shepherd23333.projecteintegration.api.plugin.APEIPlugin;
import me.shepherd23333.projecteintegration.api.plugin.OnlyIf;
import me.shepherd23333.projecteintegration.api.plugin.PEIPlugin;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;

import java.util.ArrayList;

@PEIPlugin("gregtech")
@OnlyIf(version = "[2.0")
public class PluginGregTechCEU extends APEIPlugin {
    @Override
    public void setup() {
        for (RecipeMap<?> map : RecipeMap.getRecipeMaps()) {
            addMapper(new RecipeMapper(map));
        }
    }

    private static class RecipeMapper extends PEIMapper {
        private final RecipeMap<?> map;

        public RecipeMapper(RecipeMap<?> map) {
            super(map.unlocalizedName);
            this.map = map;
        }

        @Override
        public void setup() {
            for (Recipe recipe : map.getRecipeList()) {
                ArrayList<Object> inputs = new ArrayList<>();
                for (GTRecipeInput input : recipe.getInputs()) {
                    inputs.add(new SizedObject<>(input.getAmount(), Ingredient.fromStacks(input.getInputStacks())));
                }
                for (GTRecipeInput fluidInput : recipe.getFluidInputs()) {
                    inputs.add(fluidInput.getInputFluidStack());
                }

                ArrayList<Object> outputs = new ArrayList<>(recipe.getOutputs());
                outputs.addAll(recipe.getFluidOutputs());

                for (Recipe.ChanceEntry output : recipe.getChancedOutputs()) {
                    if (output.getChance() >= 20000) {
                        ItemStack item = output.getItemStack().copy();
                        item.setCount(item.getCount() * 2);
                        outputs.add(item);
                    } else if (output.getChance() >= 10000) {
                        outputs.add(output.getItemStack());
                    }
                }

                addRecipe(outputs, inputs.toArray());
            }
        }
    }
}
