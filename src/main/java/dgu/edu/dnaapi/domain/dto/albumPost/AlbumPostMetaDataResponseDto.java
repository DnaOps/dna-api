package dgu.edu.dnaapi.domain.dto.albumPost;

import com.querydsl.core.annotations.QueryProjection;
import dgu.edu.dnaapi.domain.AlbumPost;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class AlbumPostMetaDataResponseDto {

    private Long albumPostId;
    private String title;
    private String author;
    private Long authorId;
    private int commentCount;
    private int likeCount;
    private int level;
    private String modifiedAt;
    private String thumbnailImage;

    public AlbumPostMetaDataResponseDto(AlbumPost entity) {
        this.albumPostId = entity.getAlbumPostId();
        this.title = entity.getTitle();
        this.author = entity.getAuthor().getUserName();
        this.authorId = entity.getAuthor().getId();
        this.commentCount = entity.getCommentCount();
        this.modifiedAt = entity.getLastModifiedDate().toString();
        this.likeCount = entity.getLikeCount();
        this.level = entity.getAuthor().getLevel();
        this.thumbnailImage = entity.getThumbnailImage();
    }

    @QueryProjection
    public AlbumPostMetaDataResponseDto(Long albumPostId, String title, String author, Long authorId, int commentCount, int likeCount, int level, LocalDateTime modifiedAt, String thumbnailImage) {
        this.albumPostId = albumPostId;
        this.title = title;
        this.author = author;
        this.authorId = authorId;
        this.commentCount = commentCount;
        this.likeCount = likeCount;
        this.level = level;
        this.modifiedAt = modifiedAt.toString();
        this.thumbnailImage = thumbnailImage;
    }
}
