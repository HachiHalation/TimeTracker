import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import sun.nio.cs.US_ASCII;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;

public class Options {

    private static final Path MAIN_FOLDER = Paths.get("Data");
    private static final Path OPTION_PATH = Paths.get("Data/options.txt");
    private static final Path LOG_PATH = Paths.get("Data/log.txt");
    private static final String VERSION = "0.9";
    private static final int OPTION_LENGTH = 4;

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

    public static void print(String s, Label input) throws IOException{
        Files.write(LOG_PATH, s.getBytes());
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
}
