package greenpasteures.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class StockDAO {

    private Connection conn;

    // constructor - gets the database connection
    public StockDAO() {
        this.conn = DatabaseManager.getInstance().getConnection();
    }

    // returns all stock items including a low stock status
    public List<Object[]> getAllItems() {
        List<Object[]> rows = new ArrayList<>();
        String sql = "SELECT itemId, name, category, quantity, unit, reorderLevel, lastUpdated FROM stock_items";

        try {
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                double qty = rs.getDouble("quantity");
                double reorder = rs.getDouble("reorderLevel");
                String status = "OK";
                if (qty <= reorder) {
                    status = "Low";
                }

                Object[] row = new Object[8];
                row[0] = rs.getInt("itemId");
                row[1] = rs.getString("name");
                row[2] = rs.getString("category");
                row[3] = qty;
                row[4] = rs.getString("unit");
                row[5] = reorder;
                row[6] = rs.getString("lastUpdated");
                row[7] = status;
                rows.add(row);
            }
        } catch (SQLException e) {
            System.out.println("Error loading stock: " + e.getMessage());
        }

        return rows;
    }

    // inserts a new stock item
    public boolean addItem(String name, String category, double quantity,
                           String unit, double reorderLevel) {
        String sql = "INSERT INTO stock_items (name, category, quantity, unit, reorderLevel, lastUpdated) " +
                     "VALUES (?, ?, ?, ?, ?, CURDATE())";
        try {
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, name);
            stmt.setString(2, category);
            stmt.setDouble(3, quantity);
            stmt.setString(4, unit);
            stmt.setDouble(5, reorderLevel);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Add stock item failed: " + e.getMessage());
            return false;
        }
    }

    // updates the quantity of an existing stock item
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

    // counts how many items are below the reorder level
    public int countLowStock() {
        String sql = "SELECT COUNT(*) FROM stock_items WHERE quantity <= reorderLevel";
        try {
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.out.println("Low stock count error: " + e.getMessage());
        }
        return 0;
    }
}