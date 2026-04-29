package me.ehsanmna.skygenerators.events;


import lombok.Getter;
import me.ehsanmna.skygenerators.models.PlayerGenerator;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class SkyGeneratorUpgradeEvent extends Event implements Cancellable {

    private static final HandlerList handlers = new HandlerList();
    @Getter
    private final Player player;
    @Getter
    private final PlayerGenerator generator;
    private boolean cancelled;

    public SkyGeneratorUpgradeEvent(Player player, PlayerGenerator generator) {
        this.player = player;
        this.generator = generator;
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
