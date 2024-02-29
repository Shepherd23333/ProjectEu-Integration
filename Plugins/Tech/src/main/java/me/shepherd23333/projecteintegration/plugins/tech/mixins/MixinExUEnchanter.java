package me.shepherd23333.projecteintegration.plugins.tech.mixins;

import com.google.common.collect.Lists;
import com.rwtema.extrautils2.api.machine.XUMachineEnchanter;
import me.shepherd23333.projecteintegration.plugins.tech.PluginExtraUtilities;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(XUMachineEnchanter.class)
public abstract class MixinExUEnchanter {
    @Inject(
            method = "addRecipe(Lnet/minecraft/item/ItemStack;Lnet/minecraft/item/ItemStack;IILjava/lang/String;)V",
            at = @At("HEAD"),
            remap = false
    )
    private static void saveRecipe(ItemStack input, ItemStack output, int lapisNum, int energy, String gemLapis, CallbackInfo info) {
        ItemStack lapis = OreDictionary.getOres(gemLapis).get(0).copy();
        lapis.setCount(lapisNum);
        List<ItemStack> inputs = Lists.newArrayList(input, lapis);
        PluginExtraUtilities.ENCHANTERRecipes.put(inputs, output);
    }
}
