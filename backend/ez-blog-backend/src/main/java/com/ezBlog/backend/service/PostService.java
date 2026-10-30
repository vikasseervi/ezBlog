package com.ezBlog.backend.service;

import com.ezBlog.backend.model.*;
import com.ezBlog.backend.repository.PostRepository;
import com.ezBlog.backend.repository.UserRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;

@Service
public class PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private static final int MAX_PAGE_SIZE = 50;

    public PostService(PostRepository postRepository, UserRepository userRepository) {
        this.postRepository = postRepository;
        this.userRepository = userRepository;
    }

    // ---------- reads ----------

    @Transactional(readOnly = true)
    public PostPageResponse findPublishedPosts(int page, int size, String category, String q) {
        int safePage = Math.max(page, 0);
        int safeSize = Math.clamp(size, 1, MAX_PAGE_SIZE);
        Pageable pageable = PageRequest.of(safePage, safeSize,
                Sort.by(Sort.Order.desc("createdAt"), Sort.Order.desc("id")));

        boolean hasCategory = category != null && !category.isBlank();
        boolean hasQuery = q != null && !q.isBlank();

        Slice<Post> slice;
        if (hasQuery) {
            String pattern = toLikePattern(q.trim());
            slice = hasCategory
                    ? postRepository.searchPublishedPostsByCategory(category.trim(), pattern, pageable)
                    : postRepository.searchPublishedPosts(pattern, pageable);
        } else {
            slice = hasCategory
                    ? postRepository.findPublishedPostsByCategory(category.trim(), pageable)
                    : postRepository.findPublishedPosts(pageable);
        }

        return new PostPageResponse(
                slice.getContent().stream().map(PostResponse::from).toList(),
                slice.getNumber(),
                slice.getSize(),
                slice.hasNext());
    }

    // "50%_off" must be searched literally, so % and _ typed by the user are escaped
    private static String toLikePattern(String q) {
        String limited = q.length() > 100 ? q.substring(0, 100) : q;
        String escaped = limited.toLowerCase(Locale.ROOT)
                .replace("\\", "\\\\")
                .replace("%", "\\%")
                .replace("_", "\\_");
        return "%" + escaped + "%";
    }

    @Transactional(readOnly = true)
    public List<PostResponse> findByCategory(String category) {
        return postRepository.findByCategoryAndPublishedTrueOrderByCreatedAtDesc(category)
                .stream().map(PostResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public List<PostResponse> findMyPosts(String email) {
        return postRepository.findByAuthor_EmailIgnoreCaseOrderByCreatedAtDesc(email)
                .stream().map(PostResponse::from).toList();
    }

    /** viewerEmail is null for anonymous visitors. Drafts are visible only to their author. */
    @Transactional(readOnly = true)
    public PostResponse getVisiblePost(Long id, String viewerEmail) {
        Post post = findOrThrow(id);
        boolean visible = Boolean.TRUE.equals(post.getPublished()) || isAuthor(post, viewerEmail);
        if (!visible) {
            throw notFound();   // to everyone else a draft simply "doesn't exist"
        }
        return PostResponse.from(post);
    }

    // ---------- writes ----------

    @Transactional
    public PostResponse createPost(PostRequest postRequest, String email) {
        User author = userRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Not authenticated"));

        Post post = new Post();
        post.setAuthor(author);
        post.setTitle(postRequest.getTitle());
        post.setContent(postRequest.getContent());
        post.setExcerpt(postRequest.getExcerpt());
        post.setCategory(postRequest.getCategory());
        post.setTags(distinct(postRequest.getTags()));
        post.setReadTime(postRequest.getReadTime() != null ? postRequest.getReadTime() :
                calculateReadTime(postRequest.getContent()));
        post.setPublished(postRequest.getPublished() != null ? postRequest.getPublished() : true);

        return PostResponse.from(postRepository.save(post));
    }

    @Transactional
    public PostResponse updatePost(Long id, PostRequest postRequest, String email) {
        Post post = findOrThrow(id);
        requireAuthor(post, email, "Not authorized to update this post");

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

        return PostResponse.from(postRepository.save(post));
    }

    @Transactional
    public void deletePost(Long id, String email) {
        Post post = findOrThrow(id);
        requireAuthor(post, email, "Not authorized to delete this post");
        postRepository.delete(post);
    }

    // ---------- helpers ----------

    private Post findOrThrow(Long id) {
        return postRepository.findById(id).orElseThrow(PostService::notFound);
    }

    private static ResponseStatusException notFound() {
        return new ResponseStatusException(HttpStatus.NOT_FOUND, "Post not found");
    }

    private static boolean isAuthor(Post post, String email) {
        return email != null && post.getAuthor().getEmail().equalsIgnoreCase(email);
    }

    private static void requireAuthor(Post post, String email, String message) {
        if (!isAuthor(post, email)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, message);
        }
    }

    private Integer calculateReadTime(String content) {
        if (content == null) return 5;
        int wordCount = content.trim().split("\\s+").length;
        return Math.max(1, (int) Math.ceil((double) wordCount / 200));
    }

    private static List<String> distinct(List<String> tags) {
        return tags == null ? null : new ArrayList<>(new LinkedHashSet<>(tags));
    }
}