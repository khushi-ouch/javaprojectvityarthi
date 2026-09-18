package mess.model;

public class OrderItem {

    private int itemId;
    private String itemName;
    private int qty;
    private double priceEach;

    public OrderItem(int itemId, String itemName, int qty, double priceEach) {
        this.itemId = itemId;
        this.itemName = itemName;
        this.qty = qty;
        this.priceEach = priceEach;
    }

    public int getItemId() { return itemId; }
    public String getItemName() { return itemName; }
    public int getQty() { return qty; }
    public double getPriceEach() { return priceEach; }

    public double lineTotal() {
        return qty * priceEach;
    }
}
