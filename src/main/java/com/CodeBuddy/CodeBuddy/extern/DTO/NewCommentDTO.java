package com.CodeBuddy.CodeBuddy.extern.DTO;


import lombok.Data;

@Data
public class NewCommentDTO {
    Long studentId;
    Long postId;
    String comment;

}
