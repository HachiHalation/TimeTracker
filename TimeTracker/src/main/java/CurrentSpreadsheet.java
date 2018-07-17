import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.services.sheets.v4.model.Spreadsheet;

import java.io.IOException;

public class CurrentSpreadsheet {

    private String currentID;
    private String folderID;
    private String name;
    private GoogleDrive gd;
    private GoogleSheets gs;


    public CurrentSpreadsheet() throws IOException{
        HttpTransport transport = new NetHttpTransport();
        gs = new GoogleSheets(transport);
        gd = new GoogleDrive(transport);


        folderID = gd.findFolder(gd.getHandler());
        name = "NOT SELECTED";
        currentID = "NOT SELECTED";
    }

    public CurrentSpreadsheet(String currentID, String name, String folderID, GoogleDrive gd, GoogleSheets gs) {
        this.currentID = currentID;
        this.folderID = folderID;
        this.name = name;
        this.gd = gd;
        this.gs = gs;
    }

    public void makeAndSetNew(String title) throws IOException{
        Spreadsheet spread = gs.makeNewSpread(title, gs.getHandler());
        currentID = spread.getSpreadsheetId();
        name = spread.getProperties().getTitle();

        gd.toFolder(folderID, gd.getHandler());
    }

    public void update(String time, String message) throws IOException{
        gs.updateSheet(time, message, currentID, gs.getHandler());
    }

    public String getCurrentID() {
        return currentID;
    }

    public String getFolderID() {
        return folderID;
    }

    public void setCurrentID(String currentID) {
        this.currentID = currentID;
    }

    public void setFolderID(String folderID) {
        this.folderID = folderID;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() { return name; }

    public GoogleDrive getGoogleDrive() {
        return gd;
    }

    public GoogleSheets getGoogleSheets() {
        return gs;
    }
}
