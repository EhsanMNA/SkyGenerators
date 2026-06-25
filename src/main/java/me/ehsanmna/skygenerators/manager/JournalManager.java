package me.ehsanmna.skygenerators.manager;

import lombok.Getter;
import me.ehsanmna.skygenerators.SkyGenerators;
import me.ehsanmna.skygenerators.config.JournalConfig;
import me.ehsanmna.skygenerators.models.GeneratorJournal;
import me.ehsanmna.skygenerators.service.JournalService;

import java.util.UUID;

@Getter
public class JournalManager {

    private final SkyGenerators skyGenerators = SkyGenerators.getInstance();
    private final JournalService service;
    private final JournalConfig config;

    public JournalManager() {
        service = new JournalService();
        config = new JournalConfig(skyGenerators, this);
        config.load();
    }

    public GeneratorJournal getJournal(UUID playerGeneratorId) {
        return service.getJournal(playerGeneratorId);
    }

    /**
     * Gets the journal for a playerGeneratorId, creating and registering a
     * fresh empty one if none exists yet (e.g. for generators that existed
     * before journals were introduced).
     */
    public GeneratorJournal getOrCreateJournal(UUID playerGeneratorId) {
        GeneratorJournal journal = service.getJournal(playerGeneratorId);
        if (journal == null) {
            journal = new GeneratorJournal();
            service.addJournal(playerGeneratorId, journal);
        }
        return journal;
    }

    public void removeJournal(UUID playerGeneratorId) {
        service.removeJournal(playerGeneratorId);
    }

}