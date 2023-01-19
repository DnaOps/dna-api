package dgu.edu.dnaapi.domain.dto.auth;

import dgu.edu.dnaapi.domain.Notices;
import dgu.edu.dnaapi.domain.User;
import lombok.*;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SignUpDto {

    private String username;
    private String studentId;
    private String email;
    private String password;

    /*
     * "email":"",
     * "nickname":"",
     * "password":"",
     * "studentId":""
     * */
    public User toEntity() {
        return User.builder()
                .userName(username)
                .email(email)
                .password(password)
                .level(Integer.parseInt(studentId.substring(2,4)))
                .build();
    }
}
