package me.ehsanmna.skygenerators.service;

import me.ehsanmna.skygenerators.manager.SoundManager;
import org.bukkit.Sound;

import java.util.HashMap;
import java.util.Map;

public class SoundService {

    private final Map<String, SoundManager.SoundDetails> soundDetailsMap = new HashMap<>();


    public void registerSound(SoundManager.SoundDetails details){
        soundDetailsMap.putIfAbsent(details.soundId(), details);
    }

    public SoundManager.SoundDetails getSoundDetail(String id){
        return soundDetailsMap.getOrDefault(id, new SoundManager.SoundDetails("null", Sound.BLOCK_ANVIL_USE, 5,1));
    }

    public SoundManager.SoundDetails removeSoundDetail(String id){
        return soundDetailsMap.remove(id);
    }

}
