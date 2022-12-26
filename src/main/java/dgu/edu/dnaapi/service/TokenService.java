package dgu.edu.dnaapi.service;

import dgu.edu.dnaapi.config.jwt.JwtProperties;
import dgu.edu.dnaapi.domain.User;
import dgu.edu.dnaapi.repository.UserRepository;
import dgu.edu.dnaapi.repository.refreshtoken.RefreshToken;
import dgu.edu.dnaapi.repository.refreshtoken.RefreshTokenRepository;
import dgu.edu.dnaapi.util.jwt.TokenProvider;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;

@Service
public class TokenService {

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final TokenProvider tokenProvider;
    private final SecretKey secretKey;

    public TokenService(final UserRepository memberRepository,
                        final RefreshTokenRepository refreshTokenRepository,
                        final TokenProvider tokenProvider) {
        this.userRepository = memberRepository;
        this.refreshTokenRepository = refreshTokenRepository;
        this.secretKey = Keys.hmacShaKeyFor(JwtProperties.SECRET.getBytes(StandardCharsets.UTF_8));
        this.tokenProvider = tokenProvider;
    }

    public String generateAccessToken(final String requestRefreshToken) {
        RefreshToken refreshToken = refreshTokenRepository.findById(requestRefreshToken)
                .orElseThrow(IllegalArgumentException::new);
        System.out.println("refreshToken2 = " + refreshToken);
        // Todo : RefreshToken Exception
        Long memberId = refreshToken.getUserId();
        User findUser = userRepository.findById(memberId)
                .orElseThrow(IllegalArgumentException::new);

        System.out.println("findUser = " + findUser);

        String accessToken = tokenProvider.createToken(findUser, JwtProperties.EXPIRATION_TIME);

        return accessToken;
    }

    public Long getUserId(final String accessToken) {
        try {
            String memberId = Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(accessToken)
                    .getBody()
                    .getSubject();
            return Long.parseLong(memberId);
        } catch (final JwtException e) {
            // Todo : InvalidAccessTokenException 만들기
            throw new IllegalArgumentException();
        }
    }

}
