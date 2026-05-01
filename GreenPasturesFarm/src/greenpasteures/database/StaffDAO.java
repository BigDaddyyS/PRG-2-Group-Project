package greenpasteures.database;

import greenpasteures.models.Staff;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class StaffDAO {

    private Connection conn;

    // constructor - gets the database connection
    public StaffDAO() {
        this.conn = DatabaseManager.getInstance().getConnection();
    }

    // checks username and password and returns a Staff object if correct
    public Staff authenticate(String username, String password) {
        String sql = "SELECT * FROM staff WHERE userName = ? AND password = ?";
        try {
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, username);
            stmt.setString(2, password);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return new Staff(
                    rs.getInt("staffId"),
                    rs.getString("firstName"),
                    rs.getString("lastName"),
                    rs.getString("role"),
                    rs.getString("phoneNumber"),
                    rs.getString("userName")
                );
            }
        } catch (SQLException e) {
            System.out.println("Authentication error: " + e.getMessage());
        }
        return null; // login failed
    }
}