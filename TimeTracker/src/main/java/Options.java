import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.sheets.v4.SheetsScopes;
import javafx.scene.control.Label;


import java.io.*;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;

public class Options {

    private static final Path MAIN_FOLDER = Paths.get("Data");
    private static final Path OPTION_PATH = Paths.get("Data/options.txt");
    private static final Path LOG_PATH = Paths.get("Data/log.txt");
    private static final String CLIENT_SECRET = "credentials.json";
    private static final String CREDENTIAL_FOLDER = "Data/credentials";
    private static final String VERSION = "0.9";
    private static final String APP_NAME = "TimeTracker";
    private static final int OPTION_LENGTH = 4;
    private static JsonFactory FACTORY = JacksonFactory.getDefaultInstance();

    public static Path getMainFolder() {
        return MAIN_FOLDER;
    }

    public static Path getOptionPath() {
        return OPTION_PATH;
    }

    public static Path getLogPath() {
        return LOG_PATH;
    }

    public static String getVERSION() {
        return VERSION;
    }

    public static int getOptionLength() {
        return OPTION_LENGTH;
    }

    public static String getAppName(){ return APP_NAME; }

    public static JsonFactory getFACTORY(){ return FACTORY; }

    public static void print(String s, Label input) throws IOException{
        String str = s + "\n";
        Files.write(LOG_PATH, str.getBytes(), StandardOpenOption.WRITE, StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        if(input != null) input.setText(s);
    }



    public enum UpdateTimestampType{
        FULL, TIMEONLY, NOYEAR, CLEANFULL
    }

    public static List<UpdateTimestampType> getListOfTimestampTypes(){
        ArrayList<UpdateTimestampType> types = new ArrayList<>();
        types.add(UpdateTimestampType.FULL);
        types.add(UpdateTimestampType.CLEANFULL);
        types.add(UpdateTimestampType.NOYEAR);
        types.add(UpdateTimestampType.TIMEONLY);

        return types;
    }

    public static UpdateTimestampType intToTimestamp(int i){
        switch (i){
            case 0: return UpdateTimestampType.FULL;
            case 1: return UpdateTimestampType.CLEANFULL;
            case 2: return UpdateTimestampType.NOYEAR;
            case 3: default: return UpdateTimestampType.TIMEONLY;
        }
    }

    public static int timestampToInt(UpdateTimestampType t){
        switch (t){
            case FULL: return 0;
            case CLEANFULL: return 1;
            case NOYEAR: return 2;
            case TIMEONLY: default: return 3;
        }
    }

    public static UpdateTimestampType stringToTimestamp(String s) throws NullPointerException{
        if(s == null) throw new NullPointerException();

        String type = s.toUpperCase();
        switch (type){
            case "FULL": return UpdateTimestampType.FULL;
            case "CLEAN FULL": return UpdateTimestampType.CLEANFULL;
            case "NO YEAR": return UpdateTimestampType.NOYEAR;
            case "TIME ONLY": default: return UpdateTimestampType.TIMEONLY;
        }
    }

    public static String timestampToString(UpdateTimestampType t){
        switch (t){
            case FULL: return "Full";
            case CLEANFULL: return "Clean Full";
            case NOYEAR: return "No Year";
            case TIMEONLY: return "Time Only";
            default: return "N/A";
        }
    }

    public static Credential verifyCredentials(HttpTransport transport) throws IOException{

        ArrayList<String> scopes = new ArrayList<>();
        scopes.add(DriveScopes.DRIVE);
        scopes.add(SheetsScopes.SPREADSHEETS);

        JsonFactory factory = getFACTORY();
        InputStream in = Options.class.getResourceAsStream(CLIENT_SECRET);
        GoogleClientSecrets secret = GoogleClientSecrets.load(factory, new InputStreamReader(in));

        GoogleAuthorizationCodeFlow.Builder flowbuild = new GoogleAuthorizationCodeFlow.Builder(transport, factory, secret, scopes);
        flowbuild.setDataStoreFactory(new FileDataStoreFactory(new File(CREDENTIAL_FOLDER)));
        flowbuild.setAccessType("offline");
        GoogleAuthorizationCodeFlow flow = flowbuild.build();

        return new AuthorizationCodeInstalledApp(flow, new LocalServerReceiver()).authorize("user");

    }
}
