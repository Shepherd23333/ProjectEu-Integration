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

package me.shepherd23333.projecteintegration.plugins.tech;

import com.denfop.ssp.common.Configs;
import com.denfop.ssp.fluid.neutron.FluidRegister;
import com.denfop.ssp.items.SSPItems;
import com.denfop.ssp.items.resource.CraftingThings.CraftingTypes;
import com.denfop.ssp.molecular.IMolecularTransformerRecipeManager;
import com.denfop.ssp.molecular.MolecularTransformerRecipeManager;
import com.google.common.collect.ImmutableMap;
import ic2.api.recipe.IRecipeInput;
import ic2.api.recipe.MachineRecipe;
import me.shepherd23333.projecteintegration.api.mappers.PEIMapper;
import me.shepherd23333.projecteintegration.api.plugin.APEIPlugin;
import me.shepherd23333.projecteintegration.api.plugin.PEIPlugin;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

@PEIPlugin("super_solar_panels")
public class PluginSSP extends APEIPlugin {
    @Override
    public void setup() {


        addMapper(new MTMapper());
        addMapper(new NFMapper());
    }

    private static class MTMapper extends PEIMapper {
        public MTMapper() {
            super("Molecular Transformer");
        }

        @Override
        public void setup() {
            for (MachineRecipe<IMolecularTransformerRecipeManager.Input, ItemStack> recipe : MolecularTransformerRecipeManager.RECIPES.getRecipes()) {
                IRecipeInput input = recipe.getInput().input;
                addConversion(recipe.getOutput(), ImmutableMap.of(
                        input.getIngredient(), input.getAmount()
                ));
            }
        }
    }

    private static class NFMapper extends PEIMapper {
        public NFMapper() {
            super("Neutron Fabricator");
        }

        @Override
        public void setup() {
            FluidStack neutron = new FluidStack(FluidRegister.Neutron, 1000);
            addConversion(neutron, ImmutableMap.of(
                    cobble, (int) Math.ceil(Configs.Neutronfabricator / 1e3 / PluginIndustrialCraft.uuFactor)
            ));
            addRecipe(SSPItems.CRAFTING.getItemStack(CraftingTypes.neutronshard),
                    neutron, new ItemStack(Items.BUCKET)
            );
        }
    }
}
