package mess.util;

import mess.model.MessItem;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class CsvUtil {

    // reads a seed csv like: name,category,price,qty,threshold
    public static List<String[]> readCsv(String path) throws IOException {
        List<String[]> rows = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(path))) {
            String line;
            boolean first = true;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                if (first) { // skip header row
                    first = false;
                    continue;
                }
                rows.add(line.split(","));
            }
        }
        return rows;
    }

    public static void exportSalesReport(String path, List<String> lines) {
        File f = new File(path);
        f.getParentFile().mkdirs();
        try (PrintWriter pw = new PrintWriter(new FileWriter(f))) {
            for (String l : lines) {
                pw.println(l);
            }
        } catch (IOException e) {
            System.out.println("could not write report: " + e.getMessage());
        }
    }

    public static void exportInventory(String path, List<MessItem> items) {
        File f = new File(path);
        f.getParentFile().mkdirs();
        try (PrintWriter pw = new PrintWriter(new FileWriter(f))) {
            pw.println("id,name,category,price,quantity,threshold");
            for (MessItem it : items) {
                pw.println(it.getId() + "," + it.getName() + "," + it.getCategory() + "," +
                        it.getPrice() + "," + it.getQuantity() + "," + it.getLowStockThreshold());
            }
        } catch (IOException e) {
            System.out.println("could not export inventory: " + e.getMessage());
        }
    }
}
