package dgu.edu.dnaapi.domain;

import lombok.Getter;

@Getter
public enum BoardSearchCriteria {

    TITLE("title"),
    AUTHOR("author"),
    CONTENT("content"),
    TITLE_AND_CONTENT("titleAndContent");

    private String searchCriteria;

    BoardSearchCriteria(String searchCriteria) {
        this.searchCriteria = searchCriteria;
    }
}
