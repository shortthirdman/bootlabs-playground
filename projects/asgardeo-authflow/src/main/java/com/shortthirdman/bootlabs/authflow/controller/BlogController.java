package com.shortthirdman.bootlabs.authflow.controller;

import com.shortthirdman.bootlabs.authflow.dto.Post;
import com.shortthirdman.bootlabs.authflow.dto.PostRequest;
import com.shortthirdman.bootlabs.authflow.service.BlogService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api")
public class BlogController {

    private final BlogService blogService;

    public BlogController(BlogService blogService) {
        this.blogService = blogService;
    }

    // Public endpoint - no authentication required
    @GetMapping("/public/posts")
    public ResponseEntity<List<Post>> getPublicPosts() {
        return ResponseEntity.ok(blogService.getAllPublicPosts());
    }

    // Protected endpoint - requires USER role
    @PostMapping("/posts")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Post> createPost(@RequestBody PostRequest request,
                                           @AuthenticationPrincipal Jwt jwt) {
        String username = jwt.getClaimAsString("preferred_username");
        String email = jwt.getClaimAsString("email");
        return ResponseEntity.status(201).body(blogService.saveNewPost(request, username, email));
    }

    // Get posts for the authenticated user
    @GetMapping("/posts/my")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<List<Post>> getMyPosts(@AuthenticationPrincipal Jwt jwt) {
        String username = jwt.getClaimAsString("preferred_username");
        return ResponseEntity.ok(blogService.getMyPosts(username));
    }

    // Admin-only endpoint
    @DeleteMapping("/admin/posts/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> deletePost(@PathVariable long id) {
        var status = blogService.deletePost(id);

        if (status) {
            return ResponseEntity.ok("Post deleted successfully");
        }

        return ResponseEntity.notFound().build();
    }
}
