package dgu.edu.dnaapi.domain.dto.albumPost;

import dgu.edu.dnaapi.domain.AlbumPost;
import dgu.edu.dnaapi.domain.User;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;

@NoArgsConstructor
@Getter
@Setter
public class AlbumPostSaveRequestDto_LEGACY {

    private String title;
    private String content;
    private Integer thumbnailImageIndex;

    @Builder
    public AlbumPostSaveRequestDto_LEGACY(String title, String content, Integer thumbnailImageIndex) {
        this.title = title;
        this.content = content;
        this.thumbnailImageIndex = thumbnailImageIndex;
    }

    @Builder
    public AlbumPostSaveRequestDto_LEGACY(String title, String content) {
        this.title = title;
        this.content = content;
    }

    public AlbumPost toEntity(User user) {
        return AlbumPost.builder()
                .title(title)
                .content(content)
                .author(user)
                .albumPostImages(new ArrayList<>())
                .build();
    }
}
