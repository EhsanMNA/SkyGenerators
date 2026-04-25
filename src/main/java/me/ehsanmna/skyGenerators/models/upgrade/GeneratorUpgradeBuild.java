package me.ehsanmna.skyGenerators.models.upgrade;

import lombok.Getter;
import lombok.Setter;
import me.azerima.skymaterials.utils.CustomItem;
import me.azerima.skymaterials.utils.CustomItemManager;
import me.ehsanmna.skyGenerators.models.MaterialType;
import me.ehsanmna.skyGenerators.utils.InventoryUtils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

@Getter
public enum GeneratorUpgradeBuild {
    COMPRESSOR, BLOCKER, STORAGE;

    @Setter private int inputAmount = 64;
    @Setter private int storage = 512;
    @Setter private String rawOutput;
    @Setter private GeneratorUpgrade generatorUpgrade;
    @Setter private int generatedAmount;



    public void generate(){
        if (!generatorUpgrade.isActive()) return;
        if (generatorUpgrade.getPlayerGenerator() == null) return;

        switch (this){
            case BLOCKER, COMPRESSOR -> {
                if (generatorUpgrade.getPlayerGenerator().getGeneratedBlocks() < inputAmount) return;
                generatorUpgrade.getPlayerGenerator().setGeneratedBlocks(generatorUpgrade.getPlayerGenerator().getGeneratedBlocks() - inputAmount);
                generatedAmount += Integer.parseInt(rawOutput.split(":")[2]);
            }
        }
    }

    public void collect(Player player){
        if (!generatorUpgrade.isActive()) return;
        if (generatorUpgrade.getPlayerGenerator() == null) return;

        switch (this){
            case BLOCKER, COMPRESSOR -> {
                String type = rawOutput.split(":")[0];
                String materialName = rawOutput.split(":")[1].replace("%material%", generatorUpgrade.getPlayerGenerator().getGenerator().getBaseGenerator().getMaterial().name());

                MaterialType materialType = MaterialType.valueOf(type);
                switch (materialType){
                    case MATERIAL -> {
                        Material material = Material.valueOf(materialName);
                        while (InventoryUtils.hasEmptySlots(player.getInventory())){
                            if (generatedAmount == 0) break;
                            int amount = generatedAmount > 64 ? 64 : generatedAmount;
                            generatedAmount -= amount;
                            ItemStack itemStack = new ItemStack(material, amount);
                            player.getInventory().addItem(itemStack);
                        }
                    }
                    case SKYMATERIAL -> {
                        CustomItem customItem = CustomItemManager.getItemById(materialName);
                        if (customItem == null) return;
                        while (InventoryUtils.hasEmptySlots(player.getInventory())){
                            if (generatedAmount == 0) break;
                            int amount = generatedAmount > 64 ? 64 : generatedAmount;
                            generatedAmount -= amount;
                            ItemStack itemStack = customItem.getItemStack().clone();
                            itemStack.setAmount(amount);
                            player.getInventory().addItem(itemStack);
                        }
                    }
                }
            }
        }
    }

    public ItemStack getOutputStack(){
        ItemStack itemStack = new ItemStack(Material.STONE);
        switch (this){
            case BLOCKER, COMPRESSOR -> {
                String type = rawOutput.split(":")[0];
                String materialName = rawOutput.split(":")[1].replace("%material%", generatorUpgrade.getPlayerGenerator().getGenerator().getBaseGenerator().getMaterial().name());

                MaterialType materialType = MaterialType.valueOf(type);
                switch (materialType){
                    case MATERIAL -> itemStack = new ItemStack(Material.valueOf(materialName));
                    case SKYMATERIAL -> {
                        CustomItem customItem = CustomItemManager.getItemById(materialName);
                        if (customItem == null) break;
                        itemStack = customItem.getItemStack().clone();
                    }
                }
            }
        }
        return itemStack;
    }

}
