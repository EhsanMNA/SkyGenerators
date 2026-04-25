package me.ehsanmna.skyGenerators.service;

import me.ehsanmna.skyGenerators.models.upgrade.BaseGeneratorUpgrade;

import java.util.HashMap;
import java.util.Map;

public class GeneratorUpgradeService {

    private static Map<String, BaseGeneratorUpgrade> generatorUpgradeMap = new HashMap<>();


    public BaseGeneratorUpgrade getGeneratorUpgrade(String id){
        return generatorUpgradeMap.get(id.toLowerCase());
    }

    public void addGeneratorUpgrade(BaseGeneratorUpgrade generatorUpgrade){
        generatorUpgradeMap.put(generatorUpgrade.getId().toLowerCase(), generatorUpgrade);
    }

    public Map<String, BaseGeneratorUpgrade> getGeneratorUpgradeMap() {
        return generatorUpgradeMap;
    }
}
