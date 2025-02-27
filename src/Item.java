public class Item {
    private String type = "empty";
    private String name = "EMPTY";
    private double price = 0.0;
    private int quantity = 0;

    public Item() { //anthony

    }

    public Item(String type, String name, double price, int quantity) {
        this.type = type;
        this.name = name;
        this.price = price;
        this.quantity = quantity;
    }

    public String getName() {
        return name;
    }
    public double getPrice() {
        return price;
    }
    public int getQuantity() {
        return quantity;
    }
    public String getType() {
        return type;
    }

    public void setName(String name) {
        this.name = name;
    }
    public void setPrice(double price) {
        this.price = price;
    }
    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
    public void setType(String type) {
        this.type = type;
    }
}
