package me.ehsanmna.skygenerators.events;


import lombok.Getter;
import lombok.Setter;
import me.ehsanmna.skygenerators.models.Generator;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class SkyGeneratorPreInputEvent extends Event implements Cancellable {

    private boolean cancelled = false;
    private static final HandlerList handlers = new HandlerList();

    @Setter
    @Getter
    Player player;
    @Setter
    @Getter
    Generator generator;

    public SkyGeneratorPreInputEvent(Player player, Generator generator) {
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
        this.cancelled = b;
    }

}
