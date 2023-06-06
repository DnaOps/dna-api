package dgu.edu.dnaapi.service.Forums.vo;

import dgu.edu.dnaapi.domain.ForumComments;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class ForumCommentVO {
    private Long commentId;
    private boolean isDeleted;
    public List<ForumCommentVO> childrenComment;
    private Long parentCommentId;

    public ForumCommentVO(Long commentId, boolean isDeleted, List<ForumCommentVO> childrenComment, Long parentCommentId) {
        this.commentId = commentId;
        this.isDeleted = isDeleted;
        this.childrenComment = childrenComment;
        this.parentCommentId = parentCommentId;
    }

    public static ForumCommentVO convertToForumCommentVO(ForumComments forumComments){
        Long parentCommentId = ((forumComments.getParent() != null) ? forumComments.getParent().getCommentId() : null);
        return new ForumCommentVO(forumComments.getCommentId(), forumComments.isDeleted(), new ArrayList<>(), parentCommentId);
    }

    public void addChild(ForumCommentVO forumCommentVO){
        this.childrenComment.add(forumCommentVO);
    }

    public void setDeletedStatus(){
        this.isDeleted = true;
    }

    public boolean hasParentComment(){
        return this.parentCommentId != null;
    }
}
