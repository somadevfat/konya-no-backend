# konya-no-backend

## プロジェクト概要
簡易的な記事管理 REST API です。Spring Boot 3 と MySQL を用いて、記事 (Article) エンティティの CRUD 操作が出来ます。社内テック面接やポートフォリオとして「最低限動くバックエンド」を示すことを目的に、理解しやすい構成にしています。

## 目標レベル
Java Silver 程度の知識で読み解けるコードとドキュメントを意識しています。難解な設計は避け、標準的・王道な Spring の使い方に寄せています。

---

## 主な技術スタック
| 分類 | 使用ライブラリ / ツール | 補足 |
|------|------------------------|------|
| 言語 | Java 17 | LTS バージョン |
| フレームワーク | Spring Boot 3.5 | 軽量な設定で即稼働 |
| DB アクセス | Spring Data JPA | ORM ＆ Repository パターン |
| データベース | MySQL 8.0 | Docker 例を後述 |
| ビルド | Gradle (Wrapper 同梱) | `./gradlew` 一発実行 |
| その他 | Lombok / springdoc-openapi | ボイラープレート削減 / API ドキュメント生成 |

---

## アーキテクチャ図 (超シンプル)
```text
Controller  →  Service  →  Repository  →  MySQL
        (HTTP)          (Domain)      (Spring Data JPA)
```
- **Controller**: ルーティングとリクエスト/レスポンス変換のみ
- **Service**: ビジネスロジック (今回は薄め。拡張用のレイヤー)
- **Repository**: DB とのやり取りを Spring Data JPA に委譲
- **Domain**: JPA エンティティ (Article)

---

## API 一覧
| メソッド | パス | 説明 |
|----------|------|------|
| GET | `/api/articles` | 記事をすべて取得 |
| POST | `/api/articles` | 新規記事を作成 |
| PUT | `/api/articles/{id}` | 既存記事を更新 |
| DELETE | `/api/articles/{id}` | 記事を削除 |

Swagger UI で動作確認できます:  
`http://localhost:8080/swagger-ui/index.html`

---

## セットアップ手順
1. **前提ソフト**  
   - Java 17+  
   - MySQL (ローカル or Docker)

2. **データベースを用意** (Docker 例)  
   ```bash
   docker run -d --name mysql8 \
     -e MYSQL_ROOT_PASSWORD=root \
     -e MYSQL_DATABASE=sample \
     -p 3306:3306 mysql:8
   ```

3. **環境変数を設定**  
   ```bash
   export DB_URL=jdbc:mysql://localhost:3306/sample
   export DB_USER=root
   export DB_PASSWORD=root
   ```

4. **アプリを起動**  
   ```bash
   ./gradlew bootRun
   ```
   `GET /api/articles` が 200 OK になれば成功です。

### ビルド & テスト
```bash
./gradlew clean build      # JAR 作成
./gradlew test             # JUnit テスト (Spring Context 起動確認)
```
生成物: `build/libs/simple-spring-rest-api-0.0.1-SNAPSHOT.jar`

---

## よくある質問 (FAQ)
<details>
<summary>API ドキュメントはどう生成していますか？</summary>
実行時に `springdoc-openapi-starter-webmvc-ui` が OpenAPI 仕様を自動生成します。追加設定は不要です。
</details>

<details>
<summary>Repository に複雑なクエリが無い理由は？</summary>
初学者が理解しやすいように CRUD のみ実装しています。高度な検索が必要になったら `@Query` や `Querydsl` を導入する予定です。
</details>

---

## 今後のロードマップ
- 認証・認可 (Spring Security + JWT)
- リクエストバリデーション (`@Valid`)
- CI/CD (GitHub Actions) と Docker Compose での環境構築
- Integration Test 充実 (Testcontainers)

---

## ライセンス
MIT
