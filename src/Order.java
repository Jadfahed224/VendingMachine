import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

public class Order {
    static ArrayList<String[]> arrayList;

    public Order() {
        arrayList = new ArrayList<>();
    }

    public void addProduct(Item item) {
        String[] array = new String[]{item.getName(), String.valueOf(item.getPrice()), "1"};
        if(getLength() == 0) {
            arrayList.add(array);
        }
        else if(!checkDuplicate(array)) {
            arrayList.add(array);
        }
        System.out.println("Added product " + item.getName());
    }

    public int getLength() {
        return arrayList.size();
    }

    public boolean checkDuplicate(String[] array) {
        for(int i = 0; i < arrayList.size(); i++) {
            if(arrayList.get(i)[0].equals(array[0])) {
                arrayList.get(i)[2] = Integer.toString(Integer.parseInt(arrayList.get(i)[2])+1);
                return true;
            }
        }
        return false;
    }

    public void listOrders(int getMaxLengthOfStockName) throws IOException {
        String userHome = System.getProperty("user.home");
        Path path = Paths.get(userHome, "Documents", "Receipt.txt"); //can either desktop or documents
        BufferedWriter writer = Files.newBufferedWriter(path);
        writer.newLine();
        String adjustStructure = " ".repeat(5); //five spaces
        writer.write("Product" + " ".repeat(calculateSpacing("Quantity".length(), getMaxLengthOfStockName)) + adjustStructure + "Price" + adjustStructure + "Quantity");
        writer.newLine();
        writer.newLine();
        double totalPrice = 0;

        for(String[] strings : arrayList) {
            writer.write(strings[0] + (" ".repeat(calculateSpacing(strings[0].length(), getMaxLengthOfStockName))) + adjustStructure
            + strings[1] + adjustStructure + " ".repeat("Quantity".length()/2)
            + strings[2]);
            writer.newLine();
            totalPrice += Double.parseDouble(strings[1]);
        }
        writer.newLine();
        writer.write("Total Price: " + totalPrice + "$ (US Dollar(s))");
        System.out.println("File Path: " + path);
        writer.close();
    }

    private int calculateSpacing(int lengthOfWord, int getMaxLengthOfStockName) {
        return Math.abs(getMaxLengthOfStockName - lengthOfWord);
    }
}