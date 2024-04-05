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

import com.valkyrieofnight.et.m_resources.features.ETRBlocks;
import com.valkyrieofnight.et.m_resources.features.ETRItems;
import me.shepherd23333.projecteintegration.api.mappers.PEIMapper;
import me.shepherd23333.projecteintegration.api.plugin.APEIPlugin;
import me.shepherd23333.projecteintegration.api.plugin.PEIPlugin;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

@PEIPlugin("environmentaltech")
public class PluginEnvironmentalTech extends APEIPlugin {
    static float bonus;

    @Override
    public void setup() {
        bonus = config.getFloat("factor", category, 2.0F, 1.0F, 100.0F,
                "Used to calculate the EMC of higher level crystals");

        addEMC(new ItemStack(ETRBlocks.MICA), 64);

        addMapper(new misc());
    }

    private static class misc extends PEIMapper {
        public misc() {
            super("misc", "Add support for resources");
        }

        @Override
        public void setup() {
            Item crystal = ETRItems.LITHERITE_CRYSTAL;
            addRecipe(new ItemStack(ETRItems.LONSDALEITE_CRYSTAL), new ItemStack(crystal));
            addRecipe(new ItemStack(ETRItems.ERODIUM_CRYSTAL, 1000),
                    new ItemStack(crystal, (int) Math.ceil(bonus * 1000))
            );
            addRecipe(new ItemStack(ETRItems.KYRONITE_CRYSTAL, 1000),
                    new ItemStack(crystal, (int) Math.ceil(Math.pow(bonus, 2) * 1000))
            );
            addRecipe(new ItemStack(ETRItems.PLADIUM_CRYSTAL, 1000),
                    new ItemStack(crystal, (int) Math.ceil(Math.pow(bonus, 3) * 1000))
            );
            addRecipe(new ItemStack(ETRItems.IONITE_CRYSTAL, 1000),
                    new ItemStack(crystal, (int) Math.ceil(Math.pow(bonus, 4) * 1000))
            );
            addRecipe(new ItemStack(ETRItems.AETHIUM_CRYSTAL, 1000),
                    new ItemStack(crystal, (int) Math.ceil(Math.pow(bonus, 5) * 1000))
            );
        }
    }
}
