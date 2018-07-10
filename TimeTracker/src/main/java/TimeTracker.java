import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.net.URL;

public class TimeTracker extends Application{

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent main = FXMLLoader.load(TimeTracker.class.getResource("UI/MainWindow.fxml"));
        primaryStage.setTitle("Time Tracker");
        primaryStage.setScene(new Scene(main, 700, 110));
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    public static void main(String[] args){
        launch(args);
    }
}
