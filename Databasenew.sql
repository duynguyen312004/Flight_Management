-- Xóa cơ sở dữ liệu nếu tồn tại
DROP DATABASE IF EXISTS flight_management_database;

-- Tạo lại cơ sở dữ liệu
CREATE DATABASE flight_management_database;

-- Sử dụng cơ sở dữ liệu vừa tạo
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

-- Bảng airplane
CREATE TABLE airplane (
    airplane_id VARCHAR(50) PRIMARY KEY,
    seat_capacity INT CHECK (seat_capacity >= 0),
    status ENUM('Available', 'In Use') DEFAULT 'Available'
);

-- Bảng flight
CREATE TABLE flight (
    flight_number VARCHAR(50) PRIMARY KEY,
    departure_location VARCHAR(100),
    arrival_location VARCHAR(100),
    departure_time DATETIME,
    arrival_time DATETIME,
    status ENUM('Scheduled', 'Delayed', 'Cancelled', 'Landed'),
    airplane_id VARCHAR(50),
    assigned_gate VARCHAR(50),
    FOREIGN KEY (airplane_id) REFERENCES airplane(airplane_id),
    FOREIGN KEY (assigned_gate) REFERENCES gate(gate_number)
);
CREATE TABLE flight_history (
    id INT AUTO_INCREMENT PRIMARY KEY,
    flight_number VARCHAR(50) NOT NULL,
    departure_location VARCHAR(100),
    arrival_location VARCHAR(100),
    departure_time DATETIME,
    arrival_time DATETIME,
    status ENUM('Cancelled', 'Landed') NOT NULL,
    airplane_id VARCHAR(50),
    assigned_gate VARCHAR(50),
    tickets JSON, -- Danh sách vé dưới dạng JSON
    crew JSON, -- Danh sách phi hành đoàn dưới dạng JSON
    ground_staff JSON -- Danh sách nhân viên mặt đất dưới dạng JSON
);


-- Bảng ground_staff
CREATE TABLE ground_staff (
    id VARCHAR(50) PRIMARY KEY,
    assigned_gate VARCHAR(50),
    assignment_date DATETIME DEFAULT NULL,
    FOREIGN KEY (id) REFERENCES employee(id),
    FOREIGN KEY (assigned_gate) REFERENCES gate(gate_number) ON DELETE SET NULL
);

-- Bảng flight_crew
CREATE TABLE flight_crew (
    id VARCHAR(50) PRIMARY KEY,
    crew_role VARCHAR(50),
    flight_number VARCHAR(50),
    assignment_date DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (id) REFERENCES employee(id),
    FOREIGN KEY (flight_number) REFERENCES flight(flight_number) ON DELETE SET NULL
);

-- Bảng passenger
CREATE TABLE passenger (
    id VARCHAR(50) PRIMARY KEY,
    name VARCHAR(100),
    email VARCHAR(100) UNIQUE,
    phone VARCHAR(20) UNIQUE
);

-- Bảng ticket
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
