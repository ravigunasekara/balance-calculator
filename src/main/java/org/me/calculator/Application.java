package org.me.calculator;

import org.me.calculator.model.ResponseModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Scanner;

import static org.me.calculator.common.DateFormatter.findValidDateTime;

public class Application {
    private static final Logger log = LoggerFactory.getLogger(Application.class);
    private static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
    private static Scanner scanner = new Scanner(System.in).useDelimiter("\\n");;

    public static void main(String[] args) {

        if(args.length !=1 ) {
            log.warn("Transaction file path is Missing");
            System.exit(1);
        }

        String input;
        BigDecimal sum;
        ResponseModel responseModel;
        System.out.println("accountId:");
        input = scanner.next();

        while (!"QUIT".equalsIgnoreCase(input)) {
            System.out.println("from:");
            LocalDateTime startDateTime = findDate();
            System.out.println("to:");
            LocalDateTime endDateTime = findDate();

            BankFileReader bankFileReader = new BankFileReader();
            try {
                responseModel = bankFileReader.findRawBatch(startDateTime, endDateTime, args[0], input);
                sum = responseModel.getAccountBalance();
                String balance = sum.compareTo(BigDecimal.ZERO) < 0 ? "-$" + sum.abs() : "$" + sum;
                System.out.println("\nRelative balance for the period is: " + balance);
                System.out.println("Number of transactions included is: " + responseModel.getNoOfTransactions() + "\n");

            } catch (Exception e) {
                log.error(e.getLocalizedMessage());
            }

            System.out.println("accountId:");
            input = scanner.next();
        }

    }

    private static LocalDateTime findDate() {
        String date = scanner.next();
        try {
            return findValidDateTime(date);
        } catch (DateTimeParseException e) {
            return findDate();
        }
    }
}
