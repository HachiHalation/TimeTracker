import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class ChangeSSheetWindow implements Initializable{

    private Drive driveHandler;
    private CurrentSpreadsheet currentSpread;
    private ObservableList<String> spreadNames;
    private HashMap<String, String> nameToID;

    private Path OPTION_PATH;

    @FXML
    private ChoiceBox list;
    @FXML
    private Button apply;
    @FXML
    private Button applyandreturn;
    @FXML
    private Button close;
    @FXML
    private Label current;

    public ChangeSSheetWindow(CurrentSpreadsheet current) throws IOException{
        this.driveHandler = current.getGoogleDrive().getHandler();
        this.currentSpread = current;
        OPTION_PATH = Options.getOptionPath();

        nameToID = new HashMap<>();


        List<File> files = getFiles();
        spreadNames = FXCollections.observableArrayList();

        for(File f: files){
            if(f.getMimeType().equals("application/vnd.google-apps.spreadsheet")){
                spreadNames.add(f.getName());
                nameToID.put(f.getName(), f.getId());
            }
        }


    }

    private List<File> getFiles() throws IOException{
        Drive.Files.List request = driveHandler.files().list().setQ("'" + currentSpread.getFolderID() + "' in parents");
        return request.execute().getFiles();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        list.setItems(spreadNames);
        if(currentSpread.getCurrentID() != null) current.setText("Currently Selected: " + currentSpread.getName());

        apply.setOnAction(event -> setCurrentSpread());
        close.setOnAction(event -> exit());
        applyandreturn.setOnAction(event -> {
            try{
                saveandexit();
            } catch (IOException e){
                e.printStackTrace();
            }
        });

    }

    @FXML
    private void setCurrentSpread(){
        String newName = (String) list.getValue();
        String newID = nameToID.get(newName);
        currentSpread.setCurrentID(newID);
        currentSpread.setName(newName);
        current.setText("Currently Selected: " + newName);
    }

    @FXML
    private void exit(){
        Stage temp = (Stage) close.getScene().getWindow();
        temp.close();
    }

    @FXML
    private void saveandexit() throws IOException{
        setCurrentSpread();
        saveSpreadsheet(new Properties());
        exit();
    }

    private void saveSpreadsheet(Properties options) throws IOException{
        options.load(Files.newInputStream(OPTION_PATH));
        options.setProperty("spreadsheetID", currentSpread.getCurrentID());
        options.store(Files.newOutputStream(OPTION_PATH), "Timetracker Options");

    }



}
