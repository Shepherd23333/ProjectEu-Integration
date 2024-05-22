/*
 * Copyright (c) 2019-2024 TagnumElite
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
package me.shepherd23333.projecteintegration.plugins.crafting;

import hellfirepvp.modularmachinery.common.crafting.MachineRecipe;
import hellfirepvp.modularmachinery.common.crafting.RecipeRegistry;
import hellfirepvp.modularmachinery.common.crafting.helper.ComponentRequirement;
import hellfirepvp.modularmachinery.common.crafting.requirement.RequirementFluid;
import hellfirepvp.modularmachinery.common.crafting.requirement.RequirementItem;
import hellfirepvp.modularmachinery.common.integration.ingredient.HybridFluid;
import hellfirepvp.modularmachinery.common.integration.ingredient.HybridFluidGas;
import hellfirepvp.modularmachinery.common.machine.DynamicMachine;
import hellfirepvp.modularmachinery.common.machine.IOType;
import hellfirepvp.modularmachinery.common.machine.MachineRegistry;
import me.shepherd23333.projecteintegration.api.mappers.PEIMapper;
import me.shepherd23333.projecteintegration.api.plugin.APEIPlugin;
import me.shepherd23333.projecteintegration.api.plugin.PEIPlugin;
import moze_intel.projecte.emc.IngredientMap;
import net.minecraftforge.fml.common.Loader;

import java.util.ArrayList;

@PEIPlugin("modularmachinery")
public class PluginModularMachinery extends APEIPlugin {
    @Override
    public void setup() {
        for (DynamicMachine machine : MachineRegistry.getRegistry()) {
            addMapper(new MachineMapper(machine));
        }
    }

    private static class MachineMapper extends PEIMapper {
        private final DynamicMachine machine;

        public MachineMapper(DynamicMachine machine) {
            super(machine.getLocalizedName());
            this.machine = machine;
        }

        @Override
        public void setup() {
            for (MachineRecipe recipe : RecipeRegistry.getRegistry().getRecipesFor(machine)) {
                ArrayList<Object> outputs = new ArrayList<>();
                IngredientMap<Object> ingredients = new IngredientMap<>();

                for (ComponentRequirement<?, ?> requirement : recipe.getCraftingRequirements()) {
                    if (requirement instanceof RequirementItem) {
                        RequirementItem item_req = (RequirementItem) requirement;
                        // We don't do chanced outputs/inputs
                        if (item_req.chance < 1.0)
                            continue;
                        // We also don't count fuel
                        if (item_req.requirementType == RequirementItem.ItemRequirementType.FUEL)
                            continue;

                        switch (requirement.getActionType()) {
                            case INPUT:
                                if (item_req.requirementType == RequirementItem.ItemRequirementType.ITEMSTACKS) {
                                    ingredients.addIngredient(item_req.required, item_req.required.getCount());
                                } else if (item_req.requirementType == RequirementItem.ItemRequirementType.OREDICT) {
                                    ingredients.addIngredient(item_req.oreDictName, item_req.oreDictItemAmount);
                                }
                                break;
                            case OUTPUT:
                                // We don't do OreDict outputs
                                if (item_req.requirementType == RequirementItem.ItemRequirementType.OREDICT)
                                    continue;
                                outputs.add(item_req.required);
                                break;
                        }
                    } else if (requirement instanceof RequirementFluid) {
                        RequirementFluid fluid_req = (RequirementFluid) requirement;
                        if (fluid_req.chance < 1.0)
                            continue;

                        if (requirement.getActionType() == IOType.INPUT) {
                            HybridFluid hf = fluid_req.required;
                            if (Loader.isModLoaded("mekanism") && hf instanceof HybridFluidGas) {
                                //TODO
                            } else
                                ingredients.addIngredient(hf.asFluidStack(), hf.getAmount());
                        } else
                            outputs.add(fluid_req.required.asFluidStack());
                    }
                }

                addConversion(outputs, ingredients.getMap());
            }
        }
    }
}
