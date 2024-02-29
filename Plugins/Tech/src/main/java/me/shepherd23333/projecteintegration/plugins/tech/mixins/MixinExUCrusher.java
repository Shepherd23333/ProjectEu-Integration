package me.shepherd23333.projecteintegration.plugins.tech.mixins;

import com.google.common.collect.Lists;
import com.rwtema.extrautils2.api.machine.XUMachineCrusher;
import me.shepherd23333.projecteintegration.plugins.tech.PluginExtraUtilities;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

@Mixin(XUMachineCrusher.class)
public abstract class MixinExUCrusher {
    @Inject(
            method = "addRecipe(Lnet/minecraft/item/ItemStack;Lnet/minecraft/item/ItemStack;Lnet/minecraft/item/ItemStack;F)V",
            at = @At("HEAD"),
            remap = false
    )
    private static void saveRecipe(@Nonnull ItemStack input, @Nonnull ItemStack output, @Nullable ItemStack outputSecondary, float outputSecondaryProbability, CallbackInfo info) {
        List<Object> outputs = Lists.newArrayList(output.copy());
        if (outputSecondaryProbability - 1 > -1e-6)
            outputs.add(outputSecondary.copy());
        PluginExtraUtilities.CRUSHERRecipes.put(input.copy(), outputs);
    }
}
