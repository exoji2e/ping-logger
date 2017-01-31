import java.util.*;
import java.io.*;
public class Writer {
    public String path;
    public String date;
    public Writer(String path) {
        this.path = path;
    }
    public synchronized void print(String when, LinkedList<String> what) {
        if(date == null || when.contains("00:00")) {
            date = getDate();
        }
        try(PrintWriter out = 
                new PrintWriter(
                new BufferedWriter(
                new FileWriter(path + date, true)))){

            for(String s: what) {
                out.println("" + when + " " + s);
            }
            out.close();
        } catch (IOException e) {
            System.out.println(e);
        }

    }
    public synchronized void wait4next(long next) {
        long diff;
        try{
        while((diff = next - System.currentTimeMillis()) > 0) 
            wait(diff);
        }catch(Exception e) {}
    }
    public String getDate() {
        Date d = new Date();
        int year = d.getYear() + 1900;
        int month = d.getMonth() + 1;
        int day = d.getDate();
        String yy = "" + year;
        String mm = month<10? "0" + month : "" + month;
        String dd = day<10? "0" + day : "" + day;
        return "" + yy + "_" + mm + "_" + dd + ".log";
    }
}
