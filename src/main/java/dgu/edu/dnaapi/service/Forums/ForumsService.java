package dgu.edu.dnaapi.service.Forums;

import dgu.edu.dnaapi.controller.dto.BoardSearchCondition;
import dgu.edu.dnaapi.domain.ForumComments;
import dgu.edu.dnaapi.domain.Forums;
import dgu.edu.dnaapi.domain.dto.forum.ForumSaveRequestDto;
import dgu.edu.dnaapi.domain.dto.forum.ForumsMetaDataResponseDto;
import dgu.edu.dnaapi.domain.dto.forum.ForumsResponseDto;
import dgu.edu.dnaapi.domain.response.DnaStatusCode;
import dgu.edu.dnaapi.domain.response.ListResponse;
import dgu.edu.dnaapi.exception.DNACustomException;
import dgu.edu.dnaapi.repository.forum.ForumCommentsLikesRepository;
import dgu.edu.dnaapi.repository.forum.ForumCommentsRepository;
import dgu.edu.dnaapi.repository.forum.ForumLikesRepository;
import dgu.edu.dnaapi.repository.forum.ForumsRepository;
import dgu.edu.dnaapi.service.Forums.vo.ForumCommentVO;
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
public class ForumsService {

    private final ForumsRepository forumsRepository;
    private final ForumLikesRepository forumLikesRepository;
    private final ForumCommentsRepository forumCommentsRepository;
    private final ForumCommentsLikesRepository forumCommentsLikesRepository;

    @Transactional
    public Long save(Forums forums) {
        if(!hasText(forums.getTitle()) || !hasText(forums.getContent()))
            throw new DNACustomException("제목이나 내용이 없는 글입니다. 게시글 작성 조건을 확인해주세요.", DnaStatusCode.INVALID_INPUT);
        return forumsRepository.save(forums).getForumId();
    }

    @Transactional
    public Long update(ForumSaveRequestDto requestDto, Long userId, Long forumId) {

        Forums forum = forumsRepository.findById(forumId).orElseThrow(
                () -> new DNACustomException("해당 게시글이 없습니다. id=" + forumId, DnaStatusCode.INVALID_POST));
        if (userId != forum.getAuthor().getId()){
            throw new DNACustomException("게시글의 작성자만 수정할 수 있습니다.", DnaStatusCode.INVALID_AUTHOR);
        }
        forum.update(requestDto.getTitle(), requestDto.getContent());
        return forum.getForumId();
    }

    public ForumsResponseDto findForumWithLikedInfoByForumIdAndUserId(Long id, Long userId) {
        Forums forum = forumsRepository.findById(id).orElseThrow(
                () -> new DNACustomException("해당 게시글이 없습니다. id=" + id, DnaStatusCode.INVALID_POST));
        boolean isForumLikedByUser = (userId != null && forumLikesRepository.findForumLikesByUserIdAndForumId(forum.getForumId(), userId));
        return new ForumsResponseDto(forum, isForumLikedByUser);
    }

    public Forums findById(Long id) {
        return forumsRepository.findById(id).orElseThrow(
                () -> new DNACustomException("해당 게시글이 없습니다. id=" + id, DnaStatusCode.INVALID_POST));
    }

    @Transactional
    public Long delete(Long deleteForumId, Long userId) {
        Forums entity = forumsRepository.findByIdWithComments(deleteForumId).orElseThrow(
                () -> new DNACustomException("해당 게시글이 없습니다. id=" + deleteForumId, DnaStatusCode.INVALID_POST));
        if(!entity.getAuthor().getId().equals(userId)) {
            throw new DNACustomException("해당 게시글의 작성자만 게시글을 삭제할 수 있습니다.", DnaStatusCode.INVALID_AUTHOR);
        }
        List<Long> allCommentIds = entity.getComments().stream().map(ForumComments::getCommentId).collect(Collectors.toList());
        forumCommentsLikesRepository.deleteAllForumCommentsLikesInForumCommentsIds(allCommentIds);

        if(!allCommentIds.isEmpty()){
            List<ForumComments> forumCommentsList = forumCommentsRepository.findAllCommentsByForumId(deleteForumId);
            List<ForumCommentVO> forumCommentHierarchyStructure = createForumCommentHierarchyStructure(convertToForumCommentVOList(forumCommentsList));
            deleteAllForumComments(forumCommentHierarchyStructure);
        }
        forumLikesRepository.deleteForumLikesByForumId(deleteForumId);
        forumsRepository.deleteForumsByForumId(deleteForumId);
        return deleteForumId;
    }

    private List<ForumCommentVO> convertToForumCommentVOList(List<ForumComments> forumCommentsList) {
        return forumCommentsList.stream().map(ForumCommentVO::convertToForumCommentVO).collect(Collectors.toList());
    }

    public ListResponse findAllForumsMetaDataWithCondition(BoardSearchCondition condition, Pageable pageable) {
        List<ForumsMetaDataResponseDto> forums = forumsRepository.search(condition, pageable);
        boolean hasNext = false;
        if(forums.size() > pageable.getPageSize()){
            forums.remove(pageable.getPageSize());
            hasNext = true;
        }
        return ListResponse.builder()
                        .list(forums)
                        .hasNext(hasNext)
                        .build();
    }

    private List<ForumCommentVO> createForumCommentHierarchyStructure(List<ForumCommentVO> forumCommentVO){
        List<ForumCommentVO> result = new ArrayList<>();
        Map<Long, ForumCommentVO> replyCommentMap = new HashMap<>();
        forumCommentVO.stream().forEach(
                c -> {
                    replyCommentMap.put(c.getCommentId(), c);
                    if (c.hasParentComment()) replyCommentMap.get(c.getParentCommentId()).addChild(c);
                    else result.add(c);
                });
        return result;
    }

    private void deleteAllForumComments(List<ForumCommentVO> forumComments){
        for (ForumCommentVO forumComment : forumComments) {
            deleteAllForumComments(forumComment.getChildrenComment());
//            System.out.println("DELETE => " + forumComment.getCommentId());
            forumCommentsRepository.deleteForumCommentByForumCommentId(forumComment.getCommentId());
        }
    }
}
