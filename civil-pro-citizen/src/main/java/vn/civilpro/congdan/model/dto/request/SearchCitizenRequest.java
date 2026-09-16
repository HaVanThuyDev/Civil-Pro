package vn.civilpro.congdan.dto.request;

import lombok.Data;

@Data
public class SearchCitizenRequest {

    private String fullName;
    private String idCardNumber;
    private String areaCode;
    private String citizenType;
    private Integer status;
    private Integer birthYearFrom;
    private Integer birthYearTo;
}