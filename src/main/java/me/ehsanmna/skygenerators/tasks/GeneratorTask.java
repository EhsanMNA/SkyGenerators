package me.ehsanmna.skygenerators.tasks;

import me.ehsanmna.skygenerators.models.PlayerGenerator;
import org.bukkit.scheduler.BukkitRunnable;

public class GeneratorTask extends BukkitRunnable {

    private final PlayerGenerator playerGenerator;

    public GeneratorTask(PlayerGenerator playerGenerator) {
        this.playerGenerator = playerGenerator;
    }

    @Override
    public void run() {
        if (playerGenerator == null || !playerGenerator.isActive() || playerGenerator.isStorageFull() || playerGenerator.getGenerator().getEnergy() == 0) {
            cancel();
            return;
        }
        playerGenerator.generate();
    }
}
