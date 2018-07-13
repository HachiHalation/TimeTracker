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

import java.io.IOException;
import java.time.LocalDateTime;

public class MainWindow {
    private GoogleSheets gs;
    private Sheets sheetsHandler;
    private GoogleDrive gd;
    private Drive driveHandler;
    private CurrentSpreadsheet current;

    @FXML
    private Button update;
    @FXML
    private Button create;
    @FXML
    private Label info;
    @FXML
    private TextField input;

    public Sheets getSheetsHandler(){
        return sheetsHandler;
    }

    public Drive getDriveHandler() {
        return driveHandler;
    }


    @FXML
    private void initialize() throws IOException{
        HttpTransport transport = new NetHttpTransport();
        gs = new GoogleSheets();
        gd = new GoogleDrive();
        sheetsHandler = gs.makeServiceHandler(transport);
        driveHandler = gd.makeServiceHandler(transport);

        String folderId = gd.findFolder(driveHandler);
        current = new CurrentSpreadsheet(null, folderId, null);
    }

    @FXML
    private void makeSheet() throws IOException{
        LocalDateTime time = LocalDateTime.now();
        String[] split = time.toString().split("T");


        Spreadsheet spread = gs.makeNewSpread("TimeTracker#" + split[0], sheetsHandler);
        gd.toFolder(current.getFolderID(), driveHandler);

        current.setCurrentID(spread.getSpreadsheetId());
        current.setName(spread.getProperties().getTitle());
        info.setText("New spreadsheet made (ID:" + spread.getSpreadsheetId() + ")");

        String prevName = current.getName();

        Stage newStage = new Stage();
        FXMLLoader load = new FXMLLoader(MainWindow.class.getResource("NewSSheetWindow.fxml"));
        load.setController(new NewSSheetWindow(current));
        Parent root = load.load();

        newStage.setTitle("Set Title");
        newStage.setScene(new Scene(root, 435, 37));
        newStage.setResizable(false);
        newStage.showAndWait();

        if(!prevName.equals(current.getName())){
            File temp = new File();
            temp.setName(current.getName());
            driveHandler.files().update(current.getCurrentID(), temp).execute();
            info.setText("Title changed to " + current.getName());
        }
    }

    @FXML
    private void updateSheet() throws IOException{
       LocalDateTime currentTime = LocalDateTime.now();

        gs.updateSheet(input.getText(), currentTime.toString(), current.getCurrentID(), sheetsHandler);
        info.setText("Spreadsheet updated @" + currentTime.toString());
    }

    @FXML
    private void makeChangeSheetWindow() throws IOException{
        Stage newStage = new Stage();
        FXMLLoader load = new FXMLLoader(MainWindow.class.getResource("ChangeSSheetWindow.fxml"));
        load.setController(new ChangeSSheetWindow(driveHandler, current));
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

}
