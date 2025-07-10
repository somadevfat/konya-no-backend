# Simple Spring REST API

> **⚠️ 学習中のプロジェクトです**  
> このプロジェクトは、Spring Boot・REST API・セキュリティの学習を目的として作成しています。  
> 現在も継続的に機能追加・改善を行っています。

## 📋 プロジェクト概要

記事管理システムのREST APIです。Spring Bootを使用して、基本的なCRUD操作とユーザー認証機能を実装しています。

### 🎯 学習目標
- Spring Bootの基本的な使い方
- REST APIの設計・実装
- Spring Securityによる認証・認可
- 生JDBCを使用したデータベース操作
- API仕様書の自動生成（Swagger UI）

## 🛠️ 技術スタック

- **Java 17**
- **Spring Boot 3.5.3**
- **Spring Security** - 認証・認可
- **Spring JDBC** - データベース操作（生JDBC）
- **MySQL** - データベース
- **Swagger UI** - API仕様書
- **BCrypt** - パスワードハッシュ化
- **Gradle** - ビルドツール

## 🚀 機能

### 記事管理API
- ✅ 記事一覧取得（認証不要）
- ✅ 記事詳細取得（認証不要）
- ✅ 記事作成（認証必要）
- ✅ 記事更新（認証必要）
- ✅ 記事削除（認証必要）

### セキュリティ機能
- ✅ Basic認証
- ✅ BCryptによるパスワードハッシュ化
- ✅ データベースベースのユーザー管理
- ✅ CORS設定

## 📊 API仕様

### エンドポイント一覧

| メソッド | エンドポイント | 説明 | 認証 |
|---------|---------------|------|------|
| GET | `/api/articles` | 記事一覧取得 | 不要 |
| GET | `/api/articles/{id}` | 記事詳細取得 | 不要 |
| POST | `/api/articles` | 記事作成 | 必要 |
| PUT | `/api/articles/{id}` | 記事更新 | 必要 |
| DELETE | `/api/articles/{id}` | 記事削除 | 必要 |

### データ形式

```json
{
  "id": 1,
  "title": "記事タイトル",
  "content": "記事の内容",
  "createdAt": "2025-01-01T00:00:00",
  "updatedAt": "2025-01-01T00:00:00"
}
```

## 🏃‍♂️ 実行方法

### 1. 前提条件
- Java 17以上
- MySQL 8.0以上

### 2. データベースセットアップ
```sql
CREATE DATABASE simple_spring_rest_api;
```

### 3. アプリケーション設定
`src/main/resources/application.properties` でデータベース接続情報を設定：

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/simple_spring_rest_api
spring.datasource.username=your_username
spring.datasource.password=your_password
```

### 4. アプリケーション起動
```bash
./gradlew bootRun
```

アプリケーションは `http://localhost:8080` で起動します。

## 🧪 API テスト方法

### Swagger UIを使用（推奨）

1. アプリケーション起動後、ブラウザで以下にアクセス：
   ```
   http://localhost:8080/swagger-ui.html
   ```

2. **認証が必要なエンドポイントのテスト**：
   - 「Authorize」ボタンをクリック
   - ユーザー名: `admin`、パスワード: `password` を入力
   - 「Authorize」で認証完了

3. **各エンドポイントのテスト**：
   - 試したいエンドポイントをクリック
   - 「Try it out」ボタンをクリック
   - 必要な場合はリクエストボディを入力
   - 「Execute」でリクエスト実行

### cURLを使用

```bash
# 記事一覧取得（認証不要）
curl -X GET http://localhost:8080/api/articles

# 記事作成（認証必要）
curl -X POST http://localhost:8080/api/articles \
  -u admin:password \
  -H "Content-Type: application/json" \
  -d '{"title":"テスト記事","content":"これはテスト記事です"}'

# 記事詳細取得（認証不要）
curl -X GET http://localhost:8080/api/articles/1
```

## 🗂️ プロジェクト構成

```
src/main/java/com/example/simple_spring_rest_api/
├── controller/          # REST APIエンドポイント
├── service/            # ビジネスロジック
├── repository/         # データアクセス層
├── domain/            # エンティティクラス
├── config/            # 設定クラス
└── SimpleSpringRestApiApplication.java
```

## 📚 学習のポイント

### 実装できたこと
- ✅ 3層アーキテクチャ（Controller/Service/Repository）の理解と実装
- ✅ Spring Securityの基本的な設定
- ✅ 生JDBCを使用したデータベース操作
- ✅ RESTful APIの設計原則に従った実装
- ✅ Swagger UIによるAPI仕様書の自動生成

### 今後の学習予定
- 🔄 JPA/Hibernateの導入
- 🔄 JWT認証の実装
- 🔄 単体テスト・統合テストの追加
- 🔄 Docker化
- 🔄 CI/CD パイプラインの構築

## 🔗 参考資料

- [Spring Boot公式ドキュメント](https://spring.io/projects/spring-boot)
- [Spring Security公式ドキュメント](https://spring.io/projects/spring-security)
- [Swagger UI](https://swagger.io/tools/swagger-ui/)

---

**作成日**: 2025年7月10日  
**最終更新**: 2025年7月10日
