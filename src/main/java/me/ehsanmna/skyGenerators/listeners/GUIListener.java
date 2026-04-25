package me.ehsanmna.skyGenerators.listeners;

import me.ehsanmna.skyGenerators.SkyGenerators;
import me.ehsanmna.skyGenerators.events.SkyGeneratorInputEvent;
import me.ehsanmna.skyGenerators.events.SkyGeneratorPreInputEvent;
import me.ehsanmna.skyGenerators.events.SkyGeneratorUpgradeInputEvent;
import me.ehsanmna.skyGenerators.events.SkyGeneratorUpgradePreInputEvent;
import me.ehsanmna.skyGenerators.gui.Menu;
import me.ehsanmna.skyGenerators.gui.MenuAction;
import me.ehsanmna.skyGenerators.gui.MenuHolder;
import me.ehsanmna.skyGenerators.models.Generator;
import me.ehsanmna.skyGenerators.models.upgrade.GeneratorUpgrade;
import me.ehsanmna.skyGenerators.models.PlayerGenerator;
import me.ehsanmna.skyGenerators.utils.TextUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.Objects;
import java.util.UUID;

public class GUIListener implements Listener {

    private final SkyGenerators plugin = SkyGenerators.getInstance();

    // Namespaced keys
    private NamespacedKey generatorIdKey;
    private NamespacedKey guiItemKey;
    private NamespacedKey guiIdKey;
    private NamespacedKey generatorSlotKey;
    private NamespacedKey upgradeActionKey;
    private NamespacedKey generatorNameKey;
    private NamespacedKey generatorUpgradeKey;
    private NamespacedKey upgradeSlotKey;

    // Menu identifiers
    private static final String GENERATORS_MENU_ID = "generatorsMenuGui";
    private static final String GENERATOR_MANAGER_MENU_ID = "generatorManagerMenuGui";

    public GUIListener() {
        initializeNamespacedKeys();
    }

    private void initializeNamespacedKeys() {
        generatorIdKey = new NamespacedKey(plugin, "generator-id");
        guiItemKey = new NamespacedKey(plugin, "gui-item");
        guiIdKey = new NamespacedKey(plugin, "gui-menu");
        generatorSlotKey = new NamespacedKey(plugin, "generator-slot");
        upgradeActionKey = new NamespacedKey(plugin, "upgrade-action");
        generatorNameKey = new NamespacedKey(plugin, "generator-name");
        generatorUpgradeKey = new NamespacedKey(plugin, "generator-upgrade");
        upgradeSlotKey = new NamespacedKey(plugin, "upgrade-slot");
    }



    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        Inventory clickedInventory = event.getClickedInventory();
        int slot = event.getSlot();
        ItemStack clickedItem = event.getCurrentItem();
        ItemStack cursorItem = event.getCursor();

        if (clickedInventory == null) return;

        debugInfo(player, clickedInventory, clickedItem, cursorItem, slot);

        // Check if player is viewing a custom GUI
        if (!isViewingCustomGUI(player)) return;

