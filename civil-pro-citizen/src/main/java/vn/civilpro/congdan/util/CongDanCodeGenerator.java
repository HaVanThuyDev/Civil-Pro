package vn.civilpro.congdan.util;


import org.springframework.stereotype.Component;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class CongDanCodeGenerator {

    private static final AtomicInteger counter = new AtomicInteger(0);

    public String generate() {
        String date = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        int seq = counter.incrementAndGet();
        return String.format("CD%s%04d", date, seq);
    }
}