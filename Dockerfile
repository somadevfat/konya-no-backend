# --- ステージ1: ビルド環境 ---
# Java 17 の開発キット(JDK)が入った公式イメージを「builder」として使う
FROM eclipse-temurin:17-jdk-jammy as builder

# コンテナ内の作業ディレクトリを設定
WORKDIR /app

# プロジェクトのファイルをすべてコンテナにコピー
COPY . .

# Gradleを使ってアプリケーションをビルドする
# --no-daemonはCI/CD環境で推奨されるオプション
RUN ./gradlew build -x test --no-daemon


# --- ステージ2: 実行環境 ---
# Java 17 の実行環境(JRE)が入った、より小さいイメージを最終的な箱として使う
FROM eclipse-temurin:17-jre-jammy

# コンテナ内の作業ディレクトリを設定
WORKDIR /app

# ステージ1(builder)でビルドしたJARファイルだけをコピーしてくる
# これにより、最終的な箱にソースコードやビルドツールが含まれなくなり、サイズが小さくなる
COPY --from=builder /app/build/libs/*.jar app.jar

# コンテナが起動したときに実行するコマンドを指定
ENTRYPOINT ["java", "-jar", "app.jar"] 