package ru.job4j.dreamjob.store;

import net.jcip.annotations.ThreadSafe;
import org.apache.commons.dbcp2.BasicDataSource;
import org.springframework.stereotype.Repository;
import ru.job4j.dreamjob.model.City;
import ru.job4j.dreamjob.model.Post;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ThreadSafe
@Repository
public class PostDbStore {

    private final BasicDataSource pool;
    private static final Logger LOGGER = LoggerFactory.getLogger(PostDbStore.class.getName());
    private static final String INSERT = """
                                          INSERT INTO post(name, description, created, visible, city_id)
                                          VALUES (?, ?, ?, ?, ?)
                                          """;
    private static final String UPDATE = """
                                         UPDATE post SET name = ?, description = ?, visible = ?,
                                         city_id = ? WHERE id = ?
                                         """;
    private static final String SELECT = "SELECT * FROM post";
    private static final String SELECT_WITH_WHERE = String.format("%s WHERE id = ?", SELECT);

    public PostDbStore(BasicDataSource pool) {
        this.pool = pool;
    }

    public List<Post> findAll() {
        List<Post> posts = new ArrayList<>();
        try (Connection cn = pool.getConnection();
             PreparedStatement ps =  cn.prepareStatement(SELECT)
        ) {
            try (ResultSet resultSet = ps.executeQuery()) {
                while (resultSet.next()) {
                    posts.add(newPost(resultSet));
                }
            }
        } catch (Exception e) {
            LOGGER.error("ERROR: ", e);
        }
        return posts;
    }

    public void add(Post post) {
        try (Connection cn = pool.getConnection();
             PreparedStatement ps = cn.prepareStatement(INSERT, PreparedStatement.RETURN_GENERATED_KEYS)
        ) {
            ps.setString(1, post.getName());
            ps.setString(2, post.getDescription());
            ps.setTimestamp(3, Timestamp.valueOf(LocalDateTime.now()));
            ps.setBoolean(4, post.isVisible());
            ps.setInt(5, post.getCity().getId());
            ps.execute();
            try (ResultSet resultSet = ps.getGeneratedKeys()) {
                if (resultSet.next()) {
                    post.setId(resultSet.getInt(1));
                }
            }
        } catch (Exception e) {
            LOGGER.error("ERROR: ", e);
        }
    }

    public void update(Post post) {
        try (Connection cn = pool.getConnection();
             PreparedStatement ps = cn.prepareStatement(UPDATE)) {
            ps.setString(1, post.getName());
            ps.setString(2, post.getDescription());
            ps.setBoolean(3, post.isVisible());
            ps.setInt(4, post.getCity().getId());
            ps.setInt(5, post.getId());
            ps.execute();
        } catch (Exception e) {
            LOGGER.error("ERROR: ", e);
        }
    }

    public Post findById(int id) {
        try (Connection cn = pool.getConnection();
             PreparedStatement ps =  cn.prepareStatement(SELECT_WITH_WHERE)
        ) {
            ps.setInt(1, id);
            try (ResultSet resultSet = ps.executeQuery()) {
                if (resultSet.next()) {
                    return newPost(resultSet);
                }
            }
        } catch (Exception e) {
            LOGGER.error("ERROR: ", e);
        }
        return null;
    }

    private Post newPost(ResultSet resultSet) throws SQLException {
        return new Post(
                resultSet.getInt("id"),
                resultSet.getString("name"),
                resultSet.getString("description"),
                resultSet.getTimestamp("created").toLocalDateTime(),
                resultSet.getBoolean("visible"),
                new City(resultSet.getInt("city_id"), ""));
    }
}
