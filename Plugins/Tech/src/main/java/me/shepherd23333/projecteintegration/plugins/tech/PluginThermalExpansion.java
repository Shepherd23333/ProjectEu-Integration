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
package me.shepherd23333.projecteintegration.plugins.tech;

import cofh.thermalexpansion.util.managers.machine.*;
import cofh.thermalfoundation.init.TFFluids;
import cofh.thermalfoundation.item.ItemMaterial;
import com.google.common.collect.Sets;
import me.shepherd23333.projecteintegration.api.mappers.PEIMapper;
import me.shepherd23333.projecteintegration.api.plugin.APEIPlugin;
import me.shepherd23333.projecteintegration.api.plugin.PEIPlugin;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@PEIPlugin("thermalexpansion")
public class PluginThermalExpansion extends APEIPlugin {
    @Override
    public void setup() {
        //addEMC("dustSaltpeter", 32);
        addEMC(ItemMaterial.globRosin, 1);
        addEMC(ItemMaterial.globTar, 150);
        addEMC("crystalCinnabar", 32);
        addEMC("crystalCrudeOil", 256);
        addEMC("crystalRedstone", 160);
        addEMC("crystalGlowstone", 384);
        addEMC("crystalEnder", 1024);
        addEMC("rodBlizz", 1536);
        addEMC("rodBlitz", 1536);
        addEMC("rodBasalz", 1536);

        addMapper(new CentrifugeMapper());
        addMapper(new ChargerMapper());
        addMapper(new CompactorMapper());
        addMapper(new CrucibleMapper());
        addMapper(new EnchanterMapper());
        addMapper(new ExtruderMapper());
        addMapper(new FurnaceMapper());
        addMapper(new PrecipitatorMapper());
        addMapper(new PulverizerMapper());
        addMapper(new RefineryMapper());
        addMapper(new SawmillMapper());
        addMapper(new SmelterMapper());
        addMapper(new TransposerMapper());
    }

    private static class CentrifugeMapper extends PEIMapper {
        public CentrifugeMapper() {
            super("Centrifuge");
        }

        @Override
        public void setup() {
            for (CentrifugeManager.CentrifugeRecipe recipe : CentrifugeManager.getRecipeList()) {
                ArrayList<Object> outputs = new ArrayList<>();
                List<ItemStack> outs = recipe.getOutput();
                List<Integer> ch = recipe.getChance();
                for (int i = 0; i < outs.size(); i++) {
                    ItemStack out = outs.get(i).copy();
                    out.setCount(out.getCount() * ch.get(i));
                    outputs.add(out);
                }
                if (recipe.getFluid() != null) {
                    FluidStack f = recipe.getFluid().copy();
                    f.amount *= 100;
                    outputs.add(f);
                }
                if (!outputs.isEmpty()) {
                    ItemStack in = recipe.getInput().copy();
                    in.setCount(in.getCount() * 100);
                    addRecipe(outputs, in);
                }
            }
        }
    }

    private static class ChargerMapper extends PEIMapper {
        public ChargerMapper() {
            super("Charger");
        }

        @Override
        public void setup() {
            for (ChargerManager.ChargerRecipe recipe : ChargerManager.getRecipeList()) {
                addRecipe(recipe.getOutput(), recipe.getInput());
            }
        }
    }

    private static class CompactorMapper extends PEIMapper {
        public CompactorMapper() {
            super("Compactor");
        }

        @Override
        public void setup() {
            for (CompactorManager.Mode mode : CompactorManager.Mode.values()) {
                for (CompactorManager.CompactorRecipe recipe : CompactorManager.getRecipeList(mode)) {
                    addRecipe(recipe.getOutput(), recipe.getInput());
                }
            }
        }
    }

    private static class CrucibleMapper extends PEIMapper {
        public CrucibleMapper() {
            super("Crucible");
        }

        @Override
        public void setup() {
            for (CrucibleManager.CrucibleRecipe recipe : CrucibleManager.getRecipeList()) {
                addRecipe(recipe.getOutput(), recipe.getInput());
            }
        }
    }

    private static class EnchanterMapper extends PEIMapper {
        public EnchanterMapper() {
            super("Enchanter");
        }

        @Override
        public void setup() {
            for (EnchanterManager.EnchanterRecipe recipe : EnchanterManager.getRecipeList()) {
                addRecipe(recipe.getOutput(), recipe.getPrimaryInput(), recipe.getSecondaryInput(),
                        new FluidStack(TFFluids.fluidExperience, recipe.getExperience())
                );
            }
        }
    }

    private static class ExtruderMapper extends PEIMapper {
        public ExtruderMapper() {
            super("Extruder");
        }

        private void addRecipe(ExtruderManager.ExtruderRecipe recipe) {
            addRecipe(recipe.getOutput(), recipe.getInputHot(), recipe.getInputCold());
        }

        @Override
        public void setup() {
            for (ExtruderManager.ExtruderRecipe recipe : ExtruderManager.getRecipeList(true)) {
                addRecipe(recipe);
            }

            for (ExtruderManager.ExtruderRecipe recipe : ExtruderManager.getRecipeList(false)) {
                addRecipe(recipe);
            }
        }
    }

