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
import me.shepherd23333.projecte.api.ProjectEAPI;
import me.shepherd23333.projecte.api.proxy.IConversionProxy;
import me.shepherd23333.projecte.emc.IngredientMap;
import me.shepherd23333.projecteintegration.api.PEIApi;
import me.shepherd23333.projecteintegration.api.mappers.PEIMapper;
import me.shepherd23333.projecteintegration.api.plugin.APEIPlugin;
import me.shepherd23333.projecteintegration.api.plugin.PEIPlugin;
import me.shepherd23333.projecteintegration.api.utils.IngredientHandler;
import mekanism.api.gas.Gas;
import mekanism.api.gas.GasRegistry;
import mekanism.api.gas.GasStack;
import mekanism.api.infuse.InfuseObject;
import mekanism.api.infuse.InfuseRegistry;
import mekanism.api.infuse.InfuseType;
import mekanism.common.InfuseStorage;
import mekanism.common.MekanismFluids;
import mekanism.common.MekanismItems;
import mekanism.common.block.states.BlockStateMachine.MachineType;
import mekanism.common.recipe.RecipeHandler.Recipe;
import mekanism.common.recipe.inputs.PressurizedInput;
import mekanism.common.recipe.machines.*;
import mekanism.common.recipe.outputs.ChanceOutput;
import mekanism.common.recipe.outputs.PressurizedOutput;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@PEIPlugin("mekanism")
public class PluginMekanism extends APEIPlugin {
    private static final Map<Gas, Object> GAS_MAP = new HashMap<>();
    private static final Map<InfuseType, Object> INFUSE_MAP = new HashMap<>();

    @Override
    public void setup() {
        PEIApi.addCachedMap(GAS_MAP);
        PEIApi.addCachedMap(INFUSE_MAP);
        for (Gas gas : GasRegistry.getRegisteredGasses()) {
            Object obj = new Object();
            GAS_MAP.put(gas, obj);

            // For now, don't add EMC to gases. Gas should get EMC from inherited items
            // addEMC(gas.getName(), obj, 1, "EMC for Gas: " + gas.getName());
        }

        IConversionProxy proxy = ProjectEAPI.getConversionProxy();
        for (Map.Entry<ItemStack, InfuseObject> entry : InfuseRegistry.getObjectMap().entrySet()) {
            ItemStack item = entry.getKey();
            InfuseObject infuse = entry.getValue();

            Object obj = new Object();
            if (INFUSE_MAP.containsKey(infuse.type))
                obj = INFUSE_MAP.get(infuse.type);
            else
                INFUSE_MAP.put(infuse.type, obj);

            proxy.addConversion(infuse.stored, obj, ImmutableMap.of(item, item.getCount()));
        }

        if (MachineType.ENRICHMENT_CHAMBER.isEnabled())
            addMapper(new BasicMachineMapper(Recipe.ENRICHMENT_CHAMBER));

        if (MachineType.CRUSHER.isEnabled())
            addMapper(new BasicMachineMapper(Recipe.CRUSHER));

        if (MachineType.COMBINER.isEnabled())
            addMapper(new DoubleMachineMapper(Recipe.COMBINER));

        if (MachineType.PURIFICATION_CHAMBER.isEnabled())
            addMapper(new AdvancedMachineMapper(Recipe.PURIFICATION_CHAMBER));

        if (MachineType.OSMIUM_COMPRESSOR.isEnabled())
            addMapper(new CompressorMapper());

        if (MachineType.CHEMICAL_INJECTION_CHAMBER.isEnabled())
            addMapper(new AdvancedMachineMapper(Recipe.CHEMICAL_INJECTION_CHAMBER));

        /*if (MachineType.PRECISION_SAWMILL.isEnabled())
            addMapper(new ChanceMachineMapper(Recipe.PRECISION_SAWMILL));*/

        if (MachineType.METALLURGIC_INFUSER.isEnabled())
            addMapper(new MetallurgicInfuserMapper());

        if (MachineType.CHEMICAL_CRYSTALLIZER.isEnabled())
            addMapper(new CrystallizerMapper());

        if (MachineType.CHEMICAL_DISSOLUTION_CHAMBER.isEnabled())
            addMapper(new DissolutionMapper());

        if (MachineType.CHEMICAL_INFUSER.isEnabled())
            addMapper(new ChemicalInfuserMapper());

        if (MachineType.CHEMICAL_OXIDIZER.isEnabled())
            addMapper(new ChemicalOxidizerMapper());

        if (MachineType.CHEMICAL_WASHER.isEnabled())
            addMapper(new ChemicalWasherMapper());

        if (MachineType.SOLAR_NEUTRON_ACTIVATOR.isEnabled())
            addMapper(new SolarNeutronActivatorMapper());

        if (MachineType.ELECTROLYTIC_SEPARATOR.isEnabled())
            addMapper(new ElectrolyticSeparatorMapper());

        addMapper(new ThermalEvaporationMapper());

        if (MachineType.PRESSURIZED_REACTION_CHAMBER.isEnabled())
            addMapper(new PressurizedReactionChamberMapper());

        if (MachineType.ROTARY_CONDENSENTRATOR.isEnabled())
            addMapper(new RotaryCondensentratorMapper());

        if (MachineType.ENERGIZED_SMELTER.isEnabled())
            addMapper(new BasicMachineMapper(Recipe.ENERGIZED_SMELTER));
    }

