package ru.job4j.dreamjob.service;

import net.jcip.annotations.ThreadSafe;
import org.springframework.stereotype.Service;
import ru.job4j.dreamjob.model.Candidate;
import ru.job4j.dreamjob.model.Post;
import ru.job4j.dreamjob.store.CandidateDbStore;

import java.util.ArrayList;
import java.util.List;

@Service
@ThreadSafe
public class CandidateService {
    private final CandidateDbStore store;
    private final CityService cityService;

    public CandidateService(CandidateDbStore store, CityService cityService) {
        this.store = store;
        this.cityService = cityService;
    }

    public List<Candidate> findAll() {
        List<Candidate> candidates = store.findAll();
        candidates.forEach(
                post -> post.setCity(
                        cityService.findById(post.getCity().getId())
                )
        );
        return candidates;
    }

    public void add(Candidate candidate) {
        store.add(candidate);
    }

    public Candidate findById(int id) {
        return store.findById(id);
    }

    public void update(Candidate candidate) {
        if (candidate.getPhoto().length > 0) {
            store.updateWithPhoto(candidate);
        } else {
            store.updateWithoutPhoto(candidate);
        }
    }
}
