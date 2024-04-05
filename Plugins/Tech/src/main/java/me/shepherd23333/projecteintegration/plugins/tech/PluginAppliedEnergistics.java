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

import appeng.api.AEApi;
import appeng.api.config.CondenserOutput;
import appeng.api.definitions.IDefinitions;
import appeng.api.definitions.IMaterials;
import appeng.api.features.IGrinderRecipe;
import appeng.api.features.IInscriberRecipe;
import appeng.api.features.InscriberProcessType;
import appeng.core.AEConfig;
import appeng.core.features.AEFeature;
import appeng.items.misc.ItemCrystalSeed;
import me.shepherd23333.projecte.emc.IngredientMap;
import me.shepherd23333.projecteintegration.api.mappers.PEIMapper;
import me.shepherd23333.projecteintegration.api.plugin.APEIPlugin;
import me.shepherd23333.projecteintegration.api.plugin.PEIPlugin;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@PEIPlugin("appliedenergistics2")
public class PluginAppliedEnergistics extends APEIPlugin {
    static IDefinitions def = AEApi.instance().definitions();

    @Override
    public void setup() {
        if (AEConfig.instance().isFeatureEnabled(AEFeature.SKY_STONE)) {
            Optional<Item> skyStone = def.blocks().skyStoneBlock().maybeItem();
            skyStone.ifPresent(item -> addEMC(item, 64));
        }

        if (AEConfig.instance().isFeatureEnabled(AEFeature.CONDENSER))
            addMapper(new CondenserMapper());
        if (AEConfig.instance().isFeatureEnabled(AEFeature.GRIND_STONE))
            addMapper(new GrindstoneMapper());
        addMapper(new GrowthMapper());
        if (AEConfig.instance().isFeatureEnabled(AEFeature.INSCRIBER))
            addMapper(new InscriberMapper());
    }

    private static class CondenserMapper extends PEIMapper {
        public CondenserMapper() {
            super("Condenser", "Adds condenser mapping for singularity and matter ball.\nUses cobblestone for conversion");
        }

        @Override
        public void setup() {
            Optional<ItemStack> singularity = def.materials().singularity().maybeStack(1),
                    qes = def.materials().qESingularity().maybeStack(2);
            singularity.ifPresent(itemStack1 -> {
                Map<Object, Integer> map = new HashMap<>();
                map.put(cobble.copy(), CondenserOutput.SINGULARITY.requiredPower);
                addConversion(itemStack1, map);
                qes.ifPresent(itemStack2 -> {
                    addRecipe(itemStack2,
                            itemStack1, OreDictionary.getOres("dustEnderPearl").get(0)
                    );
                });
            });
            def.materials().matterBall().maybeStack(1).ifPresent(itemStack -> {
                Map<Object, Integer> map = new HashMap<>();
                map.put(cobble.copy(), CondenserOutput.MATTER_BALLS.requiredPower);
                addConversion(itemStack, map);
            });
        }
    }

    private static class GrowthMapper extends PEIMapper {
        public GrowthMapper() {
            super("Fluid growing", "Adds seed growing and fluix crafting");
        }

        @Override
        public void setup() {
            IMaterials materials = def.materials();
            if (AEConfig.instance().isFeatureEnabled(AEFeature.IN_WORLD_FLUIX)) {
                ItemStack charged_certus = materials.certusQuartzCrystalCharged().maybeStack(1).orElse(ItemStack.EMPTY);

                if (!charged_certus.isEmpty()) {
                    materials.fluixCrystal().maybeStack(2).ifPresent(itemStack -> {
                        addRecipe(itemStack, new ItemStack(Items.QUARTZ), new ItemStack(Items.REDSTONE), charged_certus);
                    });
                }
            }
            if (AEConfig.instance().isFeatureEnabled(AEFeature.IN_WORLD_PURIFICATION)) {
                def.items().crystalSeed().maybeItem().ifPresent(crystal_seed -> {
                    materials.purifiedCertusQuartzCrystal().maybeStack(1).ifPresent(purified_certus -> {
                        addRecipe(purified_certus, new ItemStack(crystal_seed, 1, ItemCrystalSeed.CERTUS));
                    });
                    materials.purifiedNetherQuartzCrystal().maybeStack(1).ifPresent(purified_nether -> {
                        addRecipe(purified_nether, new ItemStack(crystal_seed, 1, ItemCrystalSeed.NETHER));
                    });
                    materials.purifiedFluixCrystal().maybeStack(1).ifPresent(purified_fluix -> {
                        addRecipe(purified_fluix, new ItemStack(crystal_seed, 1, ItemCrystalSeed.FLUIX));
                    });
                });
            }
        }
    }

    private static class InscriberMapper extends PEIMapper {
        public InscriberMapper() {
            super("Inscriber");
        }

        @Override
        public void setup() {
            for (IInscriberRecipe recipe : AEApi.instance().registries().inscriber().getRecipes()) {
                ItemStack output = recipe.getOutput();
                if (output.isEmpty()) continue;

                ItemStack input = recipe.getInputs().get(0);
                if (input == null || input.isEmpty())
                    continue;

                IngredientMap<Object> ingredients = new IngredientMap<>();

                ingredients.addIngredient(input, input.getCount());

                if (recipe.getProcessType() == InscriberProcessType.PRESS) {
                    Optional<ItemStack> input_top = recipe.getTopOptional();
                    if (input_top.isPresent() && input_top.get() != ItemStack.EMPTY)
                        ingredients.addIngredient(input_top.get(), input_top.get().getCount());

                    Optional<ItemStack> input_bottom = recipe.getBottomOptional();
                    if (input_bottom.isPresent() && input_bottom.get() != ItemStack.EMPTY)
                        ingredients.addIngredient(input_bottom.get(), input_bottom.get().getCount());
                }

                addConversion(output, ingredients.getMap());
            }
        }
    }

    private static class GrindstoneMapper extends PEIMapper {
        public GrindstoneMapper() {
            super("Grindstone");
        }

        @Override
        public void setup() {
            for (IGrinderRecipe recipe : AEApi.instance().registries().grinder().getRecipes()) {
                addRecipe(recipe.getOutput(), recipe.getInput());
            }
        }
    }
}
