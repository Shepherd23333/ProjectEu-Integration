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

package me.shepherd23333.projecteintegration.plugins.magic;

import com.croparia.mod.Init.ModItems;
import me.shepherd23333.projecteintegration.api.mappers.PEIMapper;
import me.shepherd23333.projecteintegration.api.plugin.APEIPlugin;
import me.shepherd23333.projecteintegration.api.plugin.PEIPlugin;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;

@PEIPlugin("croparia")
public class PluginCroparia extends APEIPlugin {
    @Override
    public void setup() {
        addEMC(ModItems.ELEMENTAL_FRUIT, 32768);
        addEMC("apricot", 64);
        addEMC("banana", 64);
        addEMC("cherry", 64);
        addEMC("kiwi", 64);
        addEMC("lemon", 64);
        addEMC("orange", 64);
        addEMC("pear", 64);

        addMapper(new FruitMapper());
        addMapper(new misc());
    }

    private static class misc extends PEIMapper {
        public misc() {
            super("misc", "Add support for resources");
        }

        @Override
        public void setup() {
            ItemStack meat = new ItemStack(Items.CHICKEN);
            addRecipe(new ItemStack(ModItems.CHICKEN_LEG), meat);
            addRecipe(new ItemStack(ModItems.ROAST_BEEF), meat);
            addRecipe(new ItemStack(ModItems.ROAST_PORC), meat);
            addRecipe(new ItemStack(ModItems.HORSE_MEAT), meat);
            addRecipe(new ItemStack(ModItems.SQUID_MEAT), meat);
        }
    }

    private static class FruitMapper extends PEIMapper {
        public FruitMapper() {
            super("Fruit", "Add conversions for fruits");
        }

        @Override
        public void setup() {
            for (IRecipe recipe : CraftingManager.REGISTRY) {
                ResourceLocation id = recipe.getRegistryName();
                if (id.getNamespace().equals("croparia")) {
                    NonNullList<Ingredient> inputs = recipe.getIngredients();
                    if (!inputs.isEmpty() && inputs.get(0).getMatchingStacks().length > 0) {
                        ItemStack input = inputs.get(0).getMatchingStacks()[0];
                        if (input.getTranslationKey().contains("croparia.fruit"))
                            addRecipe(input, recipe.getRecipeOutput());
                    }
                }
            }
        }
    }
}
