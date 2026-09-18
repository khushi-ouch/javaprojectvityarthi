package mess.service;

import mess.db.DatabaseManager;
import mess.exception.ItemNotFoundException;
import mess.model.MessItem;

import java.sql.*;
import java.util.LinkedHashMap;
import java.util.Map;

public class InventoryService {

    // cache so we don't hit the db for every single lookup, refreshed on writes.
    // the stock monitor thread reads/rebuilds this concurrently with the main
    // thread, so every access to it needs to be synchronized on this instance.
    private Map<Integer, MessItem> cache = new LinkedHashMap<>();

    public InventoryService() {
        refreshCache();
    }

    public synchronized void refreshCache() {
        Map<Integer, MessItem> fresh = new LinkedHashMap<>();
        String sql = "SELECT * FROM items";
        try (Statement st = DatabaseManager.getConnection().createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                MessItem item = new MessItem(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("category"),
                        rs.getDouble("price"),
                        rs.getInt("quantity"),
                        rs.getInt("low_stock_threshold")
                );
                fresh.put(item.getId(), item);
            }
            cache = fresh; // swap in one go so readers never see a half-built map
        } catch (SQLException e) {
            System.out.println("failed to load inventory: " + e.getMessage());
        }
    }

    public void addItem(String name, String category, double price, int qty, int threshold) {
        String sql = "INSERT INTO items(name, category, price, quantity, low_stock_threshold) VALUES(?,?,?,?,?)";
        try (PreparedStatement ps = DatabaseManager.getConnection().prepareStatement(sql)) {
            ps.setString(1, name);
            ps.setString(2, category);
            ps.setDouble(3, price);
            ps.setInt(4, qty);
            ps.setInt(5, threshold);
            ps.executeUpdate();
            refreshCache();
        } catch (SQLException e) {
            System.out.println("insert failed: " + e.getMessage());
        }
    }

    public void updateStock(int itemId, int newQty) throws ItemNotFoundException {
        if (!cache.containsKey(itemId)) {
            throw new ItemNotFoundException("no item with id " + itemId);
        }
        String sql = "UPDATE items SET quantity = ? WHERE id = ?";
        try (PreparedStatement ps = DatabaseManager.getConnection().prepareStatement(sql)) {
            ps.setInt(1, newQty);
            ps.setInt(2, itemId);
            ps.executeUpdate();
            refreshCache();
        } catch (SQLException e) {
            System.out.println("update failed: " + e.getMessage());
        }
    }

    public void deleteItem(int itemId) throws ItemNotFoundException {
        if (!cache.containsKey(itemId)) {
            throw new ItemNotFoundException("no item with id " + itemId);
        }
        String sql = "DELETE FROM items WHERE id = ?";
        try (PreparedStatement ps = DatabaseManager.getConnection().prepareStatement(sql)) {
            ps.setInt(1, itemId);
            ps.executeUpdate();
            refreshCache();
        } catch (SQLException e) {
            System.out.println("delete failed: " + e.getMessage());
        }
    }

    public synchronized MessItem getItem(int itemId) throws ItemNotFoundException {
        MessItem item = cache.get(itemId);
        if (item == null) throw new ItemNotFoundException("no item with id " + itemId);
        return item;
    }

    // return a snapshot copy so callers can safely iterate it even if the
    // monitor thread swaps the underlying cache in the background
    public synchronized Map<Integer, MessItem> getAllItems() {
        return new LinkedHashMap<>(cache);
    }
}
