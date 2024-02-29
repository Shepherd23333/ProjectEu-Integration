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
package me.shepherd23333.projecteintegration.plugins.magic;

import me.shepherd23333.projecteintegration.api.mappers.PEIMapper;
import me.shepherd23333.projecteintegration.api.plugin.APEIPlugin;
import me.shepherd23333.projecteintegration.api.plugin.PEIPlugin;
import teamroots.embers.recipe.RecipeRegistry;

@PEIPlugin("embers")
public class PluginEmbers extends APEIPlugin {
    @Override
    public void setup() {
        addMapper(new AlchemyMapper());
        addMapper(new MeltingMapper());
        addMapper(new FluidMixingMapper());
        addMapper(new StamperMapper());
    }

    private static class AlchemyMapper extends PEIMapper {
        public AlchemyMapper() {
            super("Alchemy");
        }

        @Override
        public void setup() {
            RecipeRegistry.alchemyRecipes.forEach(r -> addRecipe(r.result.copy(), r.centerInput, r.inputs.toArray()));
        }
    }

    private static class MeltingMapper extends PEIMapper {
        public MeltingMapper() {
            super("Melting");
        }

        @Override
        public void setup() {
            RecipeRegistry.meltingRecipes.forEach(r -> addRecipe(r.getFluid(), r.getStack()));
        }
    }

    private static class FluidMixingMapper extends PEIMapper {
        public FluidMixingMapper() {
            super("Fluid Mixing");
        }

        @Override
        public void setup() {
            RecipeRegistry.mixingRecipes.forEach(r -> addRecipe(r.output.copy(), r.inputs.toArray()));
        }
    }

    private static class StamperMapper extends PEIMapper {
        public StamperMapper() {
            super("Stamper");
        }

        @Override
        public void setup() {
            RecipeRegistry.stampingRecipes.forEach(r -> addRecipe(r.result.copy(), r.getStack(), r.getFluid()));
        }
    }
}
