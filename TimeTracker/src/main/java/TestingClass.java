import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.model.Spreadsheet;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;

import static java.time.temporal.ChronoField.DAY_OF_WEEK;
import static java.time.temporal.ChronoField.DAY_OF_YEAR;

public class TestingClass {

    public static void main(String[] args) throws IOException{
        LocalDateTime time = LocalDateTime.now();
        String[] split = time.toString().split("T");
        System.out.println(split[0]);

    }
}
