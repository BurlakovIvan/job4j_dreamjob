package ru.job4j.dreamjob.store;

import org.springframework.stereotype.Repository;
import ru.job4j.dreamjob.model.Post;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Map;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Repository
public class PostStore {

    private static final PostStore INST = new PostStore();
    private final AtomicInteger atomicInteger = new AtomicInteger(4);
    private final Map<Integer, Post> posts = new ConcurrentHashMap<>();

    private PostStore() {
        int id = atomicInteger.incrementAndGet();
        posts.put(id, new Post(id, "Junior", "Junior Java Job", LocalDate.now()));
        id = atomicInteger.incrementAndGet();
        posts.put(id, new Post(id, "Middle", "Middle Java Job", LocalDate.now()));
        id = atomicInteger.incrementAndGet();
        posts.put(id, new Post(id, "Senior", "Senior Java Job", LocalDate.now()));
    }

    public static PostStore instOf() {
        return INST;
    }

    public void add(Post post) {
        post.setId(atomicInteger.incrementAndGet());
        posts.putIfAbsent(post.getId(), post);
    }

    public void update(Post post) {
        post.setCreated(LocalDate.now());
        posts.replace(post.getId(), post);
    }

    public Post findById(int id) {
        return posts.get(id);
    }

    public Collection<Post> findAll() {
        return posts.values();
    }
}
