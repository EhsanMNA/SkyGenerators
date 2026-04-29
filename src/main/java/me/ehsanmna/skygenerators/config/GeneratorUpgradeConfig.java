package me.ehsanmna.skygenerators.config;

import me.ehsanmna.skygenerators.SkyGenerators;
import me.ehsanmna.skygenerators.manager.GeneratorUpgradeManager;
import me.ehsanmna.skygenerators.models.upgrade.BaseGeneratorUpgrade;
import me.ehsanmna.skygenerators.models.upgrade.GeneratorUpgradeBuild;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Objects;

public class GeneratorUpgradeConfig {

    private final SkyGenerators plugin;
    private YamlConfiguration configuration;
    File file;
    private final GeneratorUpgradeManager generatorManager;

    public GeneratorUpgradeConfig(SkyGenerators plugin, GeneratorUpgradeManager generatorManager) {
        this.plugin = plugin;
        this.generatorManager = generatorManager;
        setup();
    }

    private void setup(){
        file = new File(plugin.getDataFolder(),"Upgrade.yml");
        if(!file.exists()){
            plugin.saveResource("Upgrade.yml", false);
        }
        configuration = YamlConfiguration.loadConfiguration(file);
    }

    public void load(){
        for (String id : configuration.getKeys(false)){
            ConfigurationSection section = configuration.getConfigurationSection(id);
            String name = section.getString("name", "name");
            String displayname = section.getString("displayname", "displayname");
            Material material = Material.valueOf(section.getString("material", "STONE").toUpperCase());
            boolean glow = section.getBoolean("glow", false);
            List<String> lore = section.getStringList("lore");
            GeneratorUpgradeBuild upgradeBuild = getGeneratorUpgradeBuild(Objects.requireNonNull(section.getConfigurationSection("build")));

            BaseGeneratorUpgrade baseGeneratorUpgrade = new BaseGeneratorUpgrade();
            baseGeneratorUpgrade.setId(id);
            baseGeneratorUpgrade.setName(name);
            baseGeneratorUpgrade.setDisplayname(displayname);
            baseGeneratorUpgrade.setMaterial(material);
            baseGeneratorUpgrade.setGlow(glow);
            baseGeneratorUpgrade.setLore(lore);
            baseGeneratorUpgrade.setUpgradeBuild(upgradeBuild);
            generatorManager.getService().addGeneratorBuild(id, upgradeBuild);

            generatorManager.getService().addGeneratorUpgrade(baseGeneratorUpgrade);
        }
    }

    public void save(){
        try {
            configuration.save(file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void reloadConfig(){
        setup();
        save();
        load();
    }

    private GeneratorUpgradeBuild getGeneratorUpgradeBuild(ConfigurationSection section){
        String type = section.getString("type", "STORAGE");
        GeneratorUpgradeBuild generatorUpgradeBuild = GeneratorUpgradeBuild.valueOf(type);

        if (generatorUpgradeBuild == GeneratorUpgradeBuild.STORAGE){
            int space = section.getInt("space", 64);
            generatorUpgradeBuild.setStorage(space);
        } else if (generatorUpgradeBuild == GeneratorUpgradeBuild.BLOCKER || generatorUpgradeBuild == GeneratorUpgradeBuild.COMPRESSOR) {
            int input = section.getInt("input", 64);
            String ouput = section.getString("output","");
            generatorUpgradeBuild.setInputAmount(input);
            generatorUpgradeBuild.setRawOutput(ouput);
        }
        return generatorUpgradeBuild;
    }
}
