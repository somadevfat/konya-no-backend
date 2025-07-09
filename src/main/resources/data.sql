-- articlesテーブルのデータを初期化
DELETE FROM articles;

ALTER TABLE articles AUTO_INCREMENT = 1;

INSERT INTO
    articles (title, content)
VALUES ('はじめての記事', 'これは最初の記事です。'),
    (
        'Spring Boot学習',
        'Spring Bootの基本を学んでいます。'
    ),
    (
        '生JDBC実装',
        'JPAを使わずに生JDBCで実装してみました。'
    );

-- usersテーブルのデータを初期化
DELETE FROM users;

ALTER TABLE users AUTO_INCREMENT = 1;
-- パスワードは 'password' をBcryptでハッシュ化したもの
INSERT INTO
    users (username, password, role)
VALUES (
        'user',
        '$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HZWzG3YB1tlRy.fqvM/BG',
        'ROLE_USER'
    );