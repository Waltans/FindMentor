package com.CodeBuddy.CodeBuddy.application.services;

import com.CodeBuddy.CodeBuddy.application.repository.KeywordRepository;
import com.CodeBuddy.CodeBuddy.domain.Keyword;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class KeywordService {

    private final KeywordRepository keywordRepository;

    @Autowired
    public KeywordService(KeywordRepository keywordRepository) {
        this.keywordRepository = keywordRepository;
    }

    /**
     * Метод добавления ключевого слова в базу данных
     * @param keyword ключевое слово
     */
    public void addKeyword(Keyword keyword){
        if(keyword != null){
            keywordRepository.save(keyword);
            log.info("Ключевое слово с id = {} сохранено в базу данных", keyword.getId());
        }
    }

    /**
     * Метод получения всех ключевых слов из базы данных
     * @return Список ключевых слов
     */
    public List<Keyword> getAllKeywords(){
        List<Keyword> keywords = keywordRepository.findAll();
        log.info("Получен список всех ключевых слов");
        return keywords;
    }

    public List<Keyword> getAllKeywordsById(List<Long> id){
        List<Keyword> keywords = keywordRepository.findAll(id);
        log.info("Получен список ключевых слов по определенным id");
        return keywords;
    }

    /**
     * Метод получения ключевого слова по id
     * @param id идентификатор ключевого слова
     * @return optional ключевого слова
     */
    public Optional<Keyword> getById(Long id) {
        Optional<Keyword> keyword = keywordRepository.findById(id);
        keyword.ifPresentOrElse(k -> log.info("Ключевое слово с id = {} найдено", id),
                () -> log.info("Ключевое слово с id = {} не найдено", id));
        return keyword;
    }
}
