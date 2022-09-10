package ru.job4j.dreamjob.store;

import ru.job4j.dreamjob.model.Post;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Map;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class PostStore {

    private static final PostStore INST = new PostStore();
    private final AtomicInteger atomicInteger = new AtomicInteger(0);
    private final Map<Integer, Post> posts = new ConcurrentHashMap<>();

    private PostStore() {
        posts.put(atomicInteger.incrementAndGet(),
                new Post(1, "Junior", "Junior Java Job", LocalDate.now()));
        posts.put(atomicInteger.incrementAndGet(),
                new Post(2, "Middle", "Middle Java Job", LocalDate.now()));
        posts.put(atomicInteger.incrementAndGet(),
                new Post(3, "Senior", "Senior Java Job", LocalDate.now()));
    }

    public static PostStore instOf() {
        return INST;
    }

    public void add(Post post) {
        post.setId(atomicInteger.incrementAndGet());
        posts.putIfAbsent(post.getId(), post);
    }

    public void update(Post post) {
        posts.computeIfPresent(post.getId(), (key, value) -> {
            value.setCreated(LocalDate.now());
            value.setDescription(post.getDescription());
            value.setName(post.getName());
            return value;
        });
    }

    public Post findById(int id) {
        return posts.get(id);
    }

    public Collection<Post> findAll() {
        return posts.values();
    }
}
