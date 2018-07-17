import com.google.api.services.sheets.v4.model.Spreadsheet;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;


import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class NewSSheetWindow implements Initializable{
    private CurrentSpreadsheet current;
    private StringBuilder date;
    private GoogleSheets gs;
    private GoogleDrive gd;

    public NewSSheetWindow(CurrentSpreadsheet current, StringBuilder date) {
        this.current = current;
        this.date = date;
        this.gs = current.getGoogleSheets();
        this.gd = current.getGoogleDrive();
    }


    @FXML
    private TextField title;
    @FXML
    private Button saveandexit;
    @FXML
    private Button exit;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        title.setText(date.toString());
        saveandexit.setOnAction(event -> {
            try {
                setTitleAndClose();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        exit.setOnAction(event -> closeWindow());
    }

    @FXML
    private void setTitleAndClose() throws IOException{
        date.replace(0, date.length(), title.getText());
        current.makeAndSetNew(title.getText());

        closeWindow();

    }

    @FXML
    private void closeWindow(){
        Stage temp = (Stage) exit.getScene().getWindow();
        temp.close();
    }


}
