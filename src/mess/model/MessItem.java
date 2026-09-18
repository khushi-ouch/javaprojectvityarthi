package mess.model;

public class MessItem implements Discountable {

    private int id;
    private String name;
    private String category; // breakfast, lunch, dinner, snacks
    private double price;
    private int quantity;
    private int lowStockThreshold;

    public MessItem(int id, String name, String category, double price, int quantity, int lowStockThreshold) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.price = price;
        this.quantity = quantity;
        this.lowStockThreshold = lowStockThreshold;
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public String getCategory() { return category; }
    public double getPrice() { return price; }
    public int getQuantity() { return quantity; }
    public int getLowStockThreshold() { return lowStockThreshold; }

    public void reduceStock(int qty) {
        this.quantity -= qty;
    }

    public void addStock(int qty) {
        this.quantity += qty;
    }

    public boolean isLowStock() {
        return quantity <= lowStockThreshold;
    }

    // combo items get a flat 10% off, just a small demo of the interface being useful
    @Override
    public double applyDiscount(double amount) {
        if (category != null && category.equalsIgnoreCase("combo")) {
            return amount * 0.9;
        }
        return amount;
    }

    @Override
    public String toString() {
        return id + "\t" + name + "\t" + category + "\t Rs." + price + "\t qty:" + quantity
                + (isLowStock() ? "  <-- LOW STOCK" : "");
    }
}
