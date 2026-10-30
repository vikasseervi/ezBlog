package com.ezBlog.backend.controller;

import com.ezBlog.backend.model.PostPageResponse;
import com.ezBlog.backend.model.PostRequest;
import com.ezBlog.backend.model.PostResponse;
import com.ezBlog.backend.security.ApiPaths;
import com.ezBlog.backend.service.PostService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping(ApiPaths.BASE + "/posts")
public class PostController {

    private final PostService postService;

    public PostController(PostService postService) {
        this.postService = postService;
    }

    @GetMapping
    public PostPageResponse getAllPosts(@RequestParam(defaultValue = "0") int page,
                                        @RequestParam(defaultValue = "10") int size,
                                        @RequestParam(required = false) String category,
                                        @RequestParam(required = false) String q) {
        return postService.findPublishedPosts(page, size, category, q);
    }

    @GetMapping("/{id}")
    public Map<String, Object> getPostById(@PathVariable Long id, Authentication authentication) {
        // Public endpoint: authentication is null for visitors, set if they sent a valid token
        return Map.of("post", postService.getVisiblePost(id, currentEmail(authentication)));
    }

    @GetMapping("/my-posts")
    public Map<String, Object> getMyPosts(Authentication authentication) {
        return Map.of("posts", postService.findMyPosts(authentication.getName()));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PostResponse createPost(@Valid @RequestBody PostRequest postRequest, Authentication authentication) {
        return postService.createPost(postRequest, authentication.getName());
    }

    @PutMapping("/{id}")
    public Map<String, Object> updatePost(@PathVariable Long id,
                                          @Valid @RequestBody PostRequest postRequest,
                                          Authentication authentication) {
        PostResponse updated = postService.updatePost(id, postRequest, authentication.getName());
        return Map.of("message", "Post updated successfully", "post", updated);
    }

    @DeleteMapping("/{id}")
    public Map<String, String> deletePost(@PathVariable Long id, Authentication authentication) {
        postService.deletePost(id, authentication.getName());
        return Map.of("message", "Post deleted successfully");
    }

    private static String currentEmail(Authentication authentication) {
        if (authentication == null || authentication instanceof AnonymousAuthenticationToken) {
            return null;
        }
        return authentication.getName();
    }
}