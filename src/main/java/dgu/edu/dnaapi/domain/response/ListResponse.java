package dgu.edu.dnaapi.domain.response;

import lombok.Builder;
import lombok.Getter;

@Getter
public class ListResponse {
    private Object list;
    private int totalCount;
    private boolean hasNext;

    // Todo : totalCount 삭제예정 -> Forum 개발 Legacy 때문에 남겨둠
    @Builder
    public ListResponse(Object list, int totalCount, boolean hasNext) {
        this.list = list;
        this.totalCount = totalCount;
        this.hasNext = hasNext;
    }
}
