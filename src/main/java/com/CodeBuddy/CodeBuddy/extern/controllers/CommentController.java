package com.CodeBuddy.CodeBuddy.extern.controllers;


import com.CodeBuddy.CodeBuddy.application.services.PostService;
import com.CodeBuddy.CodeBuddy.application.services.StudentService;
import com.CodeBuddy.CodeBuddy.extern.DTO.NewCommentDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
@Slf4j
public class CommentController {
    private final StudentService studentService;
    private final PostService postService;


    @MessageMapping("/posts/{postId}/comments")
    public void createCommentByStudent(@Payload NewCommentDTO commentDTO) {
        postService.getPostById(commentDTO.getPostId()).ifPresent(post ->
                studentService.sendComment(commentDTO.getStudentId(),
                        commentDTO.getPostId(), commentDTO.getComment()));
    }
}
