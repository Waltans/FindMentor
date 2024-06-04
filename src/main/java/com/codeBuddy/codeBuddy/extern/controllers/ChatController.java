package com.codeBuddy.codeBuddy.extern.controllers;


import com.codeBuddy.codeBuddy.application.services.CommentService;
import com.codeBuddy.codeBuddy.application.services.PostService;
import com.codeBuddy.codeBuddy.domain.Post;
import com.codeBuddy.codeBuddy.extern.Dto.CommentDTO;
import com.codeBuddy.codeBuddy.extern.Dto.NewCommentDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("comments")
public class ChatController {


    private final PostService postService;
    private final CommentService commentService;

    @GetMapping("posts/{postId}")
    public ResponseEntity<Page<CommentDTO>> getCommentsByPost(@PathVariable Long postId,
                                                              @RequestParam Integer pages) {

        if (pages == null) {
            return ResponseEntity.badRequest().build();
        }
        Pageable pageable = PageRequest.of(pages, 5);
        Optional<Post> postById = postService.getPostById(postId);
        if (postById.isPresent()) {
            Page<CommentDTO> commentDTOS = commentService.getCommentsByPost(postId, pageable).map(comment ->
                    CommentDTO.builder()
                            .id(comment.getId())
                            .date(comment.getDate())
                            .content(comment.getContent())
                            .student(comment.getStudent() != null ? comment.getStudent().getId() : null)
                            .mentor(comment.getMentor() != null ? comment.getMentor().getId() : null)
                            .build()

            );
            return ResponseEntity.ok(commentDTOS);
        }
        return ResponseEntity.notFound().build();
    }

    @PutMapping("posts/{postId}")
    public ResponseEntity<Void> sendComment(@AuthenticationPrincipal UserDetails userDetails,
                                            @PathVariable Long postId,
                                            @RequestBody NewCommentDTO commentDTO) {

        Optional<Post> postById = postService.getPostById(postId);
        if (postById.isPresent()) {
            commentService.sendComment(userDetails, postId, commentDTO.getComment());
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.noContent().build();
    }
}
