import java.io.IOException;

public class Balance {
    double balance;

    public Balance() throws IOException {
        this.balance = Helper.getCarryBalance();
    }

    public double getBalance() {
        return Math.round(balance*100.0)/100.0;
    }

    public void addBalance(double balance) {
        this.balance += balance;
    }

    public void subtractBalance(double balance) {
        this.balance -= balance;
    }
}
