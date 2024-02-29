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

import com.blakebr0.mysticalagriculture.crafting.ReprocessorManager;
import com.blakebr0.mysticalagriculture.crafting.ReprocessorRecipe;
import com.blakebr0.mysticalagriculture.items.ItemChunk;
import com.blakebr0.mysticalagriculture.items.ModItems;
import me.shepherd23333.projecteintegration.api.mappers.PEIMapper;
import me.shepherd23333.projecteintegration.api.plugin.APEIPlugin;
import me.shepherd23333.projecteintegration.api.plugin.PEIPlugin;

@PEIPlugin("mysticalagriculture")
public class PluginMysticalAgriculture extends APEIPlugin {
    @Override
    public void setup() {
        addEMC("essenceInferium", 32);
        addEMC("essencePrudentium", 32 * 4);
        addEMC("essenceIntermedium", 32 * 16);
        addEMC("essenceSuperium", 32 * 64);
        addEMC("essenceSupremium", 32 * 256);
        addEMC("shardProsperity", 64);
        addEMC(ModItems.itemFertilizedEssence, 96);
        addEMC(ItemChunk.itemTier1MobChunk, 1024);

        addMapper(new SeedProcessorMapper());
    }

    private static class SeedProcessorMapper extends PEIMapper {
        public SeedProcessorMapper() {
            super("Seed Processor");
        }

        @Override
        public void setup() {
            for (ReprocessorRecipe recipe : ReprocessorManager.getRecipes()) {
                addRecipe(recipe.getOutput(), recipe.getInput()); //TODO: Improve, to support ore dict and other
            }
        }
    }
}
