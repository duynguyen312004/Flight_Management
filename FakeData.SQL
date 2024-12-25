INSERT INTO gate (gate_number, is_available) VALUES
('G1', 0), -- Đang được gán cho chuyến bay
('G2', 0), -- Đang được gán cho chuyến bay
('G3', 1), -- Chưa được sử dụng
('G4', 1); -- Chưa được sử dụng
INSERT INTO employee (id, name, address, role) VALUES
('E001', 'John Doe', '123 Main Street', 'Ground Staff'),
('E002', 'Jane Smith', '456 Maple Avenue', 'Pilot'),
('E003', 'David Johnson', '789 Oak Lane', 'Flight Attendant'),
('E004', 'Emily Davis', '321 Pine Road', 'Co-Pilot'),
('E005', 'Michael Brown', '987 Spruce Street', 'Ground Staff');
INSERT INTO ground_staff (id, assigned_gate, assignment_date) VALUES
('E001', 'G1', '2024-12-22 08:00:00'), -- Gán cổng G1
('E005', 'G2', '2024-12-22 09:00:00'); -- Gán cổng G2
INSERT INTO airplane (airplane_id, seat_capacity, status) VALUES
('A001', 180, 'In Use'), -- Được sử dụng cho chuyến bay F001
('A002', 220, 'In Use'), -- Được sử dụng cho chuyến bay F002
('A003', 150, 'Available'); -- Chưa được sử dụng
INSERT INTO flight (flight_number, departure_location, arrival_location, departure_time, arrival_time, status, airplane_id, assigned_gate) VALUES
('F001', 'New York', 'Los Angeles', '2024-12-25 08:00:00', '2024-12-25 11:30:00', 'Scheduled', 'A001', 'G1'),
('F002', 'San Francisco', 'Seattle', '2024-12-26 14:00:00', '2024-12-26 16:00:00', 'Scheduled', 'A002', 'G2');
INSERT INTO flight_crew (id, crew_role, flight_number, assignment_date) VALUES
('E002', 'Pilot', 'F001', '2024-12-22 12:00:00'), -- Pilot cho chuyến F001
('E004', 'Co-Pilot', 'F001', '2024-12-22 12:30:00'), -- Co-Pilot cho chuyến F001
('E003', 'Flight Attendant', 'F002', '2024-12-23 14:00:00'); -- Flight Attendant cho chuyến F002
INSERT INTO passenger (id, name, email, phone) VALUES
('P001', 'Alice Brown', 'alice.brown@example.com', '1234567890'),
('P002', 'Bob White', 'bob.white@example.com', '0987654321'),
('P003', 'Charlie Green', 'charlie.green@example.com', '1122334455');
INSERT INTO ticket (ticket_id, flight_number, passenger_id, seat_number, seat_class, price) VALUES
('T001', 'F001', 'P001', '12A', 'Economy', 200.00), -- Vé cho hành khách Alice trên chuyến F001
('T002', 'F001', 'P002', '12B', 'Economy', 200.00), -- Vé cho hành khách Bob trên chuyến F001
('T003', 'F002', 'P003', '1A', 'Business', 500.00); -- Vé cho hành khách Charlie trên chuyến F002
