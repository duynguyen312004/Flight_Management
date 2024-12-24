// package models;

// import models.Ticket;
// import services.TicketService;

// import java.util.List;

// public class TicketTest {

// public static void main(String[] args) {
// // Khởi tạo đối tượng TicketService
// TicketService ticketService = new TicketService();

// // 1. Kiểm tra lấy tất cả các vé từ cơ sở dữ liệu
// System.out.println("Danh sách tất cả các vé:");
// List<Ticket> tickets = ticketService.getAllTickets();
// for (Ticket ticket : tickets) {
// System.out.println(ticket);
// }

// // 2. Thêm vé mới vào cơ sở dữ liệu
// System.out.println("\nThêm vé mới:");
// Ticket newTicket = new Ticket("6004", "4002", "5003", "14A", "Economy",
// 200.00);
// boolean isAdded = ticketService.addTicket(newTicket);
// if (isAdded) {
// System.out.println("Vé đã được thêm thành công.");
// } else {
// System.out.println("Thêm vé thất bại.");
// }

// // 3. Kiểm tra lại danh sách vé sau khi thêm mới
// System.out.println("\nDanh sách vé sau khi thêm mới:");
// tickets = ticketService.getAllTickets();
// for (Ticket ticket : tickets) {
// System.out.println(ticket);
// }

// // 4. Cập nhật thông tin vé
// System.out.println("\nCập nhật vé:");
// Ticket updatedTicket = new Ticket("6004", "4002", "5003", "14B", "Business",
// 500.00);
// boolean isUpdated = ticketService.updateTicket(updatedTicket);
// if (isUpdated) {
// System.out.println("Vé đã được cập nhật thành công.");
// } else {
// System.out.println("Cập nhật vé thất bại.");
// }

// // 5. Kiểm tra lại thông tin vé sau khi cập nhật
// System.out.println("\nDanh sách vé sau khi cập nhật:");
// tickets = ticketService.getAllTickets();
// for (Ticket ticket : tickets) {
// System.out.println(ticket);
// }

// // 6. Xóa vé
// System.out.println("\nXóa vé:");
// boolean isDeleted = ticketService.deleteTicket("6004");
// if (isDeleted) {
// System.out.println("Vé đã được xóa thành công.");
// } else {
// System.out.println("Xóa vé thất bại.");
// }

// // 7. Kiểm tra lại danh sách vé sau khi xóa
// System.out.println("\nDanh sách vé sau khi xóa:");
// tickets = ticketService.getAllTickets();
// for (Ticket ticket : tickets) {
// System.out.println(ticket);
// }

// // 8. Lấy vé của hành khách theo passenger_id
// System.out.println("\nLấy vé của hành khách với passenger_id = 5002:");
// List<Ticket> passengerTickets = ticketService.getTicketsByPassenger("5002");
// for (Ticket ticket : passengerTickets) {
// System.out.println(ticket);
// }
// }
// }
