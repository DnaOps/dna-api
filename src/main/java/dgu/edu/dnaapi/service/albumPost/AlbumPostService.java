package dgu.edu.dnaapi.service.albumPost;

import dgu.edu.dnaapi.controller.dto.PostSearchCondition;
import dgu.edu.dnaapi.domain.AlbumPost;
import dgu.edu.dnaapi.domain.AlbumPostImage;
import dgu.edu.dnaapi.domain.dto.albumPost.AlbumPostResponseDto;
import dgu.edu.dnaapi.domain.dto.noticePost.NoticePostUpdateRequestDto;
import dgu.edu.dnaapi.domain.response.DnaStatusCode;
import dgu.edu.dnaapi.domain.response.ListResponse;
import dgu.edu.dnaapi.exception.DNACustomException;
import dgu.edu.dnaapi.repository.albumPost.AlbumPostCommentLikeRepository;
import dgu.edu.dnaapi.repository.albumPost.AlbumPostCommentRepository;
import dgu.edu.dnaapi.repository.albumPost.AlbumPostLikeRepository;
import dgu.edu.dnaapi.repository.albumPost.AlbumPostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.springframework.util.StringUtils.hasText;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class AlbumPostService {

    private final AlbumPostRepository albumPostRepository;
    private final AlbumPostCommentRepository albumPostCommentRepository;
    private final AlbumPostLikeRepository albumPostLikeRepository;
    private final AlbumPostCommentLikeRepository albumPostCommentLikeRepository;
    private final AlbumPostImageService albumPostImageService;

    @Transactional
    public Long save(AlbumPost albumPost, List<MultipartFile> albumPostImages, Integer thumbnailImageIndex) throws IOException {
        if(!hasText(albumPost.getTitle()) || !hasText(albumPost.getContent()))
            throw new DNACustomException("제목이나 내용이 없는 글입니다. 게시글 작성 조건을 확인해주세요.", DnaStatusCode.INVALID_INPUT);
        List<String> albumPostImageUrls = new ArrayList<>();
        if(albumPostImages != null){
            for (MultipartFile albumPostImage : albumPostImages) {
                albumPostImageUrls.add(albumPostImageService.upload(albumPostImage, "albumPost"));
            }
            List<AlbumPostImage> albumPostImageList = new ArrayList<>();
            for (String albumPostImageUrl : albumPostImageUrls) {
                albumPostImageList.add(new AlbumPostImage(albumPostImageUrl, albumPost));
            }
            albumPost.addAlbumPostImages(albumPostImageList);
            if(thumbnailImageIndex != null)
                albumPost.setThumbnailImage(albumPostImageUrls.get(thumbnailImageIndex));
        }

        return albumPostRepository.save(albumPost).getAlbumPostId();
    }

    public AlbumPostResponseDto findAlbumPostWithLikedInfoByAlbumPostIdAndUserId(Long albumPostId, Long userId) {
        AlbumPost albumPost = albumPostRepository.findWithAlbumPostImagesByAlbumPostId(albumPostId).orElseThrow(
                () -> new DNACustomException("해당 게시글이 없습니다. id=" + albumPostId, DnaStatusCode.INVALID_POST));
        boolean isNoticePostLikedByUser = (userId != null && albumPostLikeRepository.findAlbumPostLikeByAlbumPostIdAndUserId(albumPostId, userId));
        return new AlbumPostResponseDto(albumPost, isNoticePostLikedByUser);
    }

    public Long delete(Long albumPostId, Long id) {
        return albumPostId;
    }

    public Long update(NoticePostUpdateRequestDto requestDto, Long noticePostId, Long id) {
        return noticePostId;
    }

    public ListResponse findAllNoticesMetaDataWithCondition(PostSearchCondition condition, Pageable pageable) {

        return ListResponse.builder().build();
    }
}
