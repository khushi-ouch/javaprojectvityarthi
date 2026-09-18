package mess.util;

import java.io.*;
import java.time.LocalDateTime;

public class AppLogger {

    private static final String LOG_FILE = "logs/alerts.log";

    public static synchronized void log(String msg) {
        File f = new File(LOG_FILE);
        f.getParentFile().mkdirs();
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(f, true))) {
            bw.write("[" + LocalDateTime.now() + "] " + msg);
            bw.newLine();
        } catch (IOException e) {
            System.out.println("logging failed: " + e.getMessage());
        }
    }
}
