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

package me.shepherd23333.projecteintegration.plugins.misc;

import com.google.common.collect.ImmutableMap;
import com.lh_lshen.mcbbs.huajiage.config.ConfigHuaji;
import com.lh_lshen.mcbbs.huajiage.init.loaders.ItemLoader;
import com.lh_lshen.mcbbs.huajiage.recipelist.HuajiPolyRecipeList;
import com.lh_lshen.mcbbs.huajiage.recipelist.HuajiRecipeList;
import me.shepherd23333.projecteintegration.api.mappers.PEIMapper;
import me.shepherd23333.projecteintegration.api.plugin.APEIPlugin;
import me.shepherd23333.projecteintegration.api.plugin.PEIPlugin;
import net.minecraft.item.ItemStack;

import java.util.Map;

@PEIPlugin("huajiage")
public class PluginHuajiAge2 extends APEIPlugin {
    @Override
    public void setup() {
        addEMC(ItemLoader.huaji, 2048);
        addEMC(ItemLoader.disc, 131072);
        addEMC(ItemLoader.orgaRequiem, 131072);

        addMapper(new BlenderMapper());
        addMapper(new PolyMapper());
    }

    private static class BlenderMapper extends PEIMapper {
        public BlenderMapper() {
            super("Blender");
        }

        @Override
        public void setup() {
            for (Map.Entry<ItemStack, ItemStack> recipe : HuajiRecipeList.instance().getSmeltingList().entrySet())
                addRecipe(recipe.getValue(), recipe.getKey(), new ItemStack(ItemLoader.huaji, 1));
        }
    }

    private static class PolyMapper extends PEIMapper {
        public PolyMapper() {
            super("Polyfurnace");
        }

        @Override
        public void setup() {
            Map<ItemStack, Integer> points = HuajiPolyRecipeList.instance().getPointList();
            for (Map.Entry<ItemStack, ItemStack> recipe : HuajiPolyRecipeList.instance().getSmeltingList().entrySet()) {
                ItemStack input = recipe.getKey(), output = recipe.getValue();
                int point = points.get(input);
                if (point > 0)
                    addConversion(output, ImmutableMap.of(input, (int) Math.ceil(1.0 * ConfigHuaji.Huaji.point_star / point)));
            }
        }
    }
}
