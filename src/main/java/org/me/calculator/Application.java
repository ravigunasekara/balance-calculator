package org.me.calculator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class Application {
    private static final Logger log = LoggerFactory.getLogger(Application.class);
    private static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
    private static Scanner scanner = new Scanner(System.in).useDelimiter("\\n");;

    private static LocalDateTime startDateTime;
    private static LocalDateTime endDateTime;

    public static void main(String[] args) {

        if(args.length !=1 ) {
            log.warn("Transaction file path is Missing");
            System.exit(1);
        }

        String input = "";
        System.out.println("accountId:");
        input = scanner.next();

        while (!"QUIT".equalsIgnoreCase(input)) {
            System.out.println("from:");
            findDate(true);
            System.out.println("to:");
            findDate(false);

            Map<String, BigDecimal> selectedTxn = new HashMap<>();
            BankFileReader bankFileReader = new BankFileReader(selectedTxn);
            try {
                selectedTxn = bankFileReader.findRawBatch(startDateTime, endDateTime, args[0], input);
            } catch (Exception e) {
                log.error(e.getLocalizedMessage());
            }

            BigDecimal sum = selectedTxn.values().stream().reduce(BigDecimal.valueOf(0), (a, b) -> a.add(b));
            String balance = sum.compareTo(BigDecimal.ZERO) < 0 ? "-$"+sum.abs() : "$"+sum;
            System.out.println("\nRelative balance for the period is: " +balance);
            System.out.println("Number of transactions included is: " + selectedTxn.size()+"\n");
            System.out.println("accountId:");
            input = scanner.next();
        }

    }

    private static void findDate(boolean isStart) {
        String date = scanner.next();
        try {
            if (isStart)
                startDateTime = LocalDateTime.parse(date, formatter);
            else
                endDateTime = LocalDateTime.parse(date, formatter);
        } catch (DateTimeParseException e) {
            System.out.println("Invalid date format. Please use dd/MM/yyyy HH:mm:ss");
            findDate(isStart);
        }
    }
}
