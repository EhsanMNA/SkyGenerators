package me.ehsanmna.skygenerators.manager;

import lombok.Getter;
import me.ehsanmna.skygenerators.SkyGenerators;
import me.ehsanmna.skygenerators.config.PlayerGeneratorConfig;
import me.ehsanmna.skygenerators.models.PlayerGenerator;
import me.ehsanmna.skygenerators.service.PlayerGeneratorService;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
public class PlayerGeneratorManager {

    private final SkyGenerators skyGenerators = SkyGenerators.getInstance();
    private final PlayerGeneratorService service;
    private final PlayerGeneratorConfig config;

    public PlayerGeneratorManager() {
        service = new PlayerGeneratorService();
        config = new PlayerGeneratorConfig(skyGenerators, this);
        config.load();
    }

    public List<PlayerGenerator> getGenerators(Player player){
        return service.getGenerators().getOrDefault(player.getName(), new ArrayList<>());
    }

    public PlayerGenerator getGeneratorById(UUID uuid){
        for (List<PlayerGenerator> generators : service.getGenerators().values()){
            for (PlayerGenerator playerGenerator : generators){
                if (playerGenerator.getGeneratorId().equals(uuid)) return playerGenerator;
            }
        }
        return null;
    }

    public void removeGenerator(Player player, UUID id){
        removeGenerator(player.getName(),id);
    }

    public void removeGenerator(String player, UUID id){
        service.getGenerators().get(player).removeIf(playerGenerator -> playerGenerator.getGeneratorId().equals(id));
    }

    public void removeGenerator(UUID id){
        service.removeGenerator(id);
    }

    public void removeGenerator(PlayerGenerator playerGenerator){
        removeGenerator(playerGenerator.getPlayerName(), playerGenerator.getGeneratorId());
    }

}
