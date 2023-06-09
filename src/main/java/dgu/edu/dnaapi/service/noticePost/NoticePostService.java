package dgu.edu.dnaapi.service.noticePost;


import dgu.edu.dnaapi.controller.dto.PostSearchCondition;
import dgu.edu.dnaapi.domain.BoardSearchCriteria;
import dgu.edu.dnaapi.domain.NoticePost;
import dgu.edu.dnaapi.domain.NoticePostComment;
import dgu.edu.dnaapi.domain.dto.noticePost.NoticePostMetaDataResponseDto;
import dgu.edu.dnaapi.domain.dto.noticePost.NoticePostResponseDto;
import dgu.edu.dnaapi.domain.dto.noticePost.NoticePostUpdateRequestDto;
import dgu.edu.dnaapi.domain.response.DnaStatusCode;
import dgu.edu.dnaapi.domain.response.ListResponse;
import dgu.edu.dnaapi.exception.DNACustomException;
import dgu.edu.dnaapi.repository.noticePost.NoticePostCommentLikeRepository;
import dgu.edu.dnaapi.repository.noticePost.NoticePostCommentRepository;
import dgu.edu.dnaapi.repository.noticePost.NoticePostLikeRepository;
import dgu.edu.dnaapi.repository.noticePost.NoticePostRepository;
import dgu.edu.dnaapi.service.vo.PostCommentVO;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.springframework.util.StringUtils.hasText;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class NoticePostService {

    private final NoticePostRepository noticePostRepository;
    private final NoticePostLikeRepository noticePostLikeRepository;
    private final NoticePostCommentRepository noticePostCommentRepository;
    private final NoticePostCommentLikeRepository noticePostCommentLikeRepository;

    @Transactional
    public Long save(NoticePost noticePost) {
        if(!hasText(noticePost.getTitle()) || !hasText(noticePost.getContent()))
            throw new DNACustomException("제목이나 내용이 없는 글입니다. 게시글 작성 조건을 확인해주세요.", DnaStatusCode.INVALID_INPUT);
        return noticePostRepository.save(noticePost).getNoticePostId();
    }

    @Transactional
    public Long update(NoticePostUpdateRequestDto requestDto, Long updateNoticeId, Long userId) {
        NoticePost notice = noticePostRepository.findById(updateNoticeId).orElseThrow(
                () -> new DNACustomException("해당 게시글이 없습니다. id=" + updateNoticeId, DnaStatusCode.INVALID_POST));
        if (userId != notice.getAuthor().getId()){
            throw new DNACustomException("게시글의 작성자만 수정할 수 있습니다.", DnaStatusCode.INVALID_AUTHOR);
        }
        notice.update(requestDto.getTitle(), requestDto.getContent());
        return updateNoticeId;
    }

    public NoticePost findById(Long id) {
        return noticePostRepository.findById(id).orElseThrow(
                () -> new DNACustomException("해당 게시글이 없습니다. id=" + id, DnaStatusCode.INVALID_POST));
    }

    @Transactional
    public Long delete(Long deleteNoticePostId, Long userId) {
        NoticePost noticePost = noticePostRepository.findWithNoticePostCommentsByNoticePostId(deleteNoticePostId).orElseThrow(
                () -> new DNACustomException("해당 게시글이 없습니다. id=" + deleteNoticePostId, DnaStatusCode.INVALID_POST));
        if(noticePost.getAuthor().getId() != userId) {
            throw new DNACustomException("해당 게시글의 작성자만 게시글을 삭제할 수 있습니다.", DnaStatusCode.INVALID_AUTHOR);
        }
        List<Long> allCommentIds = noticePost.getComments().stream().map(NoticePostComment::getNoticePostCommentId).collect(Collectors.toList());
        noticePostCommentLikeRepository.deleteAllNoticePostCommentLikesInNoticePostCommentIds(allCommentIds);

        if(!allCommentIds.isEmpty()){
            List<NoticePostComment> noticePostCommentList = noticePostCommentRepository.findAllNoticePostCommentsWithNoticePostByNoticePostId(deleteNoticePostId);
            List<PostCommentVO> noticePostCommentHierarchyStructure = createNoticePostCommentHierarchyStructure(convertNoticePostCommentListToPostCommentVOList(noticePostCommentList));
            deleteAllMyChildNoticePostComments(noticePostCommentHierarchyStructure);
        }
        noticePostLikeRepository.deleteAllNoticePostLikesByNoticePostId(deleteNoticePostId);
        noticePostRepository.deleteNoticePostByNoticePostId(deleteNoticePostId);
        return deleteNoticePostId;
    }

    private List<PostCommentVO> convertNoticePostCommentListToPostCommentVOList(List<NoticePostComment> noticePostCommentList) {
        return noticePostCommentList.stream().map(PostCommentVO::convertToPostCommentVO).collect(Collectors.toList());
    }

    private List<PostCommentVO> createNoticePostCommentHierarchyStructure(List<PostCommentVO> postCommentVO){
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

    private void deleteAllMyChildNoticePostComments(List<PostCommentVO> noticeComments){
        for (PostCommentVO noticeComment : noticeComments) {
            deleteAllMyChildNoticePostComments(noticeComment.getChildrenComment());
            noticePostCommentRepository.deleteNoticePostCommentByNoticePostCommentId(noticeComment.getCommentId());
        }
    }

    public ListResponse findAllNoticesMetaDataWithCondition(PostSearchCondition condition, Pageable pageable) {
        List<NoticePostMetaDataResponseDto> noticePostMetaDataResponseDtoList = noticePostRepository.search(condition, pageable);
        boolean hasNext = false;
        if(noticePostMetaDataResponseDtoList.size() > pageable.getPageSize()){
            noticePostMetaDataResponseDtoList.remove(pageable.getPageSize());
            hasNext = true;
        }
        return ListResponse.builder()
                .list(noticePostMetaDataResponseDtoList)
                .hasNext(hasNext)
                .build();
    }

    public NoticePostResponseDto findNoticeWithLikedInfoByNoticeIdAndUserId(Long noticePostId, Long userId) {
        NoticePost noticePost = noticePostRepository.findById(noticePostId).orElseThrow(
                () -> new DNACustomException("해당 게시글이 없습니다. id=" + noticePostId, DnaStatusCode.INVALID_POST));
        boolean isNoticePostLikedByUser = (userId != null && noticePostLikeRepository.findNoticePostLikeByNoticePostIdAndUserId(noticePostId, userId));
        return new NoticePostResponseDto(noticePost, isNoticePostLikedByUser);
    }

    /*
    * Legacy Code (JPQL)
    * */

    public List<NoticePostMetaDataResponseDto> findAll() {
        List<NoticePost> noticePostList = noticePostRepository.findAll();
        return noticePostList.stream().map(NoticePostMetaDataResponseDto::new).collect(Collectors.toList());
    }

    public ListResponse getAllNotices(Long start, Pageable pageable){
        Slice<NoticePost> noticesList;
        if(start == 0){
            noticesList = noticePostRepository.findSliceBy(pageable);
        }else{
            noticesList = noticePostRepository.findAllByNoticeId(start, pageable);
        }

        return ListResponse.builder()
                .list(noticesList.getContent().stream().map(NoticePostMetaDataResponseDto::new))
                .hasNext(noticesList.hasNext())
                .build();
    }

    public ListResponse getAllNoticesByCriteria(Long start, BoardSearchCriteria criteria, String keyword, Pageable pageable){
        Slice<NoticePost> noticesList = null;

        if(criteria.equals(BoardSearchCriteria.TITLE)){
            if(start == 0){
                noticesList = noticePostRepository.findAllByTitleContaining(keyword, pageable);
            }else{
                noticesList = noticePostRepository.findAllByTitleContaining(start, keyword, pageable);
            }
        }else if (criteria.equals(BoardSearchCriteria.AUTHOR)){
            if(start == 0){
                noticesList = noticePostRepository.findAllByAuthor_UserNameContaining(keyword, pageable);
            }else{
                noticesList = noticePostRepository.findAllByAuthorContaining(start, keyword, pageable);
            }
        }else if (criteria.equals(BoardSearchCriteria.CONTENT)){
            if(start == 0){
                noticesList = noticePostRepository.findAllByContentContaining(keyword, pageable);
            }else{
                noticesList = noticePostRepository.findAllByContentContaining(start, keyword, pageable);
            }
        }else if (criteria.equals(BoardSearchCriteria.TITLE_AND_CONTENT)){
            if(start == 0){
                noticesList = noticePostRepository.findAllByTitleContainingOrContentContaining(keyword, keyword, pageable);
            }else{
                noticesList = noticePostRepository.findAllByTitleContainingOrContentContaining(start, keyword, pageable);
            }
        }else{
            throw new DNACustomException("Invalid BoardSearchCriteria option", DnaStatusCode.INVALID_SEARCH_OPTION);
        }

        if(noticesList == null){
            return ListResponse.builder()
                    .list(new ArrayList<>())
                    .hasNext(false)
                    .build();
        }

        return ListResponse.builder()
                .list(noticesList.getContent().stream().map(NoticePostMetaDataResponseDto::new))
                .hasNext(noticesList.hasNext())
                .build();
    }

}
