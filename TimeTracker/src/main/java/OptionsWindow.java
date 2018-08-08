import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;
import java.util.ResourceBundle;

public class OptionsWindow implements Initializable{
    @FXML
    private AnchorPane  dynamicPane;
    @FXML
    private Label generalTab;
    @FXML
    private Label appearanceTab;
    @FXML
    private Label spreadsheetTab;

    private Properties  options;
    private Path path;


    public OptionsWindow(Path optionspath){
        path = optionspath;
    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        options = new Properties();
        try {
            options.load(Files.newInputStream(path));
        } catch (IOException e) {
            e.printStackTrace();
        }


        generalTab.setOnMouseClicked(event -> {
            try{
                changeGeneralView();
            } catch (IOException e){
                e.printStackTrace();
            }
        });
        appearanceTab.setOnMouseClicked(event -> {
            try{
                changeAppearanceView();
            } catch (IOException e){
                e.printStackTrace();
            }
        });

        spreadsheetTab.setOnMouseClicked(event -> {
            try{
                changeSSView();
            } catch (IOException e){
                e.printStackTrace();
            }
        });
    }
    @FXML
    private void changeAppearanceView() throws IOException{
        loadResource("UI/AppearanceOptions.fxml", new AppearanceOptions(options));
    }

    @FXML
    private void changeSSView() throws IOException{
        loadResource("UI/SSOptions.fxml", new SSOptions(options));
    }

    @FXML
    private void changeGeneralView() throws IOException{
        loadResource("UI/GeneralOptions", new GeneralOptions(options));
    }

    private void changeView(Node n) throws IOException{
        dynamicPane.getChildren().setAll(n);
    }

    private void loadResource(String URL, OptionsSubmenu controller) throws IOException{
        FXMLLoader loader = new FXMLLoader(OptionsWindow.class.getResource(URL));
        loader.setController(controller);
        changeView(loader.load());
    }


}
