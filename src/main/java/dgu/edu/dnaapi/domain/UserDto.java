package dgu.edu.dnaapi.domain;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class UserDto {

    @ApiModelProperty(value = "유저 로그인 이메일", example = "cajun7@dgu.ac.kr", required = true)
    private String email;

    @ApiModelProperty(value = "유저 로그인 비밀번호", example = "드나짱짱1234", required = true)
    private String password;

    public UserDto(String email, String password) {
        this.email = email;
        this.password = password;
    }
}
