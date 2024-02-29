package me.shepherd23333.projecteintegration.plugins.misc;

import com.google.common.collect.ImmutableMap;
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
        addEMC(ItemLoader.huaji, 4096);

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
                    addConversion(output, ImmutableMap.of(input, (int) Math.ceil(2187.0 / point)));
            }
        }
    }
}
