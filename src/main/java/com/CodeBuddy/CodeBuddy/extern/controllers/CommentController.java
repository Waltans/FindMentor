package com.CodeBuddy.CodeBuddy.extern.controllers;


import com.CodeBuddy.CodeBuddy.application.services.CommentService;
import com.CodeBuddy.CodeBuddy.application.services.PostService;
import com.CodeBuddy.CodeBuddy.application.services.StudentService;
import com.CodeBuddy.CodeBuddy.extern.DTO.NewCommentDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
@Slf4j
public class CommentController {

    private final SimpMessagingTemplate messagingTemplate;
    private final CommentService commentService;
    private final StudentService studentService;
    private final PostService postService;


    @MessageMapping("/posts/{postId}/comments")
    public void createCommentByStudent(@Payload NewCommentDTO commentDTO) {
        log.info(String.valueOf(postService.getPostById(commentDTO.getPostId())));
        postService.getPostById(commentDTO.getPostId()).ifPresent(post ->
                studentService.sendComment(commentDTO.getStudentId(),
                        commentDTO.getPostId(), commentDTO.getComment()));
        messagingTemplate.convertAndSend("/post/comments",
                commentService.findCommentsByPostId(commentDTO.getPostId(), Pageable.unpaged()));
    }
}
