package me.ehsanmna.skyGenerators.gui;

import lombok.Getter;
import lombok.Setter;
import me.ehsanmna.skyGenerators.SkyGenerators;
import me.ehsanmna.skyGenerators.models.PlayerGenerator;
import org.bukkit.entity.Player;

public enum MenuAction {
    UPGRADE, COLLECT, MENU, CLOSE, CANCEL, PICKUP;

    @Getter
    @Setter
    private String argument;

    MenuAction(){}

    MenuAction(String argument) {
        this.argument = argument;
    }

    public void run(Player player, PlayerGenerator generator){
        switch (this){
            case MENU:
                player.closeInventory();
                SkyGenerators.getInstance().getGuiManager().openMenu(player,argument);
                break;
            case CLOSE :
                player.closeInventory();
                break;
            case COLLECT:
                if (generator != null) generator.collect(player);
                break;
            case UPGRADE:
                if (generator != null) generator.upgrade();
                break;
            case PICKUP:
                if (generator != null) generator.pickup(player);
                break;
            case CANCEL:
                break;
        }
    }

}
