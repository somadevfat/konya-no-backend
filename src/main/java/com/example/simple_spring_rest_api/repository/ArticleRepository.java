package com.example.simple_spring_rest_api.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.example.simple_spring_rest_api.domain.Article;

/**
 * 記事データベースアクセス用Repository
 * 生JDBCで実装（Spring Data JPAを使用しない）
 */
@Repository
public class ArticleRepository {
    
    private final DataSource dataSource;
    
    @Autowired
    public ArticleRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }
    
    /**
     * 全記事を取得
     * @return 全記事のリスト
     */
    public List<Article> findAll() {
        String sql = "SELECT id, title, content, created_at, updated_at FROM articles ORDER BY id";
        List<Article> articles = new ArrayList<>();
        
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            
            while (resultSet.next()) {
                Article article = mapResultSetToArticle(resultSet);
                articles.add(article);
            }
            
        } catch (SQLException e) {
            throw new RuntimeException("全記事の取得に失敗しました", e);
        }
        
        return articles;
    }
    
    /**
     * IDによる記事の取得
     * @param id 記事ID
     * @return 記事（存在しない場合はOptional.empty()）
     */
    public Optional<Article> findById(Long id) {
        String sql = "SELECT id, title, content, created_at, updated_at FROM articles WHERE id = ?";
        
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            
            statement.setLong(1, id);
            
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    Article article = mapResultSetToArticle(resultSet);
                    return Optional.of(article);
                }
            }
            
        } catch (SQLException e) {
            throw new RuntimeException("記事の取得に失敗しました。ID: " + id, e);
        }
        
        return Optional.empty();
    }
    
    /**
     * 記事の保存（新規作成）
     * @param article 保存する記事
     * @return 保存された記事（IDが設定される）
     */
    public Article save(Article article) {
        String sql = "INSERT INTO articles (title, content, created_at, updated_at) VALUES (?, ?, ?, ?)";
        
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            LocalDateTime now = LocalDateTime.now();
            
            statement.setString(1, article.getTitle());
            statement.setString(2, article.getContent());
            statement.setTimestamp(3, Timestamp.valueOf(now));
            statement.setTimestamp(4, Timestamp.valueOf(now));
            
            int affectedRows = statement.executeUpdate();
            
            if (affectedRows == 0) {
                throw new RuntimeException("記事の保存に失敗しました");
            }
            
            // 自動生成されたIDを取得
            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    article.setId(generatedKeys.getLong(1));
                    article.setCreatedAt(now);
                    article.setUpdatedAt(now);
                } else {
                    throw new RuntimeException("記事の保存に失敗しました。IDが生成されませんでした");
                }
            }
            
        } catch (SQLException e) {
            throw new RuntimeException("記事の保存に失敗しました", e);
        }
        
        return article;
    }
    
    /**
     * 記事の更新
     * @param id 更新する記事のID
     * @param article 更新内容
     * @return 更新された記事
     */
    public Article update(Long id, Article article) {
        String sql = "UPDATE articles SET title = ?, content = ?, updated_at = ? WHERE id = ?";
        
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            
            LocalDateTime now = LocalDateTime.now();
            
            statement.setString(1, article.getTitle());
            statement.setString(2, article.getContent());
            statement.setTimestamp(3, Timestamp.valueOf(now));
            statement.setLong(4, id);
            
            int affectedRows = statement.executeUpdate();
            
            if (affectedRows == 0) {
                throw new RuntimeException("記事が見つかりません。ID: " + id);
            }
            
            // 更新された記事を取得して返す
            return findById(id).orElseThrow(() -> 
                new RuntimeException("更新後の記事取得に失敗しました。ID: " + id));
            
        } catch (SQLException e) {
            throw new RuntimeException("記事の更新に失敗しました。ID: " + id, e);
        }
    }
    
    /**
     * 記事の削除
     * @param id 削除する記事のID
     */
    public void deleteById(Long id) {
        String sql = "DELETE FROM articles WHERE id = ?";
        
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            
            statement.setLong(1, id);
            
            int affectedRows = statement.executeUpdate();
            
            if (affectedRows == 0) {
                throw new RuntimeException("記事が見つかりません。ID: " + id);
            }
            
        } catch (SQLException e) {
            throw new RuntimeException("記事の削除に失敗しました。ID: " + id, e);
        }
    }
    
    /**
     * ResultSetからArticleオブジェクトにマッピング
     * @param resultSet データベースの結果セット
     * @return Articleオブジェクト
     * @throws SQLException SQL例外
     */
    private Article mapResultSetToArticle(ResultSet resultSet) throws SQLException {
        Article article = new Article();
        article.setId(resultSet.getLong("id"));
        article.setTitle(resultSet.getString("title"));
        article.setContent(resultSet.getString("content"));
        
        Timestamp createdAt = resultSet.getTimestamp("created_at");
        if (createdAt != null) {
            article.setCreatedAt(createdAt.toLocalDateTime());
        }
        
        Timestamp updatedAt = resultSet.getTimestamp("updated_at");
        if (updatedAt != null) {
            article.setUpdatedAt(updatedAt.toLocalDateTime());
        }
        
        return article;
    }
}
