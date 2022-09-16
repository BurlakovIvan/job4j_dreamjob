package ru.job4j.dreamjob.store;

import net.jcip.annotations.ThreadSafe;
import org.springframework.stereotype.Repository;
import ru.job4j.dreamjob.model.Post;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Map;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Repository
@ThreadSafe
public class PostStore {

    private final AtomicInteger atomicInteger = new AtomicInteger(4);
    private final Map<Integer, Post> posts = new ConcurrentHashMap<>();

    private PostStore() {
        int id = atomicInteger.incrementAndGet();
        posts.put(id, new Post(id, "Junior", "Junior Java Job", LocalDateTime.now(), false, null));
        id = atomicInteger.incrementAndGet();
        posts.put(id, new Post(id, "Middle", "Middle Java Job", LocalDateTime.now(), false, null));
        id = atomicInteger.incrementAndGet();
        posts.put(id, new Post(id, "Senior", "Senior Java Job", LocalDateTime.now(), false, null));
    }

    public void add(Post post) {
        post.setId(atomicInteger.incrementAndGet());
        posts.putIfAbsent(post.getId(), post);
    }

    public void update(Post post) {
        post.setCreated(LocalDateTime.now());
        posts.replace(post.getId(), post);
    }

    public Post findById(int id) {
        return posts.get(id);
    }

    public Collection<Post> findAll() {
        return posts.values();
    }
}
