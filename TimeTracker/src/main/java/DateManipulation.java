import java.time.LocalDateTime;

public class DateManipulation {
    private String fullDate;
    private int year, month, day, hour, minute, second;

    public DateManipulation(LocalDateTime now){
        setNow(now);
    }

    public void setNow(LocalDateTime now){
        fullDate = now.toString();

        year = now.getYear();
        month = now.getMonthValue();
        day = now.getDayOfMonth();

        hour = now.getHour();
        minute = now.getMinute();
        second = now.getSecond();
    }

    public String getTime(){
        return hour + ":" + minute + ":" + second;
    }

    public String getDate(){
        return year + "-" + month + "-" + day;
    }

    public String getDayAndTime(){
        return month + "" + day + " " + hour + ":" + minute;
    }

    public String getFullDate(){
        return fullDate;
    }

    public String getSimpleFullDate(){
        return getDate() + " " + getTime();
    }
}
