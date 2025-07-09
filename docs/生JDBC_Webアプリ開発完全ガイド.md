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
- ユーザー認証機能（記事の作成・更新・削除に必要）
```

### **1.2 機能一覧の作成**
- [ ] 記事一覧の取得
- [ ] 記事詳細の取得
- [ ] 記事の新規作成
- [ ] 記事の更新
- [ ] 記事の削除
- [ ] ユーザーのログイン

### **1.3 非機能要件**
- [ ] レスポンス時間: 1秒以内
- [ ] 同時接続数: 100ユーザー
- [ ] データの永続化 (MySQL)
- [ ] 適切なエラーハンドリング
- [ ] パスワードのハッシュ化 (Bcrypt)

---

## 🗄️ **Phase 2: データベース設計 (45分)**

### **2.1 データベース・ユーザー作成 (MySQL)**
このアプリケーションではMySQLを使用します。まず、データベースと専用のユーザーを作成します。
MySQLにログインし、以下のコマンドを実行してください。

```sql
-- データベースの作成
CREATE DATABASE simple_rest_api_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- ユーザーの作成と権限付与
CREATE USER 'java_user'@'localhost' IDENTIFIED BY 'password';
GRANT ALL PRIVILEGES ON simple_rest_api_db.* TO 'java_user'@'localhost';
FLUSH PRIVILEGES;
```

### **2.2 エンティティの特定**
```
主要エンティティ: Article（記事）

