# 生JDBC Webアプリケーション開発完全ガイド

**作成日時:** 2025年7月7日 13:56:52 JST  
**対象:** バックエンドエンジニアを目指す初心者  
**目標:** 実際の開発フローと効果的な学習方法の習得

---

## 🎯 **開発フロー全体像**

### **実際のプロジェクト開発順序**
```
1. 要件定義・分析
2. データベース設計
3. API設計
4. プロジェクト初期設定
5. ドメインクラス作成
6. データアクセス層実装
7. ビジネスロジック層実装
8. プレゼンテーション層実装
9. テスト実装
10. 動作確認・デバッグ
```

---

## 📋 **Phase 1: 要件定義・分析 (30分)**

### **1.1 要件の整理**
```
今回の要件:
- ブログ記事の管理システム
- 記事の作成、読み取り、更新、削除（CRUD）
- REST APIとして提供
- JSON形式でのデータ交換
```

### **1.2 機能一覧の作成**
- [ ] 記事一覧の取得
- [ ] 記事詳細の取得
- [ ] 記事の新規作成
- [ ] 記事の更新
- [ ] 記事の削除

### **1.3 非機能要件**
- [ ] レスポンス時間: 1秒以内
- [ ] 同時接続数: 100ユーザー
- [ ] データの永続化
- [ ] 適切なエラーハンドリング

---

## 🗄️ **Phase 2: データベース設計 (45分)**

### **2.1 エンティティの特定**
```
主要エンティティ: Article（記事）

属性:
- id: 一意識別子
- title: タイトル
- content: 内容
- created_at: 作成日時
- updated_at: 更新日時
```

### **2.2 テーブル設計**
```sql
-- articlesテーブルの設計
CREATE TABLE articles (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    content TEXT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);
```

### **2.3 制約の設定**
- **主キー**: id（自動採番）
- **NOT NULL制約**: title, content
- **文字数制限**: title（255文字）
- **インデックス**: 必要に応じて作成日時

### **2.4 初期データの準備**
```sql
-- 初期データ
INSERT INTO articles (title, content) VALUES 
('はじめての記事', 'これは最初の記事です。'),
('Spring Boot学習', 'Spring Bootの基本を学んでいます。'),
('生JDBC実装', 'JPAを使わずに生JDBCで実装してみました。');
```

---

## 🔌 **Phase 3: API設計 (30分)**

### **3.1 RESTful API設計**
```
GET    /api/articles      - 記事一覧取得
GET    /api/articles/{id} - 記事詳細取得
POST   /api/articles      - 記事作成
PUT    /api/articles/{id} - 記事更新
DELETE /api/articles/{id} - 記事削除
```

### **3.2 リクエスト・レスポンス形式**
```json
// 記事作成リクエスト (POST /api/articles)
{
    "title": "新しい記事",
    "content": "記事の内容です"
}

// 記事レスポンス
{
    "id": 1,
    "title": "新しい記事",
    "content": "記事の内容です",
    "createdAt": "2025-07-07T13:00:00",
    "updatedAt": "2025-07-07T13:00:00"
}
```

### **3.3 HTTPステータスコード設計**
- **200 OK**: 取得・更新成功
- **201 Created**: 作成成功
- **204 No Content**: 削除成功
- **400 Bad Request**: 入力エラー
- **404 Not Found**: リソースが見つからない
- **500 Internal Server Error**: サーバーエラー

---

## ⚙️ **Phase 4: プロジェクト初期設定 (15分)**

### **4.1 build.gradle設定**
```gradle
dependencies {
    implementation 'org.springframework.boot:spring-boot-starter-web'
    implementation 'org.springframework.boot:spring-boot-starter-jdbc'
    runtimeOnly 'com.h2database:h2'
    developmentOnly 'org.springframework.boot:spring-boot-devtools'
    implementation 'org.springdoc:springdoc-openapi-starter-webmvc-ui:2.2.0'
}
```

### **4.2 application.properties設定**
```properties
# H2データベース設定
spring.datasource.url=jdbc:h2:mem:testdb
spring.datasource.driverClassName=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=

# H2コンソール有効化
spring.h2.console.enabled=true
spring.h2.console.path=/h2-console

# ログ設定
logging.level.org.springframework.jdbc=DEBUG
```

---

## 🏗️ **Phase 5: 実装フェーズ**

### **5.1 ドメインクラス作成 (30分)**

#### **設計のポイント**
- Plain Javaで実装（Lombokなし）
- 全フィールドprivate
- 適切なコンストラクタ
- ゲッター・セッター手動実装
- toString, equals, hashCode実装

```java
/**
 * 記事を表すドメインクラス
 */
public class Article {
    private Long id;
    private String title;
    private String content;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // コンストラクタ（3種類）
    public Article() {}
    
    public Article(String title, String content) {
        this.title = title;
        this.content = content;
    }
    
    public Article(Long id, String title, String content, 
                   LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
    
    // ゲッター・セッター（全フィールド）
    // toString, equals, hashCode
}
```

