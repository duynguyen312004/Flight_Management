-- Tạo cơ sở dữ liệu
CREATE DATABASE flight_management_database;
USE flight_management_database;

-- Bảng gate
CREATE TABLE gate (
    gate_number VARCHAR(50) PRIMARY KEY,
    is_available TINYINT(1) CHECK (is_available IN (0, 1))
);

-- Bảng employee
CREATE TABLE employee (
    id VARCHAR(50) PRIMARY KEY,
    name VARCHAR(100),
    address VARCHAR(255),
    role VARCHAR(50)
);

-- Bảng ground_staff
CREATE TABLE ground_staff (
    id VARCHAR(50) PRIMARY KEY,
    assigned_gate VARCHAR(50),
    FOREIGN KEY (id) REFERENCES employee(id),
    FOREIGN KEY (assigned_gate) REFERENCES gate(gate_number)
);

-- Bảng flight_crew
CREATE TABLE flight_crew (
    id VARCHAR(50) PRIMARY KEY,
    crew_role VARCHAR(50),
    FOREIGN KEY (id) REFERENCES employee(id)
);

-- Bảng airplane
CREATE TABLE airplane (
    airplane_id VARCHAR(50) PRIMARY KEY,
    seat_capacity INT CHECK (seat_capacity >= 0)
);

-- Bảng flight với ENUM cho trạng thái
CREATE TABLE flight (
    flight_number VARCHAR(50) PRIMARY KEY,
    departure_location VARCHAR(100),
    arrival_location VARCHAR(100),
    departure_time DATETIME,
    arrival_time DATETIME,
    status ENUM('Scheduled', 'Delayed', 'Cancelled', 'Landed'),
    airplane_id VARCHAR(50),
    FOREIGN KEY (airplane_id) REFERENCES airplane(airplane_id)
);

-- Bảng passenger
CREATE TABLE passenger (
    id VARCHAR(50) PRIMARY KEY,
    name VARCHAR(100),
    email VARCHAR(100) UNIQUE,
    phone VARCHAR(20) UNIQUE
);

-- Bảng ticket với ENUM cho seat_class
CREATE TABLE ticket (
    ticket_id VARCHAR(50) PRIMARY KEY,
    flight_number VARCHAR(50),
    passenger_id VARCHAR(50),
    seat_number VARCHAR(10),
    seat_class ENUM('Economy', 'Business'),
    price DECIMAL(10, 2),
    FOREIGN KEY (flight_number) REFERENCES flight(flight_number) ON DELETE CASCADE,
    FOREIGN KEY (passenger_id) REFERENCES passenger(id)
);
