import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

class Account {
    private String accountNumber;
    private double balance;
    private Lock lock = new ReentrantLock();

    public Account(String accountNumber, double initialBalance) {
        this.accountNumber = accountNumber;
        this.balance = initialBalance;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public double getBalance() {
        return balance;
    }

    public boolean debit(double amount) {
        lock.lock();
        try {
            if (balance >= amount) {
                balance -= amount;
                return true;
            }
            return false;
        } finally {
            lock.unlock();
        }
    }

    public void credit(double amount) {
        lock.lock();
        try {
            balance += amount;
        } finally {
            lock.unlock();
        }
    }
}

class Bank {
    public boolean transferMoney(Account sender, Account receiver, double amount) {
        if (sender.debit(amount)) { // Phase 1: Debit operation
            if (receiver.credit(amount)) { // Phase 2: Credit operation
                System.out.println("Transfer Successful");
                return true;
            } else {
                // Credit failed, rollback debit
                sender.credit(amount);
                System.out.println("Transfer Failed: Rolling back transaction");
                return false;
            }
        } else {
            System.out.println("Insufficient funds for transfer");
            return false;
        }
    }
}

public class Main {
    public static void main(String[] args) {
        Account accountA = new Account("A123", 1000.0);
        Account accountB = new Account("B456", 500.0);

        Bank bank = new Bank();

        double transferAmount = 200.0;
        boolean result = bank.transferMoney(accountA, accountB, transferAmount);

        if (result) {
            System.out.println("Transfer of $" + transferAmount + " from Account A to Account B was successful.");
            System.out.println("Account A Balance: " + accountA.getBalance());
            System.out.println("Account B Balance: " + accountB.getBalance());
        } else {
            System.out.println("Transfer failed. Please try again.");
        }
    }
}