    private static class BasicMachineMapper extends PEIMapper {
        private final Recipe<?, ?, ? extends BasicMachineRecipe<?>> recipe_type;

        public BasicMachineMapper(Recipe<?, ?, ? extends BasicMachineRecipe<?>> recipe_type) {
            super(recipe_type.getRecipeName());
            this.recipe_type = recipe_type;
        }

        @Override
        public void setup() {
            recipe_type.get().values().forEach(r -> addRecipe(r.getOutput().output, r.getInput().ingredient));
        }
    }

    private static class DoubleMachineMapper extends PEIMapper {
        private final Recipe<?, ?, ? extends DoubleMachineRecipe<?>> recipe_type;

        public DoubleMachineMapper(Recipe<?, ?, ? extends DoubleMachineRecipe<?>> recipe_type) {
            super(recipe_type.getRecipeName());
            this.recipe_type = recipe_type;
        }

        @Override
        public void setup() {
            recipe_type.get().values().forEach(r -> addRecipe(r.getOutput().output, r.getInput().itemStack, r.getInput().extraStack));
        }
    }

    private static class ChanceMachineMapper extends PEIMapper {
        private final Recipe<?, ?, ? extends ChanceMachineRecipe<?>> recipe_type;

        public ChanceMachineMapper(Recipe<?, ?, ? extends ChanceMachineRecipe<?>> recipe_type) {
            super(recipe_type.getRecipeName());
            this.recipe_type = recipe_type;
        }

        @Override
        @SuppressWarnings("unchecked")
        public void setup() {
            for (ChanceMachineRecipe<?> recipe : recipe_type.get().values()) {
                ChanceOutput output = recipe.getOutput();
                List<Object> outputs = new ArrayList<>();
                if (output.hasPrimary())
                    outputs.add(output.primaryOutput);
                if (output.hasSecondary() && output.secondaryChance - 1 > -eps)
                    outputs.add(output.secondaryOutput);
                addRecipe(outputs, recipe.getInput().ingredient);
            }
        }
    }

    private static class ThermalEvaporationMapper extends PEIMapper {
        public ThermalEvaporationMapper() {
            super("Thermal Evaporation");
        }

        @Override
        public void setup() {
            Recipe.THERMAL_EVAPORATION_PLANT.get().values().forEach(r -> addRecipe(r.getOutput().output, r.getInput().ingredient));
        }
    }

    private static class AdvancedMachineMapper extends PEIMapper {
        private final Recipe<?, ?, ? extends AdvancedMachineRecipe<?>> recipe_type;

        public AdvancedMachineMapper(Recipe<?, ?, ? extends AdvancedMachineRecipe<?>> recipe_type) {
            super(recipe_type.getRecipeName());
            this.recipe_type = recipe_type;
        }

        @Override
        @SuppressWarnings("unchecked")
        public void setup() {
            for (AdvancedMachineRecipe<?> recipe : recipe_type.get().values()) {
                if (GAS_MAP.containsKey(recipe.getInput().gasType))
                    addRecipe(recipe.getOutput().output,
                            recipe.getInput().itemStack, GAS_MAP.get(recipe.getInput().gasType)
                    );
                else
                    addRecipe(recipe.getOutput().output, recipe.getInput().itemStack);
            }
        }
    }