        // Handle click based on which inventory was clicked
        if (isTopInventory(clickedInventory, player)) {
            handleTopInventoryClick(event, player, clickedInventory, slot, clickedItem, cursorItem);
        } else if (isPlayerInventory(clickedInventory, player)) {
            handlePlayerInventoryClick(event, clickedItem);
        }
    }



    private boolean isViewingCustomGUI(Player player) {
        return player.getOpenInventory().getTopInventory().getHolder() instanceof MenuHolder;
    }

    private boolean isTopInventory(Inventory inventory, Player player) {
        return inventory.equals(player.getOpenInventory().getTopInventory());
    }

    private boolean isPlayerInventory(Inventory inventory, Player player) {
        return inventory.equals(player.getInventory());
    }

    private void debugInfo(Player player, Inventory inventory, ItemStack clickedItem, ItemStack cursorItem, int slot) {
        if (!SkyGenerators.isDebugMode()) return;

        if (isViewingCustomGUI(player)) {
            player.sendMessage(TextUtils.toComponent("<yellow>- Top inv is a menu inv!"));
        }
        if (clickedItem != null) {
            player.sendMessage(TextUtils.toComponent("<yellow>- " + clickedItem));
        }
        if (cursorItem != null) {
            player.sendMessage(TextUtils.toComponent("<red>- Cursor item: <white>" + cursorItem));
        }
        player.sendMessage(TextUtils.toComponent("<gold> + slot: " + slot + ", +++"));
    }



    private void handleTopInventoryClick(InventoryClickEvent event, Player player,
                                         Inventory inventory, int slot,
                                         ItemStack clickedItem, ItemStack cursorItem) {

        MenuHolder menuHolder = (MenuHolder) player.getOpenInventory().getTopInventory().getHolder();
        String menuId = menuHolder.getDescription();

        // Handle GUI item clicks
        if (isGuiItemClick(clickedItem)) {
            handleGuiItemClick(event, player, clickedItem, slot);
        }

        // Handle specific menu logic
        if (GENERATORS_MENU_ID.equalsIgnoreCase(menuId)) {
            handleGeneratorsMenuClick(event, player, inventory, slot, clickedItem, cursorItem);
        } else if (GENERATOR_MANAGER_MENU_ID.equalsIgnoreCase(menuId)) {
            handleGeneratorManagerMenuClick(event, player, menuHolder, slot, clickedItem, cursorItem);
        }
    }



    private boolean isGuiItemClick(ItemStack item) {
        return item != null &&
                item.getType() != Material.AIR &&
                item.getItemMeta().getPersistentDataContainer().has(guiItemKey);
    }

    private void handleGuiItemClick(InventoryClickEvent event, Player player, ItemStack clickedItem, int slot) {
        event.setCancelled(true);

        String menuId = clickedItem.getItemMeta()
                .getPersistentDataContainer()
                .get(guiIdKey, PersistentDataType.STRING);

        Menu menu = plugin.getGuiManager().getGuiService().getMenu(menuId);
        MenuAction action = menu.getActions().getOrDefault(slot, MenuAction.CANCEL);
        action.run(player, null);

        // Don't process further if it's not a generator slot or manager menu
        if (!isGeneratorSlot(clickedItem) && !menuId.equalsIgnoreCase(GENERATOR_MANAGER_MENU_ID)) {
            event.setCancelled(true);
        }
    }



    private boolean isGeneratorSlot(ItemStack item) {
        return item.getItemMeta().getPersistentDataContainer().has(generatorSlotKey);
    }

    private void handleGeneratorsMenuClick(InventoryClickEvent event, Player player,
                                           Inventory inventory, int slot,
                                           ItemStack clickedItem, ItemStack cursorItem) {

        // Handle placing generator in empty slot
        if (isPlacingGeneratorInSlot(cursorItem, clickedItem)) {
            handleGeneratorPlacement(event, player, inventory, slot, cursorItem, clickedItem);
            return;
        }

        // Handle clicking on empty slot
        if (isEmptySlot(clickedItem)) {
            event.setCancelled(true);
            return;
        }

        // Handle clicking on existing generator
        if (isGeneratorItem(clickedItem)) {
            handleGeneratorClick(event, player, clickedItem);
        }

        event.setCancelled(true);
    }


    private boolean isPlacingGeneratorInSlot(ItemStack cursorItem, ItemStack slotItem) {
        return cursorItem != null &&
                cursorItem.getType() != Material.AIR &&
                isGeneratorItem(cursorItem) &&
                (isEmptySlot(slotItem) || isGeneratorSlot(slotItem));
    }

    private boolean isEmptySlot(ItemStack item) {
        return item == null || item.getType() == Material.AIR;
    }




    private boolean isGeneratorItem(ItemStack item) {
        return item != null &&
                item.getType() != Material.AIR &&
                item.getItemMeta().getPersistentDataContainer().has(generatorIdKey);
    }

    private void handleGeneratorPlacement(InventoryClickEvent event, Player player,
                                          Inventory inventory, int slot,
                                          ItemStack cursorItem, ItemStack slotItem) {

        // Check if slot is valid for placement
        if (slotItem != null && slotItem.getType() != Material.AIR &&
                !slotItem.getItemMeta().getPersistentDataContainer().has(generatorSlotKey)) {
            event.setCancelled(true);
            return;
        }


        String generatorId = cursorItem.getItemMeta()
                .getPersistentDataContainer()
                .get(generatorNameKey, PersistentDataType.STRING);

        Generator generator = plugin.getGeneratorManager().getGenerator(generatorId);

        SkyGeneratorPreInputEvent event1 = new SkyGeneratorPreInputEvent(player,generator);
        Bukkit.getServer().getPluginManager().callEvent(event1);
        if (event1.isCancelled()) {
            event.setCancelled(true);
            return;
        }

        PlayerGenerator playerGenerator = plugin.getPlayerGeneratorManager()
                .getService()
                .addGenerator(player, generator);
        playerGenerator.initilize();

        // Clear cursor and place generator
        event.setCancelled(true);
        player.getOpenInventory().setCursor(null);
        inventory.setItem(slot, generator.getAsItemStack());

        SkyGeneratorInputEvent event2 = new SkyGeneratorInputEvent(player,playerGenerator);
        Bukkit.getServer().getPluginManager().callEvent(event2);
    }

    private void handleGeneratorClick(InventoryClickEvent event, Player player, ItemStack clickedItem) {
        ItemMeta meta = clickedItem.getItemMeta();
        UUID generatorUUID = UUID.fromString(Objects.requireNonNull(
                meta.getPersistentDataContainer().get(generatorIdKey, PersistentDataType.STRING)));

        PlayerGenerator playerGenerator = plugin.getPlayerGeneratorManager()
                .getGeneratorById(generatorUUID);

        if (playerGenerator == null) {
            player.sendMessage("Could not detect the player generator! This might be a bug...");
            return;
        }

        plugin.getGuiManager().openGeneratorManagerMenu(player, playerGenerator);
    }

    private void handleGeneratorManagerMenuClick(InventoryClickEvent event, Player player,
                                                 MenuHolder menuHolder, int slot,
                                                 ItemStack clickedItem, ItemStack cursorItem) {

        UUID generatorId = UUID.fromString(menuHolder.getInformation());
        PlayerGenerator playerGenerator = plugin.getPlayerGeneratorManager().getGeneratorById(generatorId);

        if (isInvalidGenerator(playerGenerator)) {
            event.setCancelled(true);
            return;
        }

        // Handle upgrader item put
        if (isUpgraderItem(clickedItem) || isUpgraderItem(cursorItem)){
            handlePlayerGeneratorUpgrader(event, player, clickedItem, playerGenerator);
            return;
        }

        // Cancel if trying to place item or click empty slot (except upgrader put)
        if (isInvalidManagerMenuClick(cursorItem, clickedItem)) {
            event.setCancelled(true);
            return;
        }

        // Handle upgrade button click
        if (isUpgradeItem(clickedItem)) {
            handleUpgradeClick(event, player, playerGenerator);
            return;
        }

        // Handle GUI item click
        if (isGuiItemClick(clickedItem)) {
            handleManagerGuiItemClick(event, player, clickedItem, slot, playerGenerator);
            return;
        }

        // Handle resource collection
        handleResourceCollection(event, playerGenerator, clickedItem);
    }

    private boolean isInvalidManagerMenuClick(ItemStack cursorItem, ItemStack clickedItem) {
        return (cursorItem != null && cursorItem.getType() != Material.AIR) ||
                isEmptySlot(clickedItem);
    }

    private boolean isInvalidGenerator(PlayerGenerator generator) {
        return generator == null || !generator.isActive();
    }

    private boolean isUpgradeItem(ItemStack item) {
        return item.getItemMeta().getPersistentDataContainer().has(upgradeActionKey);
    }

    private void handleUpgradeClick(InventoryClickEvent event, Player player, PlayerGenerator generator) {
        event.setCancelled(true);
        generator.upgrade(player);
    }

    private void handleManagerGuiItemClick(InventoryClickEvent event, Player player,
                                           ItemStack clickedItem, int slot,
                                           PlayerGenerator generator) {
        event.setCancelled(true);

        String menuId = clickedItem.getItemMeta()
                .getPersistentDataContainer()
                .get(guiIdKey, PersistentDataType.STRING);

        Menu menu = plugin.getGuiManager().getGuiService().getMenu(menuId);
        MenuAction action = menu.getActions().getOrDefault(slot, MenuAction.CANCEL);
        action.run(player, generator);
    }

    private void handleResourceCollection(InventoryClickEvent event,
                                          PlayerGenerator generator,
                                          ItemStack clickedItem) {
        int amount = clickedItem.getAmount();
        if (clickedItem.equals(generator.getGenerator().getBaseGenerator().getGeneratorMaterial())){
            generator.setGeneratedBlocks(generator.getGeneratedBlocks() - amount);
        }else {
            for (GeneratorUpgrade generatorUpgrade : generator.getUpgrades()){
                if (generatorUpgrade.getUpgradeBuild().getOutputStack().equals(clickedItem)){
                    generatorUpgrade.getUpgradeBuild().setGeneratedAmount(generatorUpgrade.getUpgradeBuild().getGeneratedAmount() - amount);
                }
            }
        }
    }

    private boolean isUpgraderItem(ItemStack itemStack){
        System.out.println("checking for is upgrader item");
        if (itemStack == null || itemStack.getType() == Material.AIR) return false;
        return itemStack.getItemMeta().getPersistentDataContainer().has(upgradeSlotKey) || itemStack.getItemMeta().getPersistentDataContainer().has(generatorUpgradeKey);
    }

    private void handlePlayerGeneratorUpgrader(InventoryClickEvent event,
                                               Player player,
                                               ItemStack clickedItem,
                                               PlayerGenerator playerGenerator) {
        event.setCancelled(true);

        if (event.getCursor() == null || event.getCursor().getType() == Material.AIR) {
            // clicked with out any items, handle the upgrade pickup
            if (!clickedItem.getItemMeta().getPersistentDataContainer().has(generatorUpgradeKey)) return;
            handleGeneratorUpgradePickup(event, player, clickedItem, playerGenerator);
            return;
        }

        System.out.println("putttt");

        GeneratorUpgrade generatorUpgrade = plugin.getGeneratorUpgradeManager().getGeneratorUpgradeFromItem(event.getCursor(), playerGenerator);
        SkyGeneratorUpgradePreInputEvent preInputEvent = new SkyGeneratorUpgradePreInputEvent(player, playerGenerator, generatorUpgrade);
        Bukkit.getPluginManager().callEvent(preInputEvent);
        if (preInputEvent.isCancelled()) return;
        handleGeneratorUpgradePut(event, player, playerGenerator, event.getCursor());

        SkyGeneratorUpgradeInputEvent inputEvent = new SkyGeneratorUpgradeInputEvent(player, playerGenerator, generatorUpgrade);
        Bukkit.getPluginManager().callEvent(inputEvent);
    }

    private void handleGeneratorUpgradePut(InventoryClickEvent event, Player player, PlayerGenerator playerGenerator, ItemStack cursor) {
        event.getClickedInventory().setItem(event.getSlot(), cursor);
        player.setItemOnCursor(null);
        GeneratorUpgrade generatorUpgrade = plugin.getGeneratorUpgradeManager().getGeneratorUpgradeFromItem(cursor, playerGenerator);
        generatorUpgrade.setActive(true);
        generatorUpgrade.setPlayerGenerator(playerGenerator);
        playerGenerator.getUpgrades().add(generatorUpgrade);
    }

    private void handleGeneratorUpgradePickup(InventoryClickEvent event, Player player, ItemStack clickedItem, PlayerGenerator playerGenerator) {
//        player.getInventory().addItem(clickedItem);
        event.getClickedInventory().setItem(event.getSlot(), null);
        plugin.getGuiManager().openGeneratorManagerMenu(player, playerGenerator);
        GeneratorUpgrade generatorUpgrade = plugin.getGeneratorUpgradeManager().getGeneratorUpgradeFromItem(clickedItem, playerGenerator);
        generatorUpgrade.setPlayerGenerator(null);
        generatorUpgrade.pickup(player);
        playerGenerator.getUpgrades().remove(generatorUpgrade);
    }

    private void handlePlayerInventoryClick(InventoryClickEvent event, ItemStack clickedItem) {
        // Cancel if trying to move non-generator items while GUI is open
        if (clickedItem == null) return;
        if (isUpgraderItem(event.getCursor()) || isGeneratorItem(event.getCurrentItem())) return;
        if (isGeneratorItem(clickedItem) || isUpgraderItem(clickedItem)) return;

        event.setCancelled(true);
    }
}