package greenpasteures.models;

public class StockItem {

    // attributes
    private int itemId;
    private String name;
    private String category;
    private double quantity;
    private String unit;
    private double reorderLevel;
    private String lastUpdated;

    // constructor
    public StockItem(int itemId, String name, String category, double quantity,
                     String unit, double reorderLevel, String lastUpdated) {
        this.itemId = itemId;
        this.name = name;
        this.category = category;
        this.quantity = quantity;
        this.unit = unit;
        this.reorderLevel = reorderLevel;
        this.lastUpdated = lastUpdated;
    }

    // this method checks if the stock is running low
    public boolean isLowStock() {
        return quantity <= reorderLevel;
    }

    // this method reduces the quantity when items are used
    public void useItem(double amount) {
        if (amount > quantity) {
            System.out.println("Not enough stock available.");
        } else {
            quantity = quantity - amount;
        }
    }

    // this method increases the quantity when items are restocked
    public void restock(double amount) {
        quantity = quantity + amount;
    }

    // getters
    public int getItemId() {
        return itemId;
    }

    public String getName() {
        return name;
    }

    public String getCategory() {
        return category;
    }

    public double getQuantity() {
        return quantity;
    }

    public String getUnit() {
        return unit;
    }

    public double getReorderLevel() {
        return reorderLevel;
    }

    public String getLastUpdated() {
        return lastUpdated;
    }

    // setters
    public void setQuantity(double quantity) {
        this.quantity = quantity;
    }

    public void setLastUpdated(String lastUpdated) {
        this.lastUpdated = lastUpdated;
    }
}