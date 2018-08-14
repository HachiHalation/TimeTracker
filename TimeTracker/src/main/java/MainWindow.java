import com.google.api.services.sheets.v4.model.Color;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.stage.*;

import java.awt.Desktop;
import java.io.*;
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
        title.setText("TimeTracker V" + Options.getVERSION());

        loadOptions();
        loadHotkeys();

        now = new DateManipulation(LocalDateTime.now());
    }

    private void loadHotkeys(){
        input.setOnKeyReleased(event -> {
            if(event.getCode() == KeyCode.ENTER && !event.isShiftDown()){
                try {
                    updateSheet();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        input.setOnKeyPressed(event -> {
            if(event.getCode() == KeyCode.ENTER && event.isShiftDown()){
                try{
                    noteSheet();
                } catch (IOException e){
                    e.printStackTrace();
                }
            }
        });
    }

    public void loadOptions() throws IOException{
        Properties options = new Properties();

        if(!Files.exists(Options.getOptionPath())){
            print("Config file not found. Creating new config file.");
            makeOptions(options);
        }

        options.load(Files.newInputStream(Options.getOptionPath()));
        Set<String> keys = options.stringPropertyNames();

        if(keys.size() != Options.getOptionLength()){
            print("Outdated config file detected. Resetting config file.");
            System.out.println(keys.size());
            makeOptions(options);
        }

        current = new CurrentSpreadsheet();

        loadTimestampType(options);
        print("Loaded timestamp type.");
        loadTextColors(options, current);
        print("Loaded text colors.");
        if(loadInitialSpreadsheet(options, current)) print("Loaded spreadsheet \"" + current.getName() +"\".");
    }

    private void loadTimestampType(Properties options) throws IOException{
        String temp = options.getProperty("updateTimestampType");
        try{
            Integer.parseInt(temp);
        } catch (NumberFormatException e) {
            print("Illegal config detected. Resetting to default config.");
            makeOptions(options);
        }
        updateType = Options.intToTimestamp(Integer.parseInt(temp));
    }


    private void loadTextColors(Properties options, CurrentSpreadsheet current){
        String temp = options.getProperty("updateColor");
        javafx.scene.paint.Color tempupdate = javafx.scene.paint.Color.web(temp);

        Color updateColor = new Color();
        Color noteColor = new Color();

        updateColor.setBlue((float) tempupdate.getBlue());
        updateColor.setRed((float) tempupdate.getRed());
        updateColor.setGreen((float) tempupdate.getGreen());
        updateColor.setAlpha((float) 1);

        temp = options.getProperty("noteColor");
        javafx.scene.paint.Color tempnote = javafx.scene.paint.Color.web(temp);

        noteColor.setBlue((float) tempnote.getBlue());
        noteColor.setGreen((float) tempnote.getGreen());
        noteColor.setRed((float) tempnote.getRed());
        noteColor.setAlpha((float) 1);

        current.setUpdateColor(updateColor);
        current.setNoteColor(noteColor);
    }

    private boolean loadInitialSpreadsheet(Properties options, CurrentSpreadsheet current) throws IOException{
        String id = options.getProperty("spreadsheetID");
        if(!id.equals("null")){
            current.setCurrentIDAndInfo(id);
            return true;
        }
        return false;
    }



    private void makeOptions(Properties options) throws IOException{
        Files.deleteIfExists(Options.getOptionPath());
        Files.createFile(Options.getOptionPath());
        options.setProperty("updateTimestampType", "0");
        options.setProperty("updateColor", "0x000000");
        options.setProperty("noteColor", "0xFF0000");
        options.setProperty("spreadsheetID", "null");
        OutputStream file = Files.newOutputStream(Options.getOptionPath());
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
        print("Spreadsheet updated @" + now.getFullDate());
        input.setText("");
    }

    @FXML
    private void noteSheet() throws IOException{
        now.setNow(LocalDateTime.now());

        current.note(input.getText());
        print("Spreadsheet noted @" + now.getFullDate());
        input.setText("");
    }

    @FXML
    private void makeChangeSheetWindow() throws IOException{
        Stage newStage = new Stage();
        newStage.initOwner(title.getScene().getWindow());
        newStage.initModality(Modality.WINDOW_MODAL);


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
        newStage.initOwner(title.getScene().getWindow());
        newStage.initModality(Modality.WINDOW_MODAL);
        FXMLLoader load = new FXMLLoader(MainWindow.class.getResource("UI/OptionsWindow.fxml"));
        load.setController(new OptionsWindow(this));
        Parent root = load.load();


        newStage.setTitle("Options");
        newStage.setScene(new Scene(root, 420, 500));
        newStage.setResizable(false);
        newStage.show();

    }

    @FXML
    private void openBrowser() throws IOException, URISyntaxException {
        if(!current.getCurrentID().equals("NOT SELECTED") && Desktop.isDesktopSupported()){
            Desktop.getDesktop().browse(new URI("https://docs.google.com/spreadsheets/d/" + current.getCurrentID() + "/edit#gid=0"));
            print("Opened spreadsheet in browser window.");
        } else {
            print("Select a spreadsheet first.");
        }
    }

    private void print(String s) throws IOException{
        Options.print(s, info);
    }



}
