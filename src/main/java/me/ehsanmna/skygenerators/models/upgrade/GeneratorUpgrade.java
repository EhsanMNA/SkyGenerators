package me.ehsanmna.skygenerators.models.upgrade;

import lombok.Getter;
import lombok.Setter;
import me.azerima.skymaterials.utils.CustomItem;
import me.azerima.skymaterials.utils.CustomItemManager;
import me.ehsanmna.skygenerators.SkyGenerators;
import me.ehsanmna.skygenerators.models.MaterialType;
import me.ehsanmna.skygenerators.models.PlayerGenerator;
import me.ehsanmna.skygenerators.utils.InventoryUtils;
import me.ehsanmna.skygenerators.utils.TextUtils;
import org.bukkit.Material;
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
    private int generatedAmount;



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
        collect(player);
        setActive(false);
        setPlayerGenerator(null);
    }

    public void generate(){
        if (!isActive()) return;
        if (getPlayerGenerator() == null) return;
        switch (getBaseGeneratorUpgrade().getUpgradeBuild()){
            case BLOCKER, COMPRESSOR -> {
                if (getPlayerGenerator().getGeneratedBlocks() < getBaseGeneratorUpgrade().getUpgradeBuild().getInputAmount()) return;
                getPlayerGenerator().setGeneratedBlocks(getPlayerGenerator().getGeneratedBlocks() - getBaseGeneratorUpgrade().getUpgradeBuild().getInputAmount());
                generatedAmount += Integer.parseInt(getBaseGeneratorUpgrade().getUpgradeBuild().getRawOutput().split(":")[2]);
            }
        }
    }

    public void collect(Player player){
        if (!isActive()) return;
        if (getPlayerGenerator() == null) return;
        while (InventoryUtils.hasEmptySlots(player.getInventory())){
            if (generatedAmount == 0) break;
            int amount = Math.min(generatedAmount, 64);
            generatedAmount -= amount;
            ItemStack itemStack = getBaseGeneratorUpgrade().getUpgradeBuild().getOutputStack(this);
            itemStack.setAmount(amount);
            player.getInventory().addItem(itemStack);
        }
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
