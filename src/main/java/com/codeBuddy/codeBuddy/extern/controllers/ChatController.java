package com.codeBuddy.codeBuddy.extern.controllers;


import com.codeBuddy.codeBuddy.domain.Comment;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;

@Controller
@Slf4j
public class ChatController {

    @MessageMapping("/chat.sendMessage")
    @SendTo("/topic/public")
    public Comment sendMessage(@Payload Comment comment){
        log.info("Message received: " + comment);
        return comment;
    }

    @MessageMapping("/chat.addUser")
    @SendTo("/topic/public")
    public Comment addUser(@Payload Comment comment,
                               SimpMessageHeaderAccessor headerAccessor) {
        // Add username in web socket session
        headerAccessor.getSessionAttributes().put("username", comment.getStudent());
        log.info("User is added");
        return comment;
    }
}