    private static class CompressorMapper extends PEIMapper {
        public CompressorMapper() {
            super("OsmiumCompressor");
            addConversion(144, GAS_MAP.get(MekanismFluids.LiquidOsmium), ImmutableMap.of(
                    new ItemStack(MekanismItems.Ingot, 1, 1), 1
            ));
        }

        @Override
        public void setup() {
            for (AdvancedMachineRecipe<?> recipe : Recipe.OSMIUM_COMPRESSOR.get().values())
                addConversion(recipe.getOutput().output, ImmutableMap.of(
                        recipe.getInput().itemStack, 1,
                        GAS_MAP.get(recipe.getInput().gasType), 200
                ));
        }
    }

    private static class MetallurgicInfuserMapper extends PEIMapper {
        public MetallurgicInfuserMapper() {
            super("Metallurgic Infuser");
        }

        @Override
        public void setup() {
            for (MetallurgicInfuserRecipe recipe : Recipe.METALLURGIC_INFUSER.get().values()) {
                ItemStack output = recipe.getOutput().output.copy();
                ItemStack input = recipe.getInput().inputStack.copy();
                InfuseStorage infuse = recipe.getInput().infuse;

                if (INFUSE_MAP.containsKey(infuse.getType()))
                    addConversion(output, ImmutableMap.of(
                            input, input.getCount(),
                            INFUSE_MAP.get(infuse.getType()), infuse.getAmount()
                    ));
                else
                    addRecipe(output, input);
            }
        }
    }

    private static class CrystallizerMapper extends PEIMapper {
        public CrystallizerMapper() {
            super("Crystallizer");
        }

        @Override
        public void setup() {
            for (CrystallizerRecipe recipe : Recipe.CHEMICAL_CRYSTALLIZER.get().values()) {
                GasStack input = recipe.getInput().ingredient;

                if (GAS_MAP.containsKey(input.getGas()))
                    addConversion(recipe.getOutput().output.copy(),
                            ImmutableMap.of(GAS_MAP.get(input.getGas()), input.amount));
            }
        }
    }

    public abstract static class MekanismMapper extends PEIMapper {
        public MekanismMapper(String name) {
            super(name);
        }

        @Override
        protected void addRecipe(List<Object> outputs, Object... inputs) {
            if (outputs == null || outputs.size() <= 0 || inputs == null || inputs.length <= 0)
                return;

            IngredientHandler handler = new IngredientHandler();
            handler.addAll(inputs);

            for (Object output : outputs) {
                if (output instanceof GasStack) {
                    if (GAS_MAP.containsKey(((GasStack) output).getGas())) {
                        outputs.remove(output);

                    }
                }
            }

            addConversion(outputs, handler.getMap());
        }

        protected void addRecipe(GasStack output, Object... inputs) {
            if (!GAS_MAP.containsKey(output.getGas())) return;
            addRecipe(output.amount, GAS_MAP.get(output.getGas()), inputs);
        }

        protected void addConversion(GasStack output, GasStack... inputs) {
            if (inputs.length == 0)
                return;

            IngredientMap<Object> ingredients = new IngredientMap<>();

            for (GasStack input : inputs) {
                if (!GAS_MAP.containsKey(input.getGas()))
                    continue;

                ingredients.addIngredient(GAS_MAP.get(input.getGas()), input.amount);
            }

            addConversion(output, ingredients.getMap());
        }

        protected void addConversion(GasStack gas, Map<Object, Integer> map) {
            if (!GAS_MAP.containsKey(gas.getGas())) return;
            addConversion(gas.amount, GAS_MAP.get(gas.getGas()), map);
        }
    }

    private static class DissolutionMapper extends MekanismMapper {
        public DissolutionMapper() {
            super("Dissolution");
        }

        @Override
        public void setup() {
            Recipe.CHEMICAL_DISSOLUTION_CHAMBER.get().values().forEach(r -> addRecipe(r.getOutput().output, r.getInput().ingredient));
        }
    }

