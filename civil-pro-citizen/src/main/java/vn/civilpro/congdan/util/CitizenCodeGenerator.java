package vn.civilpro.congdan.util;

import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class CitizenCodeGenerator {

    private static final AtomicInteger counter = new AtomicInteger(0);
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd");

    /**
     * Dùng khi Inject bean qua Spring: citizenCodeGenerator.generate()
     */
    public String generate() {
        return generateStatic();
    }

    /**
     * Dùng khi gọi trực tiếp dạng Utility: CitizenCodeGenerator.generateStatic()
     */
    public static String generateStatic() {
        String date = LocalDate.now().format(DATE_FORMATTER);
        int seq = counter.incrementAndGet();
        return String.format("CTZ%s%04d", date, seq);
    }
}