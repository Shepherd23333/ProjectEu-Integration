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

import cofh.thermalexpansion.util.managers.machine.*;
import cofh.thermalfoundation.init.TFFluids;
import cofh.thermalfoundation.item.ItemMaterial;
import me.shepherd23333.projecteintegration.api.mappers.PEIMapper;
import me.shepherd23333.projecteintegration.api.plugin.APEIPlugin;
import me.shepherd23333.projecteintegration.api.plugin.PEIPlugin;
import net.minecraftforge.fluids.FluidStack;

import java.util.ArrayList;

@PEIPlugin("thermalexpansion")
public class PluginThermalExpansion extends APEIPlugin {
    @Override
    public void setup() {
        addEMC("dustSaltpeter", 32);
        addEMC(ItemMaterial.globRosin, 1);
        addEMC(ItemMaterial.globTar, 150);
        addEMC("crystalSlagRich", 32);
        addEMC("crystalCinnabar", 32);
        addEMC("crystalCrudeOil", 256);
        addEMC("crystalRedstone", 160);
        addEMC("crystalGlowstone", 384);
        addEMC("crystalEnder", 1024);
        addEMC("rodBlizz", 1536);
        addEMC("rodBlitz", 1536);
        addEMC("rodBasalz", 1536);

        //addMapper(new BrewerMapper());
        addMapper(new CentrifugeMapper());
        addMapper(new ChargerMapper());
        addMapper(new CompactorMapper());
        addMapper(new CrucibleMapper());
        addMapper(new EnchanterMapper());
        addMapper(new ExtruderMapper());
        addMapper(new FurnaceMapper());
        addMapper(new InsolatorMapper());
        addMapper(new PrecipitatorMapper());
        addMapper(new PulverizerMapper());
        addMapper(new RefineryMapper());
        addMapper(new SawmillMapper());
        addMapper(new SmelterMapper());
        addMapper(new TransposerMapper());
    }

    private static class BrewerMapper extends PEIMapper {
        public BrewerMapper() {
            super("Brewer");
        }

        @Override
        public void setup() {
            for (BrewerManager.BrewerRecipe recipe : BrewerManager.getRecipeList()) {
                addRecipe(recipe.getOutputFluid(), recipe.getInput(), recipe.getInputFluid());
            }
        }
    }

    private static class CentrifugeMapper extends PEIMapper {
        public CentrifugeMapper() {
            super("Centrifuge");
        }

        @Override
        public void setup() {
            for (CentrifugeManager.CentrifugeRecipe recipe : CentrifugeManager.getRecipeList()) {
                if (recipe.getFluid() == null)
                    continue; // TODO: Do this properly

                addRecipe(recipe.getFluid(), recipe.getInput());
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
                addRecipe(recipe.getOutput(), recipe.getInput());

                if (recipe.getCreosote() > 0)
                    addRecipe(new FluidStack(TFFluids.fluidCreosote, recipe.getCreosote()), recipe.getInput());
            }
        }
    }

    private static class InsolatorMapper extends PEIMapper {
        public InsolatorMapper() {
            super("Insolator");
        }

        @Override
        public void setup() {
            for (InsolatorManager.InsolatorRecipe recipe : InsolatorManager.getRecipeList()) {
                if (recipe.getType() == InsolatorManager.Type.TREE)
                    continue;

                if (recipe.hasFertilizer())
                    continue; // Most, if not all, recipes with fertilizer is dupe recipes, ignore those

                ArrayList<Object> outputs = new ArrayList<>();
                outputs.add(recipe.getPrimaryOutput());

                if (recipe.getSecondaryOutputChance() >= 100) {
                    outputs.add(recipe.getSecondaryOutput());
                }

                addRecipe(outputs, recipe.getPrimaryInput(), recipe.getSecondaryInput());
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
                outputs.add(recipe.getPrimaryOutput());

                if (recipe.getSecondaryOutputChance() >= 100) {
                    outputs.add(recipe.getSecondaryOutput());
                }

                addRecipe(outputs, recipe.getInput());
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
                if (recipe.getChance() != 100)
                    continue;

                ArrayList<Object> outputs = new ArrayList<>();
                outputs.add(recipe.getOutputFluid());
                outputs.add(recipe.getOutputItem());

                addRecipe(outputs, recipe.getInput());
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
                outputs.add(recipe.getPrimaryOutput());

                if (recipe.getSecondaryOutputChance() >= 100) {
                    outputs.add(recipe.getSecondaryOutput());
                }

                addRecipe(outputs, recipe.getInput());
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
                outputs.add(recipe.getPrimaryOutput());

                if (recipe.getSecondaryOutputChance() >= 100) {
                    outputs.add(recipe.getSecondaryOutput());
                }

                addRecipe(outputs, recipe.getPrimaryInput(), recipe.getSecondaryInput());
            }
        }
    }

    private static class TransposerMapper extends PEIMapper {
        public TransposerMapper() {
            super("Transposer");
        }

        @Override
        public void setup() {
            for (TransposerManager.TransposerRecipe recipe : TransposerManager.getExtractRecipeList()) {
                addRecipe(recipe.getFluid(), recipe.getInput()); // TODO: Make sure, this doesn't contain the bucket
            }

            for (TransposerManager.TransposerRecipe recipe : TransposerManager.getFillRecipeList()) {
                addRecipe(recipe.getOutput(), recipe.getInput(), recipe.getFluid());
            }
        }
    }
}
