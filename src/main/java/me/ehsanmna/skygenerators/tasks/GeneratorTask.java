package me.ehsanmna.skygenerators.tasks;

import lombok.Getter;
import lombok.Setter;
import me.ehsanmna.skygenerators.models.PlayerGenerator;
import org.bukkit.scheduler.BukkitRunnable;

public class GeneratorTask extends BukkitRunnable {

    private final PlayerGenerator playerGenerator;

    @Getter
    @Setter
    private boolean generating = true;

    public GeneratorTask(PlayerGenerator playerGenerator) {
        this.playerGenerator = playerGenerator;
    }

    @Override
    public void run() {
        if (playerGenerator == null || !playerGenerator.isActive() || playerGenerator.isStorageFull() || playerGenerator.getGenerator().getEnergy() == 0) {
            generating = false;
            cancel();
            return;
        }
        playerGenerator.generate();
        if (!generating) generating = true;
    }
}
