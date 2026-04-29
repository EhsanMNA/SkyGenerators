package me.ehsanmna.skygenerators.events.upgrade;


import lombok.Getter;
import me.ehsanmna.skygenerators.models.PlayerGenerator;
import me.ehsanmna.skygenerators.models.upgrade.GeneratorUpgrade;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class SkyGeneratorUpgradePickupEvent extends Event implements Cancellable {

    private static final HandlerList handlers = new HandlerList();
    @Getter private final Player player;
    @Getter private final PlayerGenerator generator;
    @Getter private final GeneratorUpgrade generatorUpgrade;

    private boolean cancelled;


    public SkyGeneratorUpgradePickupEvent(Player player, PlayerGenerator generator, GeneratorUpgrade generatorUpgrade) {
        this.player = player;
        this.generator = generator;
        this.generatorUpgrade = generatorUpgrade;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean b) {
        cancelled = b;
    }
}
