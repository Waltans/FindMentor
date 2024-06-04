package com.codeBuddy.codeBuddy.extern.assemblers;

import com.codeBuddy.codeBuddy.domain.Comment;
import com.codeBuddy.codeBuddy.extern.Dto.postDtos.CommentUnderPostDto;
import org.springframework.stereotype.Component;

@Component
public class CommentAssembler {


    public CommentUnderPostDto toModel(Comment comment) {
        if (comment != null) {
            CommentUnderPostDto commentDto = new CommentUnderPostDto();
            if (comment.getMentor() != null) {
                commentDto.setMentor(comment.getMentor().getId());
                commentDto.setPhotoUrl(comment.getMentor().getUrlPhoto());
            } else if (comment.getStudent() != null) {
                commentDto.setStudent(comment.getStudent().getId());
                commentDto.setPhotoUrl(comment.getStudent().getPhotoUrl());
            }
            commentDto.setContent(comment.getContent());
            commentDto.setDate(comment.getDate());
            return commentDto;
        }
        return null;
    }
}
