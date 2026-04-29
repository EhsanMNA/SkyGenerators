package me.ehsanmna.skygenerators.listeners;

import me.ehsanmna.skygenerators.SkyGenerators;
import me.ehsanmna.skygenerators.events.SkyGeneratorPreInputEvent;
import me.ehsanmna.skygenerators.events.SkyGeneratorUpgradePreInputEvent;
import me.ehsanmna.skygenerators.models.upgrade.GeneratorUpgrade;
import me.ehsanmna.skygenerators.models.PlayerGenerator;
import me.ehsanmna.skygenerators.utils.MessageUtils;
import me.ehsanmna.skygenerators.utils.TextUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.List;

public class GeneratorPutListener implements Listener {


    @EventHandler
    public void onPut(SkyGeneratorPreInputEvent event){
        Player player = event.getPlayer();
        int generatorAmount = SkyGenerators.getInstance().getPlayerGeneratorManager().getGenerators(player).size();

        int acceptableAmount = 5;

        for (String s : SkyGenerators.getInstance().getConfigManager().getGeneratorMapSize().keySet())
            if (player.hasPermission("skygenerator.rank."+s) && acceptableAmount < SkyGenerators.getInstance().getConfigManager().getGeneratorMapSize().get(s))
                acceptableAmount =  SkyGenerators.getInstance().getConfigManager().getGeneratorMapSize().get(s);

        if (acceptableAmount < generatorAmount + 1){
            event.setCancelled(true);
            player.sendMessage(TextUtils.toComponent(MessageUtils.getMessage("generator-put-full",
                    "<dark_red>SkyGenerators <white>| <red>Sorry but your generators amount is reached! if you want to use more generator, " +
                            "purchase ranks from <yellow>/store<white>!")));
        }
    }

    @EventHandler
    public void onUpgradePut(SkyGeneratorUpgradePreInputEvent event){
        Player player = event.getPlayer();
        PlayerGenerator playerGenerator = event.getGenerator();
        List<GeneratorUpgrade> generatorUpgrades = playerGenerator.getUpgrades();

        if (generatorUpgrades.contains(event.getGeneratorUpgrade())) {
            event.setCancelled(true);
            player.sendMessage(TextUtils.toComponent(MessageUtils.getMessage("generator-upgrade-put-contains",
                    "<dark_red>SkyGenerators <white>| <red>Sorry but your generator already have this upgrade!")));
            return;
        }
    }

}
