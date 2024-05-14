package com.CodeBuddy.CodeBuddy.extern.controllers;


import com.CodeBuddy.CodeBuddy.application.services.KeywordService;
import com.CodeBuddy.CodeBuddy.extern.DTO.KeywordDTO;
import com.CodeBuddy.CodeBuddy.extern.assemblers.KeywordAssembler;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/keywords")
public class KeywordController {

    private final KeywordService keywordService;
    private final KeywordAssembler keywordAssembler;

    @Autowired
    public KeywordController(KeywordService keywordService, KeywordAssembler keywordAssembler) {
        this.keywordService = keywordService;
        this.keywordAssembler = keywordAssembler;
    }

    @PostMapping("/create")
    public ResponseEntity<Void> createKeyword(@RequestBody @Valid KeywordDTO keywordDTO) {
        if (keywordService.addKeyword(keywordAssembler.mapToKeyword(keywordDTO)))
            return ResponseEntity.ok().build();
        return ResponseEntity.badRequest().build();
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteKeyword(@PathVariable("id") Long id) {
        keywordService.removeKeyword(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/all")
    public ResponseEntity<List<KeywordDTO>> getAllKeywords() {
        List<KeywordDTO> keywordDTOS = keywordService.getAllKeywords().stream().map(keywordAssembler::mapToKeywordDTO).toList();
        return ResponseEntity.ok(keywordDTOS);
    }


}
