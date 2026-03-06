package me.ehsanmna.skyGenerators.tasks;

import me.ehsanmna.skyGenerators.models.PlayerGenerator;
import org.bukkit.scheduler.BukkitRunnable;

public class GeneratorTask extends BukkitRunnable {

    private final PlayerGenerator playerGenerator;

    public GeneratorTask(PlayerGenerator playerGenerator) {
        this.playerGenerator = playerGenerator;
    }

    @Override
    public void run() {
        if (playerGenerator == null || !playerGenerator.isActive()) {
            cancel();
            return;
        }
        playerGenerator.generate();
    }
}
