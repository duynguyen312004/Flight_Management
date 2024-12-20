CREATE DATABASE my_database;
USE my_database;

CREATE TABLE Gate (
    gateNumber VARCHAR(50) PRIMARY KEY,
    isAvailable BOOLEAN
);

CREATE TABLE GroundStaff (
    id VARCHAR(50) PRIMARY KEY,
    assignedGate VARCHAR(50),
    shiftSchedule VARCHAR(50),
    FOREIGN KEY (assignedGate) REFERENCES Gate(gateNumber)
);

CREATE TABLE Employee (
    id VARCHAR(50) PRIMARY KEY,
    name VARCHAR(100),
    address VARCHAR(255),
    role VARCHAR(50)
);

CREATE TABLE FlightCrew (
    id VARCHAR(50) PRIMARY KEY,
    crewRole VARCHAR(50),
    FOREIGN KEY (id) REFERENCES Employee(id)
);

CREATE TABLE Admin (
    adminID VARCHAR(50) PRIMARY KEY
);

CREATE TABLE Airplane (
    airplaneID VARCHAR(50) PRIMARY KEY,
    seatCapacity INT
);

CREATE TABLE Flight (
    flightNumber VARCHAR(50) PRIMARY KEY,
    departureLocation VARCHAR(100),
    arrivalLocation VARCHAR(100),
    departureTime DATETIME,
    arrivalTime DATETIME,
    status VARCHAR(50),
    airplaneID VARCHAR(50),
    FOREIGN KEY (airplaneID) REFERENCES Airplane(airplaneID)
);

CREATE TABLE Passenger (
    id VARCHAR(50) PRIMARY KEY,
    name VARCHAR(100),
    email VARCHAR(100),
    phone VARCHAR(20)
);

CREATE TABLE Ticket (
    ticketID VARCHAR(50) PRIMARY KEY,
    flightNumber VARCHAR(50),
    passengerID VARCHAR(50),
    seatClass VARCHAR(50),
    isConfirmed BOOLEAN,
    FOREIGN KEY (flightNumber) REFERENCES Flight(flightNumber),
    FOREIGN KEY (passengerID) REFERENCES Passenger(id)
);
