package dgu.edu.dnaapi.domain.dto.auth;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class RefreshTokenDto {

    private String refreshToken;
}