属性:
- id: 一意識別子
- title: タイトル
- content: 内容
- created_at: 作成日時
- updated_at: 更新日時
```

### **2.3 テーブル設計**
```sql
-- articlesテーブルの設計
CREATE TABLE articles (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    content TEXT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS articles (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    content TEXT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(50) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);
```

### **2.4 制約の設定**
- **主キー**: id（自動採番）
- **NOT NULL制約**: title, content
- **文字数制限**: title（255文字）
- **インデックス**: 必要に応じて作成日時

### **2.4 初期データの準備**
`src/main/resources/data.sql`
```sql
-- 初期データ (実行のたびにクリアして再作成)
DELETE FROM articles;
ALTER TABLE articles AUTO_INCREMENT = 1;
INSERT INTO articles (title, content) VALUES 
('はじめての記事', 'これは最初の記事です。'),
('Spring Boot学習', 'Spring Bootの基本を学んでいます。'),
('生JDBC実装', 'JPAを使わずに生JDBCで実装してみました。');

-- ユーザーデータ (実行のたびにクリアして再作成)
DELETE FROM users;
ALTER TABLE users AUTO_INCREMENT = 1;
-- ユーザー名: user, パスワード: password
-- パスワードはBcryptでハッシュ化された値
INSERT INTO users (username, password, role) VALUES
('user', '$2a$10$g.f9.1w./iFXRk2O2/I.W.idX.a.3jV5K8JALz5aN1zE7qCNpGywS', 'ROLE_USER');
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
- **401 Unauthorized**: 認証エラー
- **404 Not Found**: リソースが見つからない
- **500 Internal Server Error**: サーバーエラー

### **3.4 認証・認可**
- **記事の取得 (GET)**: 認証不要。誰でもアクセス可能。
- **記事の作成・更新・削除 (POST, PUT, DELETE)**: **Basic認証**による認証が必要。

---

## ⚙️ **Phase 4: プロジェクト初期設定 (15分)**

### **4.1 build.gradle設定**
```gradle
dependencies {
    implementation 'org.springframework.boot:spring-boot-starter-web'
    implementation 'org.springframework.boot:spring-boot-starter-jdbc'
    implementation 'org.springframework.boot:spring-boot-starter-security'
    runtimeOnly 'com.mysql:mysql-connector-j:8.0.33'
    developmentOnly 'org.springframework.boot:spring-boot-devtools'
    implementation 'org.springdoc:springdoc-openapi-starter-webmvc-ui:2.2.0'
}
```

### **4.2 application.properties設定**
`src/main/resources/application.properties`
```properties
# MySQL Database settings
spring.datasource.url=jdbc:mysql://localhost:3306/simple_rest_api_db?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=Asia/Tokyo
spring.datasource.username=java_user
spring.datasource.password=password
spring.datasource.driverClassName=com.mysql.cj.jdbc.Driver

# JPA/Hibernate settings (Not used in pure JDBC, but good practice to specify)
spring.jpa.hibernate.ddl-auto=none

# Initialize Schema and Data
# アプリケーション起動時にschema.sqlとdata.sqlを実行する
spring.sql.init.mode=always

# ログ設定
logging.level.org.springframework.jdbc=DEBUG
```

### **4.3 CORS設定**

アプリケーションを異なるドメインのフロントエンドから利用できるようにするため、CORS設定を追加します。

#### **設計のポイント**
- `WebMvcConfigurer` を実装した設定クラスを作成
- `addCorsMappings` メソッドでCORSルールを定義
- 特定のドメインからのリクエストを許可

`src/main/java/com/example/simple_spring_rest_api/config/WebConfig.java`
```java
package com.example.simple_spring_rest_api.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
            .allowedOrigins("http://fanda-dev.com", "https://fanda-dev.com") // デプロイ先のドメイン
            .allowedMethods("GET", "POST", "PUT", "DELETE")
            .allowedHeaders("*")
            .allowCredentials(true);
    }
}
```

### **4.4 セキュリティ設定**

Spring Securityを導入し、APIに認証・認可機能を追加します。

#### **設計のポイント**
- `spring-boot-starter-security` を利用
- `SecurityFilterChain` Beanでセキュリティルールを定義
- エンドポイントごとにアクセス権限を設定
- Basic認証とインメモリユーザーで簡易的な認証を実現

#### **1. 依存関係の追加**
`build.gradle`に以下を追加します。
```gradle
implementation 'org.springframework.boot:spring-boot-starter-security'
```

#### **2. セキュリティ設定クラスの作成**
`src/main/java/com/example/simple_spring_rest_api/config/SecurityConfig.java`
```java
package com.example.simple_spring_rest_api.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(authorize -> authorize
                        // 記事の取得(GET)は誰でも許可
                        .requestMatchers(HttpMethod.GET, "/api/articles/**").permitAll()
                        // 記事の作成・更新・削除は認証が必要
                        .requestMatchers("/api/articles/**").authenticated()
                        // その他のリクエストはすべて許可 (H2コンソールやSwagger UIなど)
                        .anyRequest().permitAll()
                )
                .httpBasic(Customizer.withDefaults()); // Basic認証を有効化
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        // BCryptアルゴリズムを使用してパスワードをハッシュ化
        return new BCryptPasswordEncoder();
    }
}
```
> **解説:** 以前はここに`UserDetailsService`のBeanがありましたが、それを削除しました。`UserDetailsServiceImpl`クラスに`@Service`アノテーションを付けたことで、Springが自動的にそれを`UserDetailsService`の実装として認識し、利用してくれます。これにより、認証の仕組みがメモリ上からデータベースに切り替わりました。

---

## 🏗️ **Phase 5: 実装フェーズ**

### **5.1 ドメインクラス作成 (30分)**

#### **設計のポイント**
- Plain Javaで実装（Lombokなし）
- 全フィールドprivate
- 適切なコンストラクタ
- ゲッター・セッター手動実装
- toString, equals, hashCode実装

`src/main/java/com/example/simple_spring_rest_api/domain/Article.java`
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
    public Article() {} // デフォルトコンストラクタ
    
    // 新規作成時に使用するコンストラクタ
    public Article(String title, String content) {
        this.title = title;
        this.content = content;
    }
    
    // データベースから読み込む際に使用するコンストラクタ
    public Article(Long id, String title, String content, 
                   LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
    
    // ゲッター・セッター、toString, equals, hashCode は省略
}
```

### **5.2 ユーザー管理クラス作成**
記事のCRUD実装と同様に、ユーザー情報を扱うための`Domain`, `Repository`を作成します。さらに、Spring SecurityがDB認証を行うための`Service`も作成します。

#### **1. Userドメインクラス**
`users`テーブルのレコードを表すクラスです。
`src/main/java/com/example/simple_spring_rest_api/domain/User.java`
```java
public class User {
    private Long id;
    private String username;
    private String password;
    private String role;
    // getter, setter...
}
```

#### **2. UserRepository**
ユーザー名(`username`)をキーに、データベースからユーザー情報を取得するリポジトリです。
`src/main/java/com/example/simple_spring_rest_api/repository/UserRepository.java`
```java
@Repository
public class UserRepository {
    private final DataSource dataSource;
    
    public Optional<User> findByUsername(String username) {
        String sql = "SELECT * FROM users WHERE username = ?";
        // ... PreparedStatementを使った実装 ...
    }

    private User mapResultSetToUser(ResultSet rs) throws SQLException {
        // ... ResultSetからUserオブジェクトへの変換処理 ...
    }
}
```

#### **3. UserDetailsServiceImpl**
Spring Securityの`UserDetailsService`インターフェースを実装したクラスです。このクラスが、認証処理の核となります。
`loadUserByUsername`メソッドが、ログイン時に入力されたユーザー名で`UserRepository`を呼び出し、DBから取得したユーザー情報をSpring Securityが扱える`UserDetails`形式に変換して返します。

`src/main/java/com/example/simple_spring_rest_api/service/UserDetailsServiceImpl.java`
```java
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("ユーザーが見つかりません: " + username));

        List<GrantedAuthority> authorities = Collections.singletonList(new SimpleGrantedAuthority(user.getRole()));

        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                authorities
        );
    }
}
```

### **5.3 データアクセス層実装 (ArticleRepository)**

#### **設計のポイント**
- 生JDBC(`DataSource`, `Connection`, `PreparedStatement`, `ResultSet`)を使用
- `try-with-resources`文でリソースを自動的にクローズし、接続リークを防止
- SQLインジェクション対策として`PreparedStatement`のプレースホルダ(`?`)を利用
- `ResultSet`から`Article`オブジェクトへの変換ロジックを`mapResultSetToArticle`メソッドに集約し、コードの重複を削減
- データベース操作でエラーが発生した場合は、`RuntimeException`をスローして上位層に通知

`src/main/java/com/example/simple_spring_rest_api/repository/ArticleRepository.java`
```java
@Repository
public class ArticleRepository {
    private final DataSource dataSource;
    
