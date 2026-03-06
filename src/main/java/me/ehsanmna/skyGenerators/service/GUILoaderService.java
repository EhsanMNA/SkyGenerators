package me.ehsanmna.skyGenerators.service;

import lombok.Getter;
import me.ehsanmna.skyGenerators.gui.Menu;

import java.util.HashMap;
import java.util.Map;

@Getter
public class GUILoaderService {

    private Map<String, Menu> menus = new HashMap<>();


    public Menu getMenu(String id){
        return menus.get(id);
    }

}
