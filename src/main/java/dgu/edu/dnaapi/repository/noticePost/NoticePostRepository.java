package dgu.edu.dnaapi.repository.noticePost;

import dgu.edu.dnaapi.domain.NoticePost;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface NoticePostRepository extends JpaRepository<NoticePost, Long>, NoticePostRepositoryCustom {

    @Override
    @EntityGraph(attributePaths = {"author"}, type = EntityGraph.EntityGraphType.FETCH)
    List<NoticePost> findAll();

    @Query("SELECT n " +
            "FROM NoticePost n " +
            "WHERE n.noticePostId <= :start " +
            "ORDER BY n.noticePostId desc")
    Slice<NoticePost> findAllByNoticeId(@Param("start") Long start, Pageable pageable);

    @EntityGraph(attributePaths = {"author"}, type = EntityGraph.EntityGraphType.FETCH)
    Slice<NoticePost> findSliceBy(Pageable pageable);

    @EntityGraph(attributePaths = {"author"}, type = EntityGraph.EntityGraphType.FETCH)
    Slice<NoticePost> findAllByTitleContaining(String title, Pageable pageable);

    List<NoticePost> findAllByTitleContaining(String title);

    @Query("SELECT n " +
            "FROM NoticePost n " +
            "WHERE n.noticePostId <= :start " +
            "and n.title LIKE %:keyword% " +
            "ORDER BY n.noticePostId desc")
    Slice<NoticePost> findAllByTitleContaining(@Param("start") Long start, @Param("keyword")String keyword, Pageable pageable);

    @EntityGraph(attributePaths = {"author"}, type = EntityGraph.EntityGraphType.FETCH)
    Slice<NoticePost> findAllByContentContaining(String content, Pageable pageable);

    @Query("SELECT n " +
            "FROM NoticePost n " +
            "WHERE n.noticePostId <= :start " +
            "and n.content LIKE %:keyword% " +
            "ORDER BY n.noticePostId desc")
    Slice<NoticePost> findAllByContentContaining(@Param("start") Long start, @Param("keyword")String keyword, Pageable pageable);

    @EntityGraph(attributePaths = {"author"}, type = EntityGraph.EntityGraphType.FETCH)
    Slice<NoticePost> findAllByAuthor_UserNameContaining(String author, Pageable pageable);

    @Query("SELECT n " +
            "FROM NoticePost n " +
            "WHERE n.noticePostId <= :start " +
            "and n.author.userName LIKE %:keyword% " +
            "ORDER BY n.noticePostId desc")
    Slice<NoticePost> findAllByAuthorContaining(@Param("start") Long start, @Param("keyword")String keyword, Pageable pageable);

    @EntityGraph(attributePaths = {"author"}, type = EntityGraph.EntityGraphType.FETCH)
    Slice<NoticePost> findAllByTitleContainingOrContentContaining(String title, String content, Pageable pageable);

    @Query("SELECT n " +
            "FROM NoticePost n " +
            "WHERE n.noticePostId <= :start " +
            "and (n.title LIKE %:keyword% " +
            "or n.content LIKE %:keyword%) " +
            "ORDER BY n.noticePostId desc")
    Slice<NoticePost> findAllByTitleContainingOrContentContaining(@Param("start") Long start, @Param("keyword")String keyword, Pageable pageable);

    @Modifying(clearAutomatically = true)
    @Query("UPDATE NoticePost n set n.likeCount = n.likeCount + 1 where n.noticePostId = :noticeId")
    int increaseLikeCount(@Param("noticeId")Long noticeId);

    @Modifying(clearAutomatically = true)
    @Query("UPDATE NoticePost n set n.likeCount = n.likeCount - 1 where n.noticePostId = :noticeId")
    int decreaseLikeCount(@Param("noticeId")Long noticeId);

    @Modifying(clearAutomatically = true)
    @Query("UPDATE NoticePost n set n.commentCount = n.commentCount + 1 where n.noticePostId = :noticeId")
    int increaseCommentCount(@Param("noticeId")Long noticeId);

    @Modifying(clearAutomatically = true)
    @Query("UPDATE NoticePost n set n.commentCount = n.commentCount - 1 where n.noticePostId = :noticeId")
    int decreaseCommentCount(@Param("noticeId")Long noticeId);

    @Modifying(clearAutomatically = true)
    @Query("DELETE FROM NoticePost n WHERE n.noticePostId = :noticePostId")
    int deleteNoticePostByNoticePostId(@Param("noticePostId") Long noticePostId);
}
