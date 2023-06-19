package dgu.edu.dnaapi.service.vo;

import dgu.edu.dnaapi.domain.AlbumPostComment;
import dgu.edu.dnaapi.domain.ForumPostComment;
import dgu.edu.dnaapi.domain.NoticePostComment;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class PostCommentVO {
    private Long commentId;
    private boolean isDeleted;
    public List<PostCommentVO> childrenComment;
    private Long parentCommentId;

    public PostCommentVO(Long commentId, boolean isDeleted, List<PostCommentVO> childrenComment, Long parentCommentId) {
        this.commentId = commentId;
        this.isDeleted = isDeleted;
        this.childrenComment = childrenComment;
        this.parentCommentId = parentCommentId;
    }

    public static PostCommentVO convertToPostCommentVO(ForumPostComment forumPostComment){
        Long parentCommentId = ((forumPostComment.getParent() != null) ? forumPostComment.getParent().getForumPostCommentId() : null);
        return new PostCommentVO(forumPostComment.getForumPostCommentId(), forumPostComment.isDeleted(), new ArrayList<>(), parentCommentId);
    }

    public static PostCommentVO convertToPostCommentVO(NoticePostComment noticePostComment){
        Long parentCommentId = ((noticePostComment.getParent() != null) ? noticePostComment.getParent().getNoticePostCommentId() : null);
        return new PostCommentVO(noticePostComment.getNoticePostCommentId(), noticePostComment.isDeleted(), new ArrayList<>(), parentCommentId);
    }

    public static PostCommentVO convertToPostCommentVO(AlbumPostComment noticePostComment){
        Long parentCommentId = ((noticePostComment.getParent() != null) ? noticePostComment.getParent().getAlbumPostCommentId() : null);
        return new PostCommentVO(noticePostComment.getAlbumPostCommentId(), noticePostComment.getIsDeleted(), new ArrayList<>(), parentCommentId);
    }

    public void addChild(PostCommentVO postCommentVO){
        this.childrenComment.add(postCommentVO);
    }

    public void setDeletedStatus(){
        this.isDeleted = true;
    }

    public boolean hasParentComment(){
        return this.parentCommentId != null;
    }
}
