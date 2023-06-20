package dgu.edu.dnaapi.service.studyPost;

import dgu.edu.dnaapi.controller.dto.PostSearchCondition;
import dgu.edu.dnaapi.domain.StudyPost;
import dgu.edu.dnaapi.domain.StudyPostComment;
import dgu.edu.dnaapi.domain.dto.studyPost.StudyPostMetaDataResponseDto;
import dgu.edu.dnaapi.domain.dto.studyPost.StudyPostResponseDto;
import dgu.edu.dnaapi.domain.dto.studyPost.StudyPostSaveRequestDto;
import dgu.edu.dnaapi.domain.response.DnaStatusCode;
import dgu.edu.dnaapi.domain.response.ListResponse;
import dgu.edu.dnaapi.exception.DNACustomException;
import dgu.edu.dnaapi.repository.studyPost.StudyPostCommentLikeRepository;
import dgu.edu.dnaapi.repository.studyPost.StudyPostCommentRepository;
import dgu.edu.dnaapi.repository.studyPost.StudyPostLikeRepository;
import dgu.edu.dnaapi.repository.studyPost.StudyPostRepository;
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

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class StudyPostService {

    private final StudyPostRepository studyPostRepository;
    private final StudyPostLikeRepository studyPostLikeRepository;
    private final StudyPostCommentRepository studyPostCommentRepository;
    private final StudyPostCommentLikeRepository studyPostCommentLikeRepository;

    @Transactional
    public Long save(StudyPost studyPost) {
        if(!hasText(studyPost.getTitle()) || !hasText(studyPost.getContent()))
            throw new DNACustomException("제목이나 내용이 없는 글입니다. 게시글 작성 조건을 확인해주세요.", DnaStatusCode.INVALID_INPUT);
        return studyPostRepository.save(studyPost).getStudyPostId();
    }

    @Transactional
    public Long update(StudyPostSaveRequestDto requestDto, Long updateStudyPostId, Long userId) {
        StudyPost studyPost = studyPostRepository.findWithAuthorByStudyPostId(updateStudyPostId).orElseThrow(
                () -> new DNACustomException("해당 게시글이 없습니다. id=" + updateStudyPostId, DnaStatusCode.INVALID_POST));
        if (!studyPost.getAuthor().getId().equals(userId)){
            throw new DNACustomException("게시글의 작성자만 수정할 수 있습니다.", DnaStatusCode.INVALID_AUTHOR);
        }
        studyPost.update(requestDto.getTitle(), requestDto.getContent());
        return updateStudyPostId;
    }

    @Transactional
    public Long delete(Long deleteStudyPostId, Long userId) {
        StudyPost studyPost = studyPostRepository.findWithStudyPostCommentsByStudyPostId(deleteStudyPostId).orElseThrow(
                () -> new DNACustomException("해당 게시글이 없습니다. id=" + deleteStudyPostId, DnaStatusCode.INVALID_POST));
        if(!studyPost.getAuthor().getId().equals(userId)) {
            throw new DNACustomException("해당 게시글의 작성자만 게시글을 삭제할 수 있습니다.", DnaStatusCode.INVALID_AUTHOR);
        }
        List<Long> allCommentIds = studyPost.getComments().stream().map(StudyPostComment::getStudyPostCommentId).collect(Collectors.toList());
        studyPostCommentLikeRepository.deleteAllStudyPostCommentLikesInStudyPostCommentIds(allCommentIds);

        if(!allCommentIds.isEmpty()){
            List<StudyPostComment> studyPostCommentList = studyPostCommentRepository.findAllStudyPostCommentsWithStudyPostByStudyPostId(deleteStudyPostId);
            List<PostCommentVO> studyPostCommentHierarchyStructure = createStudyPostCommentHierarchyStructure(convertStudyPostCommentListToPostCommentVOList(studyPostCommentList));
            deleteAllMyChildStudyPostComments(studyPostCommentHierarchyStructure);
        }
        studyPostLikeRepository.deleteAllStudyPostLikesByStudyPostId(deleteStudyPostId);
        studyPostRepository.deleteStudyPostByStudyPostId(deleteStudyPostId);
        return deleteStudyPostId;
    }

    private List<PostCommentVO> convertStudyPostCommentListToPostCommentVOList(List<StudyPostComment> studyPostCommentList) {
        return studyPostCommentList.stream().map(PostCommentVO::convertToPostCommentVO).collect(Collectors.toList());
    }

    private List<PostCommentVO> createStudyPostCommentHierarchyStructure(List<PostCommentVO> postCommentVO){
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

    private void deleteAllMyChildStudyPostComments(List<PostCommentVO> studyPostComments){
        for (PostCommentVO studyPostComment : studyPostComments) {
            deleteAllMyChildStudyPostComments(studyPostComment.getChildrenComment());
            studyPostCommentRepository.deleteStudyPostCommentByStudyPostCommentId(studyPostComment.getCommentId());
        }
    }

    public ListResponse findAllStudyPostMetaDataWithCondition(PostSearchCondition condition, Pageable pageable) {
        List<StudyPostMetaDataResponseDto> studyPostMetaDataResponseDtoList = studyPostRepository.search(condition, pageable);
        boolean hasNext = false;
        if(studyPostMetaDataResponseDtoList.size() > pageable.getPageSize()){
            studyPostMetaDataResponseDtoList.remove(pageable.getPageSize());
            hasNext = true;
        }
        return ListResponse.builder()
                .list(studyPostMetaDataResponseDtoList)
                .hasNext(hasNext)
                .build();
    }

    public StudyPostResponseDto findStudyPostWithLikedInfoByStudyPostIdAndUserId(Long studyPostId, Long userId) {
        StudyPost studyPost = studyPostRepository.findById(studyPostId).orElseThrow(
                () -> new DNACustomException("해당 게시글이 없습니다. id=" + studyPostId, DnaStatusCode.INVALID_POST));
        boolean isStudyPostLikedByUser = (userId != null && studyPostLikeRepository.findStudyPostLikeByStudyPostIdAndUserId(studyPostId, userId));
        return new StudyPostResponseDto(studyPost, isStudyPostLikedByUser);
    }
}
