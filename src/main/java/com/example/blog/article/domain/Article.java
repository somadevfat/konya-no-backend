package com.example.blog.article.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

//@Entityでテーブル作成
@Entity
//ゲッターセッター自動生成
@Data
public class Article {
	//主キー
    @Id
    //主キーがGenerationType.IDENTITYで順番に自動割り振り
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String content;
}
