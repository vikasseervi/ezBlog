package com.ezBlog.backend.service;


import com.ezBlog.backend.model.Post;
import com.ezBlog.backend.model.PostRequest;
import com.ezBlog.backend.model.User;
import com.ezBlog.backend.repository.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class PostService {
    @Autowired
    private PostRepository postRepository;

    public List<Post> findAllPublishedPosts() {
        return postRepository.findAllPublishedPosts();
    }

    public List<Post> findByAuthor(User author) {
        return postRepository.findByAuthorOrderByCreatedAtDesc(author);
    }

    public List<Post> findByAuthorId(Long authorId) {
        User author = new User();
        author.setId(authorId);
        return postRepository.findByAuthorOrderByCreatedAtDesc(author);
    }

    public List<Post> findByCategory(String category) {
        return postRepository.findByCategoryAndPublishedTrueOrderByCreatedAtDesc(category);
    }

    public Optional<Post> findById(Long id) {
        return postRepository.findById(id);
    }

    public Post save(Post post) {

        if (post.getCreatedAt() == null) {
            post.setCreatedAt(LocalDateTime.now());
        }
        post.setUpdatedAt(LocalDateTime.now());

        return postRepository.save(post);
    }

    public Post createPost(PostRequest postRequest, Long userId) {
        Post post = new Post();

        post.setTitle(postRequest.getTitle());
        post.setContent(postRequest.getContent());
        post.setExcerpt(postRequest.getExcerpt());
        post.setCategory(postRequest.getCategory());
        post.setTags(postRequest.getTags());
        post.setReadTime(postRequest.getReadTime() != null ? postRequest.getReadTime() :
                calculateReadTime(postRequest.getContent()));
        post.setPublished(postRequest.getPublished() != null ? postRequest.getPublished() : true);

        User author = new User();
        author.setId(userId);

        post.setAuthor(author);

        return save(post);
    }

    public Post updatePost(Long id, PostRequest postRequest, User author) {
        Optional<Post> existingPost = findById(id);
        if (existingPost.isEmpty()) {
            throw new RuntimeException("Post not found");
        }

        Post post = existingPost.get();

        if (!post.getAuthor().getId().equals(author.getId())) {
            throw new RuntimeException("Not authorized to update this post");
        }

        if (postRequest.getTitle() != null) {
            post.setTitle(postRequest.getTitle());
        }
        if (postRequest.getContent() != null) {
            post.setContent(postRequest.getContent());
            post.setReadTime(postRequest.getReadTime() != null ? postRequest.getReadTime() :
                    calculateReadTime(postRequest.getContent()));
        }
        if (postRequest.getExcerpt() != null) {
            post.setExcerpt(postRequest.getExcerpt());
        }
        if (postRequest.getCategory() != null) {
            post.setCategory(postRequest.getCategory());
        }
        if (postRequest.getTags() != null) {
            post.setTags(postRequest.getTags());
        }
        if (postRequest.getPublished() != null) {
            post.setPublished(postRequest.getPublished());
        }

        return save(post);
    }

    public void deletePost(Long id, Long userId) {
        Optional<Post> existingPost = findById(id);
        if (existingPost.isEmpty()) {
            throw new RuntimeException("Post not found");
        }

        Post post = existingPost.get();

        if (!post.getAuthor().getId().equals(userId)) {
            throw new RuntimeException("Not authorized to delete this post");
        }

        postRepository.deleteById(id);
    }

    private Integer calculateReadTime(String content) {
        if (content == null) return 5;
        int wordCount = content.split("\\s+").length;
        return Math.max(1, (int) Math.ceil((double) wordCount / 200));
    }
}
