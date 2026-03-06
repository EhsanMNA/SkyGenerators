package me.ehsanmna.skyGenerators.listeners;

import me.ehsanmna.skyGenerators.SkyGenerators;
import me.ehsanmna.skyGenerators.gui.Menu;
import me.ehsanmna.skyGenerators.gui.MenuAction;
import me.ehsanmna.skyGenerators.gui.MenuHolder;
import me.ehsanmna.skyGenerators.models.Generator;
import me.ehsanmna.skyGenerators.models.PlayerGenerator;
import me.ehsanmna.skyGenerators.utils.TextUtils;
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

    @EventHandler
    public void onCLick(InventoryClickEvent event){
        Player player = (Player) event.getWhoClicked();
        Inventory inv = event.getClickedInventory();
        int slot = event.getSlot();
        ItemStack item = event.getCurrentItem();
        ItemStack cursor = event.getCursor();

        if (inv == null) return;

        if (SkyGenerators.isDebugMode()){
            if (player.getOpenInventory().getTopInventory().getHolder() instanceof MenuHolder)
                player.sendMessage(TextUtils.toComponent("<yellow>- Top inv is a menu inv!"));
            if (item != null)
                player.sendMessage(TextUtils.toComponent("<yellow>- "+item));
            if (event.getCursor() != null)
                player.sendMessage(TextUtils.toComponent("<red>- Cursor item:<white> "+event.getCursor()));
            player.sendMessage(TextUtils.toComponent("<gold> + slot: "+slot+", +++"));
        }

        NamespacedKey idKey = new NamespacedKey(plugin, "generator-id");
        NamespacedKey guiKey = new NamespacedKey(plugin, "gui-item");
        NamespacedKey guiIdKey = new NamespacedKey(plugin, "gui-menu");
        NamespacedKey generatorSlotKey = new NamespacedKey(plugin, "generator-slot");


        if (player.getOpenInventory().getTopInventory().getHolder() instanceof MenuHolder menuHolder){
            // Do player clicked on own inv or top inv
            if (inv.equals(player.getOpenInventory().getTopInventory())){
                // player is clicked on top inv
                if (inv.getHolder() instanceof MenuHolder){
                    // player looking into a custom GUI


                    // handle gui menu click
                    if (item != null && item.getType() != Material.AIR){
                        if (item.getItemMeta().getPersistentDataContainer().has(guiKey)){
                            event.setCancelled(true);
                            String menuId = item.getItemMeta().getPersistentDataContainer().get(guiIdKey, PersistentDataType.STRING);
                            Menu menu = plugin.getGuiManager().getGuiService().getMenu(menuId);
                            menu.getActions().getOrDefault(slot, MenuAction.CANCEL).run(player, null);
                            if (!item.getItemMeta().getPersistentDataContainer().has(generatorSlotKey) &&
                            !menuId.equalsIgnoreCase("generatorManagerMenuGui")) return;
                        }
                    }

                    String menuId = menuHolder.getDescription();
                    if (menuId.equalsIgnoreCase("generatorsMenuGui")){
                        // check if clicked with generator item
                        if (cursor != null && cursor.getType() != Material.AIR){
                            if (cursor.getItemMeta().getPersistentDataContainer().has(idKey)){
                                // Clicked generator on menu
                                if (item != null && item.getType() != Material.AIR){
                                    if (!item.getItemMeta().getPersistentDataContainer().has(new NamespacedKey(plugin, "generator-slot"))){
                                        event.setCancelled(true);
                                        return;
                                    }
                                }
                                String generatorId = cursor.getItemMeta().getPersistentDataContainer().get(new NamespacedKey(plugin, "generator-name"), PersistentDataType.STRING);
                                Generator generator = plugin.getGeneratorManager().getGenerator(generatorId);
                                PlayerGenerator playerGenerator = plugin.getPlayerGeneratorManager().getService().addGenerator(player, generator);
                                playerGenerator.initilize();

                                event.setCancelled(true);
                                player.getOpenInventory().getCursor().setAmount(0);
                                cursor.setType(null);
                                item.setType(Material.AIR);
                                inv.setItem(slot, generator.getAsItemStack());
                                return;
                            }
                        }

                        // cursor is null and player clicked without any item
                        if (item == null || item.getType() == null){
                            event.setCancelled(true);
                            return;
                        }

                        ItemMeta meta = item.getItemMeta();;
                        if (meta.getPersistentDataContainer().has(idKey)){
                            // clicked on generator
                            PlayerGenerator playerGenerator = plugin.getPlayerGeneratorManager().getGeneratorById(
                                    UUID.fromString(Objects.requireNonNull(meta.getPersistentDataContainer().get(idKey, PersistentDataType.STRING))));

                            if (playerGenerator == null) {
                                player.sendMessage("Could not detect the player generator! this should be a bug maybe....");
                                return;
                            }

                            plugin.getGuiManager().openGeneratorManagerMenu(player,playerGenerator);
                        }

                        event.setCancelled(true);
                        return;
                    }
                    else if (menuId.equalsIgnoreCase("generatorManagerMenuGui")) {
                        if ((cursor != null && cursor.getType() != Material.AIR) || (item == null || item.getType() == Material.AIR)){
                            event.setCancelled(true);
                            return;
                        }

                        UUID generatorId = UUID.fromString(menuHolder.getInformation());
                        int amount = item.getAmount();
                        PlayerGenerator playerGenerator = plugin.getPlayerGeneratorManager().getGeneratorById(generatorId);
                        if (playerGenerator == null || !playerGenerator.isActive()) {
                            event.setCancelled(true);
                            return;
                        }

                        if (item.getItemMeta().getPersistentDataContainer().has(guiKey)){
                            event.setCancelled(true);
                            Menu menu = plugin.getGuiManager().getGuiService().getMenu(menuId);
                            menu.getActions().getOrDefault(slot, MenuAction.CANCEL).run(player, playerGenerator);
                            if (!item.getItemMeta().getPersistentDataContainer().has(generatorSlotKey)) return;
                        }

                        playerGenerator.setGeneratedBlocks(playerGenerator.getGeneratedBlocks() - amount);
                        return;
                    }
                }
            }

            // or player clicked on own inventory
            if (inv.equals(player.getInventory())){
                if (item == null || item.getType() == Material.AIR) return;
                if (!item.getItemMeta().getPersistentDataContainer().has(idKey)){
                    event.setCancelled(true);
                    return;
                }
            }
        }


//        if (inv.getHolder() instanceof MenuHolder menuHolder){
//            // player clicked on menu
//            if (item.getItemMeta().getPersistentDataContainer().has(idKey)){
//                // player has clicked on a generator itemstack
//                String generatorId = item.getItemMeta().getPersistentDataContainer().get(idKey, PersistentDataType.STRING);
//                PlayerGenerator playerGenerator = plugin.getPlayerGeneratorManager().getGeneratorById(UUID.fromString(generatorId));
//                plugin.getGuiManager().openGeneratorManagerMenu(player,playerGenerator);
//                event.setCancelled(true);
//                return;
//            }
//        }
    }

}
