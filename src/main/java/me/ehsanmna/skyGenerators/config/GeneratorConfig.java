package me.ehsanmna.skyGenerators.config;

import me.ehsanmna.skyGenerators.SkyGenerators;
import me.ehsanmna.skyGenerators.manager.GeneratorManager;
import me.ehsanmna.skyGenerators.models.BaseGenerator;
import me.ehsanmna.skyGenerators.models.Generator;
import me.ehsanmna.skyGenerators.utils.TextUtils;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class GeneratorConfig {

    private final SkyGenerators plugin;
    private YamlConfiguration configuration;
    File file;
    private final GeneratorManager generatorManager;

    public GeneratorConfig(SkyGenerators plugin, GeneratorManager generatorManager) {
        this.plugin = plugin;
        this.generatorManager = generatorManager;
        setup();
    }

    private void setup(){
        file = new File(plugin.getDataFolder(),"Generators.yml");
        if(!file.exists()){
            plugin.saveResource("Generators.yml", false);
        }
        configuration = YamlConfiguration.loadConfiguration(file);
    }

    public void load(){
        for (String generatorId : configuration.getKeys(false)){
            ConfigurationSection section = configuration.getConfigurationSection(generatorId);
            String name = section.getString("name","NotFound");
            String displayName = section.getString("displayName","NotFound");
            List<String> lore = section.getStringList("lore");
            Material material = Material.valueOf(section.getString("Material","STONE"));

            // generator
            ConfigurationSection generatorSection = section.getConfigurationSection("generator");
            Material generatorMaterial = Material.valueOf(generatorSection.getString("material","STONE"));
            int speed = generatorSection.getInt("speed",1);
            int space = generatorSection.getInt("space",100);
            String upgrade = generatorSection.getString("upgrade", "MAX");

            // placeholder
            List<String> newLore = new ArrayList<>();
            for (String l : lore) newLore.add(l.replace("%speed%", speed+"").replace("%space%", space+""));

            // create and register generator
            BaseGenerator generator = new BaseGenerator();
            generator.setId(generatorId);
            generator.setName(name);
            generator.setDisplayName(TextUtils.toComponent(displayName));
            generator.setLore(newLore);
            generator.setMaterial(material);
            generator.setGeneratorMaterial(generatorMaterial);
            generator.setSpeed(speed);
            generator.setSpace(space);
            generator.setNextGeneratorUpgradeId(upgrade);
            generatorManager.getService().registerGenerator(generatorId,generator);
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

}
