package dgu.edu.dnaapi.controller.dto;

import lombok.Data;

@Data
public class BoardSearchCondition {

    private String title;
    private String author;
    private String content;
    private Long start;
}
