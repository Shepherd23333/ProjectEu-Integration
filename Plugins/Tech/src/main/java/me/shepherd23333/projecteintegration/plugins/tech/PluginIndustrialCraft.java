/*
 * Copyright (c) 2019-2020 TagnumElite
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

import com.google.common.collect.ImmutableMap;
import ic2.api.recipe.*;
import ic2.core.recipe.AdvRecipe;
import ic2.core.recipe.AdvShapelessRecipe;
import ic2.core.ref.FluidName;
import ic2.core.ref.ItemName;
import me.shepherd23333.projecteintegration.api.PEIApi;
import me.shepherd23333.projecteintegration.api.mappers.PEIMapper;
import me.shepherd23333.projecteintegration.api.plugin.APEIPlugin;
import me.shepherd23333.projecteintegration.api.plugin.OnlyIf;
import me.shepherd23333.projecteintegration.api.plugin.PEIPlugin;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;

@PEIPlugin("ic2")
@OnlyIf(versionEndsWith = "-ex112")
public class PluginIndustrialCraft extends APEIPlugin {
    private static Function<String, Item> find = reg -> ForgeRegistries.ITEMS.getValue(new ResourceLocation("ic2", reg));
    static Item resourceBlock = find.apply("resource");
    static Item nuclear = find.apply("nuclear");
    static Item dust = find.apply("dust");
    static Item scaffold = find.apply("scaffold");
    static Item wall = find.apply("wall");
    Item misc_resource = find.apply("misc_resource");

    @Override
    public void setup() {
        addEMC(new ItemStack(nuclear, 1, 1), 4096);

        addMapper(new AdvanceRecipeMapper());
        addMapper(new BasicMachineMapper(Recipes.compressor, "Compressor"));
        addMapper(new BasicMachineMapper(Recipes.extractor, "Extractor"));
        addMapper(new BasicMachineMapper(Recipes.macerator, "Macerator"));
        addMapper(new BasicMachineMapper(Recipes.blastfurnace, "Blast Furnace"));
        addMapper(new BasicMachineMapper(Recipes.blockcutter, "Block Cutter"));
        addMapper(new BasicMachineMapper(Recipes.centrifuge, "Centrifuge"));
        addMapper(new BasicMachineMapper(Recipes.metalformerCutting, "Metal Former Cutting"));
        addMapper(new BasicMachineMapper(Recipes.metalformerExtruding, "Metal Former Extruding"));
        addMapper(new BasicMachineMapper(Recipes.metalformerRolling, "Metal Former Rolling"));
        addMapper(new BasicMachineMapper(Recipes.oreWashing, "Ore Washing"));
        addMapper(new CannerBottleMapper());
        addMapper(new CannerEnrichMapper());
        addMapper(new misc());
        addMapper(new ElectrolyzerMapper());
        addMapper(new FermenterMapper());
    }

    private static class misc extends PEIMapper {
        public misc() {
            super("Misc");
        }

        @Override
        public void setup() {
            Fluid cf = FluidName.construction_foam.getInstance();
            addRecipe(new ItemStack(resourceBlock, 1, 11),
                    new ItemStack(scaffold, 1, 2), new FluidStack(cf, 100)
            );
            addRecipe(new ItemStack(wall, 1, 7), new FluidStack(cf, 100));
        }
    }

    private static class AdvanceRecipeMapper extends PEIMapper {
        public AdvanceRecipeMapper() {
            super("Advance Recipe");
        }

        @Override
        public void setup() {
            for (IRecipe recipe : CraftingManager.REGISTRY) {
                if (recipe instanceof AdvRecipe || recipe instanceof AdvShapelessRecipe) {
                    ItemStack output = recipe.getRecipeOutput();
                    if (output.isEmpty() || output.getItem().getRegistryName().getPath().equals("pipe"))
                        continue;
                    addRecipe(recipe);
                }
            }
        }
    }

    private static class BasicMachineMapper extends PEIMapper {
        private final IBasicMachineRecipeManager manager;

        public BasicMachineMapper(IBasicMachineRecipeManager manager, String name) {
            super(name);
            this.manager = manager;
            switch (name) {
                case "Compressor":
                    Item ec = find.apply("energy_crystal");
                    addRecipe(new ItemStack(ec, 1), new ItemStack(dust, 9 * 26, 6));
                case "Centrifuge":
                    addRecipe(new ItemStack(nuclear, 1, 11), ItemName.uranium_fuel_rod.getItemStack());
                    addRecipe(new ItemStack(nuclear, 1, 12), ItemName.dual_uranium_fuel_rod.getItemStack());
                    addRecipe(new ItemStack(nuclear, 1, 13), ItemName.quad_uranium_fuel_rod.getItemStack());
                    addRecipe(new ItemStack(nuclear, 1, 14), ItemName.mox_fuel_rod.getItemStack());
                    addRecipe(new ItemStack(nuclear, 1, 15), ItemName.dual_mox_fuel_rod.getItemStack());
                    addRecipe(new ItemStack(nuclear, 1, 16), ItemName.quad_mox_fuel_rod.getItemStack());
            }
        }

        @Override
        public void setup() {
            for (MachineRecipe<IRecipeInput, Collection<ItemStack>> recipe : manager.getRecipes()) {
                //TODO: Multi-output Improvement
                if (recipe.getOutput().size() >= 1) {
                    ItemStack output = recipe.getOutput().stream().findFirst().orElse(ItemStack.EMPTY);
                    if (output.isEmpty())
                        continue;
                    PEIApi.debugLog("IC2 Recipe: {} from {}*{}", output, recipe.getInput().getInputs(),
                            recipe.getInput().getAmount());
                    addConversion(output,
                            ImmutableMap.of(PEIApi.getIngredient(recipe.getInput().getIngredient()), recipe.getInput().getAmount())
                    );
                }
            }
        }
    }

    private static class MachineMapper extends PEIMapper {
        private final IMachineRecipeManager<ItemStack, ItemStack, ItemStack> manager;

        public MachineMapper(IMachineRecipeManager<ItemStack, ItemStack, ItemStack> manager, String name) {
            super(name);
            this.manager = manager;
        }

        @Override
        public void setup() {
            manager.getRecipes();
        }
    }

    private static class ElectrolyzerMapper extends PEIMapper {
        public ElectrolyzerMapper() {
            super("Electrolyzer");
        }

        @Override
        public void setup() {
            Recipes.electrolyzer.getRecipeMap().forEach((fluidName, recipe) -> {
                // Skip invalid fluid
                if (FluidRegistry.getFluid(fluidName) == null)
                    return;

                List<Object> outputs = new ArrayList<>(recipe.outputs.length);
                for (IElectrolyzerRecipeManager.ElectrolyzerOutput output : recipe.outputs) {
                    // Skip invalid fluid
                    if (FluidRegistry.getFluid(output.fluidName) == null)
                        continue;
                    outputs.add(new FluidStack(FluidRegistry.getFluid(output.fluidName), output.fluidAmount));
                }
                addRecipe(outputs, new FluidStack(FluidRegistry.getFluid(fluidName), recipe.inputAmount));
            });
        }
    }

    private static class FermenterMapper extends PEIMapper {
        public FermenterMapper() {
            super("Fermenter");
        }

        @Override
        public void setup() {
            Recipes.fermenter.getRecipeMap().forEach((fluidName, recipe) -> {
                // Skip invalid fluids
                if (FluidRegistry.getFluid(fluidName) == null || FluidRegistry.getFluid(recipe.output) == null)
                    return;
                addRecipe(new FluidStack(FluidRegistry.getFluid(recipe.output), recipe.outputAmount),
                        new FluidStack(FluidRegistry.getFluid(fluidName), recipe.inputAmount)
                );
            });
        }
    }

    private static class CannerBottleMapper extends PEIMapper {
        public CannerBottleMapper() {
            super("Canner(Bottle)");
        }

        @Override
        public void setup() {
            for (MachineRecipe<ICannerBottleRecipeManager.Input, ItemStack> recipe : Recipes.cannerBottle.getRecipes()) {
                /*if(recipe.getOutput().getItem()==find.apply("filled_tin_can"))
                    continue;*/
                IRecipeInput container = recipe.getInput().container, fill = recipe.getInput().fill;
                addConversion(recipe.getOutput(), ImmutableMap.of(
                        PEIApi.getIngredient(container.getIngredient()), container.getAmount(),
                        PEIApi.getIngredient(fill.getIngredient()), fill.getAmount()
                ));
            }
        }
    }

    private static class CannerEnrichMapper extends PEIMapper {
        public CannerEnrichMapper() {
            super("Canner(Enrich)");
            addRecipe(new FluidStack(FluidName.distilled_water.getInstance(), 1000),
                    new FluidStack(FluidRegistry.WATER, 1000)
            );
        }

        @Override
        public void setup() {
            for (MachineRecipe<ICannerEnrichRecipeManager.Input, FluidStack> recipe : Recipes.cannerEnrich.getRecipes()) {
                FluidStack fluid = recipe.getInput().fluid;
                IRecipeInput additive = recipe.getInput().additive;
                addConversion(recipe.getOutput(), ImmutableMap.of(
                        fluid.copy(), 1,
                        PEIApi.getIngredient(additive.getIngredient()), additive.getAmount()
                ));
            }
        }
    }
}
