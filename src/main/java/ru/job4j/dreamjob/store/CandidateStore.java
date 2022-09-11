package ru.job4j.dreamjob.store;

import org.springframework.stereotype.Repository;
import ru.job4j.dreamjob.model.Candidate;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Repository
public class CandidateStore {
    private static final CandidateStore INST = new CandidateStore();
    private final AtomicInteger atomicInteger = new AtomicInteger(0);
    private final Map<Integer, Candidate> candidates = new ConcurrentHashMap<>();

    private CandidateStore() {
        int id = atomicInteger.incrementAndGet();
        candidates.put(id, new Candidate(id, "Junior", "Junior Java Developer", LocalDate.now()));
        id = atomicInteger.incrementAndGet();
        candidates.put(id, new Candidate(id, "Middle", "Middle Java Developer", LocalDate.now()));
        id = atomicInteger.incrementAndGet();
        candidates.put(id, new Candidate(id, "Senior", "Senior Java Developer", LocalDate.now()));
    }

    public void update(Candidate candidate) {
        candidate.setCreated(LocalDate.now());
        candidates.replace(candidate.getId(), candidate);
    }

    public void add(Candidate candidate) {
        candidate.setId(atomicInteger.incrementAndGet());
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
