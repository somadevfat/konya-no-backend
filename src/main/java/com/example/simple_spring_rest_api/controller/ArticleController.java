package com.example.simple_spring_rest_api.controller;


import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.simple_spring_rest_api.domain.Article;
import com.example.simple_spring_rest_api.service.ArticleService;

@RequestMapping("/api/articles")
@RestController
public class ArticleController {

    @Autowired
    private ArticleService articleService;

    // すべての記事を取得
    @GetMapping
    public List<Article> getAllArticles() {
        return articleService.findAllArticles();
    }

    // 新しい記事を作成
    @PostMapping
    public Article createArticle(@RequestBody Article article) {
        return articleService.createArticle(article);
    }

    // 記事を更新
    @PutMapping("/{id}")
    public Article updateArticle(@PathVariable Long id, @RequestBody Article articleDetails) {
        return articleService.updateArticle(id, articleDetails);
    }

    // 記事を削除
    @DeleteMapping("/{id}")
    public void deleteArticle(@PathVariable Long id) {
        articleService.deleteArticle(id);
    }
    
}
