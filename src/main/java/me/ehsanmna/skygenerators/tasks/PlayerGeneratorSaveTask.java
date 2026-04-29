package me.ehsanmna.skygenerators.tasks;

import me.ehsanmna.skygenerators.SkyGenerators;
import org.bukkit.scheduler.BukkitRunnable;

public class PlayerGeneratorSaveTask extends BukkitRunnable {

    private final SkyGenerators skyGenerators = SkyGenerators.getInstance();

    @Override
    public void run() {
        skyGenerators.getPlayerGeneratorManager().getConfig().save();
        skyGenerators.getLogger().info("Auto saved player generators!");
    }
}
