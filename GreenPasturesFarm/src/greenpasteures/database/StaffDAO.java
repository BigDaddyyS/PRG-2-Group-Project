package greenpasteures.database;

import greenpasteures.models.Staff;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

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

    // registers a new staff member - role is always STAFF by default
    public boolean registerStaff(String firstName, String lastName,
                                  String phoneNumber, String username, String password) {
        // check if username already exists first
        if (usernameExists(username)) {
            return false;
        }

        String sql = "INSERT INTO staff (firstName, lastName, role, phoneNumber, userName, password) " +
                     "VALUES (?, ?, 'STAFF', ?, ?, ?)";
        try {
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, firstName);
            stmt.setString(2, lastName);
            stmt.setString(3, phoneNumber);
            stmt.setString(4, username);
            stmt.setString(5, password);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Register staff error: " + e.getMessage());
            return false;
        }
    }

    // checks if a username already exists in the database
    public boolean usernameExists(String username) {
        String sql = "SELECT COUNT(*) FROM staff WHERE userName = ?";
        try {
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            System.out.println("Username check error: " + e.getMessage());
        }
        return false;
    }

    // returns all staff members - used in the admin staff panel
    public List<Object[]> getAllStaff() {
        List<Object[]> rows = new ArrayList<>();
        String sql = "SELECT staffId, firstName, lastName, role, phoneNumber, userName FROM staff";
        try {
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Object[] row = new Object[6];
                row[0] = rs.getInt("staffId");
                row[1] = rs.getString("firstName");
                row[2] = rs.getString("lastName");
                row[3] = rs.getString("role");
                row[4] = rs.getString("phoneNumber");
                row[5] = rs.getString("userName");
                rows.add(row);
            }
        } catch (SQLException e) {
            System.out.println("Get all staff error: " + e.getMessage());
        }
        return rows;
    }

    // updates a staff member's role - admin only
    public boolean updateRole(int staffId, String newRole) {
        String sql = "UPDATE staff SET role = ? WHERE staffId = ?";
        try {
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, newRole);
            stmt.setInt(2, staffId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Update role error: " + e.getMessage());
            return false;
        }
    }

    // deletes a staff member - admin only
    public boolean deleteStaff(int staffId) {
        String sql = "DELETE FROM staff WHERE staffId = ?";
        try {
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, staffId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Delete staff error: " + e.getMessage());
            return false;
        }
    }
}