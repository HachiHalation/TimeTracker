import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;
import com.google.api.services.sheets.v4.Sheets;

import com.google.api.services.sheets.v4.model.Spreadsheet;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDateTime;

public class MainWindow {

    private CurrentSpreadsheet current;
    private DateManipulation now;

    private String VERSION = "0.5";

    @FXML
    private Button update;
    @FXML
    private Button create;
    @FXML
    private Label info;
    @FXML
    private TextField input;
    @FXML
    private Label title;

    @FXML
    private void initialize() throws IOException{
        title.setText("TimeTracker V" + VERSION);

        current = new CurrentSpreadsheet();
        now = new DateManipulation(LocalDateTime.now());
    }

    @FXML
    private void makeSheet() throws IOException{
        now.setNow(LocalDateTime.now());
        goToSetTitle("TimeTracker#" + now.getDate());

    }

    private void goToSetTitle(String title) throws IOException{
        Stage newStage = new Stage();
        FXMLLoader load = new FXMLLoader(MainWindow.class.getResource("NewSSheetWindow.fxml"));
        load.setController(new NewSSheetWindow(current, new StringBuilder(title)));
        Parent root = load.load();

        newStage.setTitle("Set Title");
        newStage.setScene(new Scene(root, 435, 37));
        newStage.setResizable(false);
        newStage.showAndWait();

    }



    @FXML
    private void updateSheet() throws IOException{
        now.setNow(LocalDateTime.now());

        current.update(now.getDayAndTime(), input.getText());
        info.setText("Spreadsheet updated @" + now.getFullDate());
    }

    @FXML
    private void makeChangeSheetWindow() throws IOException{
        Stage newStage = new Stage();
        FXMLLoader load = new FXMLLoader(MainWindow.class.getResource("ChangeSSheetWindow.fxml"));
        load.setController(new ChangeSSheetWindow(current));
        Parent root = load.load();

        newStage.setTitle("Change Spreadsheet");
        newStage.setScene(new Scene(root, 466, 92));
        newStage.setResizable(false);
        newStage.show();

    }

    @FXML
    private void makeOptionsWindow() throws IOException{
        Stage newStage = new Stage();
        newStage.setScene(new Scene(FXMLLoader.load(MainWindow.class.getResource("UI/OptionsWindow.fxml")), 450, 400));

        newStage.setTitle("Options");
        newStage.setResizable(false);
        newStage.show();

    }

    @FXML
    private void openBrowser() throws IOException, URISyntaxException {
        if(!current.getCurrentID().equals("NOT SELECTED") && Desktop.isDesktopSupported()){
            Desktop.getDesktop().browse(new URI("https://docs.google.com/spreadsheets/d/" + current.getCurrentID() + "/edit#gid=0"));
        } else {
            info.setText("Select a spreadsheet first.");
        }
    }



}
