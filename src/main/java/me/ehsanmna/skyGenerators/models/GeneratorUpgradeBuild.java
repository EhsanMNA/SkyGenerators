package me.ehsanmna.skyGenerators.models;

import lombok.Getter;
import lombok.Setter;

@Getter
public enum GeneratorUpgradeBuild {
    COMPRESSOR, BLOCKER;

    @Setter
    int inputAmount = 64;
    String rawOutput;




}
