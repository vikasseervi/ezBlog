package com.ezBlog.backend.repository;

import com.ezBlog.backend.model.Post;
import com.ezBlog.backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {

//    // Find all published posts sorted by creation date (descending)
//    @Query("SELECT p FROM Post p WHERE p.published = true ORDER BY p.createdAt DESC")
//    List<Post> findAllPublishedPosts();

    @Query("SELECT p FROM Post p WHERE p.published = true")
    Slice<Post> findPublishedPosts(Pageable pageable);

    @Query("SELECT p FROM Post p WHERE p.published = true AND p.category = :category")
    Slice<Post> findPublishedPostsByCategory(@Param("category") String category, Pageable pageable);

    @Query("""
        SELECT p FROM Post p
        WHERE p.published = true
          AND (LOWER(p.title) LIKE :pattern ESCAPE '\\'
               OR LOWER(p.excerpt) LIKE :pattern ESCAPE '\\'
               OR LOWER(p.author.name) LIKE :pattern ESCAPE '\\')
        """)
    Slice<Post> searchPublishedPosts(@Param("pattern") String pattern, Pageable pageable);

    @Query("""
        SELECT p FROM Post p
        WHERE p.published = true AND p.category = :category
          AND (LOWER(p.title) LIKE :pattern ESCAPE '\\'
               OR LOWER(p.excerpt) LIKE :pattern ESCAPE '\\'
               OR LOWER(p.author.name) LIKE :pattern ESCAPE '\\')
        """)
    Slice<Post> searchPublishedPostsByCategory(@Param("category") String category,
                                               @Param("pattern") String pattern,
                                               Pageable pageable);

    // The logged-in user's own posts (published and drafts), newest first
    List<Post> findByAuthor_EmailIgnoreCaseOrderByCreatedAtDesc(String email);

    // Find posts by author sorted by creation date (descending)
    List<Post> findByAuthorOrderByCreatedAtDesc(User author);

    // Find published posts by category sorted by creation date (descending)
    @Query("SELECT p FROM Post p WHERE p.category = :category AND p.published = true ORDER BY p.createdAt DESC")
    List<Post> findByCategoryAndPublishedTrueOrderByCreatedAtDesc(@Param("category") String category);

    // Find published posts by author
    @Query("SELECT p FROM Post p WHERE p.author = :author AND p.published = true ORDER BY p.createdAt DESC")
    List<Post> findByAuthorAndPublishedTrueOrderByCreatedAtDesc(@Param("author") User author);
}