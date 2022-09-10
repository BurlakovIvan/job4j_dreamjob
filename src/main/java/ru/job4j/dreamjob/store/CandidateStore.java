package ru.job4j.dreamjob.store;

import ru.job4j.dreamjob.model.Candidate;

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

    public void update(Candidate candidate) {
        candidates.computeIfPresent(candidate.getId(), (key, value) -> {
            value.setCreated(LocalDate.now());
            value.setDesc(candidate.getDesc());
            value.setName(candidate.getName());
            return value;
        });
    }

    public void add(Candidate candidate) {
        candidates.putIfAbsent(candidate.getId(), candidate);
    }

    public Candidate findById(int id) {
        return candidates.get(id);
    }

    public static CandidateStore instOf() {
        return INST;
    }

    public Collection<Candidate> findAll() {
        return candidates.values();
    }
}
