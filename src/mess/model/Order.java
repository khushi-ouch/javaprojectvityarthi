package mess.model;

import java.util.ArrayList;
import java.util.List;

public class Order {

    private int orderId;
    private String studentName;
    private String timestamp;
    private List<OrderItem> items = new ArrayList<>();
    private double total;

    public Order(int orderId, String studentName, String timestamp) {
        this.orderId = orderId;
        this.studentName = studentName;
        this.timestamp = timestamp;
    }

    public void addItem(OrderItem item) {
        items.add(item);
        total += item.lineTotal();
    }

    public int getOrderId() { return orderId; }
    public void setOrderId(int orderId) { this.orderId = orderId; }
    public String getStudentName() { return studentName; }
    public String getTimestamp() { return timestamp; }
    public List<OrderItem> getItems() { return items; }
    public double getTotal() { return total; }

    public void setTotal(double total) {
        this.total = total;
    }
}
