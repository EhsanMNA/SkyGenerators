package me.ehsanmna.skygenerators.manager;

import lombok.Getter;
import me.ehsanmna.skygenerators.SkyGenerators;

import java.util.HashMap;
import java.util.Map;

public class ConfigManager {

    private final SkyGenerators skyGenerators = SkyGenerators.getInstance();

    @Getter
    private Map<String, Integer> generatorMapSize = new HashMap<>();

    @Getter private boolean autoSave = true;
    @Getter private int autoSavePeriod = 1800; //seconds

    @Getter private boolean energySystemEnabled = true;


    public ConfigManager() {
        load();
    }

    public void load(){
        for (String s : skyGenerators.getConfig().getConfigurationSection("generatorAmount").getKeys(false))
            generatorMapSize.put(s, skyGenerators.getConfig().getInt("generatorAmount."+s, 5));

        autoSave = skyGenerators.getConfig().getBoolean("autoSave",true);
        autoSavePeriod = skyGenerators.getConfig().getInt("autoSave-Period",1800);

        energySystemEnabled = skyGenerators.getConfig().getBoolean("feedEnergySystem.enable", true);
    }
}
