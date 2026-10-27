package util;

import model.Account;

import java.io.*;
import java.nio.file.Path;

public class StatePersister {
    public static void save(Account[] accounts, Path file) {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(file.toFile()))) {
            out.writeObject(accounts);
            System.out.println("Accounts saved to " + file.getFileName());
        } catch (IOException e) {
            System.out.println("Error saving accounts: " + e.getMessage());
        }
    }

    public static Account[] load(Path file) {
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(file.toFile()))) {
            return (Account[]) in.readObject();
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Error loading accounts: " + e.getMessage());
            return new Account[0];
        }
    }
}
