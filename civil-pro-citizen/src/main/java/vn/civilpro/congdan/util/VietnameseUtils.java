package vn.civilpro.congdan.util;

import java.text.Normalizer;
import java.util.regex.Pattern;

public final class VietnameseUtils {

    private static final Pattern DIACRITIC_PATTERN = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");

    private VietnameseUtils() {}

    public static String removeAccent(String input) {
        if (input == null || input.isBlank()) return input;

        String normalized = Normalizer.normalize(input, Normalizer.Form.NFD);
        String withoutDiacritics = DIACRITIC_PATTERN.matcher(normalized).replaceAll("");

        return withoutDiacritics
                .replace("đ", "d")
                .replace("Đ", "D");
    }
}