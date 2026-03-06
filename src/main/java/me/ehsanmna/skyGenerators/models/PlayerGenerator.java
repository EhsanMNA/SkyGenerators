package me.ehsanmna.skyGenerators.models;

import lombok.Getter;
import lombok.Setter;
import me.ehsanmna.skyGenerators.SkyGenerators;
import me.ehsanmna.skyGenerators.tasks.GeneratorTask;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Objects;
import java.util.UUID;

@Getter
@Setter
public class PlayerGenerator {

    private final UUID generatorId;
    private String playerName;
    private Generator generator;
    private int generatedBlocks;
    private GeneratorTask generatorTask;
    private boolean active = true;

    public PlayerGenerator() {
        generatorId = UUID.randomUUID();
//        initilize();
    }

    public PlayerGenerator(UUID generatorId) {
        this.generatorId = generatorId;
//        initilize();
    }

    public PlayerGenerator(Generator generator) {
        this.generatorId = generator.getId();
        this.generator = generator;
    }

    public void generate(int minutes){
        if (generatedBlocks + (generator.getBaseGenerator().getSpeed() * minutes) > generator.getBaseGenerator().getSpace()){
            generatedBlocks = generator.getBaseGenerator().getSpace();
            return;
        }
        generatedBlocks = (int) (generatedBlocks + (generator.getBaseGenerator().getSpeed() * minutes));
    }

    public void generate(){generate(1);}

    public void collect(Player player){
        for (int i = 0; i < player.getInventory().getSize(); i++){
            ItemStack item =  player.getInventory().getItem(i);
            if (item == null || item.getType() == Material.AIR){
                int amount = Math.min(generatedBlocks, 64);
                generatedBlocks = generatedBlocks - amount;
                player.getInventory().setItem(i, new ItemStack(generator.getBaseGenerator().getGeneratorMaterial(), amount));
                player.closeInventory();
                SkyGenerators.getInstance().getGuiManager().openGeneratorManagerMenu(player,this);
            } else if (item.getType() == generator.getBaseGenerator().getGeneratorMaterial()) {
                if (item.getAmount() != 64){
                    int amount = Math.min(generatedBlocks, 64 - item.getAmount());
                    generatedBlocks = generatedBlocks - amount;
                    player.getInventory().addItem(new ItemStack(generator.getBaseGenerator().getGeneratorMaterial(), amount));
                    player.closeInventory();
                    SkyGenerators.getInstance().getGuiManager().openGeneratorManagerMenu(player,this);
                }
            }
        }
    }

    public void upgrade() {
        String nextGeneratorId = generator.getBaseGenerator().getNextGeneratorUpgradeId();
        if (nextGeneratorId.equals("MAX")) return;
        Generator newGenerator = SkyGenerators.getInstance().getGeneratorManager().getGenerator(nextGeneratorId, generatorId);
        setGenerator(newGenerator);
    }

    public void initilize(){
        generatorTask = new GeneratorTask(this);
        generatorTask.runTaskTimer(SkyGenerators.getInstance(), 1200, 20L * 60);
    }

    public void pickup(Player player) {
        collect(player);
        active = false;
        generatorTask.cancel();
        player.getInventory().addItem(generator.getAsItemStack());
        player.closeInventory();
        SkyGenerators.getInstance().getPlayerGeneratorManager().removeGenerator(player, generatorId);
        SkyGenerators.getInstance().getGuiManager().openGeneratorsMenu(player);
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
