package com.sergiocanzoneri.petapi.util;

// Supported database backends for the application.
public enum DatabaseType {

    // NOTE: Currently only PostgreSQL and H2 are supported, but MongoDB is present for future use.
    POSTGRESQL,
    H2,
    MONGODB
}

