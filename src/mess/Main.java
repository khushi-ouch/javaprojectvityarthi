package mess;

import mess.db.DatabaseManager;
import mess.exception.InsufficientStockException;
import mess.exception.InvalidQuantityException;
import mess.exception.ItemNotFoundException;
import mess.model.MessItem;
import mess.model.Order;
import mess.model.OrderItem;
import mess.service.BillingService;
import mess.service.InventoryService;
import mess.service.ReportService;
import mess.thread.StockMonitor;
import mess.util.CsvUtil;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class Main {

    static Scanner sc = new Scanner(System.in);
    static InventoryService inventoryService;
    static BillingService billingService;
    static ReportService reportService = new ReportService();

    public static void main(String[] args) {
        // schema has to exist before InventoryService's constructor tries to
        // query the items table, so this has to run first
        DatabaseManager.initSchema();
        inventoryService = new InventoryService();
        billingService = new BillingService(inventoryService);

        seedIfEmpty();

        StockMonitor monitor = new StockMonitor(inventoryService, 20);
        monitor.start();

        System.out.println("=== Hostel Mess Inventory & Billing System ===");

        boolean exit = false;
        while (!exit) {
            System.out.println("\n1. Admin login");
            System.out.println("2. Student login");
            System.out.println("3. Exit");
            System.out.print("choice: ");
            String choice = sc.nextLine().trim();

            switch (choice) {
                case "1":
                    adminMenu();
                    break;
                case "2":
                    studentMenu();
                    break;
                case "3":
                    exit = true;
                    break;
                default:
                    System.out.println("invalid option");
            }
        }

        monitor.stopMonitoring();
        DatabaseManager.close();
        System.out.println("bye");
    }

    static void seedIfEmpty() {
        if (!inventoryService.getAllItems().isEmpty()) return;

        try {
            List<String[]> rows = CsvUtil.readCsv("data/inventory_seed.csv");
            for (String[] r : rows) {
                inventoryService.addItem(r[0], r[1], Double.parseDouble(r[2]), Integer.parseInt(r[3]), Integer.parseInt(r[4]));
            }
            System.out.println("inventory seeded from csv (" + rows.size() + " items)");
        } catch (IOException e) {
            System.out.println("no seed file found, starting with empty inventory");
        }
    }

    static void adminMenu() {
        boolean back = false;
        while (!back) {
            System.out.println("\n-- admin menu --");
            System.out.println("1. View inventory");
            System.out.println("2. Add item");
            System.out.println("3. Update stock");
            System.out.println("4. Delete item");
            System.out.println("5. Generate sales report");
            System.out.println("6. Export inventory to csv");
            System.out.println("7. Back");
            System.out.print("choice: ");
            String c = sc.nextLine().trim();

            switch (c) {
                case "1":
                    printInventory();
                    break;
                case "2":
                    addItemFlow();
                    break;
                case "3":
                    updateStockFlow();
                    break;
                case "4":
                    deleteItemFlow();
                    break;
                case "5":
                    reportService.generateDailySalesReport();
                    break;
                case "6":
                    CsvUtil.exportInventory("reports/inventory_export.csv",
                            new java.util.ArrayList<>(inventoryService.getAllItems().values()));
                    System.out.println("exported to reports/inventory_export.csv");
                    break;
                case "7":
                    back = true;
                    break;
                default:
                    System.out.println("invalid option");
            }
        }
    }

    static void printInventory() {
        System.out.println("\nID\tNAME\tCATEGORY\tPRICE\tQTY");
        for (MessItem item : inventoryService.getAllItems().values()) {
            System.out.println(item);
        }
    }

    static void addItemFlow() {
        System.out.print("name: ");
        String name = sc.nextLine();
        System.out.print("category: ");
        String category = sc.nextLine();
        System.out.print("price: ");
        double price = Double.parseDouble(sc.nextLine());
        System.out.print("quantity: ");
        int qty = Integer.parseInt(sc.nextLine());
        System.out.print("low stock threshold: ");
        int threshold = Integer.parseInt(sc.nextLine());

        inventoryService.addItem(name, category, price, qty, threshold);
        System.out.println("item added");
    }

    static void updateStockFlow() {
        try {
            System.out.print("item id: ");
            int id = Integer.parseInt(sc.nextLine());
            System.out.print("new quantity: ");
            int qty = Integer.parseInt(sc.nextLine());
            inventoryService.updateStock(id, qty);
            System.out.println("updated");
        } catch (ItemNotFoundException e) {
            System.out.println("error: " + e.getMessage());
        } catch (NumberFormatException e) {
            System.out.println("that wasn't a valid number");
        }
    }

    static void deleteItemFlow() {
        try {
            System.out.print("item id to delete: ");
            int id = Integer.parseInt(sc.nextLine());
            inventoryService.deleteItem(id);
            System.out.println("deleted");
        } catch (ItemNotFoundException e) {
            System.out.println("error: " + e.getMessage());
        } catch (NumberFormatException e) {
            System.out.println("that wasn't a valid number");
        }
    }

    static void studentMenu() {
        System.out.print("enter your name: ");
        String name = sc.nextLine().trim();
        if (name.isEmpty()) name = "guest";

        boolean back = false;
        while (!back) {
            System.out.println("\n-- student menu (" + name + ") --");
            System.out.println("1. View menu");
            System.out.println("2. Place order");
            System.out.println("3. Order history");
            System.out.println("4. Back");
            System.out.print("choice: ");
            String c = sc.nextLine().trim();

            switch (c) {
                case "1":
                    printInventory();
                    break;
                case "2":
                    placeOrderFlow(name);
                    break;
                case "3":
                    printHistory(name);
                    break;
                case "4":
                    back = true;
                    break;
                default:
                    System.out.println("invalid option");
            }
        }
    }

    static void placeOrderFlow(String studentName) {
        printInventory();
        Map<Integer, Integer> cart = new HashMap<>();

        System.out.println("enter item id and qty, blank id to finish");
        while (true) {
            System.out.print("item id: ");
            String idLine = sc.nextLine().trim();
            if (idLine.isEmpty()) break;

            try {
                int id = Integer.parseInt(idLine);
                System.out.print("qty: ");
                int qty = Integer.parseInt(sc.nextLine().trim());
                cart.put(id, cart.getOrDefault(id, 0) + qty);
            } catch (NumberFormatException e) {
                System.out.println("that wasn't a number, try again");
            }
        }

        try {
            Order order = billingService.placeOrder(studentName, cart);
            System.out.println("\norder placed! id=" + order.getOrderId());
            for (OrderItem oi : order.getItems()) {
                System.out.println("  " + oi.getItemName() + " x" + oi.getQty() + " = Rs." + oi.lineTotal());
            }
            System.out.println("total: Rs." + order.getTotal());
        } catch (InvalidQuantityException | InsufficientStockException | ItemNotFoundException e) {
            System.out.println("order failed: " + e.getMessage());
        }
    }

    static void printHistory(String studentName) {
        List<Order> history = billingService.getOrderHistory(studentName);
        if (history.isEmpty()) {
            System.out.println("no past orders");
            return;
        }
        for (Order o : history) {
            System.out.println("order #" + o.getOrderId() + " on " + o.getTimestamp() + " total Rs." + o.getTotal());
        }
    }
}
