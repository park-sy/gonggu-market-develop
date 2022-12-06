package com.example.demo.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry){
        registry.addMapping("/**")
                /*
                .allowedOrigins("http://localhost:8080", "gonggu-alb-test-333249785.ap-northeast-2.elb.amazonaws.com",
                        "http://localhost:5173",
                        "https://localhost:5173",
                        "https://127.0.0.1:5173"
                )*/
                .allowedOrigins("*")
                .allowedMethods("GET", "POST", "PUT", "DELETE")
        ;

    }

}