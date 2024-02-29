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

package me.shepherd23333.projecteintegration.plugins.crafting;

import me.shepherd23333.projecteintegration.api.mappers.PEIMapper;
import me.shepherd23333.projecteintegration.api.plugin.APEIPlugin;
import me.shepherd23333.projecteintegration.api.plugin.PEIPlugin;
import net.minecraft.item.ItemStack;
import team.chisel.api.carving.CarvingUtils;
import team.chisel.api.carving.ICarvingVariation;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@PEIPlugin("chisel")
public class PluginChisel extends APEIPlugin {
    @Override
    public void setup() {
        assert CarvingUtils.getChiselRegistry() != null;
        for (String group : CarvingUtils.getChiselRegistry().getSortedGroupNames()) {
            addMapper(new GroupMapper(group));
        }
    }

    private static class GroupMapper extends PEIMapper {
        private final String group;

        public GroupMapper(String name) {
            super("Group('" + name + "')");
            group = name;
        }

        @Override
        public void setup() {
            assert CarvingUtils.getChiselRegistry() != null;
            List<ICarvingVariation> variations = Objects.requireNonNull(CarvingUtils.getChiselRegistry().getGroup(group)).getVariations();
            ArrayList<ItemStack> variants = new ArrayList<>(variations.size());
            variations.forEach(iCarvingVariation -> variants.add(iCarvingVariation.getStack()));
            ItemStack primary_item = variants.remove(0);
            for (ItemStack variation : variants) {
                addRecipe(variation, primary_item);
            }
        }
    }
}
