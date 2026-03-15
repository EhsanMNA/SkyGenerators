package me.ehsanmna.skyGenerators.models;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Material;

import java.util.List;

@Getter
@Setter
public class GeneratorUpgrade {

    private String id;
    private String name;
    private String displayname;
    private Material material;
    private boolean glow;
    private List<String> lore;


}