### **5.2 データアクセス層実装 (60分)**

#### **設計のポイント**
- 生JDBC使用
- try-with-resources活用
- SQLインジェクション対策
- 適切な例外処理
- ResultSetからオブジェクトへのマッピング

```java
@Repository
public class ArticleRepository {
    private final DataSource dataSource;
    
    // CRUD操作の実装
    public List<Article> findAll() {
        String sql = "SELECT id, title, content, created_at, updated_at FROM articles ORDER BY id";
        // 実装詳細...
    }
    
    public Optional<Article> findById(Long id) {
        String sql = "SELECT id, title, content, created_at, updated_at FROM articles WHERE id = ?";
        // 実装詳細...
    }
    
    public Article save(Article article) {
        String sql = "INSERT INTO articles (title, content, created_at, updated_at) VALUES (?, ?, ?, ?)";
        // 実装詳細...
    }
    
    // update, deleteById メソッド
    // mapResultSetToArticle ヘルパーメソッド
}
```

### **5.3 ビジネスロジック層実装 (30分)**

#### **設計のポイント**
- 入力値検証
- ビジネスルール実装
- 適切な例外処理
- トランザクション境界

```java
@Service
public class ArticleService {
    private final ArticleRepository articleRepository;
    
    public List<Article> findAllArticles() {
        return articleRepository.findAll();
    }
    
    public Article findArticleById(Long id) {
        return articleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("記事が見つかりません。ID: " + id));
    }
    
    public Article createArticle(Article article) {
        // 入力値検証
        validateArticle(article);
        return articleRepository.save(article);
    }
    
    // 他のメソッド + バリデーションロジック
}
```

### **5.4 プレゼンテーション層実装 (45分)**

#### **設計のポイント**
- RESTfulなエンドポイント
- 適切なHTTPステータスコード
- エラーハンドリング
- JSON変換

```java
@RestController
@RequestMapping("/api/articles")
public class ArticleController {
    private final ArticleService articleService;
    
    @GetMapping
    public ResponseEntity<List<Article>> getAllArticles() {
        try {
            List<Article> articles = articleService.findAllArticles();
            return ResponseEntity.ok(articles);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    // 他のエンドポイント実装
}
```

---

## 🧪 **Phase 6: テスト・動作確認 (30分)**

### **6.1 単体テスト**
```bash
# アプリケーション起動
./gradlew bootRun

# 基本動作確認
curl -X GET http://localhost:8080/api/articles
curl -X GET http://localhost:8080/api/articles/1
curl -X POST http://localhost:8080/api/articles \
  -H "Content-Type: application/json" \
  -d '{"title":"テスト記事","content":"テスト内容"}'
```

### **6.2 Swagger UI確認**
```
http://localhost:8080/swagger-ui/index.html
```

### **6.3 H2コンソール確認**
```
http://localhost:8080/h2-console
```

---

## 📚 **学習ステップ詳細**

### **Step 1: 完全模写 (3-5回)**

#### **目標**
- コードの構造を理解する
- Spring Bootの基本パターンを覚える
- 生JDBCの書き方を習得する

#### **やり方**
1. **一字一句そのまま写す**
2. **コメントも含めて全て写す**
3. **エラーが出たら原因を調べて修正**
4. **動作確認まで完了させる**

#### **注意点**
- 理解しなくても良い（まずは手に覚えさせる）
- 写し間違いでエラーが出ることを恐れない
- 1回あたり2-3時間かける

### **Step 2: TODOコメント実装 (2-3回)**

#### **目標**
- 実装の流れを理解する
- 各メソッドの役割を把握する
- 自分で考えながら実装する

#### **やり方**
```java
// TODO: 全記事を取得するメソッドを実装
// 1. SQLを定義する
// 2. データベース接続を取得する
// 3. PreparedStatementを作成する
// 4. ResultSetを処理する
// 5. Articleオブジェクトのリストを返す
public List<Article> findAll() {
    // ここに実装
}
```

#### **TODOコメントの例**
```java
// ArticleRepository.java
public class ArticleRepository {
    
    // TODO: DataSourceをコンストラクタで受け取る
    
    // TODO: 全記事取得メソッド
    // - SELECT文でid順にソート
    // - ResultSetをArticleオブジェクトに変換
    
    // TODO: ID指定取得メソッド
    // - WHERE句でid指定
    // - 見つからない場合はOptional.empty()
    
    // TODO: 記事保存メソッド
    // - INSERT文実行
    // - 自動生成されたIDを取得
    // - 作成日時・更新日時を設定
}
```

### **Step 3: 完全自力実装 (2-3回)**

#### **目標**
- 要件から設計・実装まで自力で行う
- 別ドメインで応用力を確認する
- 実務レベルの実装力を身につける

