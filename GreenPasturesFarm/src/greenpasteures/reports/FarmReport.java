package greenpasteures.reports;

import greenpasteures.database.DatabaseManager;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;

public class FarmReport {

    // attributes
    private int reportId;
    private LocalDate generatedDate;
    private String generatedBy;

    // lists to store animal and stock data
    private ArrayList<String[]> cattleList;
    private ArrayList<String[]> sheepList;
    private ArrayList<String[]> poultryList;
    private ArrayList<String[]> stockList;
    private ArrayList<String[]> healthRecordList;

    // counters for the summary
    private int totalAnimals;
    private int totalDead;
    private int totalLost;
    private int totalStolen;
    private int totalSold;
    private int totalHealthy;
    private int totalSick;

    // constructor
    public FarmReport(int reportId, String generatedBy) {
        this.reportId = reportId;
        this.generatedBy = generatedBy;
        this.generatedDate = LocalDate.now();

        cattleList = new ArrayList<>();
        sheepList = new ArrayList<>();
        poultryList = new ArrayList<>();
        stockList = new ArrayList<>();
        healthRecordList = new ArrayList<>();

        totalAnimals = 0;
        totalDead = 0;
        totalLost = 0;
        totalStolen = 0;
        totalSold = 0;
        totalHealthy = 0;
        totalSick = 0;
    }

   
    public void loadData() {
        loadCattle();
        loadSheep();
        loadPoultry();
        loadStockItems();
        loadHealthRecords();
        calculateSummary();
    }

    
    private void loadCattle() {
        String sql = "SELECT a.tagNumber, a.breed, a.gender, a.status, a.dateRegistered, c.purpose, c.milkCollected, c.weight FROM animals a JOIN cattle c ON a.tagNumber = c.tagNumber";
        try {
            Connection conn = DatabaseManager.getStaticConnection();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                String[] row = new String[8];
                row[0] = rs.getString("tagNumber");
                row[1] = rs.getString("breed");
                row[2] = rs.getString("gender");
                row[3] = rs.getString("status");
                row[4] = rs.getString("dateRegistered");
                row[5] = rs.getString("purpose");
                row[6] = String.valueOf(rs.getDouble("milkCollected"));
                row[7] = String.valueOf(rs.getDouble("weight"));
                cattleList.add(row);
            }
        } catch (Exception e) {
            System.out.println("Error loading cattle: " + e.getMessage());
        }
    }

   
    private void loadSheep() {
        String sql = "SELECT a.tagNumber, a.breed, a.gender, a.status, a.dateRegistered, s.wool, s.isBreeder FROM animals a JOIN sheep s ON a.tagNumber = s.tagNumber";
        try {
            Connection conn = DatabaseManager.getStaticConnection();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                String[] row = new String[7];
                row[0] = rs.getString("tagNumber");
                row[1] = rs.getString("breed");
                row[2] = rs.getString("gender");
                row[3] = rs.getString("status");
                row[4] = rs.getString("dateRegistered");
                row[5] = String.valueOf(rs.getDouble("wool"));
                row[6] = String.valueOf(rs.getBoolean("isBreeder"));
                sheepList.add(row);
            }
        } catch (Exception e) {
            System.out.println("Error loading sheep: " + e.getMessage());
        }
    }

    
    private void loadPoultry() {
        String sql = "SELECT a.tagNumber, a.breed, a.gender, a.status, a.dateRegistered, p.poultryType, p.eggsCollected, p.housingUnit FROM animals a JOIN poultry p ON a.tagNumber = p.tagNumber";
        try {
            Connection conn = DatabaseManager.getStaticConnection();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                String[] row = new String[8];
                row[0] = rs.getString("tagNumber");
                row[1] = rs.getString("breed");
                row[2] = rs.getString("gender");
                row[3] = rs.getString("status");
                row[4] = rs.getString("dateRegistered");
                row[5] = rs.getString("poultryType");
                row[6] = String.valueOf(rs.getInt("eggsCollected"));
                row[7] = rs.getString("housingUnit");
                poultryList.add(row);
            }
        } catch (Exception e) {
            System.out.println("Error loading poultry: " + e.getMessage());
        }
    }

    
    private void loadStockItems() {
        String sql = "SELECT itemId, name, category, quantity, unit, reorderLevel, lastUpdated FROM stock_items";
        try {
            Connection conn = DatabaseManager.getStaticConnection();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                String[] row = new String[7];
                row[0] = String.valueOf(rs.getInt("itemId"));
                row[1] = rs.getString("name");
                row[2] = rs.getString("category");
                row[3] = String.valueOf(rs.getDouble("quantity"));
                row[4] = rs.getString("unit");
                row[5] = String.valueOf(rs.getDouble("reorderLevel"));
                row[6] = rs.getString("lastUpdated");
                stockList.add(row);
            }
        } catch (Exception e) {
            System.out.println("Error loading stock items: " + e.getMessage());
        }
    }

    
    private void loadHealthRecords() {
        String sql = "SELECT h.recordId, h.tagNumber, h.treatmentDate, h.diagnosis, h.treatment, h.medicineUsed, s.firstName, s.lastName FROM health_records h JOIN staff s ON h.attendedBy = s.staffId";
        try {
            Connection conn = DatabaseManager.getStaticConnection();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                String[] row = new String[7];
                row[0] = String.valueOf(rs.getInt("recordId"));
                row[1] = rs.getString("tagNumber");
                row[2] = rs.getString("treatmentDate");
                row[3] = rs.getString("diagnosis");
                row[4] = rs.getString("treatment");
                row[5] = rs.getString("medicineUsed");
                row[6] = rs.getString("firstName") + " " + rs.getString("lastName");
                healthRecordList.add(row);
            }
        } catch (Exception e) {
            System.out.println("Error loading health records: " + e.getMessage());
        }
    }

    //counts all the animals and their statuses
    private void calculateSummary() {
        totalAnimals = cattleList.size() + sheepList.size() + poultryList.size();

       
        for (int i = 0; i < cattleList.size(); i++) {
            String status = cattleList.get(i)[3];
            if (status.equals("HEALTHY"))  { totalHealthy++; }
            else if (status.equals("SICK"))     { totalSick++;    }
            else if (status.equals("SOLD"))     { totalSold++;    }
            else if (status.equals("DECEASED")) { totalDead++;    }
            else if (status.equals("LOST"))     { totalLost++;    }
            else if (status.equals("STOLEN"))   { totalStolen++;  }
        }

        
        for (int i = 0; i < sheepList.size(); i++) {
            String status = sheepList.get(i)[3];
            if (status.equals("HEALTHY"))  { totalHealthy++; }
            else if (status.equals("SICK"))     { totalSick++;    }
            else if (status.equals("SOLD"))     { totalSold++;    }
            else if (status.equals("DECEASED")) { totalDead++;    }
            else if (status.equals("LOST"))     { totalLost++;    }
            else if (status.equals("STOLEN"))   { totalStolen++;  }
        }

       
        for (int i = 0; i < poultryList.size(); i++) {
            String status = poultryList.get(i)[3];
            if (status.equals("HEALTHY"))  { totalHealthy++; }
            else if (status.equals("SICK"))     { totalSick++;    }
            else if (status.equals("SOLD"))     { totalSold++;    }
            else if (status.equals("DECEASED")) { totalDead++;    }
            else if (status.equals("LOST"))     { totalLost++;    }
            else if (status.equals("STOLEN"))   { totalStolen++;  }
        }
    }

    
    public String generateReport() {
        String report = "";
        report = report + "========================================\n";
        report = report + "     GREEN PASTURES FARM REPORT         \n";
        report = report + "========================================\n";
        report = report + "Generated By : " + generatedBy + "\n";
        report = report + "Generated On : " + generatedDate + "\n";
        report = report + "----------------------------------------\n";
        report = report + "LIVESTOCK SUMMARY\n";
        report = report + "----------------------------------------\n";
        report = report + "Total Animals : " + totalAnimals + "\n";
        report = report + "  Cattle      : " + cattleList.size() + "\n";
        report = report + "  Sheep       : " + sheepList.size() + "\n";
        report = report + "  Poultry     : " + poultryList.size() + "\n";
        report = report + "----------------------------------------\n";
        report = report + "STATUS BREAKDOWN\n";
        report = report + "----------------------------------------\n";
        report = report + "Healthy  : " + totalHealthy + "\n";
        report = report + "Sick     : " + totalSick + "\n";
        report = report + "Sold     : " + totalSold + "\n";
        report = report + "Deceased : " + totalDead + "\n";
        report = report + "Lost     : " + totalLost + "\n";
        report = report + "Stolen   : " + totalStolen + "\n";
        report = report + "----------------------------------------\n";
        report = report + "Stock Items    : " + stockList.size() + " item(s)\n";
        report = report + "Health Records : " + healthRecordList.size() + " record(s)\n";
        report = report + "========================================\n";
        return report;
    }

    
    public int getReportId(){  
     return reportId; }
    
    public LocalDate getGeneratedDate(){
    return generatedDate; }
    
    public String getGeneratedBy(){              
     return generatedBy; }
    
    public ArrayList<String[]> getCattleList(){   
     return cattleList; }
    
    public ArrayList<String[]> getSheepList(){  
     return sheepList; }
    
    public ArrayList<String[]> getPoultryList(){  
     return poultryList; }
    
    public ArrayList<String[]> getStockList(){  
     return stockList; }
    
    public ArrayList<String[]> getHealthRecordList(){
     return healthRecordList; }
    
    public int getTotalAnimals(){         
     return totalAnimals;}
    
    public int getTotalDead(){          
     return totalDead; }
    
    public int getTotalLost(){       
     return totalLost; }
    
    public int getTotalStolen(){      
     return totalStolen; }
    
    public int getTotalSold(){      
     return totalSold;}
    
    public int getTotalHealthy(){   
     return totalHealthy;}
    
    public int getTotalSick(){ 
     return totalSick;}
}