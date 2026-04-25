package me.ehsanmna.skyGenerators.models.upgrade;

import lombok.Getter;
import lombok.Setter;
import me.ehsanmna.skyGenerators.SkyGenerators;
import me.ehsanmna.skyGenerators.models.PlayerGenerator;
import me.ehsanmna.skyGenerators.utils.TextUtils;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.Objects;
import java.util.UUID;

@Getter
@Setter
public class GeneratorUpgrade {

    private BaseGeneratorUpgrade baseGeneratorUpgrade;
    private UUID uuid;
    private boolean active;
    private PlayerGenerator playerGenerator;
    private GeneratorUpgradeBuild upgradeBuild;


    public GeneratorUpgrade() {
        active = false;
    }

    public GeneratorUpgrade(BaseGeneratorUpgrade baseGeneratorUpgrade, UUID uuid) {
        this.baseGeneratorUpgrade = baseGeneratorUpgrade;
        this.uuid = uuid;
        active = false;
    }

    public ItemStack getAsItemStack() {
        return getAsItemStack(uuid);
    }

    public ItemStack getAsItemStack(UUID uuid) {
        ItemStack itemStack = new ItemStack(baseGeneratorUpgrade.getMaterial());
        ItemMeta meta = itemStack.getItemMeta();
        meta.displayName(TextUtils.toComponent(baseGeneratorUpgrade.getDisplayname()));
        meta.lore(TextUtils.toComponent(baseGeneratorUpgrade.getLore()));
        if (baseGeneratorUpgrade.isGlow()) {
            meta.addEnchant(Enchantment.ARROW_DAMAGE, 1, false);
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        }
        meta.getPersistentDataContainer().set(new NamespacedKey(SkyGenerators.getInstance(), "generator-upgrade"), PersistentDataType.STRING, baseGeneratorUpgrade.getId().toLowerCase());
        meta.getPersistentDataContainer().set(new NamespacedKey(SkyGenerators.getInstance(), "generator-upgrade-uuid"), PersistentDataType.STRING, uuid.toString());
        itemStack.setItemMeta(meta);
        return itemStack;
    }

    public void pickup(Player player){
        player.getInventory().addItem(getAsItemStack());
        getUpgradeBuild().collect(player);
        setActive(false);
        setPlayerGenerator(null);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GeneratorUpgrade that = (GeneratorUpgrade) o;
        return Objects.equals(baseGeneratorUpgrade, that.baseGeneratorUpgrade) && Objects.equals(uuid, that.uuid);
    }

    @Override
    public int hashCode() {
        return Objects.hash(baseGeneratorUpgrade, uuid);
    }

}
