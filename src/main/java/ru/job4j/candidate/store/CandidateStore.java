package ru.job4j.candidate.store;

import ru.job4j.candidate.model.Candidate;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class CandidateStore {
    private static final CandidateStore INST = new CandidateStore();

    private final Map<Integer, Candidate> candidates = new ConcurrentHashMap<>();

    private CandidateStore() {
        candidates.put(1, new Candidate(1, "Junior", "Junior Java Developer", LocalDate.now()));
        candidates.put(2, new Candidate(2, "Middle", "Middle Java Developer", LocalDate.now()));
        candidates.put(3, new Candidate(3, "Senior", "Senior Java Developer", LocalDate.now()));
    }

    public static CandidateStore instOf() {
        return INST;
    }

    public Collection<Candidate> findAll() {
        return candidates.values();
    }
}
