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

    private String id;
    private String name;
    private Component displayName;
    private List<Component> lore;
    private Material material;
    private String permission;
    private Material generatorMaterial;
    private double speed;
    private int space;
    private int maximumEnergy;
    private String nextGeneratorUpgradeId;
    private String upgradeRequirement;

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
