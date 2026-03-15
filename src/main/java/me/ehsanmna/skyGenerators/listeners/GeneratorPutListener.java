package me.ehsanmna.skyGenerators.listeners;

import me.ehsanmna.skyGenerators.SkyGenerators;
import me.ehsanmna.skyGenerators.events.SkyGeneratorPreInputEvent;
import me.ehsanmna.skyGenerators.utils.MessageUtils;
import me.ehsanmna.skyGenerators.utils.TextUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class GeneratorPutListener implements Listener {


    @EventHandler
    public void onPut(SkyGeneratorPreInputEvent event){
        Player player = event.getPlayer();
        int generatorAmount = SkyGenerators.getInstance().getPlayerGeneratorManager().getGenerators(player).size();

        int acceptableAmount = 5;

        for (String s : SkyGenerators.getInstance().getConfigManager().getGeneratorMapSize().keySet())
            if (player.hasPermission("skygenerator.rank."+s) &&
                    acceptableAmount < SkyGenerators.getInstance().getConfigManager().getGeneratorMapSize().get(s))
                acceptableAmount =  SkyGenerators.getInstance().getConfigManager().getGeneratorMapSize().get(s);

        if (acceptableAmount < generatorAmount + 1){
            event.setCancelled(true);
            player.sendMessage(TextUtils.toComponent(MessageUtils.getMessage("generator-put-full",
                    "<dark_red>SkyGenerators <white>| <red>Sorry but your generators amount is reached! if you want to use more generator, " +
                            "purchase ranks from <yellow>/store<white>!")));
        }
    }

}
