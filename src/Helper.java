import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

public class Helper {
    private static final String BALANCE_FILE = "Data/Balance";
    private static final String ADMIN_DATA = "Data/Password";

    public static double getCarryBalance() throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(BALANCE_FILE));
        String bal = br.readLine();
        return Double.parseDouble(bal);
    }

    public static boolean updateUserBalance(String input, Balance balance) throws IOException {
        try {
            Double.parseDouble(input);
        } catch (NumberFormatException e) {
            return false;
        }
        if(input.length() >= 5 || Double.parseDouble(input) > 1.0 || Double.parseDouble(input) <= 0) {
            return false;
        }
        addBalance(input, balance);
        System.out.println("Balance added. Current balance: " + balance.getBalance());
        return true;
    }

    public static void adminPrivilege(VendingMachine vm) throws IOException {
        Admin admin = new Admin();
        label:
        while(true) {
            admin.listCommand();
            Scanner adminScanner = new Scanner(System.in);
            String adminInput = adminScanner.nextLine();
            switch (adminInput) {
                case "add":
                    System.out.println("Enter product information with the following format:");
                    System.out.println("Type,Name,Price,Quantity,Row,Col. The row must be a valid letter.");
                    String addInput = adminScanner.nextLine();
                    String[] array = addInput.split(",");
                    if (addItemAsAdmin(array, vm)) {
                        admin.addItem(vm, array[0], array[1],
                                Double.parseDouble(array[2]),
                                Integer.parseInt(array[3]),
                                (int) array[4].charAt(0) % 'A',
                                Integer.parseInt(array[5]) - 1);
                        System.out.println("Item added successfully.");
                        break;
                    }
                    else {
                        System.out.println("Invalid input.");
                    }
                    break;
                case "remove":
                    System.out.println("Enter product row & column with the following format:");
                    System.out.println("row column. Example: B5. row as a letter and column as a number.");
                    String input = adminScanner.nextLine();
                    if(removeItemAsAdmin(input, vm)) {
                        admin.removeItem(vm, (int) input.charAt(0) % 'A', Character.getNumericValue(input.charAt(1))-1);
                    }
                    break;
                case "list":
                    vm.displayStock();
                    break;
                case "exit":
                    vm.displayStock();
                    break label;
                default:
                    System.out.println("Caught an unrecognized option. Please try again.");
                    break;
            }
        }
    }

    private static boolean removeItemAsAdmin(String input, VendingMachine vm) throws IOException {
        if(checkProductCoordinates(input, vm) && isNotEmpty(input, vm)) {
            return true;
        }
        System.out.println("Error removing item.");
        return false;
    }

    private static boolean addItemAsAdmin(String[] list, VendingMachine vm) { // check add validity
        if(list.length != 6 || !checkType(list[0])) {
            System.out.println("Invalid command.");
            return false;
        }
        try {
            Double.parseDouble(list[2]);
            Integer.parseInt(list[3]);
            Integer.parseInt(list[5]);
        } catch (NumberFormatException e) {
            System.out.println("Invalid command.");
            return false;
        }
        if(Integer.parseInt(list[3]) > 10) {
            System.out.println("Stock cannot exceed 10.");
            return false;
        }
        if(!checkProductCoordinates(list[4]+list[5], vm)) {
            System.out.println("Coordinate error. Please try again.");
            return false;
        }
        if(isNotEmpty(list[4]+list[5], vm)) {
            System.out.println("Cell already in use. Please choose another empty cell.");
            return false;
        }
        return true;
        //return checkProductCoordinates(list[4]+list[5], vm) && !isNotEmpty(list[4]+list[5], vm);
    }

    public static boolean checkType(String input) {
        return input.equals("Can") || input.equals("Bottle") || input.equals("Bag") || input.equals("Sweet");
    }

    public static boolean checkProductCoordinates(String input, VendingMachine vm) {
        if(input.length() != 2 || !Character.isLetter(input.charAt(0)) || !Character.isDigit(input.charAt(1))) {
            return false;
        }
        int a = 'A';
        int inputCharacter = input.charAt(0);
        return inputCharacter >= a && inputCharacter % a <= vm.getRow() && Character.getNumericValue(input.charAt(1)) <= vm.getCol();
         //separate to other function or check if list[4] is digit
    }

    public static boolean isNotEmpty(String input, VendingMachine vm) {
        return !(vm.getItem(input.charAt(0) % 'A', Character.getNumericValue(input.charAt(1))-1).getType().equals(new Item().getType()));
    }

    private static void addBalance(String input, Balance balance) throws IOException {
        balance.addBalance(Math.round((Double.parseDouble(input))*100.0)/100.0);
        setCarryBalance(balance.getBalance());
    }

    public static void setCarryBalance(double carryBalance) throws IOException {
        FileWriter updateStock = new FileWriter(BALANCE_FILE);
        updateStock.write((String.valueOf(carryBalance)));
        updateStock.close();
    }

    public static boolean checkPassword(String password) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(ADMIN_DATA));
        String pass = br.readLine();
        return password.equals(pass);
    }

    public static Item getProductType(String item, String name, double price, int quantity) {
        switch (item) {
            case "Can" -> {
                return new Can(item, name, price, quantity);
            }
            case "Bottle" -> {
                return new Bottle(item, name, price, quantity);
            }
            case "Bag" -> {
                return new Bag(item, name, price, quantity);
            }
            case "Sweet" -> {
                return new Sweet(item, name, price, quantity);
            }
        }
        return new Item();
    }

    public static void userInput(String input, VendingMachine vm, Balance balance, Order order) throws IOException {
        if(!Helper.updateUserBalance(input, balance)) {
            if(Helper.checkProductCoordinates(input, vm) && isNotEmpty(input, vm)) {
                if(balance.getBalance() <= vm.getItem(input.charAt(0) % 'A', Character.getNumericValue(input.charAt(1)-1)).getPrice()) {
                    System.out.println("Not enough balance.");
                }
                else {
                    vm.orderItem(input.charAt(0), Character.getNumericValue(input.charAt(1)-1), balance);
                    order.addProduct(vm.getItem(input.charAt(0) % 'A', Character.getNumericValue(input.charAt(1)-1)));
                }
            }
            else {
                System.out.println("Invalid input. Please try again.");
            }
        }
    }

    public static boolean userReceipt(String input, VendingMachine vm, Order order) throws IOException {
        System.out.println("""
                    Would you like to have a receipt? (y/n)
                    WARNING: receipt will be saved in Documents as "Receipt". Any file named similarly
                    inside your Documents will be overwritten.
                """);
        if(input.isEmpty()) {
            System.out.println("Caught an empty String. Please try again.");
            return true;
        }
        else if(input.equals("y")) {
            order.listOrders(vm.getMaxLengthOfStockName());
            return false;
        }
        else if(input.equals("n")) {
            return false;
        }
        else {
            System.out.println("Invalid input. Please try again.");
            return true;
        }
    }

    public static void displayStock(Item[][] array, int maxLengthOfStockName) {
        int letterSectionCount = 'A'; //65 in ASCII
        int count = 0;
        StringBuilder s = new StringBuilder();
        String maxVendingLength = "-".repeat("Quantity:".length() - 1 + array[0].length * (6 + maxLengthOfStockName + "|".length()) + "  X  |".length());
        s.append(maxVendingLength).append("\n").append(" ".repeat(("  X  ".length()))).append("|").append(" ".repeat("Quantity:".length()));
        if(array[0].length % 2 == 0) { // TODO
            for(int i = 0; i < array[0].length; i++) {
                s.append("  ").append(" ".repeat(maxLengthOfStockName/2)).append(i+1).append(" ".repeat(maxLengthOfStockName/2)).append("    ");
            }
            s.append(" ".repeat(maxLengthOfStockName/2));
        }
        else { // TODO
            s.append("  ");
            for(int i = 0; i < array[0].length; i++) {
                //s.append("    ").append(" ".repeat((maxLengthOfStockName/2))).append(i+1).append(" ".repeat((maxLengthOfStockName/2))).append("   ");
                if(i % 2 != 0) {
                    s.append(" ");
                }
                s.append(" ".repeat(maxLengthOfStockName/2-1)).append(i+1).append(" ".repeat(maxLengthOfStockName));
            }
        }
        //s.delete(s.length()-4, s.length()-1);
        s.append("|").append("\n").append(maxVendingLength).append("\n");

        for(int i = 0; i < array.length; i++) {
            for(int k = 0; k < 4; k++) {
                if(k == 0) {
                    s.append(" ".repeat(5)).append("|Product:");
                } else if(k == 1) {
                    s.append("  ").append((char) letterSectionCount).append("  ").append("|Type:   ");
                    letterSectionCount++;
                } else if(k == 2) {
                    s.append(" ".repeat(5)).append("|Price:  ");
                }
                else {
                    s.append(" ".repeat(5)).append("|Quantity:");
                }
                for(int j = 0; j < array[0].length; j++) {
                    if(k == 1) {
                        s.append("   ").append(array[i][j].getType())
                                .append(" ".repeat(maxLengthOfStockName - array[i][j].getType().length() - 1))
                                .append("    |");
                    }
                    else if(k == 2) {
                        s.append("   ").append(array[i][j].getPrice());
                        int lenPrice = Double.toString(array[i][j].getPrice()).length() - 1;
                        s.append(" ".repeat(maxLengthOfStockName - lenPrice));
                        s.append("  |");
                    } else if(k == 3) {
                        if (count == 0) {
                            s.append("  ").append(array[i][j].getQuantity());
                            count++;
                        } else {
                            s.append("   ").append(array[i][j].getQuantity());
                        }
                        int lenQuantity = Integer.toString(array[i][j].getQuantity()).length() - 1;
                        s.append(" ".repeat(maxLengthOfStockName - lenQuantity));
                        s.append("  |");
                    } else {
                        s.append("   ").append(array[i][j].getName())
                                .append(" ".repeat(maxLengthOfStockName - array[i][j].getName().length()))
                                .append("   |");
                    }
                }
                s.append("\n");
            }
            count--;
            s.append(maxVendingLength);
            s.append("\n");
        }
        System.out.println(s);
    }
}