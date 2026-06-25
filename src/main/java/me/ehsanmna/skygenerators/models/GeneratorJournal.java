package me.ehsanmna.skygenerators.models;

import lombok.Getter;
import lombok.Setter;
import me.ehsanmna.skygenerators.models.upgrade.GeneratorUpgrade;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Tracks the lifetime history of a single {@link PlayerGenerator} instance.
 * <p>
 * One journal belongs to exactly one PlayerGenerator and follows it for its
 * whole life (created on first placement, never reset on upgrade or collect).
 */
@Getter
@Setter
public class GeneratorJournal {

    // ----- Identity / creation -----
    private long createdAt = Instant.now().getEpochSecond();

    // ----- Lifetime "mileage" totals (never decrease) -----
    private long lifetimeBlocksGenerated = 0L;
    private long lifetimeUpgradeItemsGenerated = 0L;
    private long lifetimeEnergyConsumed = 0L;
    private long lifetimeCollections = 0L;
    private long lifetimePickups = 0L;

    // ----- Activity tracking -----
    private long lastCollectedAt = -1L;
    private long lastUpgradedAt = -1L;

    // ----- Upgrade tier history (BaseGenerator ids this generator has passed through) -----
    private final List<String> generatorTierHistory = new ArrayList<>();

    // ----- Currently active upgrades (snapshot, not historical) -----
    private final List<String> activeUpgradeIds = new ArrayList<>();


    public GeneratorJournal() {
    }

    public GeneratorJournal(BaseGenerator initialBaseGenerator) {
        if (initialBaseGenerator != null) {
            generatorTierHistory.add(initialBaseGenerator.getId());
        }
    }


    // ----- Mutators called from PlayerGenerator / GeneratorUpgrade flows -----

    public void recordBlocksGenerated(int amount) {
        if (amount <= 0) return;
        lifetimeBlocksGenerated += amount;
    }

    public void recordUpgradeItemsGenerated(int amount) {
        if (amount <= 0) return;
        lifetimeUpgradeItemsGenerated += amount;
    }

    public void recordEnergyConsumed(int amount) {
        if (amount <= 0) return;
        lifetimeEnergyConsumed += amount;
    }

    public void recordCollection() {
        lifetimeCollections++;
        lastCollectedAt = Instant.now().getEpochSecond();
    }

    public void recordPickup() {
        lifetimePickups++;
    }

    public void recordGeneratorUpgrade(BaseGenerator newBaseGenerator) {
        if (newBaseGenerator == null) return;
        generatorTierHistory.add(newBaseGenerator.getId());
        lastUpgradedAt = Instant.now().getEpochSecond();
    }

    /**
     * Resyncs the "currently active upgrades" snapshot. Call after any change
     * to {@link PlayerGenerator#getUpgrades()} (added, removed, or on pickup/clear).
     */
    public void syncActiveUpgrades(List<GeneratorUpgrade> currentUpgrades) {
        activeUpgradeIds.clear();
        if (currentUpgrades == null || currentUpgrades.isEmpty()) return;
        for (GeneratorUpgrade upgrade : currentUpgrades) {
            if (upgrade.getBaseGeneratorUpgrade() == null) continue;
            activeUpgradeIds.add(upgrade.getBaseGeneratorUpgrade().getId());
        }
    }


    // ----- Convenience reads -----

    public long getTotalLifetimeItems() {
        return lifetimeBlocksGenerated + lifetimeUpgradeItemsGenerated;
    }

    public String getCurrentTierId() {
        if (generatorTierHistory.isEmpty()) return null;
        return generatorTierHistory.get(generatorTierHistory.size() - 1);
    }

    public int getUpgradeCount() {
        return Math.max(0, generatorTierHistory.size() - 1);
    }

    private String getDateFromLong(long time) {
        LocalDateTime dateTime = LocalDateTime.ofInstant(
                Instant.ofEpochSecond(time),
                ZoneId.systemDefault()
        );
        return dateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
    }

    public String replacePlaceholders(String str){
        return str.replace("%journal_create%", getDateFromLong(getCreatedAt()))
                .replace("%journal_lifetimeBlocksGenerated%", getLifetimeBlocksGenerated()+"")
                .replace("%journal_lifetimeCollections%", getLifetimeCollections()+"")
                .replace("%journal_lastCollectedAt%", getDateFromLong(getLastCollectedAt()));
    }
}