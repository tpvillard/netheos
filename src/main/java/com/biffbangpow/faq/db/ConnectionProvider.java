package com.biffbangpow.faq.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.function.Supplier;


public class ConnectionProvider implements Supplier<Connection> {

    private final String url;

    ConnectionProvider(String url) {
        this.url = url;
    }

    @Override
    public Connection get() {

        try {
            return DriverManager.getConnection(url);
        } catch (SQLException e) {

            // Tired of checked exceptions? Wrap it!
            throw new RuntimeException(e.getMessage(),e.getCause());
        }
    }
}
