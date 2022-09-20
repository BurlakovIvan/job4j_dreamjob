package ru.job4j.dreamjob.controller;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;
import org.springframework.ui.Model;
import ru.job4j.dreamjob.model.City;
import ru.job4j.dreamjob.model.Post;
import ru.job4j.dreamjob.service.CityService;
import ru.job4j.dreamjob.service.PostService;

import javax.servlet.http.HttpSession;
import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class PostControllerTest {
    @Test
    public void whenPosts() {
        CityService city = new CityService();
        List<Post> posts = Arrays.asList(
                new Post(1, "New post", "New post",
                        true, city.findById(1)),
                new Post(2, "New post", "New post",
                        true, city.findById(2))
        );
        Model model = mock(Model.class);
        HttpSession session = mock(HttpSession.class);
        PostService postService = mock(PostService.class);
        CityService cityService = mock(CityService.class);
        when(postService.findAll()).thenReturn(posts);
        PostController postController = new PostController(
                postService,
                cityService
        );
        String page = postController.posts(model, session);
        verify(model).addAttribute("posts", posts);
        assertThat(page).isEqualTo("posts");

    }

    @Test
    public void whenCreatePost() {
        CityService city = new CityService();
        Post input = new Post(1, "New post", "New post",
                true, city.findById(2));
        PostService postService = mock(PostService.class);
        CityService cityService = mock(CityService.class);
        PostController postController = new PostController(
                postService,
                cityService
        );
        String page = postController.createPost(input, 1);
        verify(postService).add(input);
        assertThat(page).isEqualTo("redirect:/posts");
    }

    @Test
    public void whenAddPost() {
        CityService city = new CityService();
        List<City> cities = List.of(
                city.findById(1),
                city.findById(2)
        );
        Model model = mock(Model.class);
        PostService postService = mock(PostService.class);
        CityService cityService = mock(CityService.class);
        HttpSession session = mock(HttpSession.class);
        when(cityService.getAllCities()).thenReturn(cities);
        PostController postController = new PostController(
                postService,
                cityService
        );
        String page = postController.addPost(model, session);
        verify(model).addAttribute("cities", cities);
        assertThat(page).isEqualTo("addPost");
    }

    @Test
    public void whenFormUpdatePost() {
        CityService city = new CityService();
        List<City> cities = Arrays.asList(
                city.findById(1),
                city.findById(2)
        );
        Model model = mock(Model.class);
        PostService postService = mock(PostService.class);
        CityService cityService = mock(CityService.class);
        HttpSession session = mock(HttpSession.class);
        Post input = new Post(1, "New post", "New post",
                true, city.findById(2));
        when(postService.findById(1)).thenReturn(input);
        when(cityService.getAllCities()).thenReturn(cities);
        PostController postController = new PostController(
                postService,
                cityService
        );
        String page = postController.formUpdatePost(model, 1, session);
        verify(model).addAttribute("post", input);
        verify(model).addAttribute("cities", cities);
        assertThat(page).isEqualTo("updatePost");
    }

    @Test
    public void whenUpdatePost() {
        CityService city = new CityService();
        PostService postService = mock(PostService.class);
        CityService cityService = mock(CityService.class);
        Post input = new Post(1, "New post", "New post",
                true, city.findById(2));
        PostController postController = new PostController(
                postService,
                cityService
        );
        String page = postController.updatePost(input, 1);
        verify(postService).update(input);
        assertThat(page).isEqualTo("redirect:/posts");
    }

}