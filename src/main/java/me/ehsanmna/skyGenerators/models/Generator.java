package me.ehsanmna.skyGenerators.models;

import lombok.Getter;
import lombok.Setter;
import me.ehsanmna.skyGenerators.SkyGenerators;
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

    public Generator(BaseGenerator baseGenerator) {
        this.baseGenerator = baseGenerator;
        id = UUID.randomUUID();
    }

    public Generator(BaseGenerator baseGenerator, UUID id) {
        this.baseGenerator = baseGenerator;
        this.id = id;
    }


    public ItemStack getAsItemStack(){
        ItemStack itemStack = new ItemStack(baseGenerator.getMaterial());
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.displayName(baseGenerator.getDisplayName());
        itemMeta.lore(baseGenerator.getLore());
        NamespacedKey key = new NamespacedKey(SkyGenerators.getInstance(), "generator-id");
        NamespacedKey nameKey = new NamespacedKey(SkyGenerators.getInstance(), "generator-name");
        itemMeta.getPersistentDataContainer().set(key, PersistentDataType.STRING, id.toString());
        itemMeta.getPersistentDataContainer().set(nameKey, PersistentDataType.STRING, baseGenerator.getId());
        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }

}
