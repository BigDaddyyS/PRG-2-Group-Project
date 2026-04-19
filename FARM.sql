CREATE DATABASE green_pastures_farm;
USE green_pastures_farm;

CREATE TABLE staff(
staffId INT AUTO_INCREMENT PRIMARY KEY,
firstName VARCHAR(50) NOT NULL,
lastName VARCHAR(50) NOT NULL,
role VARCHAR(50) NOT NULL,
phoneNumber VARCHAR(20),
userName VARCHAR(50) UNIQUE NOT NULL,
password VARCHAR(255) NOT NULL
);

CREATE TABLE animals (
tagNumber VARCHAR(20) PRIMARY KEY,
animalType VARCHAR(20) NOT NULL,
breed VARCHAR(50),
gender VARCHAR(10),
status VARCHAR(20) DEFAULT 'HEALTHY',
dateRegistered DATE
);

CREATE TABLE cattle (
tagNumber VARCHAR(20) PRIMARY KEY,
purpose VARCHAR(20),
milkCollected DOUBLE DEFAULT 0,
weight DOUBLE DEFAULT 0,
FOREIGN KEY (tagNumber) REFERENCES animals (tagNumber)
);

CREATE TABLE poultry (
tagNumber VARCHAR(20) PRIMARY KEY,
poultryTYPE VARCHAR (20),
eggsCollected INT DEFAULT 0,
housingUnit VARCHAR(20),
FOREIGN KEY (tagNumber) REFERENCES animals (tagNumber)
);

CREATE TABLE sheep (
tagNumber VARCHAR(20) PRIMARY KEY,
wool DOUBLE DEFAULT 0,
isBreeder BOOLEAN DEFAULT FALSE,
FOREIGN KEY (tagNumber) REFERENCES animals(tagNumber)
);

CREATE TABLE stock_items (
itemId INT AUTO_INCREMENT PRIMARY KEY,
name VARCHAR(100) NOT NULL,
category VARCHAR(100) NOT NULL,
quantity DOUBLE DEFAULT 0,
unit VARCHAR(20),
reorderLevel DOUBLE DEFAULT 0,
lastUpdated DATE
);

CREATE TABLE health_records (
recordID INT AUTO_INCREMENT PRIMARY KEY,
tagNumber VARCHAR(20) NOT NULL,
treatmentDate DATE NOT NULL,
diagnosis VARCHAR(255),
treatment VARCHAR(255),
medicineUsed VARCHAR(100),
attendedBy INT NOT NULL,
FOREIGN KEY (tagNumber) REFERENCES animals(tagNumber),
FOREIGN KEY (attendedBy) REFERENCES staff(staffId)
);

CREATE TABLE farm_reports (
reportId INT AUTO_INCREMENT PRIMARY KEY,
generatedDate DATE NOT NULL,
generatedBy INT NOT NULL,
FOREIGN KEY (generatedBy) REFERENCES staff(staffId) 
);

INSERT INTO staff (firstName, lastName, role, phoneNumber, username, password)
VALUES ('Admin', 'User', 'Admin','0812345678', 'admin', 'admin123');