package me.ehsanmna.skyGenerators;

import lombok.Getter;
import lombok.Setter;
import me.ehsanmna.skyGenerators.commands.SkyGeneratorCommand;
import me.ehsanmna.skyGenerators.commands.SkyGeneratorTabCompleter;
import me.ehsanmna.skyGenerators.manager.*;
import me.ehsanmna.skyGenerators.listeners.GUIListener;
import me.ehsanmna.skyGenerators.listeners.GeneratorPutListener;
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
    private GeneratorUpgradeManager generatorUpgradeManager;

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
        generatorUpgradeManager = new GeneratorUpgradeManager();
        playerGeneratorManager = new PlayerGeneratorManager();
        guiManager = new GUIManager();

        MessageUtils.initialize();

        getLogger().info(String.format("Registered %s generators, %s players generators and %s generator upgrades!",
                generatorManager.getService().getGenerators().size(),
                playerGeneratorManager.getService().getGenerators().size(),
                generatorUpgradeManager.getService().getGeneratorUpgradeMap().size()));

        if (configManager.isAutoSave()) new PlayerGeneratorSaveTask().runTaskTimer(this, 0, configManager.getAutoSavePeriod() * 20L);

        getLogger().info("Registering the commands and the listeners!");
        registerListener();
        registerCommands();

        getLogger().info("Successfully loaded the SkyGenerator plugin!");
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
        getCommand("skygenerator").setTabCompleter(new SkyGeneratorTabCompleter());
    }

    public void reloadConfiguration(){
        reloadConfig();
        getGeneratorManager().getConfig().reloadConfig();
        getPlayerGeneratorManager().getConfig().reloadConfig();
        getGuiManager().getGuiConfig().reload();
        getGeneratorUpgradeManager().getConfig().reloadConfig();
        MessageUtils.getMessageConfig().reload();
    }

}
