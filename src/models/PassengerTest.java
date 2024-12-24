package models;

import models.Passenger;
import services.PassengerService;

import java.util.List;

public class PassengerTest {

    public static void main(String[] args) {
        PassengerService passengerService = new PassengerService();

        // 1. Kiểm tra lấy tất cả hành khách từ database
        System.out.println("Danh sách hành khách trước khi thay đổi:");
        List<Passenger> passengers = passengerService.getAllPassengers();
        for (Passenger passenger : passengers) {
            System.out.println("ID: " + passenger.getId() + ", Name: " + passenger.getName() +
                    ", Email: " + passenger.getEmail() + ", Phone: " + passenger.getPhone());
        }

        // 2. Thêm hành khách mới
        System.out.println("\nThêm hành khách mới:");
        Passenger newPassenger = new Passenger("5003", "John Doe", "john.doe@example.com", "1122334455");
        boolean added = passengerService.addPassenger(newPassenger);
        if (added) {
            System.out.println("Hành khách đã được thêm thành công.");
        } else {
            System.out.println("Thêm hành khách thất bại.");
        }

        // 3. Kiểm tra lại danh sách hành khách sau khi thêm mới
        System.out.println("\nDanh sách hành khách sau khi thêm mới:");
        passengers = passengerService.getAllPassengers();
        for (Passenger passenger : passengers) {
            System.out.println("ID: " + passenger.getId() + ", Name: " + passenger.getName() +
                    ", Email: " + passenger.getEmail() + ", Phone: " + passenger.getPhone());
        }

        // 4. Cập nhật thông tin hành khách
        System.out.println("\nCập nhật thông tin hành khách:");
        Passenger updatedPassenger = new Passenger("5003", "John Doe", "john.doe@updated.com", "9988776655");
        boolean updated = passengerService.updatePassenger(updatedPassenger);
        if (updated) {
            System.out.println("Hành khách đã được cập nhật thành công.");
        } else {
            System.out.println("Cập nhật hành khách thất bại.");
        }

        // 5. Kiểm tra lại thông tin hành khách sau khi cập nhật
        System.out.println("\nDanh sách hành khách sau khi cập nhật:");
        passengers = passengerService.getAllPassengers();
        for (Passenger passenger : passengers) {
            System.out.println("ID: " + passenger.getId() + ", Name: " + passenger.getName() +
                    ", Email: " + passenger.getEmail() + ", Phone: " + passenger.getPhone());
        }

        // 6. Lấy vé của hành khách
        System.out.println("\nLấy vé của hành khách với ID 5001:");
        String tickets = passengerService.getTicketsByPassenger("5001");
        System.out.println(tickets);

        // 7. Xóa hành khách
        System.out.println("\nXóa hành khách với ID 5003:");
        boolean deleted = passengerService.deletePassenger("5003");
        if (deleted) {
            System.out.println("Hành khách đã được xóa thành công.");
        } else {
            System.out.println("Xóa hành khách thất bại.");
        }

        // 8. Kiểm tra lại danh sách hành khách sau khi xóa
        System.out.println("\nDanh sách hành khách sau khi xóa:");
        passengers = passengerService.getAllPassengers();
        for (Passenger passenger : passengers) {
            System.out.println("ID: " + passenger.getId() + ", Name: " + passenger.getName() +
                    ", Email: " + passenger.getEmail() + ", Phone: " + passenger.getPhone());
        }
    }
}
