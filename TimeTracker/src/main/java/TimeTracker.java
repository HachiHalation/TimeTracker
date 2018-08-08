import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;
import java.util.stream.Stream;


public class TimeTracker extends Application{

    private final Path MAIN_FOLDER = Paths.get("Data");



    @Override
    public void start(Stage primaryStage) throws Exception {
        makeFiles();

        Parent main = FXMLLoader.load(TimeTracker.class.getResource("UI/MainWindow.fxml"));
        primaryStage.setTitle("Time Tracker");
        primaryStage.setScene(new Scene(main, 700, 110));
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    public static void main(String[] args){
        launch(args);
    }

    private void makeFiles(){
        try {
            Files.createDirectory(MAIN_FOLDER);
        } catch (IOException e){
            System.out.println("Data directory exists, skipping init of folder");
        }
    }
}
