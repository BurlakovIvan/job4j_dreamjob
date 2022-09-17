package ru.job4j.dreamjob.store;

import org.apache.commons.dbcp2.BasicDataSource;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.job4j.dreamjob.Main;
import ru.job4j.dreamjob.model.City;
import ru.job4j.dreamjob.model.Post;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

public class PostDbStoreTest {
    private final static BasicDataSource POOL = new Main().loadPool();
    private final static PostDbStore STORE = new PostDbStore(POOL);
    private static Post post;

    @BeforeEach
    public void init() {
        post = new Post(0, "Java Job",
                "description", true, new City(1, "Москва"));
    }

    @Test
    public void whenCreatePost() {
        STORE.add(post);
        Post postInDb = STORE.findById(post.getId());
        assertThat(postInDb.getName()).isEqualTo(post.getName());
    }

    @Test
    public void whenUpdatePost() {
        STORE.add(post);
        post.setDescription("Java description");
        STORE.update(post);
        Post postInDb = STORE.findById(post.getId());
        assertThat("Java description").isEqualTo(postInDb.getDescription());
    }

    @Test
    public void whenFindByIdPost() {
        STORE.add(post);
        Post postInDb = STORE.findById(post.getId());
        assertThat(postInDb.getId()).isEqualTo(post.getId());
    }

    @Test
    public void whenNotFindById() {
        assertThat(STORE.findById(-1)).isNull();
    }

    @Test
    public void whenFindAllPosts() {
        Post post1 = new Post(0, "Junior",
                "Junior Java Job", false, new City(1, "Москва"));
        Post post2 = new Post(1, "Middle",
                "Middle Java Job", false, new City(2, "Cанкт-Петербург"));
        Post post3 = new Post(2, "Senior",
                "Senior Java Job", true, new City(3, "Екатеринбург"));
        STORE.add(post1);
        STORE.add(post2);
        STORE.add(post3);
        List<Post> posts = List.of(post1, post2, post3);
        List<Post> postsInDb = STORE.findAll();
        assertThat(postsInDb).isEqualTo(posts);
    }

    @AfterEach
    public void cleanTable() throws SQLException {
        try (Connection cn = POOL.getConnection();
             PreparedStatement ps = cn.prepareStatement("DELETE FROM post")) {
            ps.execute();
        }
    }
}