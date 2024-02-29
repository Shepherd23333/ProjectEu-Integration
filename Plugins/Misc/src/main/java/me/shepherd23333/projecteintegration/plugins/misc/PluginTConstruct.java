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
package me.shepherd23333.projecteintegration.plugins.misc;

import me.shepherd23333.projecteintegration.api.internal.lists.InputList;
import me.shepherd23333.projecteintegration.api.mappers.PEIMapper;
import me.shepherd23333.projecteintegration.api.plugin.APEIPlugin;
import me.shepherd23333.projecteintegration.api.plugin.PEIPlugin;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import slimeknights.tconstruct.library.TinkerRegistry;
import slimeknights.tconstruct.library.smeltery.CastingRecipe;
import slimeknights.tconstruct.library.smeltery.ICastingRecipe;
import slimeknights.tconstruct.library.smeltery.MeltingRecipe;
import slimeknights.tconstruct.library.tools.IToolPart;
import slimeknights.tconstruct.library.tools.ToolPart;
import slimeknights.tconstruct.shared.TinkerCommons;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@PEIPlugin("tconstruct")
public class PluginTConstruct extends APEIPlugin {
    static Set<ResourceLocation> blacklist = new HashSet<>();

    @Override
    public void setup() {
        String[] names = {
                "shard", "sharpening_kit",/*"pick_head","shovel_head","axe_head","broad_axe_head","sword_blade",
                "large_sword_blade","hammer_head","excavator_head","kama_head","scythe_head","pan_head","sign_head",
                "tool_rod","tough_tool_rod","binding","tough_binding","wide_guard","hand_guard","cross_guard",
                "large_plate","knife_blade","bow_limb","bow_string","arrow_head","arrow_shaft","fletching",*/
                "bolt_core"
        };
        for (IToolPart itp : TinkerRegistry.getToolParts())
            blacklist.add(((ToolPart) itp).getRegistryName());
        for (String name : names)
            blacklist.add(new ResourceLocation("tconstruct", name));
        blacklist.add(new ResourceLocation("tconevo:part_arcane_focus"));

        addEMC("boneWithered", 288);
        addEMC(TinkerCommons.matMendingMoss, 10);
        addEMC("blockSlimeDirt", 1);

        addMapper(new AlloyMapper());
        addMapper(new CastingMapper(CastingMapper.CastingType.BASIN));
        addMapper(new DryingMapper());
        addMapper(new MeltingMapper());
        addMapper(new CastingMapper(CastingMapper.CastingType.TABLE));
    }

    private static class AlloyMapper extends PEIMapper {
        public AlloyMapper() {
            super("Alloy", "Tinkers Smelter Alloying recipe support");
        }

        @Override
        public void setup() {
            TinkerRegistry.getAlloys().forEach(r -> addRecipe(r.getResult(), r.getFluids()));
        }
    }

    private static class CastingMapper extends PEIMapper {
        private final CastingType castingType;

        public CastingMapper(CastingType castingType) {
            super(castingType == CastingType.BASIN ? "Basin" : "Table");
            this.castingType = castingType;
        }

        @Override
        public void setup() {
            List<ICastingRecipe> recipes;
            switch (castingType) {
                case BASIN:
                    recipes = TinkerRegistry.getAllBasinCastingRecipes();
                    break;
                case TABLE:
                    recipes = TinkerRegistry.getAllTableCastingRecipes();
                    break;
                default:
                    throw new IllegalStateException("Unexpected value: " + castingType);
            }
            for (ICastingRecipe iRecipe : recipes) {
                if (iRecipe instanceof CastingRecipe) {
                    CastingRecipe recipe = (CastingRecipe) iRecipe;
                    if (!blacklist.contains(recipe.getResult().getItem().getRegistryName()))
                        addRecipe(recipe.getResult(), recipe.getFluid(),
                                recipe.consumesCast() ? new InputList<>(recipe.cast.getInputs()) : null);
                }
            }
        }

        public enum CastingType {
            BASIN,
            TABLE
        }
    }

    private static class DryingMapper extends PEIMapper {
        public DryingMapper() {
            super("Drying");
        }

        @Override
        public void setup() {
            TinkerRegistry.getAllDryingRecipes().forEach(r -> addRecipe(r.getResult(), new InputList<>(r.input.getInputs())));
        }
    }

    private static class MeltingMapper extends PEIMapper {
        public MeltingMapper() {
            super("Melting");
        }

        @Override
        public void setup() {
            //TinkerRegistry.getAllMeltingRecipies().forEach(r -> addRecipe(r.getResult(), new InputList<>(r.input.getInputs())));
            for (MeltingRecipe recipe : TinkerRegistry.getAllMeltingRecipies()) {
                List<ItemStack> inputs = new ArrayList<>(recipe.input.getInputs()), invalids = new ArrayList<>();
                //boolean flag = true;
                for (ItemStack input : inputs)
                    if (blacklist.contains(input.getItem().getRegistryName()))
                        invalids.add(input);
                inputs.removeAll(invalids);
                if (!inputs.isEmpty())
                    addRecipe(recipe.getResult(), new InputList<>(inputs));
            }
        }
    }
}
