import java.util.ArrayList;
import java.util.List;

public class Options {
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
