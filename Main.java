import java.util.*;
import java.io.*;
public class Main {
    public static void main(String[] args) {
        String path = (args.length==0)?"log":args[0];
        long t = System.currentTimeMillis();
        int sleft = sleft();
        String time = hhmm();
        long next = t - t%1000 + sleft*1000;
        while(true) {
            Thread thread = new PingThread(time, sleft);
            thread.start();
            wait4time(next);
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
        return "" + d.getHours() + "-" + d.getMinutes();
    }
    public static void wait4time(long next) {
        long sleep;
        while((sleep = next - System.currentTimeMillis()) > 0)
            try{
                Thread.sleep(sleep);
            }catch(Exception e){}
    }
}
class PingThread extends Thread {
    String time;
    int count;
    public PingThread(String time, int count) {
        this.time = time; this.count = count;
    }
    public void run() {
        try{
        Process p = Runtime.getRuntime().exec("ping -c" + count + " 8.8.8.8");
        
        p.waitFor();
        BufferedReader b = new BufferedReader(new InputStreamReader(p.getInputStream()));
        String line = "";
        while((line = b.readLine()) != null) {
            if(!line.contains("icmp_seq")) continue;
            if((line.substring(line.length()-2)).equals("ms")) {
                String[] a = line.split("=");
                String resp = a[a.length-1];
                System.out.println(time + " " + resp.substring(0,resp.length() - 3));
            }else if(line.contains("timeout")) {
                System.out.println(time + " -");
            }
        }
        b.close();
        }catch(Exception e) {}
    }
}
