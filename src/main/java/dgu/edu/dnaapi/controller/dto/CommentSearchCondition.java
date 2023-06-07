package dgu.edu.dnaapi.controller.dto;

import lombok.Data;

@Data
public class CommentSearchCondition {
    private Long start;
    private Long userId;

    public boolean hasUserId(){
        return userId != null;
    }
}
