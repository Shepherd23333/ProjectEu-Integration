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

import com.brandon3055.draconicevolution.DEFeatures;
import com.brandon3055.draconicevolution.api.fusioncrafting.FusionRecipeAPI;
import com.brandon3055.draconicevolution.api.fusioncrafting.IFusionRecipe;
import com.brandon3055.draconicevolution.api.fusioncrafting.SimpleFusionRecipe;
import me.shepherd23333.projecteintegration.api.mappers.PEIMapper;
import me.shepherd23333.projecteintegration.api.plugin.APEIPlugin;
import me.shepherd23333.projecteintegration.api.plugin.PEIPlugin;
import net.minecraft.item.ItemStack;

import java.util.List;

@PEIPlugin("draconicevolution")
public class PluginDraconicEvolution extends APEIPlugin {
    @Override
    public void setup() {
        addEMC(DEFeatures.draconiumDust, 8192);
        addEMC(DEFeatures.dragonHeart, 262144);
        addEMC(DEFeatures.chaosShard, 1024000);

        addMapper(new FusionMapper());
    }

    private static class FusionMapper extends PEIMapper {
        public FusionMapper() {
            super("fusion", "Enable mapper for Draconic Evolution Fusion Crafting?");
        }

        @Override
        public void setup() {
            for (IFusionRecipe recipe : FusionRecipeAPI.getRecipes()) {
                if (recipe instanceof SimpleFusionRecipe) {
                    ItemStack catalyst = recipe.getRecipeCatalyst();
                    List<Object> inputs = recipe.getRecipeIngredients();
                    inputs.add(catalyst);
                    addRecipe(recipe.getRecipeOutput(catalyst), inputs);
                }
            }
        }
    }
}
