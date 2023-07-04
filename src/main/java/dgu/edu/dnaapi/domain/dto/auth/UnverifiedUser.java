package dgu.edu.dnaapi.domain.dto.auth;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Data;

@Data
public class UnverifiedUser {

    private String email;
    private String username;
    private Long userId;
    private String studentId;

    @QueryProjection
    public UnverifiedUser(String email, String username, Long userId, String studentId) {
        this.email = email;
        this.username = username;
        this.userId = userId;
        this.studentId = studentId;
    }
}
