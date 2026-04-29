package me.ehsanmna.skygenerators.gui;

import lombok.Getter;
import lombok.Setter;
import me.ehsanmna.skygenerators.SkyGenerators;
import me.ehsanmna.skygenerators.models.PlayerGenerator;
import org.bukkit.entity.Player;

public enum MenuAction {
    UPGRADE, COLLECT, MENU, CLOSE, CANCEL, PICKUP, UPGRADE_SLOT, FEED;

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
                if (generator != null) generator.upgrade(player);
                break;
            case PICKUP:
                if (generator != null) generator.pickup(player);
                break;
            case FEED:
                generator.feedEnergy(generator.getGenerator().getBaseGenerator().getMaximumEnergy());
                break;
            case UPGRADE_SLOT:
                // handle upgrade slot
                break;
            case CANCEL:
                break;
        }
    }

}
