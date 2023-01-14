package dgu.edu.dnaapi.domain.dto.auth;

import dgu.edu.dnaapi.domain.UserDto;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginResponseDto {

    @ApiModelProperty(value = "유저 토큰 정보")
    private TokenResponse tokenResponse;

    @ApiModelProperty(value = "유저 정보 For 헤더")
    private UserDto userInfoResponse;
}