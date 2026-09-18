package mess.service;

import mess.db.DatabaseManager;
import mess.exception.InsufficientStockException;
import mess.exception.InvalidQuantityException;
import mess.exception.ItemNotFoundException;
import mess.model.MessItem;
import mess.model.Order;
import mess.model.OrderItem;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class BillingService {

    private InventoryService inventoryService;

    public BillingService(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    public Order placeOrder(String studentName, Map<Integer, Integer> cart)
            throws InvalidQuantityException, InsufficientStockException, ItemNotFoundException {

        if (cart == null || cart.isEmpty()) {
            throw new InvalidQuantityException("cart is empty, nothing to order");
        }

        // validate everything up front before touching the db
        for (Map.Entry<Integer, Integer> entry : cart.entrySet()) {
            int itemId = entry.getKey();
            int qty = entry.getValue();

            if (qty <= 0) {
                throw new InvalidQuantityException("quantity must be positive for item id " + itemId);
            }

            MessItem item = inventoryService.getItem(itemId);
            if (item.getQuantity() < qty) {
                throw new InsufficientStockException(
                        "not enough stock for " + item.getName() + " (have " + item.getQuantity() + ", asked " + qty + ")");
            }
        }

        String timestamp = LocalDateTime.now().toString();
        Order order = new Order(0, studentName, timestamp);

        List<OrderItem> lineItems = new ArrayList<>();
        double total = 0;

        for (Map.Entry<Integer, Integer> entry : cart.entrySet()) {
            MessItem item = inventoryService.getItem(entry.getKey());
            int qty = entry.getValue();
            double lineAmount = item.applyDiscount(item.getPrice() * qty);
            OrderItem oi = new OrderItem(item.getId(), item.getName(), qty, lineAmount / qty);
            order.addItem(oi);
            lineItems.add(oi);
            total += lineAmount;
        }
        order.setTotal(total);

        // now actually commit: insert order, order_items, and reduce stock
        String insertOrder = "INSERT INTO orders(student_name, created_at, total) VALUES(?,?,?)";
        try (PreparedStatement ps = DatabaseManager.getConnection().prepareStatement(insertOrder, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, studentName);
            ps.setString(2, timestamp);
            ps.setDouble(3, total);
            ps.executeUpdate();

            ResultSet keys = ps.getGeneratedKeys();
            int orderId = 0;
            if (keys.next()) {
                orderId = keys.getInt(1);
            }
            order.setOrderId(orderId);

            String insertLine = "INSERT INTO order_items(order_id, item_id, item_name, qty, price_each) VALUES(?,?,?,?,?)";
            try (PreparedStatement lineps = DatabaseManager.getConnection().prepareStatement(insertLine)) {
                for (OrderItem oi : lineItems) {
                    lineps.setInt(1, orderId);
                    lineps.setInt(2, oi.getItemId());
                    lineps.setString(3, oi.getItemName());
                    lineps.setInt(4, oi.getQty());
                    lineps.setDouble(5, oi.getPriceEach());
                    lineps.executeUpdate();
                }
            }

            // reduce stock for real now that the order is saved
            for (Map.Entry<Integer, Integer> entry : cart.entrySet()) {
                MessItem item = inventoryService.getItem(entry.getKey());
                inventoryService.updateStock(item.getId(), item.getQuantity() - entry.getValue());
            }

        } catch (SQLException e) {
            System.out.println("order could not be saved: " + e.getMessage());
        }

        return order;
    }

    public List<Order> getOrderHistory(String studentName) {
        List<Order> result = new ArrayList<>();
        String sql = "SELECT * FROM orders WHERE student_name = ? ORDER BY id DESC";
        try (PreparedStatement ps = DatabaseManager.getConnection().prepareStatement(sql)) {
            ps.setString(1, studentName);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Order o = new Order(rs.getInt("id"), rs.getString("student_name"), rs.getString("created_at"));
                o.setTotal(rs.getDouble("total"));
                loadOrderItems(o);
                result.add(o);
            }
        } catch (SQLException e) {
            System.out.println("could not fetch history: " + e.getMessage());
        }
        return result;
    }

    private void loadOrderItems(Order order) throws SQLException {
        String sql = "SELECT * FROM order_items WHERE order_id = ?";
        try (PreparedStatement ps = DatabaseManager.getConnection().prepareStatement(sql)) {
            ps.setInt(1, order.getOrderId());
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                order.getItems().add(new OrderItem(
                        rs.getInt("item_id"),
                        rs.getString("item_name"),
                        rs.getInt("qty"),
                        rs.getDouble("price_each")
                ));
            }
        }
    }
}