    private static class FurnaceMapper extends PEIMapper {
        public FurnaceMapper() {
            super("Furnace");
        }

        @Override
        public void setup() {
            for (FurnaceManager.FurnaceRecipe recipe : FurnaceManager.getRecipeList(false)) {
                addRecipe(recipe.getOutput(), recipe.getInput());
            }

            for (FurnaceManager.FurnaceRecipe recipe : FurnaceManager.getRecipeList(true)) {
                ArrayList<Object> outputs = new ArrayList<>();
                outputs.add(recipe.getOutput());
                if (recipe.getCreosote() > 0)
                    outputs.add(new FluidStack(TFFluids.fluidCreosote, recipe.getCreosote()));

                addRecipe(outputs, recipe.getInput());
            }
        }
    }

    private static class PrecipitatorMapper extends PEIMapper {
        public PrecipitatorMapper() {
            super("Precipitator");
        }

        @Override
        public void setup() {
            for (PrecipitatorManager.PrecipitatorRecipe recipe : PrecipitatorManager.getRecipeList()) {
                addRecipe(recipe.getOutput(), recipe.getInput());
            }
        }
    }

    private static class PulverizerMapper extends PEIMapper {
        public PulverizerMapper() {
            super("Pulverizer");
        }

        @Override
        public void setup() {
            for (PulverizerManager.PulverizerRecipe recipe : PulverizerManager.getRecipeList()) {
                ArrayList<Object> outputs = new ArrayList<>();
                ItemStack pri = recipe.getPrimaryOutput().copy(), sec = recipe.getSecondaryOutput().copy(),
                        in = recipe.getInput().copy();
                pri.setCount(pri.getCount() * 100);
                sec.setCount(sec.getCount() * recipe.getSecondaryOutputChance());
                in.setCount(in.getCount() * 100);
                outputs.add(pri);
                outputs.add(sec);

                addRecipe(outputs, in);
            }
        }
    }

    private static class RefineryMapper extends PEIMapper {
        public RefineryMapper() {
            super("Refinery");
        }

        @Override
        public void setup() {
            for (RefineryManager.RefineryRecipe recipe : RefineryManager.getRecipeList()) {
                ArrayList<Object> outputs = new ArrayList<>();
                FluidStack f = recipe.getOutputFluid().copy();
                f.amount *= 100;
                outputs.add(f);
                ItemStack out = recipe.getOutputItem().copy();
                out.setCount(out.getCount() * recipe.getChance());
                outputs.add(out);

                FluidStack in = recipe.getInput().copy();
                in.amount *= 100;

                addRecipe(outputs, in);
            }
        }
    }

    private static class SawmillMapper extends PEIMapper {
        public SawmillMapper() {
            super("Sawmill");
        }

        @Override
        public void setup() {
            for (SawmillManager.SawmillRecipe recipe : SawmillManager.getRecipeList()) {
                ArrayList<Object> outputs = new ArrayList<>();
                ItemStack pri = recipe.getPrimaryOutput().copy(), sec = recipe.getSecondaryOutput().copy(),
                        in = recipe.getInput().copy();
                pri.setCount(pri.getCount() * 100);
                sec.setCount(sec.getCount() * Math.min(100, recipe.getSecondaryOutputChance()));
                in.setCount(in.getCount() * 100);
                outputs.add(pri);
                outputs.add(sec);

                addRecipe(outputs, in);
            }
        }
    }

    private static class SmelterMapper extends PEIMapper {
        public SmelterMapper() {
            super("Smelter");
        }

        @Override
        public void setup() {
            for (SmelterManager.SmelterRecipe recipe : SmelterManager.getRecipeList()) {
                ArrayList<Object> outputs = new ArrayList<>();
                ItemStack pri = recipe.getPrimaryOutput().copy(), sec = recipe.getSecondaryOutput().copy(),
                        in1 = recipe.getPrimaryInput().copy(), in2 = recipe.getSecondaryInput().copy();
                pri.setCount(pri.getCount() * 100);
                sec.setCount(sec.getCount() * recipe.getSecondaryOutputChance());
                outputs.add(pri);
                outputs.add(sec);
                in1.setCount(in1.getCount() * 100);
                in2.setCount(in2.getCount() * 100);

                addRecipe(outputs, in1, in2);
            }
        }
    }

    private static class TransposerMapper extends PEIMapper {
        public TransposerMapper() {
            super("Transposer");
        }

        @Override
        public void setup() {
            Set<Item> blacklist = Sets.newHashSet(Items.ARROW, Items.GLASS_BOTTLE);
            for (TransposerManager.TransposerRecipe recipe : TransposerManager.getExtractRecipeList()) {
                ArrayList<Object> outputs = new ArrayList<>();
                outputs.add(recipe.getFluid());
                ItemStack out = recipe.getOutput();
                if (blacklist.contains(out.getItem()))
                    continue;
                outputs.add(out);
                addRecipe(outputs, recipe.getInput());
            }

            for (TransposerManager.TransposerRecipe recipe : TransposerManager.getFillRecipeList()) {
                ItemStack in = recipe.getInput();
                if (blacklist.contains(in.getItem()))
                    continue;
                addRecipe(recipe.getOutput(), in, recipe.getFluid());
            }
        }
    }
}
