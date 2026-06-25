package me.ehsanmna.skygenerators.config;

import me.ehsanmna.skygenerators.SkyGenerators;
import me.ehsanmna.skygenerators.manager.JournalManager;
import me.ehsanmna.skygenerators.models.GeneratorJournal;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.UUID;

public class JournalConfig {

    private final SkyGenerators plugin;
    private YamlConfiguration configuration;
    File file;
    private final JournalManager journalManager;

    public JournalConfig(SkyGenerators plugin, JournalManager journalManager) {
        this.plugin = plugin;
        this.journalManager = journalManager;
        setup();
    }

    private void setup(){
        file = new File(plugin.getDataFolder(),"Journals.yml");
        if(!file.exists()){
            plugin.saveResource("Journals.yml", false);
        }
        configuration = YamlConfiguration.loadConfiguration(file);
    }

    public void load(){
        journalManager.getService().getJournals().clear();
        for (String rawId : configuration.getKeys(false)){
            ConfigurationSection section = configuration.getConfigurationSection(rawId);
            if (section == null) continue;

            UUID playerGeneratorId;
            try {
                playerGeneratorId = UUID.fromString(rawId);
            } catch (IllegalArgumentException e) {
                plugin.getLogger().warning("Skipping invalid journal entry, not a UUID: " + rawId);
                continue;
            }

            GeneratorJournal journal = new GeneratorJournal();
            journal.setCreatedAt(section.getLong("createdAt", 0L));
            journal.setLifetimeBlocksGenerated(section.getLong("lifetimeBlocksGenerated", 0L));
            journal.setLifetimeUpgradeItemsGenerated(section.getLong("lifetimeUpgradeItemsGenerated", 0L));
            journal.setLifetimeEnergyConsumed(section.getLong("lifetimeEnergyConsumed", 0L));
            journal.setLifetimeCollections(section.getLong("lifetimeCollections", 0L));
            journal.setLifetimePickups(section.getLong("lifetimePickups", 0L));
            journal.setLastCollectedAt(section.getLong("lastCollectedAt", -1L));
            journal.setLastUpgradedAt(section.getLong("lastUpgradedAt", -1L));
            journal.getGeneratorTierHistory().addAll(section.getStringList("generatorTierHistory"));
            journal.getActiveUpgradeIds().addAll(section.getStringList("activeUpgradeIds"));

            journalManager.getService().addJournal(playerGeneratorId, journal);
        }
    }

    public void save(){
        Map<UUID, GeneratorJournal> journalMap = journalManager.getService().getJournals();
        for (Map.Entry<UUID, GeneratorJournal> entry : journalMap.entrySet()){
            String rawId = entry.getKey().toString();
            GeneratorJournal journal = entry.getValue();

            if (!configuration.contains(rawId)) configuration.createSection(rawId);
            ConfigurationSection section = configuration.getConfigurationSection(rawId);

            section.set("createdAt", journal.getCreatedAt());
            section.set("lifetimeBlocksGenerated", journal.getLifetimeBlocksGenerated());
            section.set("lifetimeUpgradeItemsGenerated", journal.getLifetimeUpgradeItemsGenerated());
            section.set("lifetimeEnergyConsumed", journal.getLifetimeEnergyConsumed());
            section.set("lifetimeCollections", journal.getLifetimeCollections());
            section.set("lifetimePickups", journal.getLifetimePickups());
            section.set("lastCollectedAt", journal.getLastCollectedAt());
            section.set("lastUpgradedAt", journal.getLastUpgradedAt());
            section.set("generatorTierHistory", journal.getGeneratorTierHistory());
            section.set("activeUpgradeIds", journal.getActiveUpgradeIds());
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

}