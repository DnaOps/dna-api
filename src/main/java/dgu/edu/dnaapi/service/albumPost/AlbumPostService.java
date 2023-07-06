package dgu.edu.dnaapi.service.albumPost;

import dgu.edu.dnaapi.controller.dto.PostSearchCondition;
import dgu.edu.dnaapi.domain.*;
import dgu.edu.dnaapi.domain.dto.albumPost.AlbumPostMetaDataResponseDto;
import dgu.edu.dnaapi.domain.dto.albumPost.AlbumPostResponseDto;
import dgu.edu.dnaapi.domain.dto.albumPost.AlbumPostSaveRequestDto;
import dgu.edu.dnaapi.domain.response.DnaStatusCode;
import dgu.edu.dnaapi.domain.response.ListResponse;
import dgu.edu.dnaapi.exception.DNACustomException;
import dgu.edu.dnaapi.repository.albumPost.*;
import dgu.edu.dnaapi.service.albumPost.dto.AmazonS3ObjectInfo;
import dgu.edu.dnaapi.service.vo.PostCommentVO;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import static org.springframework.util.StringUtils.hasText;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class AlbumPostService {

    private final AlbumPostRepository albumPostRepository;
    private final AlbumPostCommentRepository albumPostCommentRepository;
    private final AlbumPostLikeRepository albumPostLikeRepository;
    private final AlbumPostCommentLikeRepository albumPostCommentLikeRepository;
    private final AlbumPostImageRepository albumPostImageRepository;
    private final AlbumPostImageService albumPostImageService;

    @Transactional
    public Long save(AlbumPost albumPost, List<MultipartFile> albumPostImages, Integer thumbnailImageIndex) throws IOException {
        if(!hasText(albumPost.getTitle()) || !hasText(albumPost.getContent()))
            throw new DNACustomException("제목이나 내용이 없는 글입니다. 게시글 작성 조건을 확인해주세요.", DnaStatusCode.INVALID_INPUT);
        if(albumPostImages != null){
            List<AmazonS3ObjectInfo> albumPostImageInfos = new ArrayList<>();
            for (MultipartFile albumPostImage : albumPostImages) {
                albumPostImageInfos.add(albumPostImageService.upload(albumPostImage, "albumPost"));
            }
            List<AlbumPostImage> albumPostImageList = new ArrayList<>();
            for (AmazonS3ObjectInfo info : albumPostImageInfos) {
                albumPostImageList.add(new AlbumPostImage(info.getImageURL(), albumPost, info.getKey()));
            }
            albumPost.addAlbumPostImages(albumPostImageList);
            if(thumbnailImageIndex != null)
                albumPost.setThumbnailImage(albumPostImageInfos.get(thumbnailImageIndex).getImageURL());
        }

        return albumPostRepository.save(albumPost).getAlbumPostId();
    }

    public AlbumPostResponseDto findAlbumPostWithLikedInfoByAlbumPostIdAndUserId(Long albumPostId, Long userId) {
        AlbumPost albumPost = albumPostRepository.findWithAlbumPostImagesByAlbumPostId(albumPostId).orElseThrow(
                () -> new DNACustomException("해당 게시글이 없습니다. id=" + albumPostId, DnaStatusCode.INVALID_POST));
        boolean isNoticePostLikedByUser = (userId != null && albumPostLikeRepository.findAlbumPostLikeByAlbumPostIdAndUserId(albumPostId, userId));
        return new AlbumPostResponseDto(albumPost, isNoticePostLikedByUser);
    }

    @Transactional
    public Long delete(Long deleteAlbumPostId, Long userId) {
        AlbumPost albumPost = albumPostRepository.findWithAlbumPostCommentsByAlbumPostId(deleteAlbumPostId).orElseThrow(
                () -> new DNACustomException("해당 게시글이 없습니다. id=" + deleteAlbumPostId, DnaStatusCode.INVALID_POST));
        if(!albumPost.getAuthor().getId().equals(userId)) {
            throw new DNACustomException("해당 게시글의 작성자만 게시글을 삭제할 수 있습니다.", DnaStatusCode.INVALID_AUTHOR);
        }
        List<Long> allCommentIds = albumPost.getComments().stream().map(AlbumPostComment::getAlbumPostCommentId).collect(Collectors.toList());
        // List<AlbumPostImage> albumPostImages = albumPostImageRepository.findAllAlbumPostImageByAlbumPostId(deleteAlbumPostId);

        albumPostCommentLikeRepository.deleteAllAlbumPostCommentLikesInAlbumPostCommentsIds(allCommentIds);

        if(!allCommentIds.isEmpty()){
            List<AlbumPostComment> albumPostCommentList = albumPostCommentRepository.findAllAlbumPostCommentsByAlbumPostId(deleteAlbumPostId);
            List<PostCommentVO> albumPostCommentHierarchyStructure = createAlbumPostCommentHierarchyStructure(convertAlbumPostCommentListToPostCommentVOList(albumPostCommentList));
            deleteAllMyChildAlbumPostComments(albumPostCommentHierarchyStructure);
        }
        albumPostLikeRepository.deleteAllAlbumPostLikesByAlbumPostId(deleteAlbumPostId);
        // albumPostImageRepository.deleteAlbumPostImagesByAlbumPostId(deleteAlbumPostId);
        albumPostRepository.deleteAlbumPostByAlbumPostId(deleteAlbumPostId);
//        List<String> imageKeys = albumPostImages.stream().map(AlbumPostImage::getImageKey).collect(Collectors.toList());
//        for (String imageKey : imageKeys) {
//            albumPostImageService.delete(imageKey);
//        }
        return deleteAlbumPostId;
    }

    private List<PostCommentVO> convertAlbumPostCommentListToPostCommentVOList(List<AlbumPostComment> albumPostComments) {
        return albumPostComments.stream().map(PostCommentVO::convertToPostCommentVO).collect(Collectors.toList());
    }

    private List<PostCommentVO> createAlbumPostCommentHierarchyStructure(List<PostCommentVO> postCommentVO){
        List<PostCommentVO> result = new ArrayList<>();
        Map<Long, PostCommentVO> replyCommentMap = new HashMap<>();
        postCommentVO.stream().forEach(
                c -> {
                    replyCommentMap.put(c.getCommentId(), c);
                    if (c.hasParentComment()) replyCommentMap.get(c.getParentCommentId()).addChild(c);
                    else result.add(c);
                });
        return result;
    }

    private void deleteAllMyChildAlbumPostComments(List<PostCommentVO> albumPostComments){
        for (PostCommentVO albumPostComment : albumPostComments) {
            deleteAllMyChildAlbumPostComments(albumPostComment.getChildrenComment());
            albumPostCommentRepository.deleteAlbumPostCommentByAlbumPostCommentId(albumPostComment.getCommentId());
        }
    }

//    @Transactional
//    public Long update_lagacy(AlbumPostUpdateRequestDto requestDto, Long albumPostId, User user, List<MultipartFile> albumPostImages) throws IOException {
//
//        AlbumPost originalAlbumPost = albumPostRepository.findWithAlbumPostImagesByAlbumPostId(albumPostId)
//                .orElseThrow(() -> new DNACustomException(DnaStatusCode.INVALID_POST));
//
//        if (!originalAlbumPost.getAuthor().getId().equals(user.getId()))
//            throw new DNACustomException(DnaStatusCode.INVALID_AUTHOR);
//
//        List<AmazonS3ObjectInfo> albumPostImageInfos = new ArrayList<>();
//        if(albumPostImages != null){
//            for (MultipartFile albumPostImage : albumPostImages) {
//                albumPostImageInfos.add(albumPostImageService.upload(albumPostImage, "albumPost"));
//            }
//            for (AmazonS3ObjectInfo info : albumPostImageInfos) {
//                albumPostImageRepository.save(new AlbumPostImage(info.getImageURL(), originalAlbumPost, info.getKey()));
//            }
//        }
//
//        String thumbnailImage = null;
//        if(hasText(requestDto.getThumbnailImage()))
//            thumbnailImage = requestDto.getThumbnailImage();
//        else if(requestDto.getThumbnailImageIndex() != null)
//            thumbnailImage = albumPostImageInfos.get(requestDto.getThumbnailImageIndex()).getImageURL();
//
//        originalAlbumPost.update(requestDto.getTitle(), requestDto.getContent(), thumbnailImage);
//
//        List<String> deleteImageKeys = new ArrayList<>();
//        for (AlbumPostImage albumPostImage : originalAlbumPost.getAlbumPostImages()) {
//            for (String deleteImage : requestDto.getDeleteImages()) {
//                if(albumPostImage.getImageUrl().equals(deleteImage)){
//                    albumPostImageRepository.deleteByAlbumPostImageId(albumPostImage.getAlbumPostImageId());
//                    deleteImageKeys.add(albumPostImage.getImageKey());
//                    break;
//                }
//            }
//        }
//
//        for (String deleteImageKey : deleteImageKeys) {
//            albumPostImageService.delete(deleteImageKey);
//        }
//        return albumPostId;
//    }

    public ListResponse findAllAlbumPostMetaDataWithCondition(PostSearchCondition condition, Pageable pageable) {
        List<AlbumPostMetaDataResponseDto> albumPostMetaDataResponseDtoList = albumPostRepository.search(condition, pageable);
        boolean hasNext = false;
        if(albumPostMetaDataResponseDtoList.size() > pageable.getPageSize()){
            albumPostMetaDataResponseDtoList.remove(pageable.getPageSize());
            hasNext = true;
        }
        return ListResponse.builder()
                .list(albumPostMetaDataResponseDtoList)
                .hasNext(hasNext)
                .build();
    }

    @Transactional
    public AmazonS3ObjectInfo saveImage(MultipartFile albumPostImage) throws IOException {
        AmazonS3ObjectInfo amazonS3ObjectInfo = albumPostImageService.upload(albumPostImage, "albumPost");
        return amazonS3ObjectInfo;
    }

    @Transactional
    public Long savePost(AlbumPost albumPost){
        if(!hasText(albumPost.getTitle()) || !hasText(albumPost.getContent()))
            throw new DNACustomException("제목이나 내용이 없는 글입니다. 게시글 작성 조건을 확인해주세요.", DnaStatusCode.INVALID_INPUT);
        return albumPostRepository.save(albumPost).getAlbumPostId();
    }

    @Transactional
    public Long updateAlbumPost(AlbumPostSaveRequestDto requestDto, Long albumPostId, User user) {
        AlbumPost albumPost = albumPostRepository.findWithAuthorByAlbumPostId(albumPostId).orElseThrow(
                () -> new DNACustomException(DnaStatusCode.INVALID_POST));

        if(!user.getId().equals(albumPost.getAuthor().getId()))
            throw new DNACustomException(DnaStatusCode.INVALID_AUTHOR);

        albumPost.update(requestDto.getTitle(), requestDto.getTitle(), requestDto.getThumbnailImage());
        return albumPost.getAlbumPostId();
    }
}
