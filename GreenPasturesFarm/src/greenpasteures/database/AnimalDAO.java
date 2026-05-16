package greenpasteures.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class AnimalDAO {

    private Connection conn;

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

    // returns filtered animals by type and status
    public List<Object[]> getFilteredAnimals(String type, String status) {
        List<Object[]> rows = new ArrayList<>();
        String sql = "SELECT tagNumber, animalType, breed, gender, status, dateRegistered FROM animals WHERE 1=1";
        if (!type.equals("All"))   sql = sql + " AND animalType = ?";
        if (!status.equals("All")) sql = sql + " AND status = ?";
        sql = sql + " ORDER BY dateRegistered DESC";
        try {
            PreparedStatement stmt = conn.prepareStatement(sql);
            int index = 1;
            if (!type.equals("All"))   { stmt.setString(index, type);   index++; }
            if (!status.equals("All")) { stmt.setString(index, status); }
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
                             String gender, String status, String dateRegistered, double weight) {
        String animalSql = "INSERT INTO animals (tagNumber, animalType, breed, gender, status, dateRegistered) VALUES (?, ?, ?, ?, ?, ?)";
        String typeSql;
        if (animalType.toUpperCase().equals("CATTLE")) {
            typeSql = "INSERT INTO cattle (tagNumber, weight) VALUES (?, ?)";
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
            if (animalType.toUpperCase().equals("CATTLE")) {
                s2.setDouble(2, weight);
            }
            s2.executeUpdate();

            conn.commit();
            conn.setAutoCommit(true);
            return true;
        } catch (SQLException e) {
            try { conn.rollback(); conn.setAutoCommit(true); } catch (SQLException ex) {}
            System.out.println("Add animal failed: " + e.getMessage());
            return false;
        }
    }

    // updates an existing animal record
    public boolean updateAnimal(String tagNumber, String breed, String gender,
                                String status, double weight, String animalType) {
        String animalSql = "UPDATE animals SET breed = ?, gender = ?, status = ? WHERE tagNumber = ?";
        try {
            conn.setAutoCommit(false);

            PreparedStatement s1 = conn.prepareStatement(animalSql);
            s1.setString(1, breed);
            s1.setString(2, gender);
            s1.setString(3, status.toUpperCase());
            s1.setString(4, tagNumber);
            s1.executeUpdate();

            // update weight in the type specific table
            if (animalType.toUpperCase().equals("CATTLE")) {
                PreparedStatement s2 = conn.prepareStatement("UPDATE cattle SET weight = ? WHERE tagNumber = ?");
                s2.setDouble(1, weight);
                s2.setString(2, tagNumber);
                s2.executeUpdate();
            } else if (animalType.toUpperCase().equals("SHEEP")) {
                PreparedStatement s2 = conn.prepareStatement("UPDATE sheep SET wool = ? WHERE tagNumber = ?");
                s2.setDouble(1, weight);
                s2.setString(2, tagNumber);
                s2.executeUpdate();
            }

            conn.commit();
            conn.setAutoCommit(true);
            return true;
        } catch (SQLException e) {
            try { conn.rollback(); conn.setAutoCommit(true); } catch (SQLException ex) {}
            System.out.println("Update animal failed: " + e.getMessage());
            return false;
        }
    }

    // deletes an animal and all its related records
    public boolean deleteAnimal(String tagNumber, String animalType) {
        try {
            conn.setAutoCommit(false);

            // delete health records first because of foreign key
            PreparedStatement s1 = conn.prepareStatement("DELETE FROM health_records WHERE tagNumber = ?");
            s1.setString(1, tagNumber);
            s1.executeUpdate();

            // delete from type specific table
            String typeTable = "cattle";
            if (animalType.toUpperCase().equals("SHEEP"))   typeTable = "sheep";
            if (animalType.toUpperCase().equals("POULTRY")) typeTable = "poultry";
            PreparedStatement s2 = conn.prepareStatement("DELETE FROM " + typeTable + " WHERE tagNumber = ?");
            s2.setString(1, tagNumber);
            s2.executeUpdate();

            // delete from main animals table
            PreparedStatement s3 = conn.prepareStatement("DELETE FROM animals WHERE tagNumber = ?");
            s3.setString(1, tagNumber);
            s3.executeUpdate();

            conn.commit();
            conn.setAutoCommit(true);
            return true;
        } catch (SQLException e) {
            try { conn.rollback(); conn.setAutoCommit(true); } catch (SQLException ex) {}
            System.out.println("Delete animal failed: " + e.getMessage());
            return false;
        }
    }

    // checks if a tag number already exists
    public boolean tagExists(String tagNumber) {
        String sql = "SELECT COUNT(*) FROM animals WHERE tagNumber = ?";
        try {
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, tagNumber);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) return rs.getInt(1) > 0;
        } catch (SQLException e) {
            System.out.println("Tag check error: " + e.getMessage());
        }
        return false;
    }

    // returns the 5 most recently added animals for the dashboard
    public List<Object[]> getRecentAnimals(int limit) {
        List<Object[]> rows = new ArrayList<>();
        String sql = "SELECT tagNumber, animalType, breed, status, dateRegistered FROM animals ORDER BY dateRegistered DESC LIMIT ?";
        try {
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, limit);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Object[] row = new Object[5];
                row[0] = rs.getString("tagNumber");
                row[1] = rs.getString("animalType");
                row[2] = rs.getString("breed");
                row[3] = rs.getString("status");
                row[4] = rs.getString("dateRegistered");
                rows.add(row);
            }
        } catch (SQLException e) {
            System.out.println("Error loading recent animals: " + e.getMessage());
        }
        return rows;
    }

    // counts animals by status
    public int countByStatus(String status) {
        String sql = "SELECT COUNT(*) FROM animals WHERE status = ?";
        try {
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, status.toUpperCase());
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            System.out.println("Count error: " + e.getMessage());
        }
        return 0;
    }

    // counts all animals
    public int countAll() {
        String sql = "SELECT COUNT(*) FROM animals";
        try {
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            System.out.println("Count error: " + e.getMessage());
        }
        return 0;
    }
}