package com.app.authservice.config;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DotenvConfig {

    @Bean
    public Dotenv dotenv() {
        Dotenv dotenv = Dotenv.configure()
                .directory("/Users/uday/Desktop/Projects/AuthService/src")
                .load();

        System.setProperty("SPRING_APPLICATION_NAME", dotenv.get("SPRING_APPLICATION_NAME"));
        System.setProperty("SPRING_DEVTOOLS_RESTART_ENABLED", dotenv.get("SPRING_DEVTOOLS_RESTART_ENABLED"));
        System.setProperty("SPRING_DEVTOOLS_RESTART_POLL_INTERVAL", dotenv.get("SPRING_DEVTOOLS_RESTART_POLL_INTERVAL"));
        System.setProperty("SPRING_DEVTOOLS_RESTART_QUIET_PERIOD", dotenv.get("SPRING_DEVTOOLS_RESTART_QUIET_PERIOD"));
        System.setProperty("SPRING_DATASOURCE_URL", dotenv.get("SPRING_DATASOURCE_URL"));
        System.setProperty("SPRING_DATASOURCE_USERNAME", dotenv.get("SPRING_DATASOURCE_USERNAME"));
        System.setProperty("SPRING_DATASOURCE_PASSWORD", dotenv.get("SPRING_DATASOURCE_PASSWORD"));
        System.setProperty("SPRING_DATASOURCE_DRIVER_CLASS_NAME", dotenv.get("SPRING_DATASOURCE_DRIVER_CLASS_NAME"));
        System.setProperty("SPRING_JPA_PROPERTIES_HIBERNATE_DIALECT", dotenv.get("SPRING_JPA_PROPERTIES_HIBERNATE_DIALECT"));
        System.setProperty("SPRING_JPA_SHOW_SQL", dotenv.get("SPRING_JPA_SHOW_SQL"));
        System.setProperty("SPRING_JPA_HIBERNATE_DDL_AUTO", dotenv.get("SPRING_JPA_HIBERNATE_DDL_AUTO"));
        System.setProperty("SERVER_SERVLET_SESSION_COOKIE_SAME_SITE", dotenv.get("SERVER_SERVLET_SESSION_COOKIE_SAME_SITE"));
        System.setProperty("JWT_SECRET", dotenv.get("JWT_SECRET"));
        System.setProperty("JWT_EXPIRATION", dotenv.get("JWT_EXPIRATION"));
        System.setProperty("JWT_REFRESH_EXPIRATION", dotenv.get("JWT_REFRESH_EXPIRATION"));

        return dotenv;
    }
}