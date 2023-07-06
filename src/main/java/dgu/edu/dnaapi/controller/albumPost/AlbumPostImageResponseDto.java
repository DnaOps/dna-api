package dgu.edu.dnaapi.controller.albumPost;

import lombok.Builder;
import lombok.Data;

@Data
public class AlbumPostImageResponseDto {

    private String url;
    private Boolean uploaded;

    @Builder
    public AlbumPostImageResponseDto(String url, Boolean uploaded) {
        this.url = url;
        this.uploaded = uploaded;
    }
}
