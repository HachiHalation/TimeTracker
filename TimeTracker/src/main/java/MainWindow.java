import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.services.drive.Drive;
import com.google.api.services.sheets.v4.Sheets;

import com.google.api.services.sheets.v4.model.Spreadsheet;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.io.IOException;
import java.time.Instant;

public class MainWindow {
    private GoogleSheets gs;
    private Sheets sheetsHandler;
    private GoogleDrive gd;
    private Drive driveHandler;
    private Spreadsheet spread;
    private String folderId;

    @FXML
    Button update;
    @FXML
    Button create;
    @FXML
    Label info;
    @FXML
    TextField input;

    @FXML
    private void initialize() throws IOException{
        HttpTransport transport = new NetHttpTransport();
        gs = new GoogleSheets();
        gd = new GoogleDrive();
        sheetsHandler = gs.makeServiceHandler(transport);
        driveHandler = gd.makeServiceHandler(transport);

        folderId = gd.findFolder(driveHandler);
    }

    @FXML
    private void makeSheet() throws IOException{
        Instant currentTime = Instant.now();

        spread = gs.makeNewSpread("TimeTracker#" + currentTime, sheetsHandler);
        gd.toFolder(folderId, driveHandler);
        info.setText("New spreadsheet made (ID:" + spread.getSpreadsheetId() + ")");
        System.out.println("New ID:" + spread.getSpreadsheetId());
    }

    @FXML
    private void updateSheet() throws IOException{
        Instant currentTime = Instant.now();

        gs.updateSheet(input.getText(), currentTime.toString(), spread.getSpreadsheetId(), sheetsHandler);
        info.setText("Spreadsheet updated @" + currentTime.toString());
    }

}
