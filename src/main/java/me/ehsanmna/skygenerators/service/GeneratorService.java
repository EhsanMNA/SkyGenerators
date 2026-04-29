package me.ehsanmna.skygenerators.service;

import lombok.Getter;
import me.ehsanmna.skygenerators.SkyGenerators;
import me.ehsanmna.skygenerators.models.BaseGenerator;

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
