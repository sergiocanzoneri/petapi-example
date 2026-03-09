package com.sergiocanzoneri.petapi.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.sergiocanzoneri.petapi.util.DatabaseType;

// Configuration class for database-related settings loaded from application.yaml.
@Configuration
public class DatabaseConfig {

    /**
     * Exposes the active database type for use in components that need to
     * branch on the current backend (e.g. enabling JPA only for relational DBs).
     */
    @Bean
    public DatabaseType activeDatabaseType(DatabaseProperties databaseProperties) {
        return databaseProperties.type();
    }
}
