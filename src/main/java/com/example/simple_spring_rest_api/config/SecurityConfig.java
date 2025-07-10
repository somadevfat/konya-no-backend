package com.example.simple_spring_rest_api.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
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