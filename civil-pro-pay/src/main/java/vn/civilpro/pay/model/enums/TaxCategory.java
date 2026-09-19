package vn.civilpro.pay.model.enums;

import lombok.Getter;

@Getter
public enum TaxCategory {
    PERSONAL_INCOME_TAX("Thuế Thu nhập cá nhân (TNCN)"),
    VALUE_ADDED_TAX("Thuế Giá trị gia tăng (GTGT)"),
    SPECIAL_CONSUMPTION_TAX("Thuế Tiêu thụ đặc biệt"),
    NON_AGRI_LAND_TAX("Thuế Sử dụng đất phi nông nghiệp"),
    AGRI_LAND_TAX("Thuế Sử dụng đất nông nghiệp"),
    REAL_ESTATE_TRANSFER_TAX("Thuế Chuyển nhượng Bất động sản"),
    SECURITIES_TRANSFER_TAX("Thuế Chuyển nhượng Chứng khoán");

    private final String description;

    TaxCategory(String description) {
        this.description = description;
    }
}
