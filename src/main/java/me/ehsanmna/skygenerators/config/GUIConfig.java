package me.ehsanmna.skygenerators.config;

import me.ehsanmna.skygenerators.SkyGenerators;
import me.ehsanmna.skygenerators.gui.Menu;
import me.ehsanmna.skygenerators.gui.MenuAction;
import me.ehsanmna.skygenerators.gui.MenuHolder;
import me.ehsanmna.skygenerators.manager.GUIManager;
import me.ehsanmna.skygenerators.utils.TextUtils;
import org.bukkit.*;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;

public class GUIConfig {

    private final SkyGenerators plugin;
    private YamlConfiguration configuration;
    File file;
    private final GUIManager guiManager;

    public GUIConfig(SkyGenerators plugin, GUIManager guiManager) {
        this.plugin = plugin;
        this.guiManager = guiManager;
        setup();
    }

    public void setup(){
        file = new File(plugin.getDataFolder(),"gui.yml");
        if(!file.exists()){
            plugin.saveResource("gui.yml", false);
        }
        configuration = YamlConfiguration.loadConfiguration(file);
    }

    public void load(){
        for (String menuId : configuration.getKeys(false)){
            ConfigurationSection menuSection = configuration.getConfigurationSection(menuId);
            String title = menuSection.getString("title","Ich bin nein title");
            int size = menuSection.getInt("size",36);
            Inventory inventory = Bukkit.createInventory(new MenuHolder(title, menuId), size, TextUtils.toComponent(title));

            Menu menu = new Menu(menuId);
            menu.setName(menuId);
            menu.setTitle(TextUtils.toComponent(title));
            menu.setRawTitle(title);
            menu.setSize(size);

            for (String s : menuSection.getConfigurationSection("contents").getKeys(false)){
                int slot = Integer.parseInt(s);
                ConfigurationSection itemSection = menuSection.getConfigurationSection("contents."+s);
                ItemStack itemStack = loadItem(itemSection);
                itemStack.editMeta(itemMeta -> itemMeta.getPersistentDataContainer().set(new NamespacedKey(plugin, "gui-menu"), PersistentDataType.STRING, menuId));
                inventory.setItem(slot,itemStack);
                menu.getItems().put(slot, itemStack);
                if (itemSection.contains("action")) {
                    String actionName = itemSection.getString("action");
                    MenuAction menuAction;
                    if (actionName.contains("-")){
                        actionName = itemSection.getString("action").split("-")[0];
                        menuAction = MenuAction.valueOf(actionName.toUpperCase());
                        menuAction.setArgument(itemSection.getString("action").split("-")[1]);
                    }else menuAction = MenuAction.valueOf(actionName.toUpperCase().contains(":") ?
                            actionName.toUpperCase().split(":")[0] : actionName.toUpperCase());
                    menu.getActions().put(slot, menuAction);
                }
            }

            guiManager.getGuiService().getMenus().put(menuId,menu);
        }
    }

    private ItemStack loadItem(@NotNull ConfigurationSection section){
        try {
            ItemStack itemStack = new ItemStack(Material.valueOf(section.getString("material","STONE")));
            itemStack.setAmount(section.getInt("amount",1));
            itemStack.editMeta(itemMeta -> {
                itemMeta.displayName(TextUtils.toComponent(section.getString("name","<red>Ich bin Nein")));
                itemMeta.setCustomModelData(section.getInt("customModelData",0));
                itemMeta.getPersistentDataContainer().set(new NamespacedKey(plugin, "gui-item"), PersistentDataType.BOOLEAN, true);
                if (section.getBoolean("glow", false)) {
                    itemMeta.addEnchant(Enchantment.ARROW_DAMAGE, 1, false);
                    itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
                }
                if (section.contains("lore")) itemMeta.lore(TextUtils.toComponent(section.getStringList("lore")));
                if (section.contains("action")){
                    String action = section.getString("action","");
                    if (action.equalsIgnoreCase("upgrade"))
                        itemMeta.getPersistentDataContainer().set(new NamespacedKey(plugin, "upgrade-action"), PersistentDataType.BOOLEAN, true);
                    else if (action.contains("upgrade_slot")){
                        itemMeta.getPersistentDataContainer().set(new NamespacedKey(plugin, "upgrade-slot"), PersistentDataType.STRING, action);
                    }
                }
            });
            return itemStack;
        }catch (Exception error){
            return new ItemStack(Material.STONE);
        }
    }

    private Location loadLocation(@NotNull ConfigurationSection section) {
        World world = Bukkit.getWorld(section.getString("world","world"));
        double x = section.getDouble("x",0);
        double y = section.getDouble("y",0);
        double z = section.getDouble("z",0);
        double yaw = section.getDouble("yaw",0);
        double pitch = section.getDouble("pitch",0);
        return new Location(world,x,y,z, (float) yaw, (float) pitch);
    }

    public void save(){
        try {
            configuration.save(file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void reload(){
        setup();
        save();
        load();
    }

}
