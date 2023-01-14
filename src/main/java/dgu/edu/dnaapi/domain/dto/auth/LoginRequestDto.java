package dgu.edu.dnaapi.domain.dto.auth;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginRequestDto {

    @ApiModelProperty(value = "유저 로그인 이메일", example = "cajun7@dgu.ac.kr", required = true)
    private String email;

    @ApiModelProperty(value = "유저 로그인 비밀번호", example = "드나짱짱1234", required = true)
    private String password;
}
