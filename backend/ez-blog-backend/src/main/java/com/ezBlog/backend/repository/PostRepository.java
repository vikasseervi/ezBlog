package com.ezBlog.backend.repository;


import com.ezBlog.backend.model.Post;
import com.ezBlog.backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {

    // Find all published posts sorted by creation date (descending)
    @Query("SELECT p FROM Post p WHERE p.published = true ORDER BY p.createdAt DESC")
    List<Post> findAllPublishedPosts();

    // Find posts by author sorted by creation date (descending)
    List<Post> findByAuthorOrderByCreatedAtDesc(User author);

    // Find published posts by category sorted by creation date (descending)
    @Query("SELECT p FROM Post p WHERE p.category = :category AND p.published = true ORDER BY p.createdAt DESC")
    List<Post> findByCategoryAndPublishedTrueOrderByCreatedAtDesc(@Param("category") String category);

    // Find published posts by author
    @Query("SELECT p FROM Post p WHERE p.author = :author AND p.published = true ORDER BY p.createdAt DESC")
    List<Post> findByAuthorAndPublishedTrueOrderByCreatedAtDesc(@Param("author") User author);
}