#### **やり方**
1. **要件定義から開始**
2. **データベース設計**
3. **API設計**
4. **実装**
5. **テスト**

#### **練習用ドメイン**
```java
// User管理システム
public class User {
    private Long id;
    private String name;
    private String email;
    private Integer age;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

// Product管理システム
public class Product {
    private Long id;
    private String name;
    private BigDecimal price;
    private String category;
    private Integer stock;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
```

---

## 🎯 **各ステップの詳細スケジュール**

### **Week 1: 模写期間**
```
Day 1-2: 1回目の完全模写（理解度20%）
Day 3-4: 2回目の完全模写（理解度40%）
Day 5-6: 3回目の完全模写（理解度60%）
Day 7:   復習・疑問点整理
```

### **Week 2: TODO実装期間**
```
Day 1-2: ArticleRepositoryのTODO実装
Day 3-4: ArticleServiceのTODO実装
Day 5-6: ArticleControllerのTODO実装
Day 7:   全体テスト・デバッグ
```

### **Week 3: 自力実装期間**
```
Day 1-2: User管理システム設計・実装
Day 3-4: Product管理システム設計・実装
Day 5-6: 複雑な機能追加（検索、ページネーション等）
Day 7:   総合復習・次ステップ準備
```

---

## 🔧 **開発時のベストプラクティス**

### **コーディング規約**
- **クラス名**: PascalCase（例: ArticleService）
- **メソッド名**: camelCase（例: findAllArticles）
- **定数名**: UPPER_SNAKE_CASE（例: MAX_TITLE_LENGTH）
- **インデント**: スペース4つ

### **コメント規約**
```java
/**
 * 記事の一覧を取得します
 * @return 全記事のリスト
 * @throws RuntimeException データベースアクセスに失敗した場合
 */
public List<Article> findAllArticles() {
    // 実装
}
```

### **エラーハンドリング**
```java
try (Connection connection = dataSource.getConnection()) {
    // データベース処理
} catch (SQLException e) {
    // ログ出力
    logger.error("データベースアクセスエラー", e);
    // 適切な例外をスロー
    throw new RuntimeException("記事の取得に失敗しました", e);
}
```

### **SQL記述規約**
```java
// 読みやすいSQL
String sql = """
    SELECT id, title, content, created_at, updated_at 
    FROM articles 
    WHERE created_at > ? 
    ORDER BY created_at DESC
    """;
```

---

## 📊 **進捗チェックリスト**

### **Phase 1: 模写完了チェック**
- [ ] エラーなしでビルド・実行できる
- [ ] 全APIが正常に動作する
- [ ] コードの構造を説明できる
- [ ] 生JDBCの基本パターンを理解している

### **Phase 2: TODO実装完了チェック**
- [ ] TODOコメントを見て実装できる
- [ ] 各メソッドの役割を理解している
- [ ] SQLを自分で書ける
- [ ] エラーハンドリングができる

### **Phase 3: 自力実装完了チェック**
- [ ] 要件から設計・実装まで一人でできる
- [ ] 別ドメインで応用できる
- [ ] 適切なテストができる
- [ ] 実務レベルの品質で実装できる

---

## 🚀 **次のステップへの準備**

### **JdbcTemplate移行準備**
- [ ] 生JDBCの完全理解
- [ ] SQLの自在な操作
- [ ] Spring DIの理解

### **MyBatis移行準備**
- [ ] XMLの基本理解
- [ ] 動的SQLの概念理解
- [ ] 複雑なマッピングの理解

### **実践プロジェクト準備**
- [ ] 複数テーブルの設計
- [ ] トランザクション管理
- [ ] パフォーマンス最適化

---

## 💡 **よくある質問・トラブルシューティング**

### **Q: 模写でエラーが出た場合は？**
**A:** エラーメッセージをよく読み、以下を確認：
- タイポがないか
- インポート文が正しいか
- アノテーションが正しいか
- SQLの構文が正しいか

### **Q: 理解できない部分があるときは？**
**A:** 
1. まずは模写を完了させる
2. 動作確認で実際の動きを見る
3. デバッガーでステップ実行
4. 公式ドキュメントを確認

### **Q: どのくらいの期間で習得できる？**
**A:**
- 模写: 1週間
- TODO実装: 1週間  
- 自力実装: 1週間
- **合計3週間で基礎習得**

---

## 🎖️ **最終目標**

**3週間後のあなた:**
- 生JDBCで基本的なWebアプリケーションを一人で作れる
- データベース設計からAPI実装まで一通りできる
- SQLを自在に書ける
- 実務で通用する基礎力を身につけている

**この学習法の特徴:**
- 段階的で確実な習得
- 実践的なスキルに集中
- 本質的な理解を重視
- 実務で即戦力になれる

**頑張って！この流れで学習すれば必ず身につきます！** 🎯 