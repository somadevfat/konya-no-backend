package com.example.simple_spring_rest_api.domain;

import java.time.LocalDateTime;

/**
 * 記事を表すドメインクラス
 * JPA、Lombokを使わずにPlain Javaで実装
 */
public class Article {
    
    private Long id;
    private String title;
    private String content;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // デフォルトコンストラクタ
    public Article() {
    }
    
    // 全フィールドを受け取るコンストラクタ
    public Article(Long id, String title, String content, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
    
    // 新規作成用コンストラクタ（IDなし）
    public Article(String title, String content) {
        this.title = title;
        this.content = content;
    }
    
    // ゲッターメソッド
    public Long getId() {
        return id;
    }
    
    public String getTitle() {
        return title;
    }
    
    public String getContent() {
        return content;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    // セッターメソッド
    public void setId(Long id) {
        this.id = id;
    }
    
    public void setTitle(String title) {
        this.title = title;
    }
    
    public void setContent(String content) {
        this.content = content;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
    
    // toString メソッド（デバッグ用）
    @Override
    public String toString() {
        return "Article{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", content='" + content + '\'' +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
    
    // equals メソッド
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        
        Article article = (Article) o;
        return id != null ? id.equals(article.id) : article.id == null;
    }
    
    // hashCode メソッド
    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}