    private static class ChemicalInfuserMapper extends MekanismMapper {
        public ChemicalInfuserMapper() {
            super("Chemical Infuser");
        }

        @Override
        public void setup() {
            Recipe.CHEMICAL_INFUSER.get().values().forEach(r -> addConversion(r.getOutput().output,
                    r.getInput().leftGas, r.getInput().rightGas));
        }
    }

    private static class ChemicalOxidizerMapper extends MekanismMapper {
        public ChemicalOxidizerMapper() {
            super("Chemical Oxidizer");
        }

        @Override
        public void setup() {
            Recipe.CHEMICAL_OXIDIZER.get().values().forEach(r -> addRecipe(r.getOutput().output, r.getInput().ingredient));
        }
    }

    private static class ChemicalWasherMapper extends MekanismMapper {
        public ChemicalWasherMapper() {
            super("Chemical Washer");
        }

        @Override
        public void setup() {
            for (WasherRecipe recipe : Recipe.CHEMICAL_WASHER.get().values()) {
                GasStack gas_input = recipe.getInput().ingredient;
                FluidStack fluid_input = recipe.waterInput.ingredient;

                if (GAS_MAP.containsKey(gas_input.getGas()))
                    addConversion(recipe.getOutput().output, ImmutableMap.of(GAS_MAP.get(
                                    gas_input.getGas()), gas_input.amount,
                            fluid_input, fluid_input.amount
                    ));
            }
        }
    }

    private static class SolarNeutronActivatorMapper extends MekanismMapper {
        public SolarNeutronActivatorMapper() {
            super("Solar Neutron Activator");
        }

        @Override
        public void setup() {
            Recipe.SOLAR_NEUTRON_ACTIVATOR.get().values().forEach(r -> addConversion(r.getOutput().output, r.getInput().ingredient));
        }
    }

    private static class ElectrolyticSeparatorMapper extends MekanismMapper {
        public ElectrolyticSeparatorMapper() {
            super("Electrolytic Separator");
        }

        @Override
        public void setup() {
            for (SeparatorRecipe recipe : Recipe.ELECTROLYTIC_SEPARATOR.get().values()) {
                FluidStack input = recipe.recipeInput.ingredient;
                List<Object> outputs = new ArrayList<>(2);
                outputs.add(recipe.getOutput().leftGas);
                outputs.add(recipe.getOutput().rightGas);
                addRecipe(outputs, recipe.getInput().ingredient);
                addConversion(recipe.getOutput().leftGas, ImmutableMap.of(input, input.amount));
                addConversion(recipe.getOutput().rightGas, ImmutableMap.of(input, input.amount));
            }
        }
    }

    private static class PressurizedReactionChamberMapper extends MekanismMapper {
        public PressurizedReactionChamberMapper() {
            super("Pressurized Reaction Chamber");
        }

        @Override
        public void setup() {
            for (PressurizedRecipe recipe : Recipe.PRESSURIZED_REACTION_CHAMBER.get().values()) {
                PressurizedInput input = recipe.getInput();
                PressurizedOutput output = recipe.getOutput();

                Map<Object, Integer> ingredients = new HashMap<>();
                ingredients.put(input.getFluid(), 1);
                ingredients.put(input.getSolid(), 1);

                if (GAS_MAP.containsKey(input.getGas().getGas()))
                    ingredients.put(GAS_MAP.get(input.getGas().getGas()), input.getGas().amount);

                if (GAS_MAP.containsKey(output.getGasOutput().getGas())) {
                    addConversion(output.getGasOutput(), ingredients);
                }

                addConversion(output.getItemOutput(), ingredients);
            }
        }
    }

    private static class RotaryCondensentratorMapper extends MekanismMapper {
        public RotaryCondensentratorMapper() {
            super("Rotary Condensentrator");
        }

        @Override
        public void setup() {
            for (Gas gas : GasRegistry.getRegisteredGasses()) {
                if (gas.hasFluid() && GAS_MAP.containsKey(gas)) {
                    addConversion(new GasStack(gas, 1), ImmutableMap.of(gas.getFluid(), 1));
                    addConversion(new FluidStack(gas.getFluid(), 1), ImmutableMap.of(GAS_MAP.get(gas), 1));
                }
            }
        }
    }
}
