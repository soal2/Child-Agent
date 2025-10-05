package com.example.child_agent_back.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
public class CorsConfig {

    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        
        // 允许所有源
        config.addAllowedOrigin("*");
        // 允许所有方法
        config.addAllowedMethod("*");
        // 允许所有头
        config.addAllowedHeader("*");
        // 允许凭证
        config.setAllowCredentials(true);
        // 预检请求的有效期
        config.setMaxAge(3600L);
        
        // 对所有API路径应用CORS配置
        source.registerCorsConfiguration("/api/**", config);
        
        return new CorsFilter(source);
    }
}