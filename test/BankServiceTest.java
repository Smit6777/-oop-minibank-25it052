import exception.InsufficientFundsException;
import exception.InvalidAmountException;
import model.Account;
import model.SavingsAccount;
import service.AccountWorker;
import org.junit.Test;
import static org.junit.Assert.*;

public class BankServiceTest {

    // Test 1: Deposit increases balance correctly
    @Test
    public void testDepositIncreasesBalance() throws Exception {
        Account account = new SavingsAccount("TestUser", 1000, 500);
        account.deposit(500);
        assertEquals(1500, account.getBalance());
    }

    // Test 2: Deposit of zero throws InvalidAmountException
    @Test(expected = InvalidAmountException.class)
    public void testDepositZeroThrowsException() throws Exception {
        Account account = new SavingsAccount("TestUser", 1000, 500);
        account.deposit(0);
    }

    // Test 3: Deposit of negative amount throws InvalidAmountException
    @Test(expected = InvalidAmountException.class)
    public void testDepositNegativeThrowsException() throws Exception {
        Account account = new SavingsAccount("TestUser", 1000, 500);
        account.deposit(-100);
    }

    // Test 4: Withdrawal over limit throws InsufficientFundsException
    @Test(expected = InsufficientFundsException.class)
    public void testWithdrawOverLimitThrowsException() throws Exception {
        Account account = new SavingsAccount("TestUser", 1000, 500);
        account.withdraw(800); // would go below minBalance of 500
    }

    // Test 5: Withdraw valid amount reduces balance
    @Test
    public void testWithdrawReducesBalance() throws Exception {
        Account account = new SavingsAccount("TestUser", 2000, 500);
        account.withdraw(500);
        assertEquals(1500, account.getBalance());
    }

    // Test 6: Many concurrent deposits keep the balance correct (thread-safety)
    @Test
    public void testConcurrentDepositsAreCorrect() throws Exception {
        Account account = new SavingsAccount("Concurrent", 0, 0);
        int threadCount = 10;
        int depositsPerThread = 100;
        long depositAmount = 10;
        long expected = threadCount * depositsPerThread * depositAmount;

        Thread[] threads = new Thread[threadCount];
        for (int i = 0; i < threadCount; i++) {
            threads[i] = new Thread(new AccountWorker(account, depositsPerThread, depositAmount));
            threads[i].start();
        }
        for (Thread t : threads) { t.join(); }

        assertEquals(expected, account.getBalance());
    }

    // Test 7: Account number is unique for each new account
    @Test
    public void testAccountNumbersAreUnique() {
        Account a1 = new SavingsAccount("User1", 1000, 500);
        Account a2 = new SavingsAccount("User2", 2000, 500);
        assertNotEquals(a1.getAccountNumber(), a2.getAccountNumber());
    }

    // Test 8: compareTo sorts by account number
    @Test
    public void testCompareToSortsByAccountNumber() {
        Account a1 = new SavingsAccount("Alpha", 1000, 500);
        Account a2 = new SavingsAccount("Beta", 2000, 500);
        assertTrue(a1.compareTo(a2) < 0); // a1 was created before a2
    }
}
