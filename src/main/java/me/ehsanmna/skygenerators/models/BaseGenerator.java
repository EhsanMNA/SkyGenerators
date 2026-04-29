package me.ehsanmna.skygenerators.models;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import me.ehsanmna.skygenerators.utils.TextUtils;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
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
    String upgradeRequirement;

    public void setLore(List<String> rawLore){
        List<Component> newLore = new ArrayList<>();
        for (String line : rawLore) newLore.add(
                TextUtils.toComponent(line.replace("%speed%",speed+"").replace("%space%",space+"")));
        lore = newLore;
    }

    public MaterialType getNextGeneratorRequirementType(){
        if (nextGeneratorUpgradeId.equals("MAX")) return MaterialType.MATERIAL;
        return MaterialType.valueOf(upgradeRequirement.split("-")[0].toUpperCase());
    }

    public int getNextGeneratorRequirementAmount(){
        if (nextGeneratorUpgradeId.equals("MAX")) return 0;
        return Integer.parseInt(upgradeRequirement.split("-")[2]);
    }

    public String getNextGeneratorRequirementMaterialName(){
        if (nextGeneratorUpgradeId.equals("MAX")) return nextGeneratorUpgradeId;
        return upgradeRequirement.split("-")[1];
    }
}
