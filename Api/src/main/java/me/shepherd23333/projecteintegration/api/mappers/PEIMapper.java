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

package me.shepherd23333.projecteintegration.api.mappers;

import me.shepherd23333.projecteintegration.api.PEIApi;
import me.shepherd23333.projecteintegration.api.internal.sized.SizedObject;
import me.shepherd23333.projecteintegration.api.utils.IngredientHandler;
import moze_intel.projecte.api.ProjectEAPI;
import moze_intel.projecte.api.proxy.IConversionProxy;
import moze_intel.projecte.emc.IngredientMap;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.Ingredient;
import net.minecraftforge.fluids.FluidStack;
import org.apache.commons.lang3.ClassUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * The abstract class for handle recipes.
 */
public abstract class PEIMapper {
    public final String name;
    public final String desc;
    public final boolean disabled_by_default;
    public final double eps = 1e-6;
    public final ItemStack cobble = new ItemStack(Blocks.COBBLESTONE);
    protected final IConversionProxy conversion_proxy;

    /**
     * Alias for {@code PEIMapper(name, 'default description')}
     *
     * @param name The name of the mapper
     */
    public PEIMapper(String name) {
        this(name, "Enable mapper for " + name + '?');
    }

    /**
     * Alias for {@code PEIMapper(name, description, false)}
     *
     * @param name        The name of the mapper
     * @param description The config comment
     */
    public PEIMapper(String name, String description) {
        this(name, description, false);
    }

    /**
     * Alias for {@code PEIMapper(name, 'default description', disabledByDefault)}
     *
     * @param name              The name of the mapper
     * @param disabledByDefault Should this mapper be disabled by default
     */
    public PEIMapper(String name, boolean disabledByDefault) {
        this(name, "Enable mapper for " + name + '?', disabledByDefault);
    }

    /**
     * @param name              The name of the mapper
     * @param description       The comment display by the config
     * @param disabledByDefault Should this mapper be disabled by default
     */
    protected PEIMapper(String name, String description, boolean disabledByDefault) {
        this.name = name;
        this.desc = description;
        this.disabled_by_default = disabledByDefault;
        this.conversion_proxy = ProjectEAPI.getConversionProxy();
    }

    /**
     * This setup should be calling {@link #addRecipe} and/or {@link #addConversion}
     */
    public abstract void setup();

    /**
     * Shortcut to converting an {@link IRecipe} into a conversion map
     *
     * @param recipe {@link IRecipe} The recipe to convert
     */
    protected void addRecipe(IRecipe recipe) {
        ItemStack output = recipe.getRecipeOutput();
        if (output.isEmpty())
            return;

        IngredientMap<Object> ingredients = new IngredientMap<>();

        for (Ingredient ingredient : recipe.getIngredients()) {
            if (ingredient == Ingredient.EMPTY)
                continue;

            ingredients.addIngredient(PEIApi.getIngredient(ingredient), 1);
        }

        addConversion(output, ingredients.getMap());
    }

    /**
     * A shortcut for turning an {@link ItemStack} into a conversion map
     *
     * @param output A {@link ItemStack} to convert into an output
     * @param inputs A list of objects to convert into inputs
     */
    protected void addRecipe(ItemStack output, Object... inputs) {
        if (output == null || output.isEmpty())
            return;

        addRecipe(output.getCount(), output.copy(), inputs);
    }

    /**
     * Shortcut for turning {@link FluidStack} into a conversion map
     *
     * @param output A {@link FluidStack} to turn into a conversion map
     * @param inputs A list of objects to convert into inputs
     */
    protected void addRecipe(FluidStack output, Object... inputs) {
        if (output == null || output.amount <= 0)
            return;

        addRecipe(output.amount, output.copy(), inputs);
    }

    /**
     * Shortcut for turning {@link SizedObject} into a conversion map
     *
     * @param output A sized object of any type and amount
     * @param inputs A list of objects to convert into inputs
     */
    protected void addRecipe(SizedObject<?> output, Object... inputs) {
        addRecipe(output.amount, output.object, inputs);
    }

    /**
     * We turn the inputs into a usable {@link Map} using {@link IngredientHandler}.
     * Using this map we call {@link #addConversion(int, Object, Map)} using output_amount,
     * output, inputs.
     *
     * @param output_amount The amount of output there will be
     * @param output        The object that will be converted to
     * @param inputs        The inputs that will turn into the output
     */
    protected void addRecipe(int output_amount, Object output, Object... inputs) {
        if (output_amount <= 0 || output == null || inputs == null || inputs.length <= 0)
            return;

        IngredientHandler handler = new IngredientHandler();
        handler.addAll(inputs);

        addConversion(output_amount, output, handler.getMap());
    }

    /**
     * Alias for {@link #addConversion(List, Map)} were we convert the inputs
     * to a {@link Map} using {@link IngredientHandler}
     *
     * @param outputs The outputs that the inputs will be converted to
     * @param inputs  The inputs that will converted to the outputs
     */
    protected void addRecipe(List<Object> outputs, Object... inputs) {
        if (outputs == null || outputs.size() <= 0 || inputs == null || inputs.length <= 0)
            return;

        IngredientHandler handler = new IngredientHandler();
        handler.addAll(inputs);

        addConversion(outputs, handler.getMap());
    }

