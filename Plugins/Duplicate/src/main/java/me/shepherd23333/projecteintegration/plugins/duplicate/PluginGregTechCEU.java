/*
 * Copyright (c) 2019-2025 TagnumElite
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

import com.google.common.collect.ImmutableMap;
import gregtech.api.GTValues;
import gregtech.api.recipes.Recipe;
import gregtech.api.recipes.RecipeMap;
import gregtech.api.recipes.chance.output.ChancedOutputLogic;
import gregtech.api.recipes.chance.output.impl.ChancedFluidOutput;
import gregtech.api.recipes.chance.output.impl.ChancedItemOutput;
import gregtech.api.recipes.ingredients.GTRecipeInput;
import gregtech.api.unification.material.Materials;
import gregtech.common.items.MetaItems;
import me.shepherd23333.projecteintegration.api.mappers.PEIMapper;
import me.shepherd23333.projecteintegration.api.plugin.APEIPlugin;
import me.shepherd23333.projecteintegration.api.plugin.OnlyIf;
import me.shepherd23333.projecteintegration.api.plugin.PEIPlugin;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.oredict.OreDictionary;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@PEIPlugin("gregtech")
@OnlyIf(version = "[2.8,)")
public class PluginGregTechCEU extends APEIPlugin {
    static boolean boostMAX;
    static int boost = boostMAX ? GTValues.UV : GTValues.MAX;

    @Override
    public void setup() {
        boostMAX = config.getBoolean("isBoostMAX", category, false,
                "Whether to calculate the chance of chanced output at MAX tier, otherwise at UV tier.");

        addEMC(MetaItems.STICKY_RESIN.getStackForm(), 32);
        addEMC("Natural Gas(1mb)", Materials.NaturalGas.getFluid(), 100);
        addEMC("Oil(1mb)", Materials.Oil.getFluid(), 100);
        addEMC("Light Oil(1mb)", Materials.OilLight.getFluid(), 100);
        addEMC("Heavy Oil(1mb)", Materials.OilHeavy.getFluid(), 100);
        addEMC("Raw Oil(1mb)", Materials.RawOil.getFluid(), 100);

        for (RecipeMap<?> map : RecipeMap.getRecipeMaps()) {
            addMapper(new RecipeMapper(map));
        }
    }

    private static class RecipeMapper extends PEIMapper {
        private final RecipeMap<?> map;
        private final int maxChance = ChancedOutputLogic.getMaxChancedValue();

        public RecipeMapper(RecipeMap<?> map) {
            super(map.unlocalizedName);
            this.map = map;
        }

        @Override
        public void setup() {
            for (Recipe recipe : map.getRecipeList()) {
                Map<Object, Integer> inputs = new HashMap<>();
                for (GTRecipeInput input : recipe.getInputs()) {
                    if (input.isNonConsumable())
                        continue;
                    Object obj = new Object();
                    if (input.isOreDict())
                        obj = OreDictionary.getOreName(input.getOreDict());
                    else
                        for (ItemStack stack : input.getInputStacks())
                            addConversion(1, obj, ImmutableMap.of(stack, 1));
                    inputs.put(obj, input.getAmount() * maxChance);
                }
                for (GTRecipeInput input : recipe.getFluidInputs()) {
                    if (input.isNonConsumable())
                        continue;
                    FluidStack stack = input.getInputFluidStack();
                    inputs.put(stack, stack.amount * maxChance);
                }

                List<Object> outputs = new ArrayList<>();
                for (ItemStack stack : recipe.getOutputs()) {
                    ItemStack i = stack.copy();
                    i.setCount(i.getCount() * maxChance);
                    outputs.add(i);
                }
                for (FluidStack stack : recipe.getFluidOutputs()) {
                    FluidStack f = stack.copy();
                    f.amount *= maxChance;
                    outputs.add(f);
                }

                for (ChancedItemOutput output : recipe.getChancedOutputs().getChancedEntries()) {
                    int mult = Math.min(maxChance, output.getChance() + boost * output.getChanceBoost());
                    ItemStack item = output.getIngredient().copy();
                    item.setCount(item.getCount() * mult);
                    outputs.add(item);
                }

                for (ChancedFluidOutput output : recipe.getChancedFluidOutputs().getChancedEntries()) {
                    int mult = Math.min(maxChance, output.getChance() + boost * output.getChanceBoost());
                    FluidStack fluid = output.getIngredient().copy();
                    fluid.amount *= mult;
                    outputs.add(fluid);
                }

                addConversion(outputs, inputs);
            }
        }
    }
}
