package org.me.calculator.common;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class DateFormatter {
    private static final Logger log = LoggerFactory.getLogger(DateFormatter.class);
    private static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    public static LocalDateTime findValidDateTime(String date) {
        try {
            return LocalDateTime.parse(date, formatter);
        } catch (DateTimeParseException e) {
            log.error("Invalid date format. Please use dd/MM/yyyy HH:mm:ss");
            throw e;
        }
    }
}
