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

    @ApiModelProperty(value = "유저 로그인 비밀번호", example = "드나짱짱1234")
    private UserRole role;

    @ApiModelProperty(value = "유저 가입일시", example = "2016.09.01")
    private LocalDateTime createdDate;

    @ApiModelProperty(value = "유저 게시글 수", example = "10")
    @Value("0")
    private int postCount;

    @ApiModelProperty(value = "유저 댓글 수", example = "10")
    @Value("0")
    private int commentCount;

    @Builder
    public UserDto(String username, UserRole role, LocalDateTime createdDate, int postCount, int commentCount) {
        this.username = username;
        this.role = role;
        this.createdDate = createdDate;
        this.postCount = postCount;
        this.commentCount = commentCount;
    }
}
