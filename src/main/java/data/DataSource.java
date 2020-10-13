package data;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;

public class DataSource {

  private static HikariConfig config = new HikariConfig();
  private static HikariDataSource ds;

  static {
    String dbUrlLocal = "jdbc:mysql://localhost:3306/bsds_skiers";
    Map<String, String> env = System.getenv();
    String userName = env.get("DB_USER");
    String password = env.get("DB_PASS");
    int poolSizeLocal = 25;

    config.setJdbcUrl(dbUrlLocal);
    config.setDriverClassName("com.mysql.cj.jdbc.Driver");
    config.setUsername(userName);
    config.setPassword(password);
    config.addDataSourceProperty("cachePrepStmts", "true");
    config.addDataSourceProperty("prepStmtCacheSize", "250");
    config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
    config.addDataSourceProperty("serverTimezone", "UTC");
    config.setMaximumPoolSize(poolSizeLocal);
    ds = new HikariDataSource(config);
  }

  private DataSource() {
  }

  public static Connection getConnection() throws SQLException {
    return ds.getConnection();
  }
}