    /**
     * TODO: Make actual logging and error attempts
     *
     * @param output Outputs that will EMC shared across them
     * @param inputs Inputs that will have EMC taken from
     */
    protected void addConversion(List<Object> output, Map<Object, Integer> inputs) {
        final long startTime = System.currentTimeMillis();
        IngredientMap<Object> out_ing = new IngredientMap<>();

        for (Object out : output) {
            if (Objects.isNull(out)) {
                PEIApi.debugLog("Null Output Item");
                continue;
            }
            if (out instanceof ItemStack) {
                ItemStack item = (ItemStack) out;
                if (item.isEmpty()) {
                    PEIApi.debugLog("Empty ItemStack Output");
                    continue;
                }
                PEIApi.debugLog("ItemStack Output: {}", out);
                out_ing.addIngredient(out, item.getCount());
            } else if (out instanceof FluidStack) {
                FluidStack fluid = (FluidStack) out;
                if (fluid.amount <= 0) {
                    PEIApi.debugLog("Empty FluidStack Output");
                    continue;
                }
                PEIApi.debugLog("FluidStack Output: {}", out);
                out_ing.addIngredient(out, fluid.amount);
            } else if (out instanceof Item || out instanceof Block || out.getClass().equals(Object.class)) {
                PEIApi.debugLog("Output: Object||Item||Block");
                out_ing.addIngredient(out, 1);
            } else if (out instanceof SizedObject) {
                // TODO: This obviously
            } else {
                PEIApi.LOGGER.warn("Invalid Multi-Output item: {}:({})", out, out.getClass());
            }
        }

        final Map<Object, Integer> outputs = out_ing.getMap();

        if (outputs.isEmpty()) {
            PEIApi.LOGGER.warn("Multi-Output: Empty outputs");
            return;
        } else if (outputs.size() == 1) {
            Map.Entry<Object, Integer> out = outputs.entrySet().iterator().next();
            addConversion(out.getValue(), out.getKey(), inputs);
            PEIApi.debugLog("Multi-Output: Only one output {}*{}", out.getKey(), out.getValue());
            return;
        }

        int output_count = 0;
        for (int count : outputs.values()) {
            output_count += count;
        }
        Object obj = new Object();
        addConversion(output_count, obj, inputs);

        for (Map.Entry<Object, Integer> out : outputs.entrySet()) {
            HashMap<Object, Integer> ing = new HashMap<>();
            ing.put(obj, out.getValue());
            PEIApi.debugLog("Adding multi-output: {}*{}", out.getKey(), out.getValue());
            addConversion(out.getValue(), out.getKey(), ing);
        }
        PEIApi.debugLog("Multi-Output Took {}ms", (System.currentTimeMillis() - startTime));
    }

    /**
     * Add Conversion for ItemStack
     *
     * @param item  {@code ItemStack} The {@code ItemStack} to be processed
     * @param input The {@code Map<Object, Integer>} that contains the ingredients
     */
    protected void addConversion(ItemStack item, Map<Object, Integer> input) {
        if (item == null || item.isEmpty()) {
            PEIApi.LOGGER.warn("Output Item is either null or Empty: {} from {}", item, input);
            return;
        }

        addConversion(item.getCount(), item.copy(), input);
    }

    /**
     * Add Conversion for FluidStack
     *
     * @param fluid {@code FluidStack} the {@code FluidStack} to be processed
     * @param input The {@code Map} that contains the ingredients
     */
    protected void addConversion(FluidStack fluid, Map<Object, Integer> input) {
        if (fluid == null || fluid.amount == 0) {
            PEIApi.LOGGER.warn("Output Fluid is either null or Empty: {} from {}", fluid, input);
            return;
        }

        addConversion(fluid.amount, fluid.copy(), input);
    }

    /**
     * This mimics ProjectEApi IConversionProxy addConversion
     *
     * @param output_amount {@code int} The output amount
     * @param output        {@code Object} The output
     * @param input         {@code Map<Object, Integer>} The ingredient map
     */
    protected void addConversion(int output_amount, Object output, Map<Object, Integer> input) {
        if (output_amount <= 0 || output == null || input == null || input.isEmpty()) {
            Object output_l = output;
            if (output instanceof FluidStack)
                output_l = ((FluidStack) output).getFluid().getName();

            PEIApi.LOGGER.warn("Invalid Conversion: [{} ({})]*{} from {}", output_l,
                    ClassUtils.getPackageCanonicalName(output != null ? output.getClass() : null), output_amount, input);
            return;
        }

        try {
            conversion_proxy.addConversion(output_amount, output, input);
            PEIApi.mapped_conversions++;
        } catch (Exception e) {
            PEIApi.LOGGER.error("Failed to add conversion: {}*{} from {}; {}", output, output_amount, input, e);
        }
    }
}
