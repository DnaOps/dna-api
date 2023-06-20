package dgu.edu.dnaapi.service.studyPost;

import dgu.edu.dnaapi.domain.*;
import dgu.edu.dnaapi.domain.response.DnaStatusCode;
import dgu.edu.dnaapi.exception.DNACustomException;
import dgu.edu.dnaapi.repository.studyPost.StudyPostCommentLikeRepository;
import dgu.edu.dnaapi.repository.studyPost.StudyPostCommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class StudyPostCommentLikeService {

    private final StudyPostCommentLikeRepository studyPostCommentLikeRepository;
    private final StudyPostCommentRepository studyPostCommentRepository;

    @Transactional
    public String like(User user, Long studyPostId, Long studyPostCommentId) {
        StudyPostComment studyPostComment = getStudyPostComment(studyPostCommentId);
        checkStudyPostCommentIsInStudyPost(studyPostId, studyPostComment);

        studyPostCommentLikeRepository.findWithStudyPostCommentByUserIdAndStudyPostCommentId(user.getId(), studyPostCommentId).ifPresent(
                (studyPostCommentLike) -> {
                    throw new DNACustomException("이미 해당 유저가 해당 댓글을 좋아합니다.", DnaStatusCode.INVALID_LIKE_OPERATION);
                }
        );
        return likeStudyPostComment(user, studyPostComment);
    }

    @Transactional
    public String dislike(User user, Long studyPostId, Long studyPostCommentId) {
        StudyPostComment studyPostComment = getStudyPostComment(studyPostCommentId);
        checkStudyPostCommentIsInStudyPost(studyPostId, studyPostComment);

        StudyPostCommentLike studyPostCommentLike = studyPostCommentLikeRepository.findWithStudyPostCommentByUserIdAndStudyPostCommentId(user.getId(), studyPostCommentId).orElseThrow(
                () -> new DNACustomException("현재 해당 유저가 해당 댓글을 좋아하지 않습니다.", DnaStatusCode.INVALID_LIKE_OPERATION));

        return disLikeStudyPostComment(studyPostCommentLike);
    }

    private StudyPostComment getStudyPostComment(Long studyPostCommentId) {
        StudyPostComment studyPostComment = studyPostCommentRepository.findWithStudyPostByStudyPostCommentId(studyPostCommentId).orElseThrow(
                () -> new DNACustomException("해당 댓글이 존재하지 않습니다. id = " + studyPostCommentId, DnaStatusCode.INVALID_COMMENT));
        if(studyPostComment.isDeleted())
            throw new DNACustomException("해당 댓글이 존재하지 않습니다. id = " + studyPostCommentId, DnaStatusCode.INVALID_DELETE_COMMENT);
        return studyPostComment;
    }

    private void checkStudyPostCommentIsInStudyPost(Long studyPostId, StudyPostComment studyPostComment) {
        if(!studyPostComment.getStudyPost().getStudyPostId().equals(studyPostId))
            throw new DNACustomException("해당 게시글에 해당 댓글이 존재하지 않습니다. studyPostId = " + studyPostId + "commentId = " + studyPostComment.getStudyPostCommentId(), DnaStatusCode.INVALID_COMMENT);
    }

    private String disLikeStudyPostComment(StudyPostCommentLike studyPostCommentLike) {
        studyPostCommentRepository.decreaseLikeCount(studyPostCommentLike.getStudyPostComment().getStudyPostCommentId());
        studyPostCommentLikeRepository.deleteById(studyPostCommentLike.getStudyPostCommentLikeId());
        return "deleted";
    }

    private String likeStudyPostComment(User user, StudyPostComment studyPostComment) {
        studyPostCommentRepository.increaseLikeCount(studyPostComment.getStudyPostCommentId());
        studyPostCommentLikeRepository.save(new StudyPostCommentLike(studyPostComment, user));
        return "added";
    }
}
