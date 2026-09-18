package mess.service;

import mess.db.DatabaseManager;
import mess.util.CsvUtil;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ReportService {

    public void generateDailySalesReport() {
        List<String> lines = new ArrayList<>();
        lines.add("order_id,student_name,created_at,total");

        double grandTotal = 0;
        int count = 0;

        String sql = "SELECT * FROM orders ORDER BY id";
        try (Statement st = DatabaseManager.getConnection().createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                double total = rs.getDouble("total");
                lines.add(rs.getInt("id") + "," + rs.getString("student_name") + "," +
                        rs.getString("created_at") + "," + total);
                grandTotal += total;
                count++;
            }
        } catch (SQLException e) {
            System.out.println("report query failed: " + e.getMessage());
            return;
        }

        lines.add("");
        lines.add("total_orders," + count);
        lines.add("total_revenue," + grandTotal);

        String path = "reports/sales_report_" + LocalDate.now() + ".csv";
        CsvUtil.exportSalesReport(path, lines);
        System.out.println("report saved to " + path);
        System.out.println("orders: " + count + "   revenue: Rs." + grandTotal);
    }
}
