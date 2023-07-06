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
public class AlbumPostSaveRequestDto {

    private String title;
    private String content;
    private String thumbnailImage;

    @Builder
    public AlbumPostSaveRequestDto(String title, String content, String thumbnailImage) {
        this.title = title;
        this.content = content;
        this.thumbnailImage = thumbnailImage;
    }

    public AlbumPost toEntity(User user) {
        return AlbumPost.builder()
                .title(title)
                .content(content)
                .author(user)
                .thumbnailImage(thumbnailImage)
                .albumPostImages(new ArrayList<>())
                .build();
    }
}
