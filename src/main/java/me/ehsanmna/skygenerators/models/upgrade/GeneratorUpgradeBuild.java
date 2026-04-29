package me.ehsanmna.skygenerators.models.upgrade;

import lombok.Getter;
import lombok.Setter;
import me.azerima.skymaterials.utils.CustomItem;
import me.azerima.skymaterials.utils.CustomItemManager;
import me.ehsanmna.skygenerators.models.MaterialType;
import me.ehsanmna.skygenerators.utils.InventoryUtils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

@Getter
public enum GeneratorUpgradeBuild {
    COMPRESSOR, BLOCKER, STORAGE;

    @Setter private int inputAmount = 64;
    @Setter private int storage = 512;
    @Setter private String rawOutput;



    public ItemStack getOutputStack(GeneratorUpgrade generatorUpgrade){
        ItemStack itemStack = new ItemStack(Material.STONE);
        switch (this){
            case BLOCKER, COMPRESSOR -> {
                String type = rawOutput.split(":")[0];
                String materialName = rawOutput.split(":")[1].replace("%material%",
                        generatorUpgrade.getPlayerGenerator().getGenerator().getBaseGenerator().getGeneratorMaterial().name());

                MaterialType materialType = MaterialType.valueOf(type);
                switch (materialType){
                    case MATERIAL -> itemStack = new ItemStack(Material.valueOf(materialName.replace("_INGOT","").toUpperCase()));
                    case SKYMATERIAL -> {
                        CustomItem customItem = CustomItemManager.getItemById(materialName.toLowerCase());
                        if (customItem == null) break;
                        itemStack = customItem.getItemStack().clone();
                    }
                }
            }
        }
        return itemStack;
    }



}
