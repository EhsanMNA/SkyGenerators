package me.ehsanmna.skyGenerators.utils;

import lombok.Getter;
import me.ehsanmna.skyGenerators.SkyGenerators;
import me.ehsanmna.skyGenerators.config.MessageConfig;

import java.util.HashMap;
import java.util.Map;

@Getter
public class MessageUtils {

    @Getter
    private static Map<String, String> messages = new HashMap<>();

    @Getter
    private static MessageConfig messageConfig = new MessageConfig(SkyGenerators.getInstance());


    public static String getMessage(String id){
        return messages.get(id);
    }



}
