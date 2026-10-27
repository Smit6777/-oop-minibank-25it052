package util;

import java.io.IOException;
import java.nio.file.*;

public class TransactionLog {
    public static void append(Path file, String line) {
        try {
            Files.write(file, (line + System.lineSeparator()).getBytes(),
                    StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        } catch (IOException e) {
            System.out.println("Error writing log: " + e.getMessage());
        }
    }
}
