package MainApp;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

public class DBconnection {
    public static Connection connect() {
        Connection con = null;
        try {
            Class.forName("org.sqlite.JDBC");
            con = DriverManager.getConnection("jdbc:sqlite:WeatherDB.db");
            System.out.println("Connected..");

            // Create table if not exists
            createTable(con);

        } catch (ClassNotFoundException | SQLException e) {
            System.out.println(e + "");
        }
        return con;
    }

    private static void createTable(Connection con) {
        try {
            Statement stmt = con.createStatement();
            String createTableSQL = "CREATE TABLE IF NOT EXISTS WeatherData (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "city TEXT NOT NULL," +
                    "temperature REAL NOT NULL," +
                    "humidity INTEGER NOT NULL," +
                    "wind_speed REAL NOT NULL);";

            stmt.execute(createTableSQL);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void insertData(Connection con, String city, double temperature, int humidity, double windSpeed) {
        try {
            String insertDataSQL = "INSERT INTO WeatherData (city, temperature, humidity, wind_speed) VALUES (?, ?, ?, ?)";
            try (PreparedStatement pstmt = con.prepareStatement(insertDataSQL)) {
                pstmt.setString(1, city);
                pstmt.setDouble(2, temperature);
                pstmt.setInt(3, humidity);
                pstmt.setDouble(4, windSpeed);

                pstmt.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
