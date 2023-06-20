package dgu.edu.dnaapi.service.studyPost;

import dgu.edu.dnaapi.domain.*;
import dgu.edu.dnaapi.domain.response.DnaStatusCode;
import dgu.edu.dnaapi.exception.DNACustomException;
import dgu.edu.dnaapi.repository.studyPost.StudyPostLikeRepository;
import dgu.edu.dnaapi.repository.studyPost.StudyPostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class StudyPostLikeService {

    private final StudyPostLikeRepository studyPostLikeRepository;
    private final StudyPostRepository studyPostRepository;

    @Transactional
    public String like(User user, Long studyPostId) {
        StudyPost studyPost = getStudyPost(studyPostId);
        studyPostLikeRepository.findByUserAndStudyPost(user, studyPost).ifPresent(
                (studyPostLike) -> {
                    throw new DNACustomException("이미 해당 유저가 해당 게시글을 좋아합니다.", DnaStatusCode.INVALID_LIKE_OPERATION);
                }
        );
        studyPostRepository.increaseLikeCount(studyPost.getStudyPostId());
        studyPostLikeRepository.save(new StudyPostLike(studyPost, user));
        return "added";
    }

    @Transactional
    public String dislike(User user, Long studyPostId) {
        StudyPost studyPost = getStudyPost(studyPostId);
        StudyPostLike studyPostLike = studyPostLikeRepository.findByUserAndStudyPost(user, studyPost).orElseThrow(
                () -> new DNACustomException("현재 해당 유저가 해당 게시글을 좋아하지 않는 상태입니다.", DnaStatusCode.INVALID_LIKE_OPERATION));

        studyPostRepository.decreaseLikeCount(studyPost.getStudyPostId());
        studyPostLikeRepository.delete(studyPostLike);
        return "deleted";
    }

    private StudyPost getStudyPost(Long studyPostId) {
        return studyPostRepository.findById(studyPostId).orElseThrow(
                () -> new DNACustomException("해당 ForumPost를 찾을 수 없습니다. id = " + studyPostId, DnaStatusCode.INVALID_POST));
    }
}
