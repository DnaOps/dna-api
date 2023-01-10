package dgu.edu.dnaapi.domain.response;

import lombok.Builder;
import lombok.Getter;

@Getter
public class ListResponse {
    private Object list;
    private int totalCount;

    @Builder
    public ListResponse(Object list, int totalCount) {
        this.list = list;
        this.totalCount = totalCount;
    }
}
