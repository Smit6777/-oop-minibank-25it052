package util;

import java.io.*;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;

public class ReportGenerator {
    public static void generate(Path logsFolder, Path reportFile) {
        long totalDeposits = 0;
        long totalWithdrawals = 0;

        try (DirectoryStream<Path> stream = Files.newDirectoryStream(logsFolder, "*.log")) {
            for (Path logFile : stream) {
                try (BufferedReader reader = new BufferedReader(new FileReader(logFile.toFile()))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        if (line.startsWith("DEPOSIT")) {
                            long amount = Long.parseLong(line.split(",")[1].trim());
                            totalDeposits += amount;
                        } else if (line.startsWith("WITHDRAW")) {
                            long amount = Long.parseLong(line.split(",")[1].trim());
                            totalWithdrawals += amount;
                        }
                    }
                }

                BasicFileAttributes attrs = Files.readAttributes(logFile, BasicFileAttributes.class);
                System.out.println("Log: " + logFile.getFileName() + " | Size: " + attrs.size() + " bytes | Modified: " + attrs.lastModifiedTime());
            }
        } catch (IOException e) {
            System.out.println("Error reading logs: " + e.getMessage());
        }

        String report = "==== END OF DAY REPORT ====\n" +
                        "Total Deposits   : Rs." + totalDeposits + "\n" +
                        "Total Withdrawals: Rs." + totalWithdrawals + "\n" +
                        "Net Flow         : Rs." + (totalDeposits - totalWithdrawals) + "\n" +
                        "===========================\n";

        try {
            Files.write(reportFile, report.getBytes(),
                    StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
            System.out.println("\n" + report);
        } catch (IOException e) {
            System.out.println("Error writing report: " + e.getMessage());
        }
    }
}
