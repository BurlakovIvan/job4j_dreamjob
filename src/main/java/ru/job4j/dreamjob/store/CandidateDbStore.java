package ru.job4j.dreamjob.store;

import net.jcip.annotations.ThreadSafe;
import org.apache.commons.dbcp2.BasicDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import ru.job4j.dreamjob.model.Candidate;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@ThreadSafe
@Repository
public class CandidateDbStore {

    private final BasicDataSource pool;
    private static final Logger LOGGER = LoggerFactory.getLogger(PostDbStore.class.getName());
    private final static String SELECT = "SELECT * FROM candidate";
    private final static String SELECT_WITH_WHERE = String.format("%s WHERE id = ?", SELECT);
    private final static String UPDATE = "UPDATE candidate SET name = ?, description = ?%s WHERE id = ?";
    private final static String UPDATE_WITH_PHOTO = String.format(UPDATE, ", photo = ?");
    private final static String UPDATE_WITHOUT_PHOTO = String.format(UPDATE, "");
    private final static String INSERT = """
                                         INSERT INTO candidate(name, description, created, photo)
                                         VALUES (?, ?, ?, ?)
                                         """;

    public CandidateDbStore(BasicDataSource pool) {
        this.pool = pool;
    }

    public List<Candidate> findAll() {
        List<Candidate> candidates = new ArrayList<>();
        try (Connection cn = pool.getConnection();
             PreparedStatement ps =  cn.prepareStatement(SELECT)
        ) {
            try (ResultSet resultSet = ps.executeQuery()) {
                while (resultSet.next()) {
                    candidates.add(newCandidate(resultSet));
                }
            }
        } catch (Exception ex) {
            LOGGER.error("ERROR: ", ex);
        }
        return candidates;
    }

    public void add(Candidate candidate) {
        try (Connection cn = pool.getConnection();
             PreparedStatement ps = cn.prepareStatement(INSERT, PreparedStatement.RETURN_GENERATED_KEYS)
        ) {
            ps.setString(1, candidate.getName());
            ps.setString(2, candidate.getDesc());
            ps.setTimestamp(3, Timestamp.valueOf(LocalDateTime.now()));
            ps.setBytes(4, candidate.getPhoto());
            ps.execute();
            try (ResultSet id = ps.getGeneratedKeys()) {
                if (id.next()) {
                    candidate.setId(id.getInt(1));
                }
            }
        } catch (Exception ex) {
            LOGGER.error("ERROR: ", ex);
        }
    }

    public void updateWithPhoto(Candidate candidate) {
        try (Connection cn = pool.getConnection();
             PreparedStatement ps = cn.prepareStatement(UPDATE_WITH_PHOTO)) {
            ps.setString(1, candidate.getName());
            ps.setString(2, candidate.getDesc());
            ps.setBytes(3, candidate.getPhoto());
            ps.setInt(4, candidate.getId());
            ps.execute();
        } catch (Exception ex) {
            LOGGER.error("ERROR: ", ex);
        }
    }

    public void updateWithoutPhoto(Candidate candidate) {
        try (Connection cn = pool.getConnection();
             PreparedStatement ps = cn.prepareStatement(UPDATE_WITHOUT_PHOTO)) {
            ps.setString(1, candidate.getName());
            ps.setString(2, candidate.getDesc());
            ps.setInt(3, candidate.getId());
            ps.execute();
        } catch (Exception ex) {
            LOGGER.error("ERROR: ", ex);
        }
    }

    public Candidate findById(int id) {
        try (Connection cn = pool.getConnection();
             PreparedStatement ps =  cn.prepareStatement(SELECT_WITH_WHERE)
        ) {
            ps.setInt(1, id);
            try (ResultSet resultSet = ps.executeQuery()) {
                if (resultSet.next()) {
                    return newCandidate(resultSet);
                }
            }
        } catch (Exception ex) {
            LOGGER.error("ERROR: ", ex);
        }
        return null;
    }

    private Candidate newCandidate(ResultSet resultSet) throws SQLException {
        return new Candidate(
                resultSet.getInt("id"),
                resultSet.getString("name"),
                resultSet.getString("description"),
                resultSet.getTimestamp("created").toLocalDateTime(),
                resultSet.getBytes("photo"));
    }
}
