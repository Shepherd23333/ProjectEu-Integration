package me.shepherd23333.projecteintegration.plugins.tech;

import com.rwtema.extrautils2.api.resonator.IResonatorRecipe;
import com.rwtema.extrautils2.backend.entries.XU2Entries;
import com.rwtema.extrautils2.compatibility.XUShapedRecipe;
import com.rwtema.extrautils2.compatibility.XUShapelessRecipe;
import com.rwtema.extrautils2.crafting.PlayerSpecificCrafting;
import com.rwtema.extrautils2.items.ItemIngredients;
import com.rwtema.extrautils2.tile.TileResonator;
import me.shepherd23333.projecteintegration.api.mappers.PEIMapper;
import me.shepherd23333.projecteintegration.api.plugin.APEIPlugin;
import me.shepherd23333.projecteintegration.api.plugin.PEIPlugin;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@PEIPlugin("extrautils2")
public class PluginExtraUtilities extends APEIPlugin {
    public static final Map<List<ItemStack>, ItemStack> ENCHANTERRecipes = new HashMap<>();
    public static final Map<ItemStack, List<Object>> CRUSHERRecipes = new HashMap<>();

    @Override
    public void setup() {
        addEMC(ItemIngredients.Type.EVIL_DROP.newStack(1), 128);
        addEMC(XU2Entries.blockEnderLilly.newStack(1), 32);
        addEMC(XU2Entries.blockRedOrchid.newStack(1), 32);

        addMapper(new XURecipeMapper());
        addMapper(new ResonatorMapper());
        addMapper(new EnchanterMapper());
        addMapper(new CrusherMapper());
    }

    private static class XURecipeMapper extends PEIMapper {
        public XURecipeMapper() {
            super("XURecipe", "Add special crafting recipe.");
        }

        @Override
        public void setup() {
            //I could only achieve this now...
            addRecipe(XU2Entries.sunCrystal.newStack(1), Items.DIAMOND, new ItemStack(Items.GLOWSTONE_DUST, 4));
            addRecipe(XU2Entries.wateringCan.newStack(1), Items.BOWL, new ItemStack(Blocks.STONE, 4));

            for (IRecipe recipe : CraftingManager.REGISTRY) {
                if (recipe instanceof PlayerSpecificCrafting)
                    addRecipe(((PlayerSpecificCrafting) recipe).recipe);
                else if (recipe instanceof XUShapedRecipe || recipe instanceof XUShapelessRecipe)
                    addRecipe(recipe);
            }
        }
    }

    private static class ResonatorMapper extends PEIMapper {
        public ResonatorMapper() {
            super("Resonator");
        }

        @Override
        public void setup() {
            for (IResonatorRecipe recipe : TileResonator.resonatorRecipes)
                addRecipe(recipe.getOutput(), recipe.getInputs());
        }
    }

    private static class EnchanterMapper extends PEIMapper {
        public EnchanterMapper() {
            super("Enchanter");
        }

        @Override
        public void setup() {
            for (Map.Entry<List<ItemStack>, ItemStack> recipe : ENCHANTERRecipes.entrySet())
                addRecipe(recipe.getValue(), recipe.getKey());
        }
    }

    private static class CrusherMapper extends PEIMapper {
        public CrusherMapper() {
            super("Crusher");
        }

        @Override
        public void setup() {
            for (Map.Entry<ItemStack, List<Object>> recipe : CRUSHERRecipes.entrySet())
                addRecipe(recipe.getValue(), recipe.getKey());
        }
    }
}
