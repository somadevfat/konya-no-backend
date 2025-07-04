package com.example.simple_spring_rest_api.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.simple_spring_rest_api.domain.Article;
import com.example.simple_spring_rest_api.repository.ArticleRepository;

@Service
public class ArticleService {

    @Autowired
    private ArticleRepository articleRepository;

    public List<Article> findAllArticles() {
        return articleRepository.findAll();
    }
    public Article createArticle(Article article) {
        return articleRepository.save(article);
    }
    public Article updateArticle(Long id, Article articleDetails) {
        return articleRepository.save(articleDetails);
    }
    public void deleteArticle(Long id) {
        articleRepository.deleteById(id);
    }
}
