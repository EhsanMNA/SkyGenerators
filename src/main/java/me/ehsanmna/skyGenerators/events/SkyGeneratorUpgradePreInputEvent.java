package me.ehsanmna.skyGenerators.events;


import lombok.Getter;
import lombok.Setter;
import me.ehsanmna.skyGenerators.models.upgrade.GeneratorUpgrade;
import me.ehsanmna.skyGenerators.models.PlayerGenerator;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class SkyGeneratorUpgradePreInputEvent extends Event implements Cancellable {

    private boolean cancelled = false;
    private static final HandlerList handlers = new HandlerList();

    @Setter
    @Getter
    Player player;
    @Setter
    @Getter
    PlayerGenerator generator;

    @Getter
    @Setter
    GeneratorUpgrade generatorUpgrade;

    public SkyGeneratorUpgradePreInputEvent(Player player, PlayerGenerator generator) {
        this.player = player;
        this.generator = generator;
    }

    public SkyGeneratorUpgradePreInputEvent(Player player, PlayerGenerator generator, GeneratorUpgrade generatorUpgrade) {
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
        this.cancelled = b;
    }

}
