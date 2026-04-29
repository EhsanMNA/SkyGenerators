package me.ehsanmna.skygenerators.utils;

import org.bukkit.Material;
import org.bukkit.inventory.Inventory;

public class InventoryUtils {

    public static boolean hasEmptySlots(Inventory inv){
        // handle
        for (int i = 0; i < inv.getSize(); i++){
            if (inv.getItem(i) == null || inv.getItem(i).getType() == Material.AIR) return true;
        }
        return false;
    }

    public static int getEmptySlotsCount(Inventory inv){
        int slots = 0;

        for (int i = 0; i < inv.getSize(); i++){
            if (inv.getItem(i) == null || inv.getItem(i).getType() == Material.AIR) slots++;
        }

        return slots;
    }

}
