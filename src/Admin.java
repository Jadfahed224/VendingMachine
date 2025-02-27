import java.io.IOException;

public class Admin {

    public Admin() {

    }

    public void listCommand() {
        System.out.println("""
                Commands:
                add: to add item.
                remove: to remove item.
                list: to list vending machine.
                exit: to exit as Admin.
                """);
    }

    public void removeItem(VendingMachine vm, int row, int col) throws IOException {
        vm.removeItem(row, col);
    }

    public void addItem(VendingMachine vm, String type, String name, double price, int quantity, int row, int col) throws IOException {
        vm.addItem(type, name, price, quantity, row, col);
    }
}
