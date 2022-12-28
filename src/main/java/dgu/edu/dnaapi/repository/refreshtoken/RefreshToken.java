package dgu.edu.dnaapi.repository.refreshtoken;

import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

@RedisHash(value = "refreshToken", timeToLive = 6000)
@Getter
public class RefreshToken {

    @Id
    private String refreshToken;
    private Long userId;

    public RefreshToken(final String refreshToken, final Long userId) {
        this.refreshToken = refreshToken;
        this.userId = userId;
    }
}
