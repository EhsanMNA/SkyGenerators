package me.ehsanmna.skygenerators.models;

import lombok.Getter;
import lombok.Setter;
import me.azerima.skymaterials.utils.CustomItemManager;
import me.ehsanmna.skygenerators.SkyGenerators;
import me.ehsanmna.skygenerators.models.upgrade.GeneratorUpgrade;
import me.ehsanmna.skygenerators.models.upgrade.GeneratorUpgradeBuild;
import me.ehsanmna.skygenerators.tasks.GeneratorTask;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Getter
@Setter
public class PlayerGenerator {

    private final UUID generatorId; // Generator uuid is same with this
    private String playerName;
    private Generator generator;
    private int generatedBlocks;
    private GeneratorTask generatorTask;
    private List<GeneratorUpgrade> upgrades = new ArrayList<>();
    private boolean active = true;


    public PlayerGenerator(Generator generator) {
        this.generatorId = generator.getId();
        this.generator = generator;
    }

    public void generate(int minutes){
        // check for empty space
        if (getAllGenerated() + (generator.getBaseGenerator().getSpeed() * minutes) > getSpace()) return;

        generatedBlocks = (int) (generatedBlocks + (generator.getBaseGenerator().getSpeed() * minutes));

        // also call generate functions for upgrades
        if (!upgrades.isEmpty()){
            for (GeneratorUpgrade generatorUpgrade : upgrades) {
                if (generatorUpgrade.getBaseGeneratorUpgrade().getUpgradeBuild() == null) continue;
                generatorUpgrade.generate();
            }
        }
    }

    public void generate(){generate(1);}

    public void collect(Player player){
        if (!upgrades.isEmpty())
            for (GeneratorUpgrade generatorUpgrade : upgrades){
                if (generatorUpgrade.getBaseGeneratorUpgrade().getUpgradeBuild() == null) continue;
                generatorUpgrade.collect(player);
            }

        for (int i = 0; i < player.getInventory().getSize(); i++){
            if (i > 35) break;
            ItemStack item =  player.getInventory().getItem(i);
            if (item == null || item.getType() == Material.AIR){
                int amount = Math.min(generatedBlocks, 64);
                generatedBlocks = generatedBlocks - amount;
                player.getInventory().setItem(i, new ItemStack(generator.getBaseGenerator().getGeneratorMaterial(), amount));
            } else if (item.getType() == generator.getBaseGenerator().getGeneratorMaterial()) {
                if (item.getAmount() != 64){
                    int amount = Math.min(generatedBlocks, 64 - item.getAmount());
                    generatedBlocks = generatedBlocks - amount;
                    player.getInventory().addItem(new ItemStack(generator.getBaseGenerator().getGeneratorMaterial(), amount));
                }
            }
        }
        player.closeInventory();
        SkyGenerators.getInstance().getGuiManager().openGeneratorManagerMenu(player,this);
    }

    public void upgrade(Player player) {
        String nextGeneratorId = generator.getBaseGenerator().getNextGeneratorUpgradeId();
        if (nextGeneratorId.equals("MAX")) return;

        String materialName = generator.getBaseGenerator().getNextGeneratorRequirementMaterialName();
        int amount = generator.getBaseGenerator().getNextGeneratorRequirementAmount();

        switch (generator.getBaseGenerator().getNextGeneratorRequirementType()){
            case MATERIAL -> {
                if(!player.getInventory().containsAtLeast(new ItemStack(Material.valueOf(materialName)), amount)){
                    player.playSound(player.getLocation(), Sound.BLOCK_SLIME_BLOCK_BREAK, 10 ,2);
                    return;
                }
                player.getInventory().removeItemAnySlot(new ItemStack(Material.valueOf(materialName), amount));
            }
            case SKYMATERIAL -> {
                if (CustomItemManager.getItemById(materialName) == null){
                    SkyGenerators.getInstance().getLogger().severe("Could not get "+materialName+" custom material! please check config file!");
                    return;
                }
                if (!CustomItemManager.hasItem(player, CustomItemManager.getItemById(materialName)) ||
                        CustomItemManager.getAmount(player, CustomItemManager.getItemById(materialName)) < amount){
                    if (SkyGenerators.isDebugMode()){
                        player.sendMessage("Has item: "+ CustomItemManager.hasItem(player, CustomItemManager.getItemById(materialName)));
                        player.sendMessage("Amount: "+ CustomItemManager.getItemById(materialName));
                        player.sendMessage("Data: A:"+amount + ", B:"+materialName);
                    }
                    player.playSound(player.getLocation(), Sound.BLOCK_SLIME_BLOCK_BREAK, 10 ,2);
                    return;
                }
                ItemStack item = CustomItemManager.getItemById(materialName).getItemStack().clone();
                item.setAmount(amount);
                player.getInventory().removeItemAnySlot(item);
            }
        }

        Generator newGenerator = SkyGenerators.getInstance().getGeneratorManager().getGenerator(nextGeneratorId, generatorId);
        setGenerator(newGenerator);
        SkyGenerators.getInstance().getGuiManager().openGeneratorsMenu(player);
        SkyGenerators.getInstance().getLogger().info(playerName+" generator has been upgraded into "+generator.getBaseGenerator().getName()+"!");
    }

    public void initilize(){
        generatorTask = new GeneratorTask(this);
        generatorTask.runTaskTimer(SkyGenerators.getInstance(), 1200, 20L * 60);
        for (GeneratorUpgrade generatorUpgrade : upgrades) generatorUpgrade.setActive(true);
    }

    public void pickup(Player player) {
        player.getInventory().addItem(generator.getAsItemStack());
        collect(player);
        for (GeneratorUpgrade generatorUpgrade : upgrades) {
            generatorUpgrade.pickup(player);
        }
        upgrades.clear();
        setActive(false);
        generatorTask.cancel();
        SkyGenerators.getInstance().getPlayerGeneratorManager().removeGenerator(player, generatorId);
        SkyGenerators.getInstance().getGuiManager().openGeneratorsMenu(player);
    }

    public int getSpace(){
        int space = generator.getBaseGenerator().getSpace();
        if (upgrades.isEmpty()) return space;
        for (GeneratorUpgrade generatorUpgrade : upgrades){
            if (generatorUpgrade.getBaseGeneratorUpgrade().getUpgradeBuild() != GeneratorUpgradeBuild.STORAGE) continue;
            space += generatorUpgrade.getBaseGeneratorUpgrade().getUpgradeBuild().getStorage();
        }
        return space;
    }

    public int getAllGenerated(){
        int allGenerated = generatedBlocks;
        if (!upgrades.isEmpty())
            for (GeneratorUpgrade generatorUpgrade : upgrades){
                if (generatorUpgrade.getBaseGeneratorUpgrade().getUpgradeBuild() == null) continue;
                allGenerated += generatorUpgrade.getGeneratedAmount();
            }

        return allGenerated;
    }

    public void setActive(boolean active) {
        this.active = active;
        for (GeneratorUpgrade generatorUpgrade : getUpgrades()) generatorUpgrade.setActive(true);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PlayerGenerator that = (PlayerGenerator) o;
        return Objects.equals(generatorId, that.generatorId);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(generatorId);
    }
}
