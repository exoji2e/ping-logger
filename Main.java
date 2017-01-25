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
        int h = d.getHours();
        String hh = h<10?"0"+h:""+h;
        int m = d.getMinutes();
        String mm = m<10?"0"+m:""+m;

        return "" + hh + ":" + mm;
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
