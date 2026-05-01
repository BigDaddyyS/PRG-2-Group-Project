package greenpasteures.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class AnimalDAO {

    private Connection conn;

    // constructor - gets the database connection
    public AnimalDAO() {
        this.conn = DatabaseManager.getInstance().getConnection();
    }

    // returns all animals as a list of rows for the JTable
    public List<Object[]> getAllAnimals() {
        List<Object[]> rows = new ArrayList<>();
        String sql = "SELECT tagNumber, animalType, breed, gender, status, dateRegistered FROM animals ORDER BY dateRegistered DESC";

        try {
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Object[] row = new Object[6];
                row[0] = rs.getString("tagNumber");
                row[1] = rs.getString("animalType");
                row[2] = rs.getString("breed");
                row[3] = rs.getString("gender");
                row[4] = rs.getString("status");
                row[5] = rs.getString("dateRegistered");
                rows.add(row);
            }
        } catch (SQLException e) {
            System.out.println("Error loading animals: " + e.getMessage());
        }

        return rows;
    }

    // returns animals filtered by type and status
    public List<Object[]> getFilteredAnimals(String type, String status) {
        List<Object[]> rows = new ArrayList<>();

        String sql = "SELECT tagNumber, animalType, breed, gender, status, dateRegistered FROM animals WHERE 1=1";

        if (!type.equals("All")) {
            sql = sql + " AND animalType = ?";
        }
        if (!status.equals("All")) {
            sql = sql + " AND status = ?";
        }
        sql = sql + " ORDER BY dateRegistered DESC";

        try {
            PreparedStatement stmt = conn.prepareStatement(sql);

            int index = 1;
            if (!type.equals("All")) {
                stmt.setString(index, type);
                index++;
            }
            if (!status.equals("All")) {
                stmt.setString(index, status);
            }

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Object[] row = new Object[6];
                row[0] = rs.getString("tagNumber");
                row[1] = rs.getString("animalType");
                row[2] = rs.getString("breed");
                row[3] = rs.getString("gender");
                row[4] = rs.getString("status");
                row[5] = rs.getString("dateRegistered");
                rows.add(row);
            }
        } catch (SQLException e) {
            System.out.println("Error filtering animals: " + e.getMessage());
        }

        return rows;
    }

    // adds a new animal to both the animals table and the specific type table
    public boolean addAnimal(String tagNumber, String animalType, String breed,
                             String gender, String status, String dateRegistered) {
        String animalSql = "INSERT INTO animals (tagNumber, animalType, breed, gender, status, dateRegistered) VALUES (?, ?, ?, ?, ?, ?)";
        String typeSql;

        if (animalType.toUpperCase().equals("CATTLE")) {
            typeSql = "INSERT INTO cattle (tagNumber) VALUES (?)";
        } else if (animalType.toUpperCase().equals("SHEEP")) {
            typeSql = "INSERT INTO sheep (tagNumber) VALUES (?)";
        } else if (animalType.toUpperCase().equals("POULTRY")) {
            typeSql = "INSERT INTO poultry (tagNumber) VALUES (?)";
        } else {
            return false;
        }

        try {
            conn.setAutoCommit(false);

            PreparedStatement s1 = conn.prepareStatement(animalSql);
            s1.setString(1, tagNumber);
            s1.setString(2, animalType.toUpperCase());
            s1.setString(3, breed);
            s1.setString(4, gender);
            s1.setString(5, status.toUpperCase());
            s1.setString(6, dateRegistered);
            s1.executeUpdate();

            PreparedStatement s2 = conn.prepareStatement(typeSql);
            s2.setString(1, tagNumber);
            s2.executeUpdate();

            conn.commit();
            conn.setAutoCommit(true);
            return true;

        } catch (SQLException e) {
            try {
                conn.rollback();
                conn.setAutoCommit(true);
            } catch (SQLException ex) {
                System.out.println("Rollback failed: " + ex.getMessage());
            }
            System.out.println("Add animal failed: " + e.getMessage());
            return false;
        }
    }

    // updates the status of an animal
    public boolean updateStatus(String tagNumber, String newStatus) {
        String sql = "UPDATE animals SET status = ? WHERE tagNumber = ?";
        try {
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, newStatus.toUpperCase());
            stmt.setString(2, tagNumber);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Update status failed: " + e.getMessage());
            return false;
        }
    }

    // counts animals by a specific status
    public int countByStatus(String status) {
        String sql = "SELECT COUNT(*) FROM animals WHERE status = ?";
        try {
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, status.toUpperCase());
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.out.println("Count error: " + e.getMessage());
        }
        return 0;
    }

    // counts all animals in the database
    public int countAll() {
        String sql = "SELECT COUNT(*) FROM animals";
        try {
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.out.println("Count error: " + e.getMessage());
        }
        return 0;
    }
}