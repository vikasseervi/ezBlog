package com.ezBlog.backend.model;

import java.time.Instant;
import java.util.List;

public record PostResponse(
        Long id,
        String title,
        String content,
        String excerpt,
        AuthorResponse author,
        String category,
        List<String> tags,
        Integer readTime,
        Boolean published,
        Instant createdAt,
        Instant updatedAt) {

    public record AuthorResponse(Long id, String name, String bio) {}

    public static PostResponse from(Post post) {
        User author = post.getAuthor();
        return new PostResponse(
                post.getId(),
                post.getTitle(),
                post.getContent(),
                post.getExcerpt(),
                new AuthorResponse(author.getId(), author.getName(), author.getBio()),
                post.getCategory(),
                post.getTags() == null ? List.of() : List.copyOf(post.getTags()),
                post.getReadTime(),
                post.getPublished(),
                post.getCreatedAt(),
                post.getUpdatedAt());
    }
}