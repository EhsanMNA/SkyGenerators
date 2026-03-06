package me.ehsanmna.skyGenerators.gui;

import lombok.Getter;
import lombok.Setter;
import me.ehsanmna.skyGenerators.SkyGenerators;
import me.ehsanmna.skyGenerators.models.PlayerGenerator;
import me.ehsanmna.skyGenerators.utils.InventoryUtils;
import me.ehsanmna.skyGenerators.utils.TextUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.*;

@Getter
@Setter
public class Menu implements Cloneable{

    private String name;
    private String rawTitle;
    private String id;
    private Component title;
    private int size;
    private Map<Integer, ItemStack> items = new HashMap<>();
    private Map<Integer, MenuAction> actions = new HashMap<>();

    public Menu(String id) {
        this.id = id;
    }

    public void open(Player player){
        open(player,null);
    }

    public void open(Player player, PlayerGenerator generator){
        String newRawTitle = rawTitle;
        if (generator != null) newRawTitle = rawTitle.replace("%generator%", generator.getGenerator().getBaseGenerator().getName());
        Inventory gui = Bukkit.createInventory(new MenuHolder(newRawTitle, id), size, TextUtils.toComponent(newRawTitle));
        for (Map.Entry<Integer, ItemStack> entry : items.entrySet()){
            ItemStack itemStack = entry.getValue().clone();
            ItemMeta meta = itemStack.getItemMeta();
            if (meta.hasLore() && generator != null){
                List<Component> itemLore = meta.lore();
                List<Component> newItemLore = new ArrayList<>();
                meta.lore().clear();
                for (Component s : itemLore){
                    String newLore = MiniMessage.miniMessage().serialize(s)
                            .replace("%generated%",generator.getGeneratedBlocks()+"")
                            .replace("%storage%",generator.getGenerator().getBaseGenerator().getSpace()+"");
                    newItemLore.add(TextUtils.toComponent(newLore));
                }
                meta.lore(newItemLore);
                itemStack.setItemMeta(meta);
            }
            gui.setItem(entry.getKey(), itemStack);
        }

        if (id.equalsIgnoreCase("generatorsMenuGui")){

            if (!SkyGenerators.getInstance().getPlayerGeneratorManager().getGenerators(player).isEmpty())
                for (PlayerGenerator playerGenerator : SkyGenerators.getInstance().getPlayerGeneratorManager().getGenerators(player)){
                    gui.addItem(playerGenerator.getGenerator().getAsItemStack());
                }

            int i = 0;
            while (i < gui.getSize()){
                if (gui.getItem(i) == null || gui.getItem(i).getType() == Material.AIR){
                    ItemStack itemStack = new ItemStack(Material.WHITE_STAINED_GLASS_PANE);
                    itemStack.editMeta(itemMeta -> {
                        itemMeta.displayName(TextUtils.toComponent("<yellow>Drag a generator to put it on work!"));
                        itemMeta.getPersistentDataContainer().set(new NamespacedKey(SkyGenerators.getInstance(), "gui-item"), PersistentDataType.BOOLEAN, true);
                        itemMeta.getPersistentDataContainer().set(new NamespacedKey(SkyGenerators.getInstance(), "generator-slot"), PersistentDataType.BOOLEAN, true);
                        itemMeta.getPersistentDataContainer().set(new NamespacedKey(SkyGenerators.getInstance(), "gui-menu"), PersistentDataType.STRING, id);
                            });
                    gui.setItem(i, itemStack);
                }
                i++;
            }
        }
        else if (id.equalsIgnoreCase("generatorManagerMenuGui")){
            if (generator == null) return;

            ((MenuHolder) gui.getHolder()).setInformation(generator.getGeneratorId().toString());

            if (generator.getGeneratedBlocks() != 0){
                int generatedAmount = generator.getGeneratedBlocks();
                ItemStack itemStack = new ItemStack(generator.getGenerator().getBaseGenerator().getGeneratorMaterial());
                int emptySlots = InventoryUtils.getEmptySlotsCount(gui) * 64;
                if (generatedAmount <= emptySlots){
                    int added = 0;
                    while (added != generatedAmount) {
                        gui.addItem(itemStack);
                        added++;
                    }
                }else {
                    while (InventoryUtils.hasEmptySlots(gui)) gui.addItem(itemStack);
                }
            }

            if (InventoryUtils.hasEmptySlots(gui)){
                int i = 0;
                while (i < gui.getSize()){
                    if (gui.getItem(i) == null || gui.getItem(i).getType() == Material.AIR){
                        ItemStack itemStack = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
                        itemStack.editMeta(itemMeta -> {
                            itemMeta.displayName(TextUtils.toComponent(" "));
                            itemMeta.getPersistentDataContainer().set(new NamespacedKey(SkyGenerators.getInstance(), "gui-item"), PersistentDataType.BOOLEAN, true);
                            itemMeta.getPersistentDataContainer().set(new NamespacedKey(SkyGenerators.getInstance(), "gui-menu"), PersistentDataType.STRING, id);
                        });
                        gui.setItem(i, itemStack);
                    }
                    i++;
                }
            }

        }

        player.openInventory(gui);
    }

    @Override
    public Menu clone() {
        Menu menu = null;
        try {
            menu = (Menu) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
        return menu;
    }
}
