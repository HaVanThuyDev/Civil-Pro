package vn.civilpro.congdan.util;

import java.text.Normalizer;
import java.util.regex.Pattern;

/**
 * TIỆN ÍCH XỬ LÝ TIẾNG VIỆT
 * Bỏ dấu để phục vụ tìm kiếm không phân biệt dấu
 */
public final class VietNameseUtils {

    private static final Pattern DIACRITIC_PATTERN = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");

    private VietNameseUtils() {}

    /**
     * Bỏ dấu tiếng Việt
     * Ví dụ: "Nguyễn Văn A" → "Nguyen Van A"
     */
    public static String removeAccent(String input) {
        if (input == null || input.isBlank()) return input;

        // Chuẩn hóa NFD: tách base char và dấu
        String normalized = Normalizer.normalize(input, Normalizer.Form.NFD);

        // Xóa combining marks (dấu)
        String withoutDiacritics = DIACRITIC_PATTERN.matcher(normalized).replaceAll("");

        // Xử lý Đ/đ (không bị xử lý bởi NFD normalization)
        return withoutDiacritics
                .replace("đ", "d")
                .replace("Đ", "D");
    }
}