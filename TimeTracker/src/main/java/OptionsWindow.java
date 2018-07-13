import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;

public class OptionsWindow {

    @FXML
    private AnchorPane  dynamicPane;

    @FXML
    private void changeAppearanceView() throws IOException{
        loadResource("UI/AppearanceOptions.fxml");
    }

    private void changeView(Node n) throws IOException{
        dynamicPane.getChildren().setAll(n);
    }

    private void loadResource(String URL) throws IOException{
        changeView(FXMLLoader.load(OptionsWindow.class.getResource(URL)));
    }


}
