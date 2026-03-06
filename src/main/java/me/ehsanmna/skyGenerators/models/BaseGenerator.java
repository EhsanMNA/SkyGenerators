package me.ehsanmna.skyGenerators.models;

import lombok.Getter;
import lombok.Setter;
import me.ehsanmna.skyGenerators.utils.TextUtils;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class BaseGenerator {

    String id;
    String name;
    Component displayName;
    List<Component> lore;
    Material material;
    String permission;
    Material generatorMaterial;
    double speed;
    int space;
    String nextGeneratorUpgradeId;

    public void setLore(List<String> rawLore){
        List<Component> newLore = new ArrayList<>();
        for (String line : rawLore) newLore.add(
                TextUtils.toComponent(line.replace("%speed%",speed+"").replace("%space%",space+"")));
        lore = newLore;
    }
}
