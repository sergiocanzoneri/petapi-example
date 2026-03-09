package com.sergiocanzoneri.petapi.config;

import java.util.Objects;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import com.sergiocanzoneri.petapi.util.DatabaseType;

/**
 * Type-safe binding for app.database.* from application.yaml.
 * Values are profile-specific (currently: postgresql, h2, mongodb).
 */
@ConfigurationProperties(prefix = "app.database")
@Validated
public record DatabaseProperties(
        DatabaseType type,
        JpaSettings jpa
) {

    public DatabaseProperties {
        if (Objects.isNull(type)) {
            // Default to PostgreSQL if no type is specified
            type = DatabaseType.POSTGRESQL;
        }
        if (Objects.isNull(jpa)) {
            // Default to true for showSql and "update" for ddlAuto
            jpa = new JpaSettings(true, "update");
        }
    }

    // Nested JPA settings under app.database.jpa.
    public record JpaSettings(
            boolean showSql,
            String ddlAuto
    ) {

        public JpaSettings {
            // Default to "update" if no ddlAuto is specified
            if (Objects.isNull(ddlAuto) || ddlAuto.isBlank()) {
                ddlAuto = "update";
            }
        }
    }
}
