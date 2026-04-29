package me.ehsanmna.skygenerators.gui;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.jetbrains.annotations.NotNull;

@Getter
@Setter
public class MenuHolder implements InventoryHolder {

    String rawTitle;
    String description;
    String information;

    Inventory gui;

    public MenuHolder(String rawTitle, String description) {
        this.rawTitle = rawTitle;
        this.description = description;
    }

    @Override
    public @NotNull Inventory getInventory() {
        return gui;
    }
}
