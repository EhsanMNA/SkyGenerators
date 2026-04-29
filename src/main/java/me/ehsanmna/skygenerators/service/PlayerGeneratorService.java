package me.ehsanmna.skygenerators.service;

import lombok.Getter;
import me.ehsanmna.skygenerators.SkyGenerators;
import me.ehsanmna.skygenerators.models.Generator;
import me.ehsanmna.skygenerators.models.PlayerGenerator;
import org.bukkit.entity.Player;

import java.util.*;

public class PlayerGeneratorService {

    // playerName - generator
    @Getter
    private Map<String, List<PlayerGenerator>> generators = new HashMap<>();

    private final SkyGenerators main = SkyGenerators.getInstance();

    public void addGenerator(Player player, PlayerGenerator generator){
        addGenerator(player.getName(), generator);
    }

    public void addGenerator(String player, PlayerGenerator generator){
        if (generators.containsKey(player)){
            generators.get(player).add(generator);
        }else {
            List<PlayerGenerator> playerGenerators = new ArrayList<>();
            playerGenerators.add(generator);
            generators.put(player, playerGenerators);
        }
    }

    public PlayerGenerator addGenerator(Player player, Generator generator){
        return addGenerator(player.getName(), generator);
    }

    public PlayerGenerator addGenerator(String playerName, Generator generator){
        PlayerGenerator playerGenerator = new PlayerGenerator(generator);
        playerGenerator.setGeneratedBlocks(0);
        playerGenerator.setPlayerName(playerName);
        addGenerator(playerName,playerGenerator);
        return playerGenerator;
    }

    public void removeGenerator(UUID id){
        for (List<PlayerGenerator> generators : generators.values()){
            for (PlayerGenerator playerGenerator : generators){
                if (playerGenerator.getGeneratorId().equals(id)) generators.remove(playerGenerator);
            }
        }
    }

    /**
        Use this function only for getting itemstack of generator!
    **/
    public PlayerGenerator convertPlayerGenerator(Player player, String generatorId){
        Generator generator = main.getGeneratorManager().getGenerator(generatorId);
        return convertPlayerGenerator(player,generator);
    }

    /**
        Use this function only for getting itemstack of generator!
     **/
    public PlayerGenerator convertPlayerGenerator(Player player, Generator generator){
        PlayerGenerator playerGenerator = new PlayerGenerator(generator);
        playerGenerator.setGeneratedBlocks(0);
        playerGenerator.setPlayerName(player.getName());
        return playerGenerator;
    }

}
