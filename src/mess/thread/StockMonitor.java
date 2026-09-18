package mess.thread;

import mess.model.MessItem;
import mess.service.InventoryService;
import mess.util.AppLogger;

import java.util.Map;

public class StockMonitor extends Thread {

    private InventoryService inventoryService;
    private int intervalSeconds;
    private volatile boolean running = true;

    public StockMonitor(InventoryService inventoryService, int intervalSeconds) {
        this.inventoryService = inventoryService;
        this.intervalSeconds = intervalSeconds;
        setDaemon(true); // so it doesn't stop the jvm from exiting on its own
    }

    public void stopMonitoring() {
        running = false;
        this.interrupt();
    }

    @Override
    public void run() {
        while (running) {
            inventoryService.refreshCache();
            Map<Integer, MessItem> items = inventoryService.getAllItems();

            for (MessItem item : items.values()) {
                if (item.isLowStock()) {
                    String msg = "LOW STOCK: " + item.getName() + " only " + item.getQuantity() + " left";
                    AppLogger.log(msg);
                }
            }

            try {
                Thread.sleep(intervalSeconds * 1000L);
            } catch (InterruptedException e) {
                running = false;
            }
        }
    }
}
