package utilities;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** PostgreSQL bağlantısı ve parametreli sorgu yardımcıları (salt okunur kullanıcı). */
public final class DBUtils {

    private static Connection connection;

    private DBUtils() {
    }

    public static Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                String url = "jdbc:postgresql://" + ConfigReader.get("db.host") + ":" + ConfigReader.get("db.port")
                        + "/" + ConfigReader.get("db.name") + "?currentSchema=" + ConfigReader.get("db.schema")
                        + "&connectTimeout=20&ApplicationName=snackattack-qa";
                connection = DriverManager.getConnection(url, ConfigReader.get("db.user"), ConfigReader.get("db.password"));
                connection.setReadOnly(true);
            }
            return connection;
        } catch (SQLException e) {
            throw new IllegalStateException("Veritabanına bağlanılamadı: " + e.getMessage(), e);
        }
    }

    public static List<Map<String, Object>> query(String sql, Object... params) {
        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            for (int i = 0; i < params.length; i++) {
                ps.setObject(i + 1, params[i]);
            }
            try (ResultSet rs = ps.executeQuery()) {
                ResultSetMetaData md = rs.getMetaData();
                List<Map<String, Object>> rows = new ArrayList<>();
                while (rs.next()) {
                    Map<String, Object> row = new LinkedHashMap<>();
                    for (int c = 1; c <= md.getColumnCount(); c++) {
                        row.put(md.getColumnLabel(c), rs.getObject(c));
                    }
                    rows.add(row);
                }
                return rows;
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Sorgu hatası: " + sql + " -> " + e.getMessage(), e);
        }
    }

    public static Map<String, Object> queryOne(String sql, Object... params) {
        List<Map<String, Object>> rows = query(sql, params);
        return rows.isEmpty() ? null : rows.get(0);
    }

    public static long count(String sql, Object... params) {
        Map<String, Object> row = queryOne(sql, params);
        return row == null ? 0 : ((Number) row.values().iterator().next()).longValue();
    }

    public static void close() {
        try {
            if (connection != null) {
                connection.close();
            }
        } catch (SQLException ignored) {
            // bağlantı zaten kapalı
        } finally {
            connection = null;
        }
    }
}
