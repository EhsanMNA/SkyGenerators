package me.ehsanmna.skyGenerators.service;

import lombok.Getter;
import me.ehsanmna.skyGenerators.SkyGenerators;
import me.ehsanmna.skyGenerators.models.BaseGenerator;
import me.ehsanmna.skyGenerators.models.Generator;

import java.util.HashMap;
import java.util.Map;

public class GeneratorService {

    // generatorId(lower case) - Generator
    @Getter
    private Map<String, BaseGenerator> generators = new HashMap<>();

    private final SkyGenerators main = SkyGenerators.getInstance();



    public void registerGenerator(String id, BaseGenerator generator){
        generators.put(id.toLowerCase(),generator);
    }

    public BaseGenerator getBaseGenerator(String generatorId){
        return generators.get(generatorId.toLowerCase());
    }

}
