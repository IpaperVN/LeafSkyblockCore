package me.ipapervn.leafskyblockcore.database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import me.ipapervn.leafskyblockcore.LeafSkyblockCore;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Central database manager using HikariCP connection pool.
 * All features should use this class to interact with SQLite database.
 */
public class DatabaseManager {

    private final LeafSkyblockCore plugin;
    private HikariDataSource dataSource;

    public DatabaseManager(@NotNull LeafSkyblockCore plugin) {
        this.plugin = plugin;
        initialize();
    }

    private void initialize() {
        if (!createDataFolder()) {
            return;
        }

        File databaseFile = new File(plugin.getDataFolder(), "data.db");
        
        HikariConfig config = createHikariConfig(databaseFile);
        dataSource = new HikariDataSource(config);
        
        plugin.getComponentLogger().info("Database initialized successfully");
    }

    private boolean createDataFolder() {
        File dataFolder = plugin.getDataFolder();
        if (!dataFolder.exists() && !dataFolder.mkdirs()) {
            plugin.getComponentLogger().error("Failed to create plugin data folder");
            return false;
        }
        return true;
    }

    private HikariConfig createHikariConfig(File databaseFile) {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl("jdbc:sqlite:" + databaseFile.getAbsolutePath());
        config.setDriverClassName("org.sqlite.JDBC");
        config.setMaximumPoolSize(10);
        config.setMinimumIdle(2);
        config.setMaxLifetime(1800000);
        config.setConnectionTimeout(30000);
        config.setPoolName("LeafSkyblockCore-Pool");
        
        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", "250");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
        
        return config;
    }

    /**
     * Get connection from pool.
     * MUST use try-with-resources to auto-close connection.
     *
     * @return Connection from pool
     * @throws SQLException if connection fails
     */
    @NotNull
    public Connection getConnection() throws SQLException {
        if (dataSource == null || dataSource.isClosed()) {
            throw new SQLException("Database connection pool is closed");
        }
        return dataSource.getConnection();
    }

    /**
     * Execute SQL statement (CREATE TABLE, etc).
     * WARNING: Only use with trusted/hardcoded SQL, not user input.
     *
     * @param sql SQL statement
     */
    @SuppressWarnings("SqlSourceToSinkFlow")
    public void execute(@NotNull String sql) {
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            plugin.getComponentLogger().error("Failed to execute SQL", e);
        }
    }

    /**
     * Create table if not exists.
     *
     * @param tableName Table name
     * @param schema Table schema (columns definition)
     */
    public void createTable(@NotNull String tableName, @NotNull String schema) {
        String sql = "CREATE TABLE IF NOT EXISTS " + tableName + " (" + schema + ")";
        execute(sql);
        plugin.getComponentLogger().info("Table '{}' initialized", tableName);
    }

    /**
     * Close database connection pool.
     */
    public void close() {
        if (dataSource != null && !dataSource.isClosed()) {
            dataSource.close();
            plugin.getComponentLogger().info("Database connection pool closed");
        }
    }

    /**
     * Check if database is connected.
     *
     * @return true if connected
     */
    @SuppressWarnings("unused")
    public boolean isConnected() {
        return dataSource != null && !dataSource.isClosed();
    }
}
