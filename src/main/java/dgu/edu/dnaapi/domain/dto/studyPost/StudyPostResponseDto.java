package dgu.edu.dnaapi.domain.dto.studyPost;

import dgu.edu.dnaapi.domain.StudyPost;
import lombok.Getter;

@Getter
public class StudyPostResponseDto extends StudyPostMetaDataResponseDto {

    private String content;
    private Boolean isLikedByUser;

    public StudyPostResponseDto(StudyPost studyPost, Boolean isLikedByUser){
        super(studyPost);
        this.content = studyPost.getContent();
        this.isLikedByUser = isLikedByUser;
    }
}
