package dgu.edu.dnaapi.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class TokenDto {
    private String AccessToken;
    private String RefreshToken;
}
