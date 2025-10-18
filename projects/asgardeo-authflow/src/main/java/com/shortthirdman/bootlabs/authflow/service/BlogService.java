package com.shortthirdman.bootlabs.authflow.service;

import com.shortthirdman.bootlabs.authflow.dto.Post;
import com.shortthirdman.bootlabs.authflow.dto.PostRequest;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class BlogService {

    private final Map<String, List<Post>> userPosts = new HashMap<>();
    private final Map<Long, Post> allPosts = new HashMap<>();
    private long nextId = 1;

    public List<Post> getAllPublicPosts() {
        return allPosts.values().stream()
                .limit(10)
                .toList();
    }

    public Post saveNewPost(PostRequest post, String username, String email) {
        Post newPost = new Post(nextId++, post.title(), post.content(), username);
        allPosts.put(newPost.id(), newPost);

        // Add to user's personal posts
        userPosts.computeIfAbsent(username, k -> new ArrayList<>()).add(newPost);
        return newPost;
    }

    public List<Post> getMyPosts(String username) {
        return userPosts.getOrDefault(username, Collections.emptyList());
    }

    public Boolean deletePost(long id) {
        if (allPosts.containsKey(id)) {
            // Remove from allPosts and userPosts
            Post post = allPosts.remove(id);
            userPosts.getOrDefault(post.author(), Collections.emptyList())
                    .removeIf(p -> p.id() == id);
            return true;
        }

        return false;
    }
}
