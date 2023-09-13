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

package com.tagnumelite.projecteintegration.addons;

import com.tagnumelite.projecteintegration.api.recipe.ARecipeTypeMapper;
import dev.compactmods.crafting.api.components.IRecipeBlockComponent;
import dev.compactmods.crafting.core.CCMiniaturizationRecipes;
import dev.compactmods.crafting.recipes.MiniaturizationRecipe;
import dev.compactmods.crafting.recipes.components.BlockComponent;
import moze_intel.projecte.api.mapper.recipe.RecipeTypeMapper;
import moze_intel.projecte.api.nss.NSSItem;
import moze_intel.projecte.api.nss.NormalizedSimpleStack;
import moze_intel.projecte.emc.IngredientMap;
import net.minecraft.util.Tuple;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeType;

import java.util.*;

public class CompactCraftingAddon {
    @RecipeTypeMapper(requiredMods = {"compactcrafting"}, priority = 1)
    public static class CCMiniaturizationMapper extends ARecipeTypeMapper<MiniaturizationRecipe> {
        @Override
        public String getName() {
            return "CompactCraftingMiniaturizationMapper";
        }

        @Override
        public boolean canHandle(RecipeType<?> recipeType) {
            return recipeType == CCMiniaturizationRecipes.MINIATURIZATION_RECIPE_TYPE;
        }

        @Override
        public boolean convertRecipe(MiniaturizationRecipe recipe) {
            IngredientMap<NormalizedSimpleStack> ingredientMap = new IngredientMap<>();
            List<Tuple<NormalizedSimpleStack, List<IngredientMap<NormalizedSimpleStack>>>> fakeGroupMap = new ArrayList<>();

            List<ItemStack> catalysts;
            if (recipe.getCatalyst() != null && !recipe.getCatalyst().matches(ItemStack.EMPTY)) {
                catalysts = new ArrayList<>(recipe.getCatalyst().getPossible());
            } else {
                catalysts = Collections.singletonList(new ItemStack(Items.REDSTONE));
            }
            // TODO: The fakeGroupMap isn't used elsewhere. I must make sure that it is fine.
            convertIngredient(1, Ingredient.of(catalysts.stream()), ingredientMap, fakeGroupMap);

            for (Map.Entry<String, Integer> entry : recipe.getComponentTotals().entrySet()) {
                String componentKey = entry.getKey();
                int componentAmount = entry.getValue();

                if (componentAmount <= 0) continue;

                Optional<IRecipeBlockComponent> component = recipe.getComponents().getBlock(componentKey);
                if (component.isPresent()) {
                    IRecipeBlockComponent iBlockComponent = component.get();
                    if (iBlockComponent instanceof BlockComponent blockComponent) {
                        Item blockItem = blockComponent.getBlock().asItem();
                        if (blockItem != Items.AIR)
                            ingredientMap.addIngredient(NSSItem.createItem(new ItemStack(blockItem)), componentAmount);
                    }
                }
            }

            ItemStack[] outputs = recipe.getOutputs();
            if (outputs.length == 0) {
                return false;
            } else if (outputs.length == 1) {
                ItemStack output = outputs[0];
                mapper.addConversion(output.getCount(), NSSItem.createItem(output), ingredientMap.getMap());
                return true;
            } else {
                // If the recipe contains multiple outputs, here comes the special stuff.
                Set<NormalizedSimpleStack> rawNSSMatches = new HashSet<>();
                for (ItemStack output : outputs) {
                    if (!output.isEmpty()) {
                        rawNSSMatches.add(NSSItem.createItem(output));
                    }
                }
                Tuple<NormalizedSimpleStack, Boolean> group = fakeGroupManager.getOrCreateFakeGroup(rawNSSMatches);
                NormalizedSimpleStack nssFake = group.getA();

                IngredientMap<NormalizedSimpleStack> outputInputIng = new IngredientMap<>();
                outputInputIng.addIngredient(nssFake, 1);
                Map<NormalizedSimpleStack, Integer> outputInputMap = outputInputIng.getMap();

                for (ItemStack output : outputs) {
                    mapper.addConversion(output.getCount(), NSSItem.createItem(output), outputInputMap);
                }
                // TODO: Decide whether it better to outputs.length or total outputs for each conversions
                mapper.addConversion(outputs.length, nssFake, ingredientMap.getMap());
                return true;
            }
        }
    }
}
