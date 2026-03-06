package me.ehsanmna.skyGenerators.config;

import me.ehsanmna.skyGenerators.SkyGenerators;
import me.ehsanmna.skyGenerators.gui.Menu;
import me.ehsanmna.skyGenerators.gui.MenuAction;
import me.ehsanmna.skyGenerators.gui.MenuHolder;
import me.ehsanmna.skyGenerators.manager.GUIManager;
import me.ehsanmna.skyGenerators.utils.TextUtils;
import org.bukkit.*;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.Inventory;
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
                    }else menuAction = MenuAction.valueOf(actionName.toUpperCase());
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
                if (section.contains("lore")) itemMeta.lore(TextUtils.toComponent(section.getStringList("lore")));
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
