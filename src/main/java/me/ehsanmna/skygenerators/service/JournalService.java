package me.ehsanmna.skygenerators.service;

import lombok.Getter;
import me.ehsanmna.skygenerators.models.GeneratorJournal;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Getter
public class JournalService {

    // playerGeneratorId - journal
    private final Map<UUID, GeneratorJournal> journals = new HashMap<>();

    public void addJournal(UUID playerGeneratorId, GeneratorJournal journal) {
        journals.put(playerGeneratorId, journal);
    }

    public GeneratorJournal getJournal(UUID playerGeneratorId) {
        return journals.get(playerGeneratorId);
    }

    public GeneratorJournal removeJournal(UUID playerGeneratorId) {
        return journals.remove(playerGeneratorId);
    }

}