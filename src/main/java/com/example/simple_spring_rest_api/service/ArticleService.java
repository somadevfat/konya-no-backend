package com.example.simple_spring_rest_api.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.simple_spring_rest_api.domain.Article;
import com.example.simple_spring_rest_api.repository.ArticleRepository;

/**
 * 記事のビジネスロジック処理を担当するServiceクラス
 */
@Service
public class ArticleService {

    private final ArticleRepository articleRepository;

    @Autowired
    public ArticleService(ArticleRepository articleRepository) {
        this.articleRepository = articleRepository;
    }

    /**
     * 全記事を取得
     * 
     * @return 全記事のリスト
     */
    public List<Article> findAllArticles() {
        return articleRepository.findAll();
    }

    /**
     * IDによる記事の取得
     * 
     * @param id 記事ID
     * @return 記事
     * @throws RuntimeException 記事が見つからない場合
     */
    public Article findArticleById(Long id) {
        return articleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("記事が見つかりません。ID: " + id));
    }

    /**
     * 記事の作成
     * 
     * @param article 作成する記事
     * @return 作成された記事
     */
    public Article createArticle(Article article) {
        // 入力値の検証
        if (article.getTitle() == null || article.getTitle().trim().isEmpty()) {
            throw new IllegalArgumentException("タイトルは必須です");
        }
        if (article.getContent() == null || article.getContent().trim().isEmpty()) {
            throw new IllegalArgumentException("内容は必須です");
        }

        return articleRepository.save(article);
    }

    /**
     * 記事の更新
     * 
     * @param id             更新する記事のID
     * @param articleDetails 更新内容
     * @return 更新された記事
     */
    public Article updateArticle(Long id, Article articleDetails) {
        // 入力値の検証
        if (articleDetails.getTitle() == null || articleDetails.getTitle().trim().isEmpty()) {
            throw new IllegalArgumentException("タイトルは必須です");
        }
        if (articleDetails.getContent() == null || articleDetails.getContent().trim().isEmpty()) {
            throw new IllegalArgumentException("内容は必須です");
        }

        // 記事の存在確認
        findArticleById(id);

        return articleRepository.update(id, articleDetails);
    }

    /**
     * 記事の削除
     * 
     * @param id 削除する記事のID
     */
    public void deleteArticle(Long id) {
        // 記事の存在確認
        findArticleById(id);

        articleRepository.deleteById(id);
    }
}
