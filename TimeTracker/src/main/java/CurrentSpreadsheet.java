import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.model.Color;
import com.google.api.services.sheets.v4.model.Spreadsheet;

import java.io.IOException;
import java.security.GeneralSecurityException;

public class CurrentSpreadsheet {

    private String currentID;
    private String folderID;
    private String name;
    private GoogleDrive gd;
    private GoogleSheets gs;

    private Color updateColor;
    private Color noteColor;

    public CurrentSpreadsheet() throws IOException, GeneralSecurityException{
        HttpTransport transport = GoogleNetHttpTransport.newTrustedTransport();
        Credential credential = Options.verifyCredentials(transport);
        gs = new GoogleSheets(transport, credential);
        gd = new GoogleDrive(transport, credential);


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


    public void makeAndSetNew(String title) throws IOException{
        Spreadsheet spread = gs.makeNewSpread(title, gs.getHandler(), updateColor, noteColor);
        currentID = spread.getSpreadsheetId();
        name = spread.getProperties().getTitle();
        Options.print("New spreadsheet \"" + name + "\" created.", null);

        gd.toFolder(currentID, folderID, gd.getHandler());
    }

    public void setCurrentIDAndInfo(String ssid) throws IOException{
        setCurrentID(ssid);
        setSSInfo();
        Options.print("Set info under spreadsheet ID" + ssid + ".", null);
    }

    public void update(String time, String message) throws IOException{
        gs.updateSheet(time, message, currentID, gs.getHandler(), false);
    }

    public void note(String message) throws IOException{
        gs.updateSheet("NOTE", message, currentID, gs.getHandler(), true);
    }

    private void setSSInfo() throws IOException{
        if(currentID != null){
            Sheets.Spreadsheets.Get test = gs.getHandler().spreadsheets().get(currentID);
            Spreadsheet current = test.execute();
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
