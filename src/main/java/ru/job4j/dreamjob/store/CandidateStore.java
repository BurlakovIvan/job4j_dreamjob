package ru.job4j.dreamjob.store;

import net.jcip.annotations.ThreadSafe;
import org.springframework.stereotype.Repository;
import ru.job4j.dreamjob.model.Candidate;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Repository
@ThreadSafe
public class CandidateStore {
    private final AtomicInteger atomicInteger = new AtomicInteger(4);
    private final Map<Integer, Candidate> candidates = new ConcurrentHashMap<>();

    private CandidateStore() {
        int id = atomicInteger.incrementAndGet();
        candidates.put(id, new Candidate(id, "Junior", "Junior Java Developer", null, null));
        id = atomicInteger.incrementAndGet();
        candidates.put(id, new Candidate(id, "Middle", "Middle Java Developer", null, null));
        id = atomicInteger.incrementAndGet();
        candidates.put(id, new Candidate(id, "Senior", "Senior Java Developer", null, null));
    }

    public void update(Candidate candidate) {
        candidate.setCreated(LocalDateTime.now());
        candidates.replace(candidate.getId(), candidate);
    }

    public void add(Candidate candidate) {
        candidate.setId(atomicInteger.incrementAndGet());
        candidates.putIfAbsent(candidate.getId(), candidate);
    }

    public Candidate findById(int id) {
        return candidates.get(id);
    }

    public Collection<Candidate> findAll() {
        return candidates.values();
    }
}
