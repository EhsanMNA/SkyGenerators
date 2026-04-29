package me.ehsanmna.skygenerators.models;

import lombok.Getter;
import lombok.Setter;
import me.ehsanmna.skygenerators.SkyGenerators;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.UUID;

@Getter
@Setter
public class Generator{

    private final BaseGenerator baseGenerator;
    private final UUID id;
    private int energy = 0;

    public Generator(BaseGenerator baseGenerator) {
        this.baseGenerator = baseGenerator;
        id = UUID.randomUUID();
        energy = baseGenerator.getMaximumEnergy();
    }

    public Generator(BaseGenerator baseGenerator, UUID id) {
        this.baseGenerator = baseGenerator;
        this.id = id;
        energy = baseGenerator.getMaximumEnergy();
    }

    public Generator(BaseGenerator baseGenerator, UUID id, int energy) {
        this.baseGenerator = baseGenerator;
        this.id = id;
        this.energy = energy;
    }

    public ItemStack getAsItemStack(){
        ItemStack itemStack = new ItemStack(baseGenerator.getMaterial());
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.displayName(baseGenerator.getDisplayName());
        itemMeta.lore(baseGenerator.getLore());
        NamespacedKey key = new NamespacedKey(SkyGenerators.getInstance(), "generator-id");
        NamespacedKey nameKey = new NamespacedKey(SkyGenerators.getInstance(), "generator-name");
        NamespacedKey energyKey = new NamespacedKey(SkyGenerators.getInstance(), "generator-energy");
        itemMeta.getPersistentDataContainer().set(key, PersistentDataType.STRING, id.toString());
        itemMeta.getPersistentDataContainer().set(nameKey, PersistentDataType.STRING, baseGenerator.getId());
        itemMeta.getPersistentDataContainer().set(energyKey, PersistentDataType.INTEGER, getEnergy());
        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }

}
