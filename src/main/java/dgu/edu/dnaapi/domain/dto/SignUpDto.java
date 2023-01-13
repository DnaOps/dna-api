package dgu.edu.dnaapi.domain.dto;

import dgu.edu.dnaapi.domain.Notices;
import dgu.edu.dnaapi.domain.User;
import lombok.*;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SignUpDto {

    private String userName;
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
                .userName(userName)
                .email(email)
                .password(password)
                .level(studentId.substring(2,4))
                .build();
    }
}
