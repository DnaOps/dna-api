package dgu.edu.dnaapi.domain;

import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Data
@NoArgsConstructor
public class User extends BaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "userId")
    private Long id;
    private String userName;
    private String password;
    private String email;
    private int level;

    @Enumerated(EnumType.STRING)
    private UserRole role;

    private String provider;
    private String providerId;

    @Builder
    public User(Long id, String userName, String password, String email, int level, UserRole role, String provider, String providerId) {
        this.id = id;
        this.userName = userName;
        this.password = password;
        this.email = email;
        this.level = level;
        this.role = role;
        this.provider = provider;
        this.providerId = providerId;
    }
}
