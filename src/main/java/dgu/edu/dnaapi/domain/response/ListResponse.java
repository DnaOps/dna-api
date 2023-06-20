package dgu.edu.dnaapi.domain.response;

import lombok.Builder;
import lombok.Getter;

@Getter
public class ListResponse {
    private Object list;
    private boolean hasNext;

    @Builder
    public ListResponse(Object list, boolean hasNext) {
        this.list = list;
        this.hasNext = hasNext;
    }
}
