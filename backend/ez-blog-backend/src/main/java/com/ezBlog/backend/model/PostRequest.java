package com.ezBlog.backend.model;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PostRequest {

    @NotBlank(message = "Title is required")
    @Size(max = 200, message = "Title must be at most 200 characters")
    private String title;

    @NotBlank(message = "Content is required")
    @Size(max = 50000, message = "Content must be at most 50,000 characters")
    private String content;

    @Size(max = 500, message = "Excerpt must be at most 500 characters")
    private String excerpt;

    @NotBlank(message = "Category is required")
    @Size(max = 50, message = "Category must be at most 50 characters")
    private String category;

    @Size(max = 10, message = "At most 10 tags")
    private List<@NotBlank @Size(max = 30, message = "Each tag must be at most 30 characters") String> tags;

    @Min(value = 1, message = "Read time must be at least 1 minute")
    @Max(value = 600, message = "Read time must be at most 600 minutes")
    private Integer readTime;

    private Boolean published;
}