import java.util.*;
import java.io.*;
public class Main {
    public static void main(String[] args) {
        String path = (args.length==0)?"log/":args[0];
        long t = System.currentTimeMillis();
        int sleft = sleft();
        String time = hhmm();
        long next = t - t%1000 + sleft*1000;
        Writer w = new Writer(path);
        while(true) {
            Thread thread = new PingThread(time, sleft, w);
            thread.start();
            w.wait4next(next);
            time = hhmm();
            next += 60*1000;
            sleft = 60;
        }
    }
    public static int sleft() {
        Date d = new Date();
        return 60 - d.getSeconds();
    }
    public static String hhmm() {
        Date d = new Date();
        int year = d.getYear() + 1900;
        int month = d.getMonth() + 1;
        int day = d.getDate();
        String yy = "" + year;
        String mm = month<10? "0" + month : "" + month;
        String dd = day<10? "0" + day : "" + day;
        int h = d.getHours();
        String hh = h<10?"0"+h:""+h;
        int m = d.getMinutes();
        String min = m<10?"0"+m:""+m;

        return yy + "_" + mm + "_" + dd + "_" + hh + ":" + min;
    }
}
class PingThread extends Thread {
    String time;
    int count;
    Writer w;
    public PingThread(String time, int count, Writer w) {
        this.time = time; this.count = count; this.w = w;
    }
    public void run() {
        try{
        Process p = Runtime.getRuntime().exec("ping -c" + count + " 8.8.8.8");
        
        p.waitFor();
        BufferedReader b = new BufferedReader(new InputStreamReader(p.getInputStream()));
        String line = "";
        LinkedList<String> toPrint = new LinkedList<>();
        while((line = b.readLine()) != null) {
            if(!line.contains("icmp_seq")) continue;
            if((line.substring(line.length()-2)).equals("ms")) {
                String[] a = line.split("=");
                String resp = a[a.length-1];
                System.out.println(time + " " + resp.substring(0,resp.length() - 3));
                toPrint.addLast(resp.substring(0,resp.length() - 3));
            }else if(line.contains("timeout")) {
                System.out.println(time + " -");
                toPrint.addLast("-");
            }
        }
        b.close();
        w.print(time, toPrint);
        }catch(Exception e) {}
    }
}
