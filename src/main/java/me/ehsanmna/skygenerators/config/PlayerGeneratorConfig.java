package me.ehsanmna.skygenerators.config;

import me.ehsanmna.skygenerators.SkyGenerators;
import me.ehsanmna.skygenerators.manager.PlayerGeneratorManager;
import me.ehsanmna.skygenerators.models.upgrade.BaseGeneratorUpgrade;
import me.ehsanmna.skygenerators.models.upgrade.GeneratorUpgrade;
import me.ehsanmna.skygenerators.models.PlayerGenerator;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class PlayerGeneratorConfig {

    private final SkyGenerators plugin;
    private YamlConfiguration configuration;
    File file;
    private final PlayerGeneratorManager playerGeneratorManager;

    public PlayerGeneratorConfig(SkyGenerators plugin, PlayerGeneratorManager playerGeneratorManager) {
        this.plugin = plugin;
        this.playerGeneratorManager = playerGeneratorManager;
        setup();
    }

    private void setup(){
        file = new File(plugin.getDataFolder(),"Data.yml");
        if(!file.exists()){
            plugin.saveResource("Data.yml", false);
        }
        configuration = YamlConfiguration.loadConfiguration(file);
    }

    public void load(){
        playerGeneratorManager.getService().getGenerators().clear();
        for (String playerName : configuration.getKeys(false)){
            for (String g : configuration.getConfigurationSection(playerName + ".generators").getKeys(false)){
                ConfigurationSection generatorSection = configuration.getConfigurationSection(playerName+".generators."+g);
                assert generatorSection != null;
                UUID id = UUID.fromString(generatorSection.getString("playerGeneratorId", UUID.randomUUID().toString()));
                String generatorId = generatorSection.getString("id", "CobbleStoneGenerator1");
                int generated = generatorSection.getInt("generated");
                int energy = generatorSection.getInt("energy");
                PlayerGenerator playerGenerator = new PlayerGenerator(plugin.getGeneratorManager().getGenerator(generatorId));
                playerGenerator.setGeneratedBlocks(generated);
                playerGenerator.getGenerator().setEnergy(energy);
                if (generatorSection.contains("upgrades")) loadUpgrades(generatorSection.getConfigurationSection("upgrades"), playerGenerator);
                playerGenerator.initilize();
                playerGeneratorManager.getService().addGenerator(playerName,playerGenerator);
            }
        }
    }

    public void save(){
        Map<String, List<PlayerGenerator>> playerGeneratorMap = playerGeneratorManager.getService().getGenerators();
        for (Map.Entry<String, List<PlayerGenerator>> entrySet : playerGeneratorMap.entrySet()){
            String playerName = entrySet.getKey();
            if (!configuration.contains(playerName)) configuration.createSection(playerName);
            ConfigurationSection generatorSection = configuration.getConfigurationSection(playerName+".generators");
            if (generatorSection == null) generatorSection = configuration.createSection(playerName+".generators");
//            configuration.set("uuid", playerUUID);;
            int i = 0;
            for (PlayerGenerator playerGenerator : entrySet.getValue()){
                i++;
                generatorSection.set(i+".id", playerGenerator.getGenerator().getBaseGenerator().getId());
                generatorSection.set(i+".playerGeneratorId", playerGenerator.getGeneratorId().toString());
                generatorSection.set(i+".generatorId", playerGenerator.getGenerator().getId().toString());
                generatorSection.set(i+".generated", playerGenerator.getGeneratedBlocks());
                generatorSection.set(i+".energy", playerGenerator.getGenerator().getEnergy());

                int n =0;
                ConfigurationSection upgradeSection = generatorSection.createSection(i+".upgrades");
                for (GeneratorUpgrade generatorUpgrade : playerGenerator.getUpgrades()){
                    n++;
                    upgradeSection.set(n+".id", generatorUpgrade.getBaseGeneratorUpgrade().getId());
                    upgradeSection.set(n+".uuid", generatorUpgrade.getUuid().toString());
                    upgradeSection.set(n+".generated", generatorUpgrade.getGeneratedAmount());
                }

            }
        }

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

    private void loadUpgrades(ConfigurationSection section, PlayerGenerator playerGenerator){
        for (String i : section.getKeys(false)){
            ConfigurationSection upgradeSection = section.getConfigurationSection(i);
            String id = upgradeSection.getString("id", "none");
            String uuid = upgradeSection.getString("uuid", UUID.randomUUID().toString());
            BaseGeneratorUpgrade baseGeneratorUpgrade = plugin.getGeneratorUpgradeManager().getGeneratorUpgrade(id);
            GeneratorUpgrade generatorUpgrade = new GeneratorUpgrade(baseGeneratorUpgrade, UUID.fromString(uuid));
            generatorUpgrade.setActive(true);
            generatorUpgrade.setPlayerGenerator(playerGenerator);
            generatorUpgrade.setGeneratedAmount(upgradeSection.getInt("generated",0));

            playerGenerator.getUpgrades().add(generatorUpgrade);
        }
    }
}
