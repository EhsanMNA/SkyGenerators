package me.ehsanmna.skyGenerators.events;


import me.ehsanmna.skyGenerators.models.upgrade.GeneratorUpgrade;
import me.ehsanmna.skyGenerators.models.PlayerGenerator;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class SkyGeneratorUpgradeInputEvent extends Event {

    private static final HandlerList handlers = new HandlerList();
    private final Player player;
    private final PlayerGenerator generator;
    private final GeneratorUpgrade generatorUpgrade;

    public SkyGeneratorUpgradeInputEvent(Player player, PlayerGenerator generator, GeneratorUpgrade generatorUpgrade) {
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

    public Player getPlayer() {
        return player;
    }

    public PlayerGenerator getGenerator() {
        return generator;
    }

    public GeneratorUpgrade getGeneratorUpgrade() {
        return generatorUpgrade;
    }
}
