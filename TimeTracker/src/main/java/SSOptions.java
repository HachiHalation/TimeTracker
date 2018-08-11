import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ColorPicker;
import javafx.scene.paint.Color;

import java.net.URL;
import java.util.List;
import java.util.Properties;
import java.util.ResourceBundle;

public class SSOptions extends OptionsSubmenu implements Initializable{

    private Properties options;

    @FXML
    private ChoiceBox timestampMenu;
    @FXML
    private ColorPicker updateColorPicker;
    @FXML
    private ColorPicker noteColorPicker;


    public SSOptions(Properties options) {

        super(options);
        this.options = getOptions();

    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Color updateColor = Color.web(options.getProperty("updateColor"));
        Color noteColor = Color.web(options.getProperty("noteColor"));

        updateColorPicker.setValue(updateColor);
        noteColorPicker.setValue(noteColor);

        ObservableList<String> timestamps = FXCollections.observableArrayList();
        List<Options.UpdateTimestampType> temp = Options.getListOfTimestampTypes();

        for(Options.UpdateTimestampType t: temp){
            timestamps.add(Options.timestampToString(t));
        }

        timestampMenu.setItems(timestamps);

        String currentTimestamp = Options.timestampToString(Options.intToTimestamp(Integer.parseInt(options.getProperty("updateTimestampType"))));
        timestampMenu.setValue(currentTimestamp);

        updateColorPicker.setOnAction(event -> changeOption());
        noteColorPicker.setOnAction(event -> changeOption());
        timestampMenu.setOnAction(event -> changeOption());

    }

    @FXML
    private void changeOption(){
        options.setProperty("updateColor", updateColorPicker.getValue().toString());
        options.setProperty("noteColor", noteColorPicker.getValue().toString());
        options.setProperty("updateTimestampType", "" + Options.timestampToInt(Options.stringToTimestamp(timestampMenu.getValue().toString())));

    }
}
