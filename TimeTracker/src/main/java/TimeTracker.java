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

    @Override
    public void start(Stage primaryStage) throws Exception {

        Parent main = FXMLLoader.load(TimeTracker.class.getResource("UI/MainWindow.fxml"));
        primaryStage.setTitle("Time Tracker");
        primaryStage.setScene(new Scene(main, 700, 110));
        primaryStage.setResizable(false);
        primaryStage.setOnCloseRequest(event -> {
            try {
                Files.deleteIfExists(Options.getLogPath());
            } catch (IOException e){
                e.printStackTrace();
            }
        });
        primaryStage.show();
        Options.print("TimeTracker V" + Options.getVERSION(), null);

        makeFiles();
    }

    public static void main(String[] args){
        launch(args);
    }

    private void makeFiles() throws IOException{
        if(!Files.exists(Options.getMainFolder())){
            Options.print("Data folder not found. Creating.", null);
            Files.createDirectory(Options.getMainFolder());
        }
    }


}
