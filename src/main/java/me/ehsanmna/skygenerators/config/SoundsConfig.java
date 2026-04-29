package me.ehsanmna.skygenerators.config;

import me.ehsanmna.skygenerators.SkyGenerators;
import me.ehsanmna.skygenerators.manager.SoundManager;
import org.bukkit.Sound;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public class SoundsConfig {

    private final SkyGenerators plugin;
    private YamlConfiguration configuration;
    private File file;
    private final SoundManager soundManager;

    public SoundsConfig(SkyGenerators plugin, SoundManager soundManager) {
        this.plugin = plugin;
        this.soundManager = soundManager;
        setup();
    }

    private void setup(){
        file = new File(plugin.getDataFolder(),"sounds.yml");
        if(!file.exists()){
            plugin.saveResource("sounds.yml", false);
        }
        configuration = YamlConfiguration.loadConfiguration(file);
    }

    public void load(){
        for (String soundId : configuration.getKeys(false)){
            ConfigurationSection section = configuration.getConfigurationSection(soundId);
            assert section != null;
            String soundName = section.getString("sound", Sound.BLOCK_STONE_BUTTON_CLICK_OFF.name()).toUpperCase();
            int power = section.getInt("power", 5);
            double pitch = section.getDouble("pitch",1);
            soundManager.registerSound(new SoundManager.SoundDetails(soundId, Sound.valueOf(soundName), power,pitch));
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
