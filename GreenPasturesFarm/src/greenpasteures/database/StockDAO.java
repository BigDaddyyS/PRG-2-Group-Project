package greenpasteures.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class StockDAO {

    private final Connection conn;

    public StockDAO() {
        this.conn = DatabaseManager.getInstance().getConnection();
    }

    // returns all stock items
    public List<Object[]> getAllItems() {
        List<Object[]> rows = new ArrayList<>();
        String sql = "SELECT itemId, name, category, quantity, unit, reorderLevel, lastUpdated FROM stock_items";
        try {
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Object[] row = new Object[7];
                row[0] = rs.getInt("itemId");
                row[1] = rs.getString("name");
                row[2] = rs.getString("category");
                row[3] = rs.getDouble("quantity");
                row[4] = rs.getString("unit");
                row[5] = rs.getDouble("reorderLevel");
                row[6] = rs.getString("lastUpdated");
                rows.add(row);
            }
        } catch (SQLException e) {
            System.out.println("Error loading stock: " + e.getMessage());
        }
        return rows;
    }

    // adds a new stock item - no reorderLevel needed from user
    public boolean addItem(String name, String category, double quantity, String unit) {
        String sql = "INSERT INTO stock_items (name, category, quantity, unit, lastUpdated) " +
                     "VALUES (?, ?, ?, ?, CURDATE())";
        try {
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, name);
            stmt.setString(2, category);
            stmt.setDouble(3, quantity);
            stmt.setString(4, unit);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Add stock item failed: " + e.getMessage());
            return false;
        }
    }

    // updates an existing stock item - no reorderLevel needed from user
    public boolean updateItem(int itemId, String name, String category,
                              double quantity, String unit) {
        String sql = "UPDATE stock_items SET name = ?, category = ?, quantity = ?, unit = ?, lastUpdated = CURDATE() WHERE itemId = ?";
        try {
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, name);
            stmt.setString(2, category);
            stmt.setDouble(3, quantity);
            stmt.setString(4, unit);
            stmt.setInt(5, itemId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Update stock item failed: " + e.getMessage());
            return false;
        }
    }

    // updates only the quantity when restocking
    public boolean updateQuantity(int itemId, double newQuantity) {
        String sql = "UPDATE stock_items SET quantity = ?, lastUpdated = CURDATE() WHERE itemId = ?";
        try {
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setDouble(1, newQuantity);
            stmt.setInt(2, itemId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Update quantity failed: " + e.getMessage());
            return false;
        }
    }

    // deletes a stock item
    public boolean deleteItem(int itemId) {
        String sql = "DELETE FROM stock_items WHERE itemId = ?";
        try {
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, itemId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Delete stock item failed: " + e.getMessage());
            return false;
        }
    }

    // checks if an item name already exists
    public boolean itemNameExists(String name) {
        String sql = "SELECT COUNT(*) FROM stock_items WHERE name = ?";
        try {
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, name);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) return rs.getInt(1) > 0;
        } catch (SQLException e) {
            System.out.println("Item name check error: " + e.getMessage());
        }
        return false;
    }

    // counts how many items are below the reorder level - still used by dashboard
    public int countLowStock() {
        String sql = "SELECT COUNT(*) FROM stock_items WHERE quantity <= reorderLevel";
        try {
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            System.out.println("Low stock count error: " + e.getMessage());
        }
        return 0;
    }
}