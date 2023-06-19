package dgu.edu.dnaapi.domain.dto.albumPost;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@Getter
@Setter
public class AlbumPostUpdateRequestDto {

    private String title;
    private String content;
    private Integer thumbnailImageIndex;
    private String thumbnailImage;
    private List<String> deleteImages = new ArrayList<>();

    @Builder
    public AlbumPostUpdateRequestDto(String title, String content, Integer thumbnailImageIndex, String thumbnailImage, List<String> deleteImages) {
        this.title = title;
        this.content = content;
        this.thumbnailImageIndex = thumbnailImageIndex;
        this.thumbnailImage = thumbnailImage;
        this.deleteImages = deleteImages;
    }
}
