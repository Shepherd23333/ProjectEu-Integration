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

import lumien.randomthings.block.ModBlocks;
import lumien.randomthings.item.ModItems;
import me.shepherd23333.projecteintegration.api.mappers.PEIMapper;
import me.shepherd23333.projecteintegration.api.plugin.APEIPlugin;
import me.shepherd23333.projecteintegration.api.plugin.PEIPlugin;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

@PEIPlugin("randomthings")
public class PluginRandomThing extends APEIPlugin {
    @Override
    public void setup() throws Exception {
        addEMC(new ItemStack(ModItems.ingredients, 1, 2), 64);
        addEMC(ModItems.beans, 64);
        addEMC(ModItems.summoningPendulum, 16384);
        addEMC(ModItems.biomeCrystal, 16384);

        addMapper(new misc());
    }

    private static class misc extends PEIMapper {
        public misc() {
            super("Misc", "Add support for resources");
        }

        @Override
        public void setup() {
            addRecipe(new ItemStack(ModBlocks.glowingMushroom), new ItemStack(Items.GLOWSTONE_DUST));
        }
    }
}
