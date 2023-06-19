package dgu.edu.dnaapi.service.albumPost;

import dgu.edu.dnaapi.controller.dto.CommentSearchCondition;
import dgu.edu.dnaapi.domain.*;
import dgu.edu.dnaapi.domain.dto.comment.CommentSaveRequestDto;
import dgu.edu.dnaapi.domain.dto.comment.CommentUpdateRequestDto;
import dgu.edu.dnaapi.domain.dto.comment.PostCommentResponseDto;
import dgu.edu.dnaapi.domain.response.DnaStatusCode;
import dgu.edu.dnaapi.domain.response.ListResponse;
import dgu.edu.dnaapi.exception.DNACustomException;
import dgu.edu.dnaapi.repository.albumPost.AlbumPostCommentLikeRepository;
import dgu.edu.dnaapi.repository.albumPost.AlbumPostCommentRepository;
import dgu.edu.dnaapi.repository.albumPost.AlbumPostRepository;
import dgu.edu.dnaapi.service.vo.PostCommentVO;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class AlbumPostCommentService {

    private final AlbumPostRepository albumPostRepository;
    private final AlbumPostCommentRepository albumPostCommentRepository;
    private final AlbumPostCommentLikeRepository albumPostCommentLikeRepository;

    @Transactional
    public Long save(User user, CommentSaveRequestDto requestDto, Long albumPostId) {
        AlbumPostComment comment = requestDto.toAlbumPostComment(user);
        AlbumPost albumPost = albumPostRepository.findById(albumPostId).orElseThrow(
                () -> new DNACustomException("해당 글이 없습니다. id=" + albumPostId, DnaStatusCode.INVALID_POST));
        comment.registerAlbumPost(albumPost);

        if(requestDto.getParentCommentId() != null) {
            Long parentCommentId = requestDto.getParentCommentId();
            AlbumPostComment parent = albumPostCommentRepository.findWithAlbumPostByAlbumPostCommentId(parentCommentId).orElseThrow(
                    () -> new DNACustomException("해당 댓글이 없습니다. id=" + parentCommentId, DnaStatusCode.INVALID_COMMENT));
            if(parent.getAlbumPost().getAlbumPostId() != albumPostId)
                throw new DNACustomException("대댓글 부모 연관관계가 잘못되었습니다.", DnaStatusCode.INVALID_COMMENT);

            comment.registerParentAlbumPostComment(parent);
        }
        albumPostRepository.increaseCommentCount(albumPostId);
        return albumPostCommentRepository.save(comment).getAlbumPostCommentId();
    }

    public ListResponse findAllAlbumComments(CommentSearchCondition commentSearchCondition, Long albumPostId, Pageable pageable) {
        List<AlbumPostComment> commentsList = albumPostCommentRepository.search(albumPostId, commentSearchCondition.getStart(), pageable);
        boolean hasNext = isHasNext(pageable.getPageSize(), commentsList);

        List<Long> commentIds = getAlbumPostCommentIds(commentsList);
        commentsList.addAll(albumPostCommentRepository.findAllReplyAlbumPostComments(albumPostId, commentIds));
        List<PostCommentResponseDto> albumPostCommentResponseDtoList = convertToAlbumPostCommentResponseDto(commentsList);
        Set<Long> albumCommentIdsLikedByUser = new HashSet<>();
        if(commentSearchCondition.hasUserId()){
            albumCommentIdsLikedByUser = getAlbumPostCommentIdsLikedByUser(commentSearchCondition.getUserId(), getAlbumPostCommentIds(commentsList));
        }
        List<PostCommentResponseDto> result = createPostCommentResponseHierarchyStructure(albumPostCommentResponseDtoList, albumCommentIdsLikedByUser);
        return ListResponse.builder().list(result).hasNext(hasNext).build();
    }

    private boolean isHasNext(int pageSize, List<AlbumPostComment> commentsList) {
        if(commentsList.size() > pageSize){
            commentsList.remove(pageSize);
            return true;
        }
        return false;
    }

    private List<Long> getAlbumPostCommentIds(List<AlbumPostComment> commentsList) {
        return commentsList.stream().map(AlbumPostComment::getAlbumPostCommentId).collect(Collectors.toList());
    }

    private List<PostCommentResponseDto> convertToAlbumPostCommentResponseDto(List<AlbumPostComment> commentsList) {
        return commentsList.stream().
                map(c -> PostCommentResponseDto.convertAlbumCommentToResponseDto(c)).collect(Collectors.toList());
    }

    private Set<Long> getAlbumPostCommentIdsLikedByUser(Long userId, List<Long> allForumCommentIds) {
        return albumPostCommentLikeRepository.findAlbumPostCommentIdsLikedByUserId(userId, allForumCommentIds);
    }

    private List<PostCommentResponseDto> createPostCommentResponseHierarchyStructure(List<PostCommentResponseDto> postCommentResponseDtoList, Set<Long> forumCommentIdsLikedByUser){
        List<PostCommentResponseDto> result = new ArrayList<>();
        Map<Long, PostCommentResponseDto> replyCommentMap = new HashMap<>();
        postCommentResponseDtoList.stream().forEach(
                c -> {
                    if(forumCommentIdsLikedByUser.contains(c.getCommentId())) c.likedByUser();
                    replyCommentMap.put(c.getCommentId(), c);
                    if (c.getParentCommentId() != null) replyCommentMap.get(c.getParentCommentId()).getChildrenComments().add(c);
                    else result.add(c);
                });
        return result;
    }

    @Transactional
    public Long update(Long userId, CommentUpdateRequestDto requestDto, Long albumPostCommentId, Long albumPostId) {
        AlbumPostComment comment = albumPostCommentRepository.findWithAlbumPostByAlbumPostCommentId(albumPostCommentId).orElseThrow(
                () -> new DNACustomException("해당 댓글이 없습니다. id=" + albumPostCommentId, DnaStatusCode.INVALID_COMMENT));
        if(!albumPostId.equals(comment.getAlbumPost().getAlbumPostId())){
            throw new DNACustomException("해당 댓글이 해당 게시글에 존재하지 않습니다.", DnaStatusCode.INVALID_COMMENT);
        }
        if(!comment.getAuthor().getId().equals(userId)) {
            throw new DNACustomException("작성자만 댓글을 수정할 수 있습니다.", DnaStatusCode.INVALID_AUTHOR);
        }
        comment.update(requestDto.getContent());
        return comment.getAlbumPostCommentId();
    }

    @Transactional
    public Long delete(Long userId, Long deleteAlbumPostCommentId, Long albumPostId) {
        AlbumPostComment comment = albumPostCommentRepository.findWithAuthorAndAlbumPostByAlbumPostCommentId(deleteAlbumPostCommentId).orElseThrow(
                () -> new DNACustomException("해당 댓글이 없습니다. id=" + deleteAlbumPostCommentId, DnaStatusCode.INVALID_COMMENT));
        if(comment.getIsDeleted())
            throw new DNACustomException("이미 삭제된 댓글입니다. id = " + deleteAlbumPostCommentId ,DnaStatusCode.INVALID_DELETE_COMMENT);
        if(!comment.getAuthor().getId().equals(userId)) {
            throw new DNACustomException("작성자만 댓글을 삭제할 수 있습니다.", DnaStatusCode.INVALID_AUTHOR);
        }
        if (!albumPostId.equals(comment.getAlbumPost().getAlbumPostId())) {
            throw new DNACustomException("해당 댓글이 해당 게시글에 존재하지 않습니다.", DnaStatusCode.INVALID_COMMENT);
        }

        albumPostRepository.decreaseCommentCount(comment.getAlbumPost().getAlbumPostId());
        Long commentGroupId = comment.getCommentGroupId();
        List<AlbumPostComment> albumPostCommentList = new ArrayList<>();
        if(commentGroupId == null){
            commentGroupId = comment.getAlbumPostCommentId();
            albumPostCommentList.add(comment);
        }
        List<AlbumPostComment> allReplyComments = albumPostCommentRepository.findAllReplyAlbumPostCommentsByCommentGroupId(comment.getAlbumPost().getAlbumPostId(), commentGroupId);
        albumPostCommentList.addAll(allReplyComments);
        List<PostCommentVO> postCommentVOList = albumPostCommentList.stream().map(PostCommentVO::convertToPostCommentVO).collect(Collectors.toList());

        Map<Long, PostCommentVO> albumPostCommentsMap = createAlbumPostCommentHierarchyStructure(postCommentVOList);

        PostCommentVO deletedPostCommentVO = albumPostCommentsMap.get(deleteAlbumPostCommentId);
        deletedPostCommentVO.setDeletedStatus();
        if (checkAllMyChildAlbumPostCommentIsDeleted(deletedPostCommentVO.getChildrenComment())){
            albumPostCommentRepository.deleteById(deleteAlbumPostCommentId);
        }else{
            albumPostCommentRepository.softDelete(deleteAlbumPostCommentId);
        }
        albumPostCommentLikeRepository.deleteAllAlbumPostCommentLikesByAlbumPostCommentId(deleteAlbumPostCommentId);
        if(deletedPostCommentVO.hasParentComment())
            shouldParentCommentBeDeleted(albumPostCommentsMap, deletedPostCommentVO.getParentCommentId());

        return deleteAlbumPostCommentId;
    }

    private Map<Long, PostCommentVO> createAlbumPostCommentHierarchyStructure(List<PostCommentVO> postCommentVO){
        Map<Long, PostCommentVO> commentsMap = new HashMap<>();
        postCommentVO.stream().forEach(
                c -> {
                    commentsMap.put(c.getCommentId(), c);
                    if(commentsMap.containsKey(c.getParentCommentId())) commentsMap.get(c.getParentCommentId()).addChild(c);
                }
        );
        return commentsMap;
    }

    private boolean checkAllMyChildAlbumPostCommentIsDeleted(List<PostCommentVO> albumPostComments){
        for (PostCommentVO postCommentVO : albumPostComments) {
            if (!postCommentVO.isDeleted()) {
                return false;
            }
            if(!checkAllMyChildAlbumPostCommentIsDeleted(postCommentVO.getChildrenComment())){
                return false;
            }
        }
        return true;
    }

    private void shouldParentCommentBeDeleted(Map<Long, PostCommentVO> albumPostCommentsMap, Long parentCommentId){
        if(albumPostCommentsMap.containsKey(parentCommentId)){
            PostCommentVO parentComment = albumPostCommentsMap.get(parentCommentId);
            if(parentComment.isDeleted() && checkAllMyChildAlbumPostCommentIsDeleted(parentComment.getChildrenComment())){
                albumPostCommentRepository.deleteById(parentCommentId);
                if(parentComment.hasParentComment())
                    shouldParentCommentBeDeleted(albumPostCommentsMap, parentComment.getParentCommentId());
            }
        }
    }
}
