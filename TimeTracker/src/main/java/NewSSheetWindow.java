import com.google.api.services.drive.Drive;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;


import java.net.URL;
import java.util.ResourceBundle;

public class NewSSheetWindow implements Initializable{
    private CurrentSpreadsheet current;

    public NewSSheetWindow(CurrentSpreadsheet current) {
        this.current = current;
    }


    @FXML
    private TextField title;
    @FXML
    private Button saveandexit;
    @FXML
    private Button exit;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        title.setText(current.getName());
        saveandexit.setOnAction(event -> setTitleAndClose());
        exit.setOnAction(event -> closeWindow());
    }

    @FXML
    private void setTitleAndClose(){
        current.setName(title.getText());
        closeWindow();

    }

    @FXML
    private void closeWindow(){
        Stage temp = (Stage) exit.getScene().getWindow();
        temp.close();
    }
}
