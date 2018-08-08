import com.google.api.services.sheets.v4.model.Color;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;

import java.awt.Desktop;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.Properties;
import java.util.Set;
import java.util.stream.Stream;

public class MainWindow {

    private CurrentSpreadsheet current;
    private DateManipulation now;

    private final Path MAIN_FOLDER = Paths.get("Data");
    private final Path OPTION_PATH = Paths.get("Data/options.txt");
    private final String VERSION = "0.7";
    private final int OPTION_LENGTH = 3;

    private Color updateColor = new Color();
    private Color noteColor = new Color();

    private Options.UpdateTimestampType updateType;

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
        loadOptions();

        title.setText("TimeTracker V" + VERSION);

        now = new DateManipulation(LocalDateTime.now());

        current = new CurrentSpreadsheet();
        current.setUpdateColor(updateColor);
        current.setNoteColor(noteColor);

        input.setOnKeyReleased(event -> {
            if(event.getCode() == KeyCode.ENTER){
                try {
                    updateSheet();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        input.setOnKeyPressed(event -> {
            if(event.getCode() == KeyCode.SHIFT){
                input.setOnKeyReleased(event1 -> {
                    if(event1.getCode() == KeyCode.ENTER){
                        try{
                            noteSheet();
                        } catch ( IOException e){
                            e.printStackTrace();
                        }
                    }
                });
            }
        });
    }

    private void loadOptions() throws IOException{
        Properties options = new Properties();

        if(!Files.exists(OPTION_PATH)){
            makeOptions(options);
        }

        options.load(Files.newInputStream(OPTION_PATH));
        Set<String> keys = options.stringPropertyNames();

        if(keys.size() != OPTION_LENGTH){
            Files.deleteIfExists(OPTION_PATH);
            makeOptions(options);
        }

        loadTimestampType(options);
        loadTextColors(options);
    }

    private void loadTimestampType(Properties options){
        String temp = options.getProperty("updateTimestampType");
        updateType = Options.intToTimestamp(Integer.parseInt(temp));

    }


    private void loadTextColors(Properties options){
        String temp = options.getProperty("updateColor");
        javafx.scene.paint.Color tempupdate = javafx.scene.paint.Color.web(temp);

        System.out.println(tempupdate.toString());
        double blue = 1.0;
        float b = (float) blue;
        updateColor.setBlue((float) tempupdate.getBlue());
        updateColor.setRed((float) tempupdate.getRed());
        updateColor.setGreen((float) tempupdate.getGreen());
        updateColor.setAlpha((float) 1);

        temp = options.getProperty("noteColor");
        javafx.scene.paint.Color tempnote = javafx.scene.paint.Color.web(temp);

        System.out.println(tempnote.toString());
        noteColor.setBlue((float) tempnote.getBlue());
        noteColor.setGreen((float) tempnote.getGreen());
        noteColor.setRed((float) tempnote.getRed());
        noteColor.setAlpha((float) 1);
    }

    private void makeOptions(Properties options) throws IOException{
        Files.createFile(OPTION_PATH);
        options.setProperty("updateTimestampType", "0");
        options.setProperty("updateColor", "0x000000");
        options.setProperty("noteColor", "0xFF0000");
        OutputStream file = Files.newOutputStream(OPTION_PATH);
        options.store(file, "TimeTracker Options");
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
        input.setText("");
    }

    @FXML
    private void noteSheet() throws IOException{
        now.setNow(LocalDateTime.now());

        current.note(input.getText());
        info.setText("Spreadsheet noted @" + now.getFullDate());
        input.setText("");
    }

    @FXML
    private void makeChangeSheetWindow() throws IOException{
        Stage newStage = new Stage();
        FXMLLoader load = new FXMLLoader(MainWindow.class.getResource("UI/ChangeSSheetWindow.fxml"));
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
        FXMLLoader load = new FXMLLoader(MainWindow.class.getResource("UI/OptionsWindow.fxml"));
        load.setController(new OptionsWindow(OPTION_PATH));
        Parent root = load.load();


        newStage.setTitle("Options");
        newStage.setScene(new Scene(root, 450, 400));
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
