package dgu.edu.dnaapi.domain;

import lombok.Data;

@Data
public class UserDto {

    private String email;
    private String password;

    public UserDto(String email, String password) {
        this.email = email;
        this.password = password;
    }
}
