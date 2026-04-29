package me.ehsanmna.skygenerators.manager;

import lombok.Getter;
import me.ehsanmna.skygenerators.SkyGenerators;
import me.ehsanmna.skygenerators.config.SoundsConfig;
import me.ehsanmna.skygenerators.service.SoundService;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

@Getter
public class SoundManager {


    private final SoundService soundsService;
    private final SoundsConfig soundsConfig;


    public SoundManager() {
        this.soundsService = new SoundService();
        this.soundsConfig = new SoundsConfig(SkyGenerators.getInstance(), this);
    }

    public void registerSound(SoundDetails soundDetails){
        soundsService.registerSound(soundDetails);
    }

    public SoundDetails getSoundDetail(String id){
        return soundsService.getSoundDetail(id);
    }


    public record SoundDetails(String soundId, Sound sound, int power, double pitch){
        public void playSound(Player player) {
            player.playSound(player.getLocation(), sound, (float) power, (float) pitch);
        }
    }

}
