package me.ehsanmna.skygenerators.service;

import me.ehsanmna.skygenerators.models.upgrade.BaseGeneratorUpgrade;
import me.ehsanmna.skygenerators.models.upgrade.GeneratorUpgradeBuild;

import java.util.HashMap;
import java.util.Map;

public class GeneratorUpgradeService {

    private static Map<String, BaseGeneratorUpgrade> generatorUpgradeMap = new HashMap<>();

    private static Map<String, GeneratorUpgradeBuild> generatorUpgradeBuildMap = new HashMap<>();


    public BaseGeneratorUpgrade getGeneratorUpgrade(String id){
        return generatorUpgradeMap.get(id.toLowerCase());
    }

    public void addGeneratorUpgrade(BaseGeneratorUpgrade generatorUpgrade){
        generatorUpgradeMap.put(generatorUpgrade.getId().toLowerCase(), generatorUpgrade);
    }

    public Map<String, BaseGeneratorUpgrade> getGeneratorUpgradeMap() {
        return generatorUpgradeMap;
    }

    public Map<String, GeneratorUpgradeBuild> getGeneratorUpgradeBuildMap() {
        return generatorUpgradeBuildMap;
    }

    public void addGeneratorBuild(String id, GeneratorUpgradeBuild build){
        generatorUpgradeBuildMap.put(id, build);
    }
}
