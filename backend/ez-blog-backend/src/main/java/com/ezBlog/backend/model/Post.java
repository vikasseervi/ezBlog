package com.ezBlog.backend.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "posts")
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Title is required")
    @Size(max = 200)
    @Column(nullable = false, length = 200)
    private String title;

    @NotBlank(message = "Content is required")
    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(columnDefinition = "TEXT")
    private String excerpt;

    @NotNull(message = "Author is required")
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "author_id", nullable = false)
    private User author;

    @NotBlank(message = "Category is required")
    @Column(nullable = false, length = 50)
    private String category;

    @org.hibernate.annotations.BatchSize(size = 50)  // This let Hibernate fetch the tags in batches of 50
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "post_tags", joinColumns = @JoinColumn(name = "post_id"))
    @Column(name = "tag", length = 30)
    private List<String> tags;

    @Column(name = "read_time")
    private Integer readTime = 5;

    private Boolean published = true;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = Instant.now();
        updatedAt = createdAt;
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = Instant.now();
    }

    // Enum for categories (currently unused: category is stored as free text)
    public enum Category {
        TECHNOLOGY, LIFESTYLE, TRAVEL, FOOD, BUSINESS, HEALTH, EDUCATION, ENTERTAINMENT
    }

    // Constructor for creating post
    public Post(String title, String content, String excerpt, User author, String category,
                List<String> tags, Integer readTime, Boolean published) {
        this.title = title;
        this.content = content;
        this.excerpt = excerpt;
        this.author = author;
        this.category = category;
        this.tags = tags;
        this.readTime = readTime;
        this.published = published;
    }
}