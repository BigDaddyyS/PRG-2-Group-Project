package greenpasteures.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class HealthDAO {

    private Connection conn;

    // constructor - gets the database connection
    public HealthDAO() {
        this.conn = DatabaseManager.getInstance().getConnection();
    }

    // returns all health records joined with staff name
    public List<Object[]> getAllRecords() {
        List<Object[]> rows = new ArrayList<>();
        String sql = "SELECT h.recordId, h.tagNumber, h.diagnosis, h.treatment, " +
                     "h.medicineUsed, CONCAT(s.firstName,' ',s.lastName) AS attendedBy, h.treatmentDate " +
                     "FROM health_records h " +
                     "JOIN staff s ON h.attendedBy = s.staffId " +
                     "ORDER BY h.treatmentDate DESC";

        try {
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Object[] row = new Object[7];
                row[0] = rs.getInt("recordId");
                row[1] = rs.getString("tagNumber");
                row[2] = rs.getString("diagnosis");
                row[3] = rs.getString("treatment");
                row[4] = rs.getString("medicineUsed");
                row[5] = rs.getString("attendedBy");
                row[6] = rs.getString("treatmentDate");
                rows.add(row);
            }
        } catch (SQLException e) {
            System.out.println("Error loading health records: " + e.getMessage());
        }

        return rows;
    }

    // inserts a new health record
    public boolean addRecord(String tagNumber, String treatmentDate, String diagnosis,
                             String treatment, String medicineUsed, int attendedBy) {
        String sql = "INSERT INTO health_records (tagNumber, treatmentDate, diagnosis, treatment, medicineUsed, attendedBy) " +
                     "VALUES (?, ?, ?, ?, ?, ?)";
        try {
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, tagNumber);
            stmt.setString(2, treatmentDate);
            stmt.setString(3, diagnosis);
            stmt.setString(4, treatment);
            stmt.setString(5, medicineUsed);
            stmt.setInt(6, attendedBy);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Add health record failed: " + e.getMessage());
            return false;
        }
    }

    // returns the most recent health records for the dashboard
    public List<Object[]> getRecentRecords(int limit) {
        List<Object[]> rows = new ArrayList<>();
        String sql = "SELECT h.tagNumber, a.animalType, h.treatment, " +
                     "CONCAT(s.firstName,' ',s.lastName) AS attendedBy, h.treatmentDate " +
                     "FROM health_records h " +
                     "JOIN staff s ON h.attendedBy = s.staffId " +
                     "JOIN animals a ON h.tagNumber = a.tagNumber " +
                     "ORDER BY h.treatmentDate DESC LIMIT ?";

        try {
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, limit);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Object[] row = new Object[5];
                row[0] = rs.getString("tagNumber");
                row[1] = rs.getString("animalType");
                row[2] = rs.getString("treatment");
                row[3] = rs.getString("attendedBy");
                row[4] = rs.getString("treatmentDate");
                rows.add(row);
            }
        } catch (SQLException e) {
            System.out.println("Error loading recent records: " + e.getMessage());
        }

        return rows;
    }
}