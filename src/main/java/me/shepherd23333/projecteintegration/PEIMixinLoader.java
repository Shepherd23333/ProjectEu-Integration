package me.shepherd23333.projecteintegration;

import net.minecraftforge.fml.common.Loader;
import zone.rong.mixinbooter.ILateMixinLoader;

import java.util.ArrayList;
import java.util.List;

public class PEIMixinLoader implements ILateMixinLoader {
    @Override
    public List<String> getMixinConfigs() {
        List<String> configs = new ArrayList<>();
        configs.add("mixins.plugins.tech.json");
        return configs;
    }

    @Override
    public boolean shouldMixinConfigQueue(String mixinConfig) {
        switch (mixinConfig) {
            case "mixins.plugins.tech.json":
                return Loader.isModLoaded("extrautils2");
        }
        return true;
    }
}