    // 全記事取得
    public List<Article> findAll() { /* ... 実装 ... */ }
    
    // IDで記事取得
    public Optional<Article> findById(Long id) { /* ... 実装 ... */ }
    
    // 記事の保存（新規作成）
    public Article save(Article article) {
        String sql = "INSERT INTO articles (title, content, created_at, updated_at) VALUES (?, ?, ?, ?)";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            LocalDateTime now = LocalDateTime.now();
            statement.setString(1, article.getTitle());
            statement.setString(2, article.getContent());
            statement.setTimestamp(3, Timestamp.valueOf(now));
            statement.setTimestamp(4, Timestamp.valueOf(now));
            
            statement.executeUpdate();
            
            // 自動生成されたIDを取得してセット
            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    article.setId(generatedKeys.getLong(1));
                    article.setCreatedAt(now);
                    article.setUpdatedAt(now);
                }
            }
            return article;
        } catch (SQLException e) {
            throw new RuntimeException("記事の保存に失敗しました", e);
        }
    }
    
    // 記事の更新
    public Article update(Long id, Article article) {
        String sql = "UPDATE articles SET title = ?, content = ?, updated_at = ? WHERE id = ?";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, article.getTitle());
            statement.setString(2, article.getContent());
            statement.setTimestamp(3, Timestamp.valueOf(LocalDateTime.now()));
            statement.setLong(4, id);
            statement.executeUpdate();
            return findById(id).orElse(null); // 更新後のデータを返す
        } catch (SQLException e) {
            throw new RuntimeException("記事の更新に失敗しました", e);
        }
    }

    // 記事の削除
    public void deleteById(Long id) {
        String sql = "DELETE FROM articles WHERE id = ?";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, id);
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("記事の削除に失敗しました", e);
        }
    }
    
    // ResultSetからArticleへのマッピングを行うヘルパーメソッド
    private Article mapResultSetToArticle(ResultSet resultSet) throws SQLException {
        return new Article(
            resultSet.getLong("id"),
            resultSet.getString("title"),
            resultSet.getString("content"),
            resultSet.getTimestamp("created_at").toLocalDateTime(),
            resultSet.getTimestamp("updated_at").toLocalDateTime()
        );
    }
}
```

### **5.4 ビジネスロジック層実装 (30分)**

#### **設計のポイント**
- `Controller`層と`Repository`層の間に立ち、ビジネスルールを実装
- 入力値の検証（バリデーション）を行い、不正なデータでの処理を防ぐ
- `Repository`を呼び出してデータの永続化を指示
- 更新や削除の前に、対象のデータが存在するかを`findArticleById`で確認し、存在しない場合は例外をスロー（防衛的プログラミング）

`src/main/java/com/example/simple_spring_rest_api/service/ArticleService.java`
```java
@Service
public class ArticleService {
    private final ArticleRepository articleRepository;
    
