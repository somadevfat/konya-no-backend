package com.example.blog.article.infrastructure;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.blog.article.domain.Article;

public interface ArticleRepository extends JpaRepository<Article, Long> {

}
