package models;

import services.GroundStaffService;
import java.util.List;
import java.time.LocalDateTime;

public class GroundStaffTest {

    public static void main(String[] args) {
        // Khởi tạo service
        GroundStaffService groundStaffService = new GroundStaffService();

        // 1. Thử thêm một nhân viên mặt đất mới
        GroundStaff newStaff = new GroundStaff("G005", "miumiu", "456 Elm St", "Technician", "G1", LocalDateTime.now());
        boolean addResult = groundStaffService.addGroundStaff(newStaff);
        System.out.println("Add result: " + addResult);

        // 2. Thử lấy tất cả nhân viên mặt đất
        List<GroundStaff> allStaff = groundStaffService.getAllGroundStaff();
        System.out.println("All Ground Staff:");
        for (GroundStaff staff : allStaff) {
            System.out.println(staff.getDetails());
        }

        // 3. Cập nhật thông tin nhân viên mặt đất
        // Thử cập nhật thông tin gate
        boolean updateResult = groundStaffService.updateGroundStaff("G005", "assigned_gate", "G2");
        System.out.println("Update result: " + updateResult);

        // 4. Thử lấy thông tin chi tiết của một nhân viên mặt đất bằng ID
        GroundStaff staffDetails = groundStaffService.getGroundStaffById("E001");
        if (staffDetails != null) {
            System.out.println("Details of E001: " + staffDetails.getDetails());
        } else {
            System.out.println("Ground Staff with ID E001 not found.");
        }

        // 5. Thử xóa nhân viên mặt đất theo ID
        boolean deleteResult = groundStaffService.deleteGroundStaff("G005");
        System.out.println("Delete result: " + deleteResult);
    }
}
