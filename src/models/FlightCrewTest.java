package models;

import services.FlightCrewService;
import java.util.List;
import java.time.LocalDateTime;

public class FlightCrewTest {

    public static void main(String[] args) {
        // Khởi tạo service
        FlightCrewService flightCrewService = new FlightCrewService();
        
        // 1. Thử thêm một phi hành đoàn mới
        FlightCrew newCrew = new FlightCrew("333", "santan", "123 Main St", "Captain", "Pilot", "F001", LocalDateTime.now());
        boolean addResult = flightCrewService.addFlightCrew(newCrew);
        System.out.println("Add result: " + addResult);

        // 2. Thử lấy tất cả phi hành đoàn
        List<FlightCrew> allCrew = flightCrewService.getAllFlightCrews();
        System.out.println("All Flight Crew:");
        for (FlightCrew crew : allCrew) {
            System.out.println(crew.getDetails());
        }

        // 3. Cập nhật thông tin phi hành đoàn
        // Thử cập nhật vai trò phi hành đoàn
        boolean updateResult = flightCrewService.updateFlightCrew("E002", "crew_role", "Co-Pilot");
        System.out.println("Update result: " + updateResult);

        //5. Thử xóa phi hành đoàn theo ID
        boolean deleteResult = flightCrewService.deleteFlightCrew("E004");
        System.out.println("Delete result: " + deleteResult);

    }
}
