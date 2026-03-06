package me.ehsanmna.skyGenerators.config;

import me.ehsanmna.skyGenerators.SkyGenerators;
import me.ehsanmna.skyGenerators.utils.MessageUtils;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;

public class MessageConfig {

    private final SkyGenerators plugin;
    private YamlConfiguration configuration;
    File file;

    public MessageConfig(SkyGenerators plugin) {
        this.plugin = plugin;
    }

    public void setup(){
        file = new File(plugin.getDataFolder(),"messages.yml");
        if(!file.exists()){
            plugin.saveResource("messages.yml", false);
        }
        configuration = YamlConfiguration.loadConfiguration(file);
    }

    public void load(){
        for (String messageId : configuration.getKeys(false)){
            String message = configuration.getString(messageId);
            MessageUtils.getMessages().put(messageId,message);
        }
    }

    public void save(){

    }

    public void reload(){
        save();
        setup();
        load();
    }
}
