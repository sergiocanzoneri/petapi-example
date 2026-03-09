package com.sergiocanzoneri.petapi.util;

import java.util.Arrays;

import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

// Condition to check whether the database type is relational (currently PostgreSQL and H2 are supported).
public class RelationalDatabaseCondition implements Condition {

    private static final String DB_TYPE_PROPERTY = "app.database.type";

    @Override
    public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
        String raw = context.getEnvironment().getProperty(DB_TYPE_PROPERTY, "postgresql");
        DatabaseType type;
        try {
            type = DatabaseType.valueOf(raw.toUpperCase());
        } catch (IllegalArgumentException ex) {
            throw new IllegalStateException(
                    "Invalid value '%s' for property '%s'. Expected one of: %s"
                            .formatted(raw, DB_TYPE_PROPERTY,
                                    Arrays.toString(DatabaseType.values())),
                    ex);
        }
        return type == DatabaseType.POSTGRESQL
            || type == DatabaseType.H2;
    }
}