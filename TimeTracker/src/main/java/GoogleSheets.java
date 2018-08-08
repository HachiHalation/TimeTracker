import com.google.api.client.auth.oauth2.Credential;

import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.http.HttpTransport;

import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.SheetsScopes;
import com.google.api.services.sheets.v4.model.*;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.time.Instant;

public class GoogleSheets {

    private String APP_NAME;

    private String CREDENTIAL_FOLDER;
    private String CLIENT_SCERET;
    private JsonFactory FACTORY;
    private List<String> scopes;

    private Sheets handler;

    public GoogleSheets(HttpTransport transport) throws IOException{
        APP_NAME = "Time Tracker";
        CREDENTIAL_FOLDER = "Data/credentialsSheets";
        CLIENT_SCERET = "credentials_drive.json";
        FACTORY = JacksonFactory.getDefaultInstance();
        scopes = Collections.singletonList(SheetsScopes.SPREADSHEETS);

        handler = makeServiceHandler(transport);
    }


    private Sheets makeServiceHandler(HttpTransport transport) throws IOException{
        InputStream in = GoogleSheets.class.getResourceAsStream(CLIENT_SCERET);
        GoogleClientSecrets secret = GoogleClientSecrets.load(FACTORY, new InputStreamReader(in));

        GoogleAuthorizationCodeFlow.Builder flowbuild = new GoogleAuthorizationCodeFlow.Builder(transport, FACTORY, secret, scopes);
        flowbuild.setDataStoreFactory(new FileDataStoreFactory(new java.io.File(CREDENTIAL_FOLDER)));
        flowbuild.setAccessType("offline");
        GoogleAuthorizationCodeFlow flow = flowbuild.build();

        Credential credential = new AuthorizationCodeInstalledApp(flow, new LocalServerReceiver()).authorize("user");
        Sheets.Builder serviceBuild = new Sheets.Builder(transport, FACTORY, credential);
        serviceBuild.setApplicationName(APP_NAME);
        return serviceBuild.build();
    }

    public Sheets getHandler(){
        return handler;
    }


    public Spreadsheet makeNewSpread(String title, Sheets service_handler, Color updateColor, Color noteColor) throws IOException{
        Spreadsheet base = new Spreadsheet();

        SpreadsheetProperties properties = new SpreadsheetProperties();
        properties.setTitle(title);
        Spreadsheet newSpread = base.setProperties(properties);
        //List<Sheet> list = new ArrayList<>();
        //Sheet temp = new Sheet();
        //SheetProperties temp2 = new SheetProperties();
        //temp2.setTitle("LOG #1 [" + Instant.now().toString() + "]");
        //temp.setProperties(temp2);
        //list.add(temp);

        //newSpread.setSheets(list);

        Sheets.Spreadsheets.Create newSheetRequest = service_handler.spreadsheets().create(newSpread);

        Spreadsheet current = newSheetRequest.execute();
        String id = current.getSpreadsheetId();


        BatchUpdateSpreadsheetRequest update = new BatchUpdateSpreadsheetRequest();
        Request requestNote = new Request();

        ConditionalFormatRule noterule = new ConditionalFormatRule();
        noterule.setRanges(Collections.singletonList(new GridRange()
                .setSheetId(0)
                .setStartColumnIndex(0)
                .setEndColumnIndex(2)));

        noterule.setBooleanRule(new BooleanRule()
                .setCondition(new BooleanCondition()
                        .setType("CUSTOM_FORMULA")
                        .setValues(Collections.singletonList(new ConditionValue()
                            .setUserEnteredValue("=REGEXMATCH($A1, \"NOTE\")"))))
                .setFormat(new CellFormat()
                        .setTextFormat(new TextFormat()
                        .setForegroundColor(noteColor))));

        requestNote.setAddConditionalFormatRule(new AddConditionalFormatRuleRequest().setRule(noterule).setIndex(0));

        Request requestUpdate = new Request();
        ConditionalFormatRule updaterule = new ConditionalFormatRule();
        updaterule.setRanges(Collections.singletonList(new GridRange()
                .setSheetId(0)
                .setStartColumnIndex(0)
                .setEndColumnIndex(2)));

        updaterule.setBooleanRule(new BooleanRule()
                .setCondition(new BooleanCondition()
                        .setType("CUSTOM_FORMULA")
                        .setValues(Collections.singletonList(new ConditionValue()
                                .setUserEnteredValue("=NOT(REGEXMATCH($A1, \"NOTE\"))"))))
                .setFormat(new CellFormat()
                        .setTextFormat(new TextFormat()
                                .setForegroundColor(updateColor))));

        requestUpdate.setAddConditionalFormatRule(new AddConditionalFormatRuleRequest().setRule(updaterule).setIndex(1));

        ArrayList<Request> requests = new ArrayList<>();
        requests.add(requestNote);
        requests.add(requestUpdate);
        update.setRequests(requests);

        service_handler.spreadsheets().batchUpdate(id, update).execute();
        return current;
    }

    public void addNewSheet(Spreadsheet spread){
        List<Sheet> sheets = spread.getSheets();
        Sheet newsheet = new Sheet();
        //SheetProperties sp = new SheetProperties();
        //sp.setTitle("LOG #" + sheets.size() + " [" + Instant.now().toString() + "]");
        //newsheet.setProperties(sp);
        sheets.add(newsheet);
    }

    public void updateSheet(String timestamp, String message, String spreadsheet_id, Sheets service_handler, Boolean note) throws IOException{

        ValueRange newVals = new ValueRange();
        newVals.setRange("A1");
        List<Object> input = new ArrayList<>();
        if(note){
            input.add("NOTE");
        }else {
            input.add(timestamp);
        }
        input.add(message);

        List<List<Object>> vals = new ArrayList<>();
        vals.add(input);

        newVals.setValues(vals);

        Sheets.Spreadsheets.Values.Append updateRequest = service_handler.spreadsheets().values().
                append(spreadsheet_id, "A1", newVals);
        updateRequest.setValueInputOption("USER_ENTERED");

        updateRequest.execute();

    }


}
