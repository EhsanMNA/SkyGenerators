package me.ehsanmna.skyGenerators.manager;

import lombok.Getter;
import me.ehsanmna.skyGenerators.SkyGenerators;
import me.ehsanmna.skyGenerators.config.GeneratorConfig;
import me.ehsanmna.skyGenerators.models.BaseGenerator;
import me.ehsanmna.skyGenerators.models.Generator;
import me.ehsanmna.skyGenerators.service.GeneratorService;

import java.util.UUID;

@Getter
public class GeneratorManager {

    private final SkyGenerators skyGenerators = SkyGenerators.getInstance();
    private final GeneratorService service;
    private final GeneratorConfig config;


    public GeneratorManager() {
        service = new GeneratorService();
        config = new GeneratorConfig(skyGenerators, this);
        config.load();
    }

    public BaseGenerator getBaseGenerator(String generatorId){
        return service.getBaseGenerator(generatorId.toLowerCase());
    }

    public Generator getGenerator(BaseGenerator baseGenerator){
        return new Generator(baseGenerator);
    }

    public Generator getGenerator(String baseGenerator){
        return new Generator(service.getBaseGenerator(baseGenerator));
    }

    public Generator getGenerator(String baseGenerator, UUID id){
        return new Generator(service.getBaseGenerator(baseGenerator), id);
    }

}
