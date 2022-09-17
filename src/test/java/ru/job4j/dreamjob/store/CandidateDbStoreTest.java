package ru.job4j.dreamjob.store;

import org.apache.commons.dbcp2.BasicDataSource;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.job4j.dreamjob.Main;
import ru.job4j.dreamjob.model.Candidate;
import ru.job4j.dreamjob.model.City;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class CandidateDbStoreTest {
    private final static BasicDataSource POOL = new Main().loadPool();
    private final static CandidateDbStore STORE = new CandidateDbStore(POOL);
    private static Candidate candidate;

    @BeforeEach
    public void init() {
        candidate = new Candidate(0, "Ivanov",
                "Programmer", new City(1, "Москва"), new byte[]{21, 22, 23});
    }

    @Test
    public void whenCreateCandidate() {
        STORE.add(candidate);
        Candidate candidateInDb = STORE.findById(candidate.getId());
        assertThat(candidateInDb.getName()).isEqualTo(candidate.getName());
    }

    @Test
    public void whenUpdateCandidateWithPhoto() {
        STORE.add(candidate);
        byte[] photo = new byte[]{20, 20, 20};
        candidate.setName("Petrov");
        candidate.setPhoto(photo);
        STORE.updateWithPhoto(candidate);
        Candidate candidateInDb = STORE.findById(candidate.getId());
        assertThat(photo).isEqualTo(candidateInDb.getPhoto());
        assertThat("Petrov").isEqualTo(candidateInDb.getName());
    }

    @Test
    public void whenUpdateCandidateWithoutPhoto() {
        STORE.add(candidate);
        byte[] photo = new byte[]{20, 20, 20};
        candidate.setName("Petrov");
        candidate.setPhoto(photo);
        STORE.updateWithoutPhoto(candidate);
        Candidate candidateInDb = STORE.findById(candidate.getId());
        assertThat("Petrov").isEqualTo(candidateInDb.getName());
        assertThat(photo).isNotEqualTo(candidateInDb.getPhoto());
    }

    @Test
    public void whenFindByIdCandidate() {
        STORE.add(candidate);
        Candidate candidateInDb = STORE.findById(candidate.getId());
        assertThat(candidateInDb.getId()).isEqualTo(candidate.getId());
    }

    @Test
    public void whenNotFindById() {
        assertThat(STORE.findById(-1)).isNull();
    }

    @Test
    public void whenFindAllPosts() {
        Candidate candidate1 = new Candidate(0, "Junior",
                "Junior Java Developer", new City(1, "Москва"), null);
        Candidate candidate2 = new Candidate(1, "Middle",
                "Middle Java Developer", new City(2, "Cанкт-Петербург"), null);
        Candidate candidate3 = new Candidate(2, "Senior",
                "Senior Java Developer", new City(3, "Екатеринбург"), null);
        STORE.add(candidate1);
        STORE.add(candidate2);
        STORE.add(candidate3);
        List<Candidate> candidates = List.of(candidate1, candidate2, candidate3);
        List<Candidate> candidateInDb = STORE.findAll();
        assertThat(candidateInDb).isEqualTo(candidates);
    }

    @AfterEach
    public void cleanTable() throws SQLException {
        try (Connection cn = POOL.getConnection();
             PreparedStatement ps = cn.prepareStatement("DELETE FROM candidate")) {
            ps.execute();
        }
    }
}