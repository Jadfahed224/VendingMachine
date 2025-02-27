import java.io.*;

public class VendingMachine {

    public final Item[][] array = new Item[8][7];
    private final String stockFile = "Data/Stock";
    private int maxLengthOfStockName = 5;

    public VendingMachine() throws IOException {
        loadStock();
        Helper.displayStock(array,maxLengthOfStockName);
        printIntroduction();
    }

    public Item getItem(int i, int j) {
        return array[i][j];
    }

    public void addItem(String type, String name, double price, int quantity, int row, int col) throws IOException {
        array[row][col] = Helper.getProductType(type, name, price, quantity);
        rewriteStock();
        System.out.println("Item added to stock.");
        System.out.println("Proof: " + array[row][col].getType()
                + " " + array[row][col].getName()
                + " " + array[row][col].getPrice()
                + " " + array[row][col].getQuantity());
    }

    public void removeItem(int row, int col) throws IOException {
        array[row][col] = new Item();
        rewriteStock();
        System.out.println("Item removed from stock.");
        System.out.println("Proof: " + array[row][col].getType() + " " + array[row][col].getName());
        loadStock();
    }

    public int getMaxLengthOfStockName() {
        return maxLengthOfStockName;
    }

    public void printIntroduction() {
        System.out.println("Welcome to the vending machine program. Please type a valid number " +
                "to add funds to your balance, or a valid coordinate to order an item." +
                " To exit, type: exit");
    }

    private void removeNullElement() {
        for(int i = 0; i < array.length; i++) {
            for(int j = 0; j < array[i].length; j++) {
                if(array[i][j] == null) {
                    array[i][j] = new Item();
                }
                else {
                    if(array[i][j].getName().length() > maxLengthOfStockName) {
                        maxLengthOfStockName = array[i][j].getName().length();
                    }
                }
            }
        }
    }

    public int getRow() {
        return array.length;
    }

    public int getCol() {
        return array[0].length;
    }

    public void displayStock() {
        Helper.displayStock(array,maxLengthOfStockName);
    }

    public void orderItem(int i, int j, Balance balance) throws IOException {
        i = i % 'A';
        if(array[i][j].getQuantity() == 0) {
            array[i][j] = new Item();
            System.out.println("Item out of stock.");
            return;
        }
        array[i][j].setQuantity(array[i][j].getQuantity() - 1);
        balance.subtractBalance(array[i][j].getPrice());
        Helper.setCarryBalance(balance.getBalance());
        rewriteStock();
        Helper.displayStock(array,maxLengthOfStockName);
    }

    private void rewriteStock() throws IOException {
        FileWriter updateStock = new FileWriter(stockFile);
        for(int i = 0; i < array.length; i++) {
            for(int j = 0; j < array[0].length; j++) {
                if(!array[i][j].getType().equals(new Item().getType())) {
                    updateStock.write(array[i][j].getType() + ","
                            + array[i][j].getName() + ","
                            + array[i][j].getPrice() + ","
                            + array[i][j].getQuantity() + ","
                            + i + ","
                            + j + "\n");
                }
            }
        }
        updateStock.close();
    }

    private void loadStock() throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(stockFile));
        String line;
        while((line = br.readLine()) != null) {
            String[] parts = line.split(",");
            int row = Integer.parseInt(parts[4]);
            int col = Integer.parseInt(parts[5]);
            array[row][col] = Helper.getProductType(parts[0], parts[1], Double.parseDouble(parts[2]), Integer.parseInt(parts[3]));
        }
        removeNullElement();
    }
}