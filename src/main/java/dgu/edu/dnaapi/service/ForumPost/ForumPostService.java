package dgu.edu.dnaapi.service.ForumPost;

import dgu.edu.dnaapi.controller.dto.PostSearchCondition;
import dgu.edu.dnaapi.domain.ForumPostComment;
import dgu.edu.dnaapi.domain.ForumPost;
import dgu.edu.dnaapi.domain.dto.forumPost.ForumPostMetaDataResponseDto;
import dgu.edu.dnaapi.domain.dto.forumPost.ForumPostResponseDto;
import dgu.edu.dnaapi.domain.dto.forumPost.ForumPostSaveRequestDto;
import dgu.edu.dnaapi.domain.response.DnaStatusCode;
import dgu.edu.dnaapi.domain.response.ListResponse;
import dgu.edu.dnaapi.exception.DNACustomException;
import dgu.edu.dnaapi.repository.forumPost.ForumPostCommentLikeRepository;
import dgu.edu.dnaapi.repository.forumPost.ForumPostCommentRepository;
import dgu.edu.dnaapi.repository.forumPost.ForumPostLikeRepository;
import dgu.edu.dnaapi.repository.forumPost.ForumPostRepository;
import dgu.edu.dnaapi.service.vo.PostCommentVO;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.springframework.util.StringUtils.hasText;

@RequiredArgsConstructor
@Service
public class ForumPostService {

    private final ForumPostRepository forumPostRepository;
    private final ForumPostLikeRepository forumPostLikeRepository;
    private final ForumPostCommentRepository forumPostCommentRepository;
    private final ForumPostCommentLikeRepository forumPostCommentLikeRepository;

    @Transactional
    public Long save(ForumPost forumPost) {
        if(!hasText(forumPost.getTitle()) || !hasText(forumPost.getContent()))
            throw new DNACustomException("제목이나 내용이 없는 글입니다. 게시글 작성 조건을 확인해주세요.", DnaStatusCode.INVALID_INPUT);
        return forumPostRepository.save(forumPost).getForumPostId();
    }

    @Transactional
    public Long update(ForumPostSaveRequestDto requestDto, Long userId, Long forumId) {

        ForumPost forumPost = forumPostRepository.findById(forumId).orElseThrow(
                () -> new DNACustomException("해당 게시글이 없습니다. id=" + forumId, DnaStatusCode.INVALID_POST));
        if (userId != forumPost.getAuthor().getId()){
            throw new DNACustomException("게시글의 작성자만 수정할 수 있습니다.", DnaStatusCode.INVALID_AUTHOR);
        }
        forumPost.update(requestDto.getTitle(), requestDto.getContent());
        return forumPost.getForumPostId();
    }

    public ForumPostResponseDto findForumPostWithLikedInfoByForumPostIdAndUserId(Long forumPostId, Long userId) {
        ForumPost forumPost = forumPostRepository.findById(forumPostId).orElseThrow(
                () -> new DNACustomException("해당 게시글이 없습니다. id=" + forumPostId, DnaStatusCode.INVALID_POST));
        boolean isForumPostLikedByUser = (userId != null && forumPostLikeRepository.findForumPostLikeByForumPostIdAndUserId(forumPost.getForumPostId(), userId));
        return new ForumPostResponseDto(forumPost, isForumPostLikedByUser);
    }

    public ForumPost findById(Long id) {
        return forumPostRepository.findById(id).orElseThrow(
                () -> new DNACustomException("해당 게시글이 없습니다. id=" + id, DnaStatusCode.INVALID_POST));
    }

    @Transactional
    public Long delete(Long deleteForumId, Long userId) {
        ForumPost entity = forumPostRepository.findWithForumPostCommentsByForumPostId(deleteForumId).orElseThrow(
                () -> new DNACustomException("해당 게시글이 없습니다. id=" + deleteForumId, DnaStatusCode.INVALID_POST));
        if(!entity.getAuthor().getId().equals(userId)) {
            throw new DNACustomException("해당 게시글의 작성자만 게시글을 삭제할 수 있습니다.", DnaStatusCode.INVALID_AUTHOR);
        }
        List<Long> allCommentIds = entity.getComments().stream().map(ForumPostComment::getForumPostCommentId).collect(Collectors.toList());
        forumPostCommentLikeRepository.deleteAllForumCommentsLikesInForumCommentsIds(allCommentIds);

        if(!allCommentIds.isEmpty()){
            List<ForumPostComment> forumPostCommentList = forumPostCommentRepository.findAllForumPostCommentsByForumPostId(deleteForumId);
            List<PostCommentVO> forumCommentHierarchyStructure = createForumPostCommentHierarchyStructure(convertForumPostCommentListToPostCommentVOList(forumPostCommentList));
            deleteAllMyChildForumPostComments(forumCommentHierarchyStructure);
        }
        forumPostLikeRepository.deleteAllForumPostLikesByForumPostId(deleteForumId);
        forumPostRepository.deleteForumPostByForumPostId(deleteForumId);
        return deleteForumId;
    }

    private List<PostCommentVO> convertForumPostCommentListToPostCommentVOList(List<ForumPostComment> forumPostCommentList) {
        return forumPostCommentList.stream().map(PostCommentVO::convertToPostCommentVO).collect(Collectors.toList());
    }

    public ListResponse findAllForumPostMetaDataWithCondition(PostSearchCondition condition, Pageable pageable) {
        List<ForumPostMetaDataResponseDto> forumPostMetaDataResponseDtoList = forumPostRepository.search(condition, pageable);
        boolean hasNext = false;
        if(forumPostMetaDataResponseDtoList.size() > pageable.getPageSize()){
            forumPostMetaDataResponseDtoList.remove(pageable.getPageSize());
            hasNext = true;
        }
        return ListResponse.builder()
                        .list(forumPostMetaDataResponseDtoList)
                        .hasNext(hasNext)
                        .build();
    }

    private List<PostCommentVO> createForumPostCommentHierarchyStructure(List<PostCommentVO> postCommentVO){
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

    private void deleteAllMyChildForumPostComments(List<PostCommentVO> forumComments){
        for (PostCommentVO forumComment : forumComments) {
            deleteAllMyChildForumPostComments(forumComment.getChildrenComment());
//            System.out.println("DELETE => " + forumComment.getCommentId());
            forumPostCommentRepository.deleteForumPostCommentByForumPostCommentId(forumComment.getCommentId());
        }
    }
}
