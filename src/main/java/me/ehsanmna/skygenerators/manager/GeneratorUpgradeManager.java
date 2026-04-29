package me.ehsanmna.skygenerators.manager;

import lombok.Getter;
import me.ehsanmna.skygenerators.SkyGenerators;
import me.ehsanmna.skygenerators.config.GeneratorUpgradeConfig;
import me.ehsanmna.skygenerators.models.PlayerGenerator;
import me.ehsanmna.skygenerators.models.upgrade.BaseGeneratorUpgrade;
import me.ehsanmna.skygenerators.models.upgrade.GeneratorUpgrade;
import me.ehsanmna.skygenerators.service.GeneratorUpgradeService;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.UUID;

public class GeneratorUpgradeManager {

    private final SkyGenerators skyGenerators = SkyGenerators.getInstance();

    @Getter
    private final GeneratorUpgradeService service;
    @Getter
    private final GeneratorUpgradeConfig config;


    public GeneratorUpgradeManager() {
        service = new GeneratorUpgradeService();
        config = new GeneratorUpgradeConfig(skyGenerators, this);
        config.load();
    }

    public GeneratorUpgrade getGeneratorUpgradeFromItem(ItemStack itemStack, PlayerGenerator playerGenerator){
        ItemMeta meta = itemStack.getItemMeta();
        String generatorId = meta.getPersistentDataContainer().get(new NamespacedKey(skyGenerators, "generator-upgrade"), PersistentDataType.STRING);
        String generatorUUID = meta.getPersistentDataContainer().get(new NamespacedKey(skyGenerators, "generator-upgrade-uuid"), PersistentDataType.STRING);
        if (generatorId == null) return null;
        BaseGeneratorUpgrade baseGeneratorUpgrade = getGeneratorUpgrade(generatorId);
        GeneratorUpgrade generatorUpgrade = null;

        for (GeneratorUpgrade upgrade : playerGenerator.getUpgrades())
            if (upgrade.getUuid().equals(generatorUUID)) generatorUpgrade = upgrade;
        if (generatorUpgrade == null) {
            generatorUpgrade = new GeneratorUpgrade();
            generatorUpgrade.setBaseGeneratorUpgrade(baseGeneratorUpgrade);
            generatorUpgrade.setUuid(UUID.fromString(generatorUUID));
            generatorUpgrade.setPlayerGenerator(playerGenerator);
//            generatorUpgrade.setUpgradeBuild(service.getGeneratorUpgradeBuildMap().get(generatorUpgrade.getBaseGeneratorUpgrade().getId()));
        }
        return generatorUpgrade;
    }

    public BaseGeneratorUpgrade getGeneratorUpgrade(String id){
        return service.getGeneratorUpgrade(id);
    }


}
