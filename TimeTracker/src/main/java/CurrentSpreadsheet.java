import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.services.sheets.v4.model.Color;
import com.google.api.services.sheets.v4.model.Spreadsheet;

import java.io.IOException;

public class CurrentSpreadsheet {

    private String currentID;
    private String folderID;
    private String name;
    private GoogleDrive gd;
    private GoogleSheets gs;

    private Color updateColor;
    private Color noteColor;

    public CurrentSpreadsheet() throws IOException{
        HttpTransport transport = new NetHttpTransport();
        gs = new GoogleSheets(transport);
        gd = new GoogleDrive(transport);


        folderID = gd.findFolder(gd.getHandler());
        name = "NOT SELECTED";
        currentID = "NOT SELECTED";

        float defblue = 1;
        float defgreen = 1;
        float defred = 1;
        float alpha = 1;

        updateColor = new Color();
        updateColor.setBlue(defblue);
        updateColor.setGreen(defgreen);
        updateColor.setRed(defred);
        updateColor.setAlpha(alpha);

        defblue = 0;
        defgreen = 0;

        noteColor = new Color();
        noteColor.setRed(defred);
        noteColor.setGreen(defgreen);
        noteColor.setBlue(defblue);
    }

    public CurrentSpreadsheet(String currentID, String name, String folderID, GoogleDrive gd, GoogleSheets gs) {
        this.currentID = currentID;
        this.folderID = folderID;
        this.name = name;
        this.gd = gd;
        this.gs = gs;
    }

    public void makeAndSetNew(String title) throws IOException{
        Spreadsheet spread = gs.makeNewSpread(title, gs.getHandler(), updateColor, noteColor);
        currentID = spread.getSpreadsheetId();
        name = spread.getProperties().getTitle();


        gd.toFolder(currentID, folderID, gd.getHandler());
    }

    public void setCurrentIDAndInfo(String ssid) throws IOException{
        setCurrentID(ssid);
        setSSInfo();
    }

    public void update(String time, String message) throws IOException{
        gs.updateSheet(time, message, currentID, gs.getHandler(), false);
    }

    public void note(String message) throws IOException{
        gs.updateSheet("NOTE", message, currentID, gs.getHandler(), true);
    }

    private void setSSInfo() throws IOException{
        if(currentID != null){
            Spreadsheet current = gs.getHandler().spreadsheets().get(currentID).execute();
            name = current.getProperties().getTitle();
            folderID = gd.findFolder(gd.getHandler());
        }
    }

    public String getCurrentID() {
        return currentID;
    }

    public String getFolderID() {
        return folderID;
    }

    public void setCurrentID(String currentID){
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

    public void setUpdateColor(Color updateColor) {
        this.updateColor = updateColor;
    }

    public void setNoteColor(Color noteColor) {
        this.noteColor = noteColor;
    }
}
