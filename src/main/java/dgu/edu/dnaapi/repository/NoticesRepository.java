package dgu.edu.dnaapi.repository;

import dgu.edu.dnaapi.domain.Notices;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface NoticesRepository extends JpaRepository<Notices, Long> {

    @Override
    @EntityGraph(attributePaths = {"author"}, type = EntityGraph.EntityGraphType.FETCH)
    List<Notices> findAll();

    @Query("SELECT n " +
            "FROM Notices n " +
            "WHERE n.noticeId <= :start " +
            "ORDER BY n.noticeId desc")
    Slice<Notices> findAllByNoticeId(@Param("start") Long start, Pageable pageable);

    @EntityGraph(attributePaths = {"author"}, type = EntityGraph.EntityGraphType.FETCH)
    Slice<Notices> findSliceBy(Pageable pageable);

    @EntityGraph(attributePaths = {"author"}, type = EntityGraph.EntityGraphType.FETCH)
    Slice<Notices> findAllByTitleContaining(String title, Pageable pageable);

    List<Notices> findAllByTitleContaining(String title);

    @Query("SELECT n " +
            "FROM Notices n " +
            "WHERE n.noticeId <= :start " +
            "and n.title LIKE %:keyword% " +
            "ORDER BY n.noticeId desc")
    Slice<Notices> findAllByTitleContaining(@Param("start") Long start, @Param("keyword")String keyword, Pageable pageable);

    @EntityGraph(attributePaths = {"author"}, type = EntityGraph.EntityGraphType.FETCH)
    Slice<Notices> findAllByContentContaining(String content, Pageable pageable);

    @Query("SELECT n " +
            "FROM Notices n " +
            "WHERE n.noticeId <= :start " +
            "and n.content LIKE %:keyword% " +
            "ORDER BY n.noticeId desc")
    Slice<Notices> findAllByContentContaining(@Param("start") Long start, @Param("keyword")String keyword, Pageable pageable);

    @EntityGraph(attributePaths = {"author"}, type = EntityGraph.EntityGraphType.FETCH)
    Slice<Notices> findAllByAuthor_UserNameContaining(String author, Pageable pageable);

    @Query("SELECT n " +
            "FROM Notices n " +
            "WHERE n.noticeId <= :start " +
            "and n.author.userName LIKE %:keyword% " +
            "ORDER BY n.noticeId desc")
    Slice<Notices> findAllByAuthorContaining(@Param("start") Long start, @Param("keyword")String keyword, Pageable pageable);

    @EntityGraph(attributePaths = {"author"}, type = EntityGraph.EntityGraphType.FETCH)
    Slice<Notices> findAllByTitleContainingOrContentContaining(String title,String content, Pageable pageable);

    @Query("SELECT n " +
            "FROM Notices n " +
            "WHERE n.noticeId <= :start " +
            "and (n.title LIKE %:keyword% " +
            "or n.content LIKE %:keyword%) " +
            "ORDER BY n.noticeId desc")
    Slice<Notices> findAllByTitleContainingOrContentContaining(@Param("start") Long start, @Param("keyword")String keyword, Pageable pageable);
}
