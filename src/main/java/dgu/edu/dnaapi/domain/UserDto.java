package dgu.edu.dnaapi.domain;

import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;

import java.time.LocalDateTime;

@Data
public class UserDto {

    @ApiModelProperty(value = "유저 아이디 정보", example = "scorpion")
    private String username;

    @ApiModelProperty(value = "유저 등급", example = "USER_ROLE")
    private UserRole role;

    @ApiModelProperty(value = "유저 가입일시", example = "2016.09.01")
    private LocalDateTime createdDate;

    @ApiModelProperty(value = "유저 게시글 수", example = "10")
    @Value("0")
    private long postCount;

    @ApiModelProperty(value = "유저 댓글 수", example = "10")
    @Value("0")
    private long commentCount;

    @ApiModelProperty(value = "유저 ID", example = "1")
    private long userId;

    @Builder
    public UserDto(String username, UserRole role, LocalDateTime createdDate, long postCount, long commentCount, long userId) {
        this.username = username;
        this.role = role;
        this.createdDate = createdDate;
        this.postCount = postCount;
        this.commentCount = commentCount;
        this.userId = userId;
    }
}
