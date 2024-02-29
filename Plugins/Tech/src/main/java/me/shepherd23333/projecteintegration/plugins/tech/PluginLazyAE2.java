package me.shepherd23333.projecteintegration.plugins.tech;

import io.github.phantamanta44.libnine.LibNine;
import io.github.phantamanta44.libnine.recipe.IRecipeManager;
import io.github.phantamanta44.libnine.util.IDisplayableMatcher;
import io.github.phantamanta44.threng.recipe.AggRecipe;
import io.github.phantamanta44.threng.recipe.EnergizeRecipe;
import io.github.phantamanta44.threng.recipe.EtchRecipe;
import io.github.phantamanta44.threng.recipe.PurifyRecipe;
import me.shepherd23333.projecteintegration.api.mappers.PEIMapper;
import me.shepherd23333.projecteintegration.api.plugin.APEIPlugin;
import me.shepherd23333.projecteintegration.api.plugin.PEIPlugin;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

@PEIPlugin("threng")
public class PluginLazyAE2 extends APEIPlugin {
    static IRecipeManager recipeManager = LibNine.PROXY.getRecipeManager();

    @Override
    public void setup() {
        addMapper(new AggMapper());
        addMapper(new PurifyMapper());
        addMapper(new EtcherMapper());
        addMapper(new EnergizerMapper());
    }

    private static class AggMapper extends PEIMapper {
        public AggMapper() {
            super("Fluix Aggregator");
        }

        @Override
        public void setup() {
            for (AggRecipe recipe : recipeManager.getRecipeList(AggRecipe.class).recipes()) {
                List<ItemStack> inputs = new ArrayList<>();
                for (IDisplayableMatcher<ItemStack> input : recipe.input().getInputs())
                    inputs.add(input.getVisual());
                addRecipe(recipe.getOutput().getOutput(), inputs);
            }
        }
    }

    private static class PurifyMapper extends PEIMapper {
        public PurifyMapper() {
            super("Pulse Centrifuge");
        }

        @Override
        public void setup() {
            for (PurifyRecipe recipe : recipeManager.getRecipeList(PurifyRecipe.class).recipes())
                addRecipe(recipe.getOutput().getOutput(), recipe.input().getMatcher().getVisual());
        }
    }

    private static class EtcherMapper extends PEIMapper {
        public EtcherMapper() {
            super("Circuit Etcher");
        }

        @Override
        public void setup() {
            for (EtchRecipe recipe : recipeManager.getRecipeList(EtchRecipe.class).recipes()) {
                List<ItemStack> inputs = new ArrayList<>();
                for (IDisplayableMatcher<ItemStack> input : recipe.input().getInputs())
                    inputs.add(input.getVisual());
                addRecipe(recipe.getOutput().getOutput(), inputs);
            }
        }
    }

    private static class EnergizerMapper extends PEIMapper {
        public EnergizerMapper() {
            super("Crystal Energizer");
        }

        @Override
        public void setup() {
            for (EnergizeRecipe recipe : recipeManager.getRecipeList(EnergizeRecipe.class).recipes())
                addRecipe(recipe.getOutput().getOutput(), recipe.input().getMatcher().getVisual());
        }
    }
}