    public List<Article> findAllArticles() { /* ... 実装 ... */ }
    
    public Article findArticleById(Long id) {
        return articleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("記事が見つかりません。ID: " + id));
    }
    
    public Article createArticle(Article article) {
        validateArticle(article); // 入力値検証
        return articleRepository.save(article);
    }
    
    public Article updateArticle(Long id, Article articleDetails) {
        findArticleById(id); // 存在確認
        validateArticle(articleDetails); // 入力値検証
        return articleRepository.update(id, articleDetails);
    }

    public void deleteArticle(Long id) {
        findArticleById(id); // 存在確認
        articleRepository.deleteById(id);
    }

    // タイトルと内容が空でないことを確認するヘルパーメソッド
    private void validateArticle(Article article) {
        if (article.getTitle() == null || article.getTitle().trim().isEmpty()) {
            throw new IllegalArgumentException("タイトルは必須です");
        }
        if (article.getContent() == null || article.getContent().trim().isEmpty()) {
            throw new IllegalArgumentException("内容は必須です");
        }
    }
}
```

### **5.5 プレゼンテーション層実装 (45分)**

#### **設計のポイント**
- `@RestController`でクラスがRESTfulなエンドポイントであることを示す
- `@RequestMapping`でURLのベースパスを設定
- `@GetMapping`, `@PostMapping`, `@PutMapping`, `@DeleteMapping`で各HTTPメソッドに対応する処理をマッピング
- `@PathVariable`でURLパスからIDを取得、`@RequestBody`でリクエストボディのJSONを`Article`オブジェクトに変換
- `Service`層からスローされた例外を`try-catch`で捕捉し、`ResponseEntity`を使って適切なHTTPステータスコード（200, 201, 204, 400, 404など）とレスポンスボディを返す

`src/main/java/com/example/simple_spring_rest_api/controller/ArticleController.java`
```java
@RestController
@RequestMapping("/api/articles")
public class ArticleController {
    private final ArticleService articleService;
    
    // GET /api/articles
    @GetMapping
    public ResponseEntity<List<Article>> getAllArticles() { /* ... 実装 ... */ }
    
    // GET /api/articles/{id}
    @GetMapping("/{id}")
    public ResponseEntity<Article> getArticleById(@PathVariable Long id) {
        try {
            Article article = articleService.findArticleById(id);
            return ResponseEntity.ok(article);
        } catch (RuntimeException e) {
            // Serviceでスローされた例外をキャッチし、404 Not Foundを返す
            return ResponseEntity.notFound().build();
        }
    }
    
    // POST /api/articles
    @PostMapping
    public ResponseEntity<Article> createArticle(@RequestBody Article article) {
        try {
            Article createdArticle = articleService.createArticle(article);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdArticle);
        } catch (IllegalArgumentException e) {
            // Serviceの入力値検証でスローされた例外をキャッチし、400 Bad Requestを返す
            return ResponseEntity.badRequest().build();
        }
    }
    
    // PUT /api/articles/{id}
    @PutMapping("/{id}")
    public ResponseEntity<Article> updateArticle(@PathVariable Long id, @RequestBody Article articleDetails) {
        try {
            Article updatedArticle = articleService.updateArticle(id, articleDetails);
            return ResponseEntity.ok(updatedArticle);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // DELETE /api/articles/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteArticle(@PathVariable Long id) {
        try {
            articleService.deleteArticle(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
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

# 記事作成 (認証あり)
# -u user:password でBasic認証の認証情報を付与
curl -X POST http://localhost:8080/api/articles \
  -u user:password \
  -H "Content-Type: application/json" \
  -d '{"title":"テスト記事","content":"テスト内容"}'

# 記事作成 (認証なし、エラーになることを確認)
curl -X POST http://localhost:8080/api/articles \
  -H "Content-Type: application/json" \
  -d '{"title":"テスト記事","content":"テスト内容"}' -i
```

### **6.2 Swagger UI確認**
```
http://localhost:8080/swagger-ui/index.html
```

### **6.3 MySQLでのデータ確認**
ターミナルからMySQLにログインし、データが正しく保存されているか確認します。
```bash
mysql -u java_user -p simple_rest_api_db

-- ログイン後
SELECT * FROM articles;
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