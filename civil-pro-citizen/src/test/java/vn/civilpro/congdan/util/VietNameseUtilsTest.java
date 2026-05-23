package vn.civilpro.congdan.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * UNIT TEST: VIETNAMESE UTILS
 */
@DisplayName("VietNameseUtils Tests")
class VietNameseUtilsTest {

    @ParameterizedTest(name = "'{0}' → '{1}'")
    @CsvSource({
            "Nguyễn Văn A,        Nguyen Van A",
            "Lê Thị Mai Liên,     Le Thi Mai Lien",
            "Trần Đức Thành,      Tran Duc Thanh",
            "Đặng Thị Hương,      Dang Thi Huong",
            "NGUYỄN VĂN B,        NGUYEN VAN B",
            "Phạm Thị Ý Nhi,      Pham Thi Y Nhi",
    })
    @DisplayName("Bỏ dấu tiếng Việt đúng")
    void removeAccent_correctlyRemovesDiacritics(String input, String expected) {
        assertThat(VietNameseUtils.removeAccent(input.trim()))
                .isEqualToIgnoringWhitespace(expected.trim());
    }
}