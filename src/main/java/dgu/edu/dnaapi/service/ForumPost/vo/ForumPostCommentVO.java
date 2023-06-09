package dgu.edu.dnaapi.service.ForumPost.vo;

import dgu.edu.dnaapi.domain.ForumPostComment;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class ForumPostCommentVO {
    private Long commentId;
    private boolean isDeleted;
    public List<ForumPostCommentVO> childrenComment;
    private Long parentCommentId;

    public ForumPostCommentVO(Long commentId, boolean isDeleted, List<ForumPostCommentVO> childrenComment, Long parentCommentId) {
        this.commentId = commentId;
        this.isDeleted = isDeleted;
        this.childrenComment = childrenComment;
        this.parentCommentId = parentCommentId;
    }

    public static ForumPostCommentVO convertToForumPostCommentVO(ForumPostComment forumPostComment){
        Long parentCommentId = ((forumPostComment.getParent() != null) ? forumPostComment.getParent().getForumPostCommentId() : null);
        return new ForumPostCommentVO(forumPostComment.getForumPostCommentId(), forumPostComment.isDeleted(), new ArrayList<>(), parentCommentId);
    }

    public void addChild(ForumPostCommentVO forumPostCommentVO){
        this.childrenComment.add(forumPostCommentVO);
    }

    public void setDeletedStatus(){
        this.isDeleted = true;
    }

    public boolean hasParentComment(){
        return this.parentCommentId != null;
    }
}
