import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {
        try {
            // Load file FXML chính (FlightManagement.fxml)
            Parent root = FXMLLoader.load(getClass().getResource("views/FlightManagement.fxml"));

            // Đặt tiêu đề cho cửa sổ
            primaryStage.setTitle("Flight Management System");
            primaryStage.getIcons()
                    .add(new javafx.scene.image.Image(getClass().getResourceAsStream("assets/flight.png")));
            primaryStage.setMaximized(true);
            // Thiết lập Scene và kích thước
            primaryStage.setScene(new Scene(root, 800, 600));

            // Hiển thị cửa sổ
            primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        // Khởi động JavaFX
        launch(args);
    }
}
