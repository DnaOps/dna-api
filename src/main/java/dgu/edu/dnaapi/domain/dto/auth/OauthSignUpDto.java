package dgu.edu.dnaapi.domain.dto.auth;

import dgu.edu.dnaapi.domain.User;
import dgu.edu.dnaapi.domain.UserRole;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OauthSignUpDto {

    private String username;
    private String studentId;
    private String email;
    private String provider;
    private String providerId;

    public User toUserEntity() {
        return User.builder()
                .userName(username)
                .email(email)
                .password("DNAProjectOAuthPassword12345")
                .level(Integer.parseInt(studentId.substring(2, 4)))
                .studentId(studentId)
                .role(UserRole.UNVERIFIED_USER_ROLE)
                .provider(provider)
                .providerId(providerId)
                .oauthId(provider + "_" + providerId)
                .build();
    }
}
