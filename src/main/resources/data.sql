-- 初期データの投入
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