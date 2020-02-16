package com.todolist.config;

import org.flywaydb.core.Flyway;
import org.h2.jdbcx.JdbcDataSource;

import javax.sql.DataSource;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import static java.util.Objects.requireNonNull;

/**
 * @author Tymur Berezhnoi
 */
public final class DataSourceManager {

    private static final DataSource DATA_SOURCE;

    static {
        try {
            FlywayManager.migrate();

            Properties appProperties = applicationProperties();
            JdbcDataSource h2DataSource = new JdbcDataSource();
            h2DataSource.setURL(appProperties.getProperty("database.url"));
            h2DataSource.setUser(appProperties.getProperty("database.user"));
            h2DataSource.setPassword(appProperties.getProperty("database.password"));

            DATA_SOURCE = h2DataSource;
        } catch(IOException e) {
            throw new RuntimeException("Can't reach local database, because: " + e.getMessage());
        }
    }

    /**
     * Prevent instantiation.
     *
     * @throws UnsupportedOperationException if create new instance.
     */
    private DataSourceManager() {
        throw new UnsupportedOperationException("Do not instantiate the util class.");
    }

    public static DataSource getDataSource() {
        return DATA_SOURCE;
    }

    /**
     * Flyway migration stuff.
     */
    private static final class FlywayManager {

        /**
         * Prevent instantiation.
         *
         * @throws UnsupportedOperationException if create new instance.
         */
        private FlywayManager() {
            throw new UnsupportedOperationException("Do not instantiate the util class.");
        }

        /**
         * Execute migration.
         */
        private static void migrate() throws IOException {
            Properties appProperties = applicationProperties();

            Flyway flyway = Flyway.configure().dataSource(
                        appProperties.getProperty("database.url"),
                        appProperties.getProperty("database.user"),
                        appProperties.getProperty("database.password"))
                        .table(appProperties.getProperty("flyway.schemaName"))
                        .locations(appProperties.getProperty("flyway.location"))
                    .load();

            flyway.migrate();
        }
    }

    private static Properties applicationProperties() throws IOException {
        Properties appProperties = new Properties();
        InputStream resourceAsStream = DataSourceManager.class.getClassLoader().getResourceAsStream("application.properties");

        appProperties.load(requireNonNull(resourceAsStream, "File application.properties not found!"));
        return appProperties;
    }
}
