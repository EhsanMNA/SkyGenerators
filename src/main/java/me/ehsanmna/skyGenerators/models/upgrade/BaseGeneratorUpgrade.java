package me.ehsanmna.skyGenerators.models.upgrade;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Material;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Getter
@Setter
public class BaseGeneratorUpgrade {

    private String id;
    private String name;
    private String displayname;
    private Material material;
    private boolean glow;
    private List<String> lore;


    public GeneratorUpgrade getAsGenerator(){
        GeneratorUpgrade generatorUpgrade = new GeneratorUpgrade();
        generatorUpgrade.setUuid(UUID.randomUUID());
        generatorUpgrade.setBaseGeneratorUpgrade(this);
        generatorUpgrade.setActive(false);
        return generatorUpgrade;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BaseGeneratorUpgrade that = (BaseGeneratorUpgrade) o;
        return Objects.equals(id, that.id) && Objects.equals(name, that.name) && material == that.material;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, material);
    }
}
