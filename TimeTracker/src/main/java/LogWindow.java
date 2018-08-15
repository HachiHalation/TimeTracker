import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;


import java.io.IOException;
import java.nio.file.Files;
import java.util.Scanner;

public class LogWindow {
    @FXML private VBox container;

    @FXML
    private void initialize() throws IOException{
        Scanner s = new Scanner(Files.newInputStream(Options.getLogPath()));
        ObservableList<Node> list = container.getChildren();
        list.add(new Label());
        while(s.hasNextLine()){
            Label l = new Label();
            l.setFont(Font.font("Segoe UI"));
            String str = s.nextLine();
           // System.out.println(str);
            l.setText(str);
            list.add(l);
        }
    }
}
