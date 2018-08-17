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

    private Sheets handler;

    public GoogleSheets(HttpTransport transport, Credential credential) throws IOException{
        handler = makeServiceHandler(transport, credential);
    }


    private Sheets makeServiceHandler(HttpTransport transport, Credential credential) throws IOException{
        Sheets.Builder serviceBuild = new Sheets.Builder(transport, Options.getFACTORY(), credential);
        serviceBuild.setApplicationName(Options.getAppName());
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
