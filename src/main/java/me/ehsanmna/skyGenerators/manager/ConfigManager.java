package me.ehsanmna.skyGenerators.manager;

import lombok.Getter;
import me.ehsanmna.skyGenerators.SkyGenerators;

import java.util.HashMap;
import java.util.Map;

public class ConfigManager {

    private final SkyGenerators skyGenerators = SkyGenerators.getInstance();

    @Getter
    private Map<String, Integer> generatorMapSize = new HashMap<>();

    public ConfigManager() {
        load();
    }

    public void load(){
        for (String s : skyGenerators.getConfig().getConfigurationSection("generatorAmount").getKeys(false))
            generatorMapSize.put(s, skyGenerators.getConfig().getInt("generatorAmount."+s, 5));

    }
}
