package com.example.simple_spring_rest_api.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.simple_spring_rest_api.domain.Article;

public interface ArticleRepository extends JpaRepository<Article, Long> {

}
