import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.model.Spreadsheet;

import java.io.IOException;

public class TestingClass {

    public static void main(String[] args) throws IOException{
        NetHttpTransport transport = new NetHttpTransport();
        GoogleSheets gs = new GoogleSheets();

        Sheets service_handler = gs.makeServiceHandler(transport);

        Spreadsheet spread = gs.makeNewSpread("Testing Sheet", service_handler);
        System.out.println("New spreadsheet created. ID:" + spread.getSpreadsheetId());

        gs.updateSheet("Hello World!", "test", spread.getSpreadsheetId(), service_handler);

        gs.addNewSheet(spread);
    }
}
