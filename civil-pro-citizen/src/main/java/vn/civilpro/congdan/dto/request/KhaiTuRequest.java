package vn.civilpro.congdan.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class KhaiTuRequest {

    @NotBlank(message = "Lý do khai tử không được để trống")
    private String lyDo;
}