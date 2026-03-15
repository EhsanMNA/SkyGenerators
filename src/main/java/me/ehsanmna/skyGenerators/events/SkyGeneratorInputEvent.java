package me.ehsanmna.skyGenerators.events;


import me.ehsanmna.skyGenerators.models.Generator;
import me.ehsanmna.skyGenerators.models.PlayerGenerator;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class SkyGeneratorInputEvent extends Event {

    private static final HandlerList handlers = new HandlerList();
    private final Player player;
    private final PlayerGenerator generator;

    public SkyGeneratorInputEvent(Player player, PlayerGenerator generator) {
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

    public Player getPlayer() {
        return player;
    }

    public PlayerGenerator getGenerator() {
        return generator;
    }
}
