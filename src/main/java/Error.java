import java.util.Date;

public class Error {
    private String date;
    private int times = 1;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getTimes() {
        return times;
    }

    public void setTimes(int times) {
        this.times = times;
    }

    public void increaseTimes(){
        this.times +=1;
    }

    public Error(String date, int times) {
        this.date = date;
        this.times = times;
    }

    public Error(String date) {
        this.date = date;
    }

    @Override
    public String toString() {
        return "Error{" +
                "date='" + date + '\'' +
                ", times=" + times +
                '}';
    }
}
