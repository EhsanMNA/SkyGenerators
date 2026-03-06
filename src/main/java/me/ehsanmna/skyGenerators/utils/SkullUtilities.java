package me.ehsanmna.skyGenerators.utils;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.UUID;

public class SkullUtilities {

    public static ItemStack createCustomSkull(String base64Texture) {
        ItemStack skull = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta skullMeta = (SkullMeta) skull.getItemMeta();

        try {
            // Use reflections to create a GameProfile
            Class<?> gameProfileClass = Class.forName("com.mojang.authlib.GameProfile");
            Constructor<?> gameProfileConstructor = gameProfileClass.getConstructor(UUID.class, String.class);
            Object gameProfile = gameProfileConstructor.newInstance(UUID.randomUUID(), null);

            // Use reflections to create a Property
            Class<?> propertyClass = Class.forName("com.mojang.authlib.properties.Property");
            Constructor<?> propertyConstructor = propertyClass.getConstructor(String.class, String.class);
            Object property = propertyConstructor.newInstance("textures", base64Texture);

            // Use reflections to add the Property to the GameProfile
            Field propertiesField = gameProfileClass.getDeclaredField("properties");
            propertiesField.setAccessible(true);
            Object propertyMap = propertiesField.get(gameProfile);
            Method putMethod = propertyMap.getClass().getMethod("put", Object.class, Object.class);
            putMethod.invoke(propertyMap, "textures", property);

            // Use reflections to set the GameProfile in the SkullMeta
            Field profileField = skullMeta.getClass().getDeclaredField("profile");
            profileField.setAccessible(true);
            profileField.set(skullMeta, gameProfile);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Set the SkullMeta back to the ItemStack
        skull.setItemMeta(skullMeta);
        return skull;
    }

}
