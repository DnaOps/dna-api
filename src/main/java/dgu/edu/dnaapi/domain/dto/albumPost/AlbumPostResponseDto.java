package dgu.edu.dnaapi.domain.dto.albumPost;

import dgu.edu.dnaapi.domain.AlbumPost;
import dgu.edu.dnaapi.domain.AlbumPostImage;
import lombok.Getter;

import java.util.List;
import java.util.stream.Collectors;

@Getter
public class AlbumPostResponseDto extends AlbumPostMetaDataResponseDto{

    private String content;
    private Boolean isLikedByUser;
    private List<String> albumPostImages;
    private String thumbnailImage;

    public AlbumPostResponseDto(AlbumPost albumPost, Boolean isLikedByUser) {
        super(albumPost);
        this.content = albumPost.getContent();
        this.isLikedByUser = isLikedByUser;
        this.albumPostImages = albumPost.getAlbumPostImages().stream().map(AlbumPostImage::getImageUrl).collect(Collectors.toList());
        this.thumbnailImage = albumPost.getThumbnailImage();
    }
}
