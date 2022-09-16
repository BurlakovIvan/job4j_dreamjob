package ru.job4j.dreamjob.store;

import net.jcip.annotations.ThreadSafe;
import org.apache.commons.dbcp2.BasicDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import ru.job4j.dreamjob.model.Candidate;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@ThreadSafe
@Repository
public class CandidateDbStore {

    private final BasicDataSource pool;
    private static final Logger LOGGER = LoggerFactory.getLogger(PostDbStore.class.getName());

    public CandidateDbStore(BasicDataSource pool) {
        this.pool = pool;
    }

    public List<Candidate> findAll() {
        List<Candidate> candidates = new ArrayList<>();
        try (Connection cn = pool.getConnection();
             PreparedStatement ps =  cn.prepareStatement("SELECT * FROM candidate")
        ) {
            try (ResultSet it = ps.executeQuery()) {
                while (it.next()) {
                    candidates.add(new Candidate(
                            it.getInt("id"),
                            it.getString("name"),
                            it.getString("description"),
                            it.getTimestamp("created").toLocalDateTime(),
                            it.getBytes("photo")));
                }
            }
        } catch (Exception ex) {
            LOGGER.error("ERROR: ", ex);
        }
        return candidates;
    }

    public void add(Candidate candidate) {
        try (Connection cn = pool.getConnection();
             PreparedStatement ps = cn.prepareStatement(
                     "INSERT INTO candidate(name, description, created, photo) VALUES (?, ?, ?, ?)",
                     PreparedStatement.RETURN_GENERATED_KEYS)
        ) {
            LocalDateTime now = LocalDateTime.now();
            ps.setString(1, candidate.getName());
            ps.setString(2, candidate.getDesc());
            ps.setTimestamp(3, Timestamp.valueOf(now));
            ps.setBytes(4, candidate.getPhoto());
            ps.execute();
            try (ResultSet id = ps.getGeneratedKeys()) {
                if (id.next()) {
                    candidate.setId(id.getInt(1));
                    candidate.setCreated(now);
                }
            }
        } catch (Exception ex) {
            LOGGER.error("ERROR: ", ex);
        }
    }

    public void update(Candidate candidate) {
        String stringStatement = "update candidate SET name = ?, description = ? where id = ?";
        if (candidate.getPhoto().length > 0) {
            stringStatement = "update candidate SET name = ?, description = ?, photo = ? where id = ?";
        }
        try (Connection cn = pool.getConnection();
             PreparedStatement ps = cn.prepareStatement(stringStatement)) {
            ps.setString(1, candidate.getName());
            ps.setString(2, candidate.getDesc());
            if (candidate.getPhoto().length > 0) {
                ps.setBytes(3, candidate.getPhoto());
                ps.setInt(4, candidate.getId());
            } else {
                ps.setInt(3, candidate.getId());
            }
            ps.execute();
        } catch (Exception ex) {
            LOGGER.error("ERROR: ", ex);
        }
    }

    public Candidate findById(int id) {
        try (Connection cn = pool.getConnection();
             PreparedStatement ps =  cn.prepareStatement("SELECT * FROM candidate WHERE id = ?")
        ) {
            ps.setInt(1, id);
            try (ResultSet it = ps.executeQuery()) {
                if (it.next()) {
                    return new Candidate(
                            it.getInt("id"),
                            it.getString("name"),
                            it.getString("description"),
                            it.getTimestamp("created").toLocalDateTime(),
                            it.getBytes("photo"));
                }
            }
        } catch (Exception ex) {
            LOGGER.error("ERROR: ", ex);
        }
        return null;
    }
}
