package me.ehsanmna.skyGenerators.gui;

import lombok.Getter;
import lombok.Setter;
import me.ehsanmna.skyGenerators.SkyGenerators;
import me.ehsanmna.skyGenerators.models.upgrade.GeneratorUpgrade;
import me.ehsanmna.skyGenerators.models.PlayerGenerator;
import me.ehsanmna.skyGenerators.models.upgrade.GeneratorUpgradeBuild;
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
public class Menu implements Cloneable {

    private static final SkyGenerators PLUGIN = SkyGenerators.getInstance();
    private static final NamespacedKey GUI_ITEM_KEY = new NamespacedKey(PLUGIN, "gui-item");
    private static final NamespacedKey GENERATOR_SLOT_KEY = new NamespacedKey(PLUGIN, "generator-slot");
    private static final NamespacedKey GUI_MENU_KEY = new NamespacedKey(PLUGIN, "gui-menu");

    private String name;
    private String rawTitle;
    private String id;
    private Component title;
    private int size;
    private Map<Integer, ItemStack> items = new HashMap<>();
    private Map<Integer, MenuAction> actions = new HashMap<>();

    // GUI identifiers
    private static final String GENERATORS_MENU_ID = "generatorsMenuGui";
    private static final String GENERATOR_MANAGER_MENU_ID = "generatorManagerMenuGui";

    public Menu(String id) {
        this.id = id;
    }

    public void openToOther(Player playerMenu, Player playerToOpen) {
        String processedTitle = processTitle(null);
        Inventory gui = createInventory(playerMenu, processedTitle);

        fillMenuItems(gui, null);
        applySpecialMenuLogic(gui, playerMenu, null);

        playerToOpen.openInventory(gui);
    }

    public void open(Player player) {
        open(player, null);
    }

    public void open(Player player, PlayerGenerator generator) {
        String processedTitle = processTitle(generator);
        Inventory gui = createInventory(player, processedTitle);

        fillMenuItems(gui, generator);
        applySpecialMenuLogic(gui, player, generator);

        player.openInventory(gui);
    }

    private String processTitle(PlayerGenerator generator) {
        if (generator != null && rawTitle.contains("%generator%")) {
            return rawTitle.replace("%generator%",
                    generator.getGenerator().getBaseGenerator().getName());
        }
        return rawTitle;
    }

    private Inventory createInventory(Player player, String title) {
        return Bukkit.createInventory(new MenuHolder(title, id), size, TextUtils.toComponent(title));
    }

    private void fillMenuItems(Inventory gui, PlayerGenerator generator) {
        for (Map.Entry<Integer, ItemStack> entry : items.entrySet()) {
            ItemStack item = processItemStack(entry.getValue().clone(), generator);
            gui.setItem(entry.getKey(), item);
        }
    }

    private ItemStack processItemStack(ItemStack item, PlayerGenerator generator) {
        if (generator == null) return item;

        ItemMeta meta = item.getItemMeta();

        if (meta.getPersistentDataContainer().has(new NamespacedKey(SkyGenerators.getInstance(), "upgrade-slot"))) item = processUpgrade(item, generator);

        if (meta.hasLore()) {
            List<Component> processedLore = processLore(meta.lore(), generator);
            meta.lore(processedLore);
            item.setItemMeta(meta);
        }
        return item;
    }

    private List<Component> processLore(List<Component> lore, PlayerGenerator generator) {
        List<Component> processedLore = new ArrayList<>();

        for (Component line : lore) {
            String serializedLine = MiniMessage.miniMessage().serialize(line);
            String processedLine = replacePlaceholders(serializedLine, generator);
            processedLore.add(TextUtils.toComponent(processedLine));
        }

        return processedLore;
    }

    private ItemStack processUpgrade(ItemStack item, PlayerGenerator generator){
        ItemMeta meta = item.getItemMeta();
        String action = meta.getPersistentDataContainer().get(new NamespacedKey(SkyGenerators.getInstance(), "upgrade-slot"), PersistentDataType.STRING);
        int number = Integer.parseInt(action.split(":")[1]);
        if (generator.getUpgrades().size() < number) return item;
        GeneratorUpgrade generatorUpgrade = generator.getUpgrades().get(number - 1);
        return generatorUpgrade.getAsItemStack();
    }

    private String replacePlaceholders(String text, PlayerGenerator generator) {
        return text
                .replace("%generated%", String.valueOf(generator.getAllGenerated()))
                .replace("%cost%", formatCost(generator))
                .replace("%storage%", String.valueOf(generator.getSpace()));
    }

    private String formatCost(PlayerGenerator generator) {
        return generator.getGenerator().getBaseGenerator().getNextGeneratorRequirementMaterialName()
                + " x" + generator.getGenerator().getBaseGenerator().getNextGeneratorRequirementAmount();
    }

