package dgu.edu.dnaapi.domain.dto.auth;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class TokenDto {
    @ApiModelProperty(value = "AccessToken", example = "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJ0ZXN0QG5hdmVyLmNvbSIsInJvbGUiOiJVU0VSX1JPTEUiLCJleHAiOjE2NzM1MTA3ODB9.CDrF696fDw9aps7q0dvsTy1mBgGlUwEqTvMtrDehH1WqAEdZkV5s7bN2S1RitnlJR43YP7UkzaZRUrP2KSt5wg")
    private String AccessToken;

    @ApiModelProperty(value = "RefreshToken", example = "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJ0ZXN0QG5hdmVyLmNvbSIsInJvbGUiOiJVU0VSX1JPTEUiLCJleHAiOjE2NzQyODgzODB9.ppmxv8RJnL_Rv8hG4mTDSuhqzM1gi8N6Eo9zmgQgkF0LBpTBtMVO2XHApB387EFIt2TUKa5gJFIqEeG8dA-Qrg")
    private String RefreshToken;
}
