//package com.ezBlog.backend.controller.task1;
//
//import com.ezBlog.backend.model.Post;
//import com.ezBlog.backend.model.User;
//import com.ezBlog.backend.repository.PostRepository;
//import com.ezBlog.backend.repository.UserRepository;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureWebMvc;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.http.MediaType;
//import org.springframework.test.context.ActiveProfiles;
//import org.springframework.test.web.servlet.MockMvc;
//import org.springframework.test.web.servlet.setup.MockMvcBuilders;
//import org.springframework.web.context.WebApplicationContext;
//
//import java.time.LocalDateTime;
//import java.util.Arrays;
//import java.util.Map;
//
//import static org.hamcrest.Matchers.*;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
//
//@SpringBootTest
//@AutoConfigureWebMvc
//@ActiveProfiles("test")
//public class PostControllerTest {
//
//    @Autowired
//    private WebApplicationContext webApplicationContext;
//
//    @Autowired
//    private UserRepository userRepository;
//
//    @Autowired
//    private PostRepository postRepository;
//
//    @Autowired
//    private ObjectMapper objectMapper;
//
//    private MockMvc mockMvc;
//    private User testUser;
//
//    @BeforeEach
//    void setUp() {
//        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
//
//        postRepository.deleteAll();
//        userRepository.deleteAll();
//
//        testUser = new User();
//        testUser.setName("John Doe");
//        testUser.setEmail("john@example.com");
//        testUser.setPassword("password123");
//        testUser.setCreatedAt(LocalDateTime.now());
//        testUser.setUpdatedAt(LocalDateTime.now());
//        testUser = userRepository.save(testUser);
//    }
//
//    @Test
//    void shouldCreatePostWithExpectedResponseStructureAndValues() throws Exception {
//        String postData = objectMapper.writeValueAsString(Map.of(
//                "title", "Test Blog Post",
//                "content", "This is a test blog post content with some sample text for testing purposes.",
//                "excerpt", "This is a test excerpt",
//                "category", "Technology",
//                "tags", Arrays.asList("test", "blog", "sample"),
//                "readTime", 5
//        ));
//
//        mockMvc.perform(post("/api/posts")
//                        .header("x-user-id", testUser.getId())
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(postData))
//                .andExpect(status().isCreated())
//                .andExpect(jsonPath("$.title").value("Test Blog Post"))
//                .andExpect(jsonPath("$.content").value("This is a test blog post content with some sample text for testing purposes."))
//                .andExpect(jsonPath("$.excerpt").value("This is a test excerpt"))
//                .andExpect(jsonPath("$.category").value("Technology"))
//                .andExpect(jsonPath("$.tags").isArray())
//                .andExpect(jsonPath("$.tags[0]").value("test"))
//                .andExpect(jsonPath("$.tags[1]").value("blog"))
//                .andExpect(jsonPath("$.tags[2]").value("sample"))
//                .andExpect(jsonPath("$.readTime").value(5))
//                .andExpect(jsonPath("$.published").value(true))
//                .andExpect(jsonPath("$.author").exists())
//                .andExpect(jsonPath("$.author.id").value(testUser.getId()))
//                .andExpect(jsonPath("$.author.bio").value(""));
//    }
//
//    @Test
//    void shouldSaveCreatedPostToDatabase() throws Exception {
//
//        String postData = objectMapper.writeValueAsString(Map.of(
//                "title", "Test Blog Post 2",
//                "content", "This is a test blog post content with different sample text for testing purposes.",
//                "excerpt", "This is another test excerpt",
//                "category", "Travel",
//                "tags", Arrays.asList("testing", "blogs", "sample"),
//                "readTime", 3
//        ));
//
//        String response = mockMvc.perform(post("/api/posts")
//                        .header("x-user-id", testUser.getId())
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(postData))
//                .andExpect(status().isCreated())
//                .andReturn()
//                .getResponse()
//                .getContentAsString();
//
//        String postId = objectMapper.readTree(response).get("id").asText();
//
//        Post savedPost = postRepository.findById(Long.parseLong(postId)).orElse(null);
//        assert savedPost != null;
//        assert savedPost.getTitle().equals("Test Blog Post 2");
//        assert savedPost.getContent().equals("This is a test blog post content with different sample text for testing purposes.");
//        assert savedPost.getExcerpt().equals("This is another test excerpt");
//        assert savedPost.getCategory().equals("Travel");
//        assert savedPost.getTags().contains("testing");
//        assert savedPost.getTags().contains("blogs");
//        assert savedPost.getTags().contains("sample");
//        assert savedPost.getReadTime() == 3;
//        assert savedPost.getPublished();
//
//        mockMvc.perform(get("/api/posts"))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.posts").isArray())
//                .andExpect(jsonPath("$.posts[*].id", hasItem(Integer.parseInt(postId))))
//                .andExpect(jsonPath("$.posts[?(@.id == " + postId + ")].title").value("Test Blog Post 2"));
//    }
//}