    private void applySpecialMenuLogic(Inventory gui, Player player, PlayerGenerator generator) {
        if (GENERATORS_MENU_ID.equalsIgnoreCase(id)) {
            applyGeneratorsMenuLogic(gui, player);
        } else if (GENERATOR_MANAGER_MENU_ID.equalsIgnoreCase(id) && generator != null) {
            applyGeneratorManagerMenuLogic(gui, player, generator);
        }
    }

    private void applyGeneratorsMenuLogic(Inventory gui, Player player) {
        addPlayerGenerators(gui, player);
        fillEmptySlotsWithGlass(gui,
                Material.WHITE_STAINED_GLASS_PANE,
                "<yellow>Drag a generator to put it on work!",
                true);
    }

    private void addPlayerGenerators(Inventory gui, Player player) {
        List<PlayerGenerator> playerGenerators = PLUGIN.getPlayerGeneratorManager().getGenerators(player);
        for (PlayerGenerator generator : playerGenerators) {
            gui.addItem(generator.getGenerator().getAsItemStack());
        }
    }

    private void applyGeneratorManagerMenuLogic(Inventory gui, Player player, PlayerGenerator generator) {
        // Store generator info in holder
        MenuHolder holder = (MenuHolder) gui.getHolder();
        holder.setInformation(generator.getGeneratorId().toString());

        // Add generated blocks to inventory
        addGeneratedBlocks(gui, generator);

        // Fill remaining slots with black glass
        fillEmptySlotsWithGlass(gui,
                Material.BLACK_STAINED_GLASS_PANE,
                " ",
                false);
    }

    private void addGeneratedBlocks(Inventory gui, PlayerGenerator generator) {
        int generatedAmount = generator.getGeneratedBlocks();
        if (generator.getAllGenerated() == 0) return;

        fillUpgradesItem(gui, generator);

        ItemStack blockItem = new ItemStack(generator.getGenerator().getBaseGenerator().getGeneratorMaterial());
        int emptySlots = InventoryUtils.getEmptySlotsCount(gui);
        int maxStackable = emptySlots * 64;

        if (emptySlots == 0) return;

        if (generatedAmount <= maxStackable) {
            // Add all generated blocks
            for (int i = 0; i < generatedAmount; i++) {
                gui.addItem(blockItem);
            }
        } else {
            // Fill all empty slots
            while (InventoryUtils.hasEmptySlots(gui)) {
                blockItem.setAmount(64);
                gui.addItem(blockItem);
            }
        }
    }

    private void fillUpgradesItem(Inventory gui, PlayerGenerator generator) {
        for (GeneratorUpgrade generatorUpgrade : generator.getUpgrades()){
            if (generatorUpgrade.getUpgradeBuild() == GeneratorUpgradeBuild.STORAGE) continue;
            int generated = generatorUpgrade.getUpgradeBuild().getGeneratedAmount();
            int emptySlots = InventoryUtils.getEmptySlotsCount(gui);
            int maxStackable = emptySlots * 64;
            ItemStack blockItem = new ItemStack(generatorUpgrade.getUpgradeBuild().getOutputStack());

            if (generated <= maxStackable) {
                // Add all generated blocks
                for (int i = 0; i < generated; i++) gui.addItem(blockItem);
            } else {
                // Fill all empty slots
                while (InventoryUtils.hasEmptySlots(gui)) {
                    blockItem.setAmount(64);
                    gui.addItem(blockItem);
                }
            }
        }
    }

    private void fillEmptySlotsWithGlass(Inventory gui, Material glassType, String displayName, boolean isGeneratorSlot) {
        for (int i = 0; i < gui.getSize(); i++) {
            if (isEmptySlot(gui, i)) {
                ItemStack glass = createGlassItem(glassType, displayName, isGeneratorSlot);
                gui.setItem(i, glass);
            }
        }
    }

    private boolean isEmptySlot(Inventory gui, int slot) {
        return gui.getItem(slot) == null || gui.getItem(slot).getType() == Material.AIR;
    }

    private ItemStack createGlassItem(Material glassType, String displayName, boolean isGeneratorSlot) {
        ItemStack glass = new ItemStack(glassType);
        glass.editMeta(meta -> {
            meta.displayName(TextUtils.toComponent(displayName));
            meta.getPersistentDataContainer().set(GUI_ITEM_KEY, PersistentDataType.BOOLEAN, true);
            meta.getPersistentDataContainer().set(GUI_MENU_KEY, PersistentDataType.STRING, id);

            if (isGeneratorSlot) {
                meta.getPersistentDataContainer().set(GENERATOR_SLOT_KEY, PersistentDataType.BOOLEAN, true);
            }
        });
        return glass;
    }

    @Override
    public Menu clone() {
        try {
            return (Menu) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException("Failed to clone menu: " + id, e);
        }
    }
}