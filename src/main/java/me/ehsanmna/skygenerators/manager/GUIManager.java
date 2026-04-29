package me.ehsanmna.skygenerators.manager;

import lombok.Getter;
import me.ehsanmna.skygenerators.SkyGenerators;
import me.ehsanmna.skygenerators.config.GUIConfig;
import me.ehsanmna.skygenerators.models.PlayerGenerator;
import me.ehsanmna.skygenerators.service.GUILoaderService;
import org.bukkit.entity.Player;

@Getter
public class GUIManager {

    private final GUILoaderService guiService;
    private final GUIConfig guiConfig;


    public GUIManager() {
        guiService = new GUILoaderService();
        guiConfig = new GUIConfig(SkyGenerators.getInstance(), this);
        guiConfig.load();
    }

    public void openMenu(Player player, String menu){
        guiService.getMenu(menu).clone().open(player);
    }

    public void openGeneratorsMenu(Player player){
        SkyGenerators.getInstance().getSoundManager().getSoundDetail("generator-gui-open").playSound(player);
        guiService.getMenu("generatorsMenuGui").clone().open(player);
    }

    public void openGeneratorManagerMenu(Player player, PlayerGenerator playerGenerator){
        SkyGenerators.getInstance().getSoundManager().getSoundDetail("generator-menu-open").playSound(player);
        guiService.getMenu("generatorManagerMenuGui").clone().open(player, playerGenerator);
    }

    public void openGeneratorMenuOfPlayer(Player playerToOpen, Player targetPlayerGeneratorMenu){
        SkyGenerators.getInstance().getSoundManager().getSoundDetail("generator-gui-open").playSound(playerToOpen);
        guiService.getMenu("generatorsMenuGui").clone().openToOther(targetPlayerGeneratorMenu, playerToOpen);
    }

}
