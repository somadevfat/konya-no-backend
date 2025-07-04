package com.example.blog.article.application;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.blog.article.domain.Article;
import com.example.blog.article.infrastructure.ArticleRepository;

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
