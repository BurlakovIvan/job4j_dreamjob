package ru.job4j.dreamjob.store;

import net.jcip.annotations.ThreadSafe;
import org.apache.commons.dbcp2.BasicDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import ru.job4j.dreamjob.model.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@ThreadSafe
@Repository
public class UserDbStore {
    private final BasicDataSource pool;
    private static final Logger LOGGER = LoggerFactory.getLogger(UserDbStore.class.getName());
    private final static String SELECT = "SELECT * FROM users";
    private final static String SELECT_WITH_WHERE = String.format("%s WHERE id = ?", SELECT);
    private final static String INSERT = "INSERT INTO users(name) VALUES (?)";

    public UserDbStore(BasicDataSource pool) {
        this.pool = pool;
    }

    public List<User> findAll() {
        List<User> users = new ArrayList<>();
        try (Connection cn = pool.getConnection();
             PreparedStatement ps =  cn.prepareStatement(SELECT)
        ) {
            try (ResultSet resultSet = ps.executeQuery()) {
                while (resultSet.next()) {
                    users.add(newUser(resultSet));
                }
            }
        } catch (Exception ex) {
            LOGGER.error("ERROR: ", ex);
        }
        return users;
    }

    public Optional<User> add(User user) {
        Optional<User> rsl = Optional.empty();
        try (Connection cn = pool.getConnection();
             PreparedStatement ps = cn.prepareStatement(INSERT, PreparedStatement.RETURN_GENERATED_KEYS)
        ) {
            ps.setString(1, user.getName());
            ps.execute();
            try (ResultSet resultSet = ps.getGeneratedKeys()) {
                if (resultSet.next()) {
                    user.setId((resultSet.getInt(1)));
                }
            }
            rsl = Optional.of(user);
        } catch (Exception ex) {
            LOGGER.error("ERROR: ", ex);
        }
        return rsl;
    }

    public User findById(int id) {
        try (Connection cn = pool.getConnection();
             PreparedStatement ps =  cn.prepareStatement(SELECT_WITH_WHERE)
        ) {
            ps.setInt(1, id);
            try (ResultSet resultSet = ps.executeQuery()) {
                if (resultSet.next()) {
                    return newUser(resultSet);
                }
            }
        } catch (Exception ex) {
            LOGGER.error("ERROR: ", ex);
        }
        return null;
    }

    private User newUser(ResultSet resultSet) throws SQLException {
        return new User(
                resultSet.getInt("id"),
                resultSet.getString("name"));
    }
}
