package vn.civilpro.model.dto.request;


import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Date;

@Data
@AllArgsConstructor
public class ErrorDetail {
    private Date Timetamp;
    private  String message;
    private  Object  details;
    private  String path ;
}
