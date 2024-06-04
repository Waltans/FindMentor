package com.codeBuddy.codeBuddy.extern.controllers;

import com.codeBuddy.codeBuddy.application.repository.PostRepository;
import com.codeBuddy.codeBuddy.application.services.PostService;
import com.codeBuddy.codeBuddy.domain.Post;
import com.codeBuddy.codeBuddy.extern.Dto.postDtos.PostDTO;
import com.codeBuddy.codeBuddy.extern.assemblers.CommentAssembler;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("posts")
@RequiredArgsConstructor
public class PostController {


    private final PostRepository postRepository;
    private final CommentAssembler commentAssembler;
    private final PostService postService;


    @GetMapping
    public ResponseEntity<Page<PostDTO>> getPosts(Pageable pageable) {
        Page<Post> posts = postRepository.findAll(pageable);
        Page<PostDTO> postDto = posts.map(post ->
                PostDTO.builder()
                        .id(post.getId())
                        .comments(post.getComments().stream().map(commentAssembler::toModel)
                                .limit(3).toList())
                        .countOfLikes(post.getCountOfLikes())
                        .description(post.getDescription())
                        .urlPhoto(post.getUrlPhoto())
                        .studentId(post.getStudent().getId())
                        .localDateTime(post.getLocalDateTime())
                        .build()
        );
        return ResponseEntity.ok(postDto);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PostDTO> getPostById(@PathVariable Long id) {
        return postService.getPostById(id).map(post -> {
                    PostDTO postDto = PostDTO.builder()
                            .id(post.getId())
                            .comments(post.getComments().stream().map(commentAssembler::toModel)
                                    .limit(3).toList())
                            .countOfLikes(post.getCountOfLikes())
                            .description(post.getDescription())
                            .urlPhoto(post.getUrlPhoto())
                            .studentId(post.getStudent().getId())
                            .localDateTime(post.getLocalDateTime())
                            .build();
                    return ResponseEntity.ok(postDto);
                }
        ).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> likePost(@PathVariable Long id) {
        Optional<Post> post = postService.getPostById(id);
        if (post.isPresent()) {
            post.get().setCountOfLikes();
            postRepository.save(post.get());
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.noContent().build();
    }
}

