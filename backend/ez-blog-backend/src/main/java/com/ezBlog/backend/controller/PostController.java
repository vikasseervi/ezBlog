package com.ezBlog.backend.controller;

import com.ezBlog.backend.model.Post;
import com.ezBlog.backend.model.PostRequest;
import com.ezBlog.backend.model.User;
import com.ezBlog.backend.service.PostService;
import com.ezBlog.backend.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/posts")
public class PostController {
    @Autowired
    private PostService postService;

    @Autowired
    private UserService userService;

    @GetMapping
    public ResponseEntity<?> getAllPosts() {
        try {
            List<Post> posts = postService.findAllPublishedPosts();
            Map<String, Object> response = new HashMap<>();
            response.put("posts", posts);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Server error fetching posts");
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getPostById(@PathVariable("id") String id) {
        try {
            if (id == null || id.equals("undefined") || id.trim().isEmpty()) {
                return ResponseEntity.status(400).body("Invalid post ID");
            }

            Long postId;
            try {
                postId = Long.parseLong(id);
            } catch (NumberFormatException e) {
                return ResponseEntity.status(400).body("Invalid post ID format");
            }

            Optional<Post> post = postService.findById(postId);
            if (post.isEmpty()) {
                return ResponseEntity.status(404).body("Post not found");
            }

            Map<String, Object> response = new HashMap<>();
            response.put("post", post.get());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Server error fetching post");
        }
    }

    @GetMapping("/my-posts")
    public ResponseEntity<?> getMyPosts(@RequestHeader(value = "x-user-id", required = false) Long userId) {
        try {
            if (userId == null) {
                return ResponseEntity.status(401).body("Not authenticated");
            }

            List<Post> posts = postService.findByAuthorId(userId);

            Map<String, Object> response = new HashMap<>();
            response.put("posts", posts);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Server error fetching posts");
        }
    }

    @PostMapping
    public ResponseEntity<?> createPost(@Valid @RequestBody PostRequest postRequest,
                                        @RequestHeader(value = "x-user-id", required = false) Long userId) {
        try {
            if (userId == null) {
                return ResponseEntity.status(401).body("Not authenticated");
            }

            Post post = postService.createPost(postRequest, userId);

            return ResponseEntity.status(201).body(post);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Server error creating post");
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updatePost(@PathVariable("id") String id,
                                        @Valid @RequestBody PostRequest postRequest,
                                        @RequestHeader(value = "x-user-id", required = false) Long userId) {
        try {
            if (id == null || id.equals("undefined") || id.trim().isEmpty()) {
                return ResponseEntity.status(400).body("Invalid post ID");
            }

            Long postId;
            try {
                postId = Long.parseLong(id);
            } catch (NumberFormatException e) {
                return ResponseEntity.status(400).body("Invalid post ID format");
            }

            if (userId == null) {
                return ResponseEntity.status(401).body("Not authenticated");
            }

            User user = userService.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            Post updatedPost = postService.updatePost(postId, postRequest, user);

            Map<String, Object> response = new HashMap<>();
            response.put("message", "Post updated successfully");
            response.put("post", updatedPost);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Server error updating post");
        }
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletePost(@PathVariable("id") String id,
                                        @RequestHeader(value = "x-user-id", required = false) Long userId) {
        try {
            if (id == null || id.equals("undefined") || id.trim().isEmpty()) {
                return ResponseEntity.status(400).body("Invalid post ID");
            }

            Long postId;
            try {
                postId = Long.parseLong(id);
            } catch (NumberFormatException e) {
                return ResponseEntity.status(400).body("Invalid post ID format");
            }

            if (userId == null) {
                return ResponseEntity.status(401).body("Not authenticated");
            }

            Optional<Post> existingPost = postService.findById(postId);
            if (existingPost.isEmpty()) {
                return ResponseEntity.status(404).body("Post not found");
            }

            Post post = existingPost.get();
            if (!post.getAuthor().getId().equals(userId)) {
                return ResponseEntity.status(403).body("Not authorized to delete this post");
            }

            postService.deletePost(postId, userId);

            Map<String, Object> response = new HashMap<>();
            response.put("message", "Post deleted successfully");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Server error deleting post");
        }
    }

}
