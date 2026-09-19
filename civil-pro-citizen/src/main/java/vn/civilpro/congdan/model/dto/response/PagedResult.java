package vn.civilpro.congdan.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PagedResult<T> {
    private List<T> items;
    private long totalElements;
    private int page;
    private int size;
}