package me.ehsanmna.skyGenerators;

import lombok.Getter;
import lombok.Setter;
import me.ehsanmna.skyGenerators.commands.SkyGeneratorCommand;
import me.ehsanmna.skyGenerators.manager.ConfigManager;
import me.ehsanmna.skyGenerators.listeners.GUIListener;
import me.ehsanmna.skyGenerators.listeners.GeneratorPutListener;
import me.ehsanmna.skyGenerators.manager.GUIManager;
import me.ehsanmna.skyGenerators.manager.GeneratorManager;
import me.ehsanmna.skyGenerators.manager.PlayerGeneratorManager;
import me.ehsanmna.skyGenerators.tasks.PlayerGeneratorSaveTask;
import me.ehsanmna.skyGenerators.utils.MessageUtils;
import org.bukkit.plugin.java.JavaPlugin;

@Getter
public final class SkyGenerators extends JavaPlugin {

    @Getter
    private static SkyGenerators instance;

    private PlayerGeneratorManager playerGeneratorManager;
    private GeneratorManager generatorManager;
    private GUIManager guiManager;
    private ConfigManager configManager;

    @Getter
    @Setter
    private static boolean debugMode = false;

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();

        getLogger().info("Loading managers and services!");
        configManager = new ConfigManager();
        generatorManager = new GeneratorManager();
        playerGeneratorManager = new PlayerGeneratorManager();
        guiManager = new GUIManager();

        MessageUtils.initialize();

        getLogger().info(String.format("Registered %s generators and %s players generators!",
                generatorManager.getService().getGenerators().size(), playerGeneratorManager.getService().getGenerators().size()));

        if (configManager.isAutoSave()) new PlayerGeneratorSaveTask().runTaskTimer(this, 0, configManager.getAutoSavePeriod() * 20L);

        getLogger().info("Registering the commands and the listeners!");
        registerListener();
        registerCommands();

        getLogger().info("Successfully loaded the SKyGenerator plugin!");
    }

    @Override
    public void onDisable() {
        getGeneratorManager().getConfig().save();
        getPlayerGeneratorManager().getConfig().save();
        getGuiManager().getGuiConfig().save();
        MessageUtils.getMessageConfig().save();
        getLogger().info("Successfully unloaded SKyGenerator plugin!");
    }

    private void registerListener(){
        getServer().getPluginManager().registerEvents(new GUIListener(), this);
        getServer().getPluginManager().registerEvents(new GeneratorPutListener(), this);
    }

    private void registerCommands(){
        getCommand("SkyGenerator").setExecutor(new SkyGeneratorCommand());
    }

    public void reloadConfiguration(){
        reloadConfig();
        getGeneratorManager().getConfig().reloadConfig();
        getPlayerGeneratorManager().getConfig().reloadConfig();
        getGuiManager().getGuiConfig().reload();
        MessageUtils.getMessageConfig().reload();
    }

}
