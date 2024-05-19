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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
                        .comments(post.getComments().stream().map(commentAssembler::toModel).limit(3).toList())
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
                            .comments(post.getComments().stream().map(commentAssembler::toModel).limit(3).toList())
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
}
