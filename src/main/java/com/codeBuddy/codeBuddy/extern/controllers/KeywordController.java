package com.codeBuddy.codeBuddy.extern.controllers;


import com.codeBuddy.codeBuddy.application.services.KeywordService;
import com.codeBuddy.codeBuddy.extern.Dto.keywordDtos.CreatedKeywordDto;
import com.codeBuddy.codeBuddy.extern.assemblers.KeywordAssembler;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    @PostMapping
    public ResponseEntity<Void> createKeyword(@RequestBody @Valid CreatedKeywordDto createdKeywordDto) {
        if (keywordService.addKeyword(keywordAssembler.mapToKeyword(createdKeywordDto)))
            return ResponseEntity.ok().build();
        return ResponseEntity.badRequest().build();
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Void> deleteKeyword(@PathVariable("id") Long id) {
        keywordService.removeKeyword(id);
        return ResponseEntity.ok().build();
    }


}
