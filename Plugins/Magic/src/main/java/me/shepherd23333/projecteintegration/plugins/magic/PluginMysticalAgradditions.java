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

import com.blakebr0.mysticalagradditions.items.ModItems;
import com.blakebr0.mysticalagradditions.lib.CropType;
import me.shepherd23333.projecteintegration.api.mappers.PEIMapper;
import me.shepherd23333.projecteintegration.api.plugin.APEIPlugin;
import me.shepherd23333.projecteintegration.api.plugin.PEIPlugin;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.oredict.OreDictionary;

@PEIPlugin("mysticalagradditions")
public class PluginMysticalAgradditions extends APEIPlugin {
    @Override
    public void setup() {
        Item stuff = ModItems.itemStuff;
        addEMC(new ItemStack(stuff, 1, 1), 2048);
        addEMC(new ItemStack(stuff, 1, 3), 6144);

        addMapper(new misc());
    }

    private static class misc extends PEIMapper {
        public misc() {
            super("Misc", "Add support for essences");
        }

        @Override
        public void setup() {
            addRecipe(new ItemStack(CropType.Type.NETHER_STAR.getCrop(), 27), new ItemStack(Items.NETHER_STAR));
            addRecipe(new ItemStack(CropType.Type.DRAGON_EGG.getCrop(), 27), new ItemStack(Blocks.DRAGON_EGG));
            if (Loader.isModLoaded("draconicevolution"))
                addRecipe(new ItemStack(CropType.Type.AWAKENED_DRACONIUM.getCrop(), 3),
                        OreDictionary.getOres("nuggetDraconiumAwakened").get(0)
                );
            if (Loader.isModLoaded("avaritia"))
                addRecipe(new ItemStack(CropType.Type.NEUTRONIUM.getCrop(), 3),
                        OreDictionary.getOres("nuggetCosmicNeutronium").get(0)
                );
        }
    }
}
