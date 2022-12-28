package dgu.edu.dnaapi.controller;

import dgu.edu.dnaapi.config.jwt.JwtProperties;
import dgu.edu.dnaapi.domain.User;
import dgu.edu.dnaapi.domain.UserRole;
import dgu.edu.dnaapi.domain.dto.LoginRequestDto;
import dgu.edu.dnaapi.domain.dto.RefreshTokenDto;
import dgu.edu.dnaapi.domain.dto.TokenDto;
import dgu.edu.dnaapi.domain.dto.TokenResponse;
import dgu.edu.dnaapi.domain.response.Message;
import dgu.edu.dnaapi.domain.response.ResponseEntity;
import dgu.edu.dnaapi.domain.response.StatusEnum;
import dgu.edu.dnaapi.repository.refreshtoken.RefreshToken;
import dgu.edu.dnaapi.repository.refreshtoken.RefreshTokenRepository;
import dgu.edu.dnaapi.service.TokenService;
import dgu.edu.dnaapi.service.UserService;
import dgu.edu.dnaapi.util.jwt.TokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    private final TokenProvider tokenProvider;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final RefreshTokenRepository refreshTokenRepository;
    private final TokenService tokenService;


    @PostMapping("/signUp")
    public String signUp(@RequestBody User user) {
        user.setRole(UserRole.USER_ROLE);
        System.out.println("user = " + user);
        userService.join(user);
        return user.getUsername();
    }

    /**
     * 로그인
     * */
    @PostMapping("/authenticate")
    public ResponseEntity<Message> authorize(@RequestBody LoginRequestDto loginRequestDto) {

        System.out.println("loginRequestDto.getEmail() = " + loginRequestDto.getEmail());

        User findUser = userService.login(loginRequestDto);
        String accessToken = tokenProvider.createToken(findUser, JwtProperties.EXPIRATION_TIME);
        String refreshToken = tokenProvider.createToken(findUser, JwtProperties.REFRESH_EXPIRATION_TIME);

        /**
         * Todo : 1. Refresh Token 발행, Refresh Token Redis 저장, logOut 시나리오 구현
         */

        refreshTokenRepository.save(new RefreshToken(refreshToken, findUser.getId()));

        System.out.println("jwt = " + accessToken + refreshToken);

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add(JwtProperties.HEADER_STRING, "Bearer " + accessToken);
        TokenResponse result = TokenResponse.builder()
                .jwt(new TokenDto(accessToken, refreshToken))
                .exist(false)
                .build();
        Message message = Message.builder().data(result).status(StatusEnum.OK).message(null).build();
        return new ResponseEntity(message, httpHeaders, HttpStatus.OK);
    }

    @PostMapping("/accessToken")
    /**
     *  {
     *      refreshToken : xx
     *  }
     */
    public ResponseEntity<Message> accessToken(@RequestBody RefreshTokenDto refreshToken) {
        System.out.println("refreshToken = " + refreshToken);
        String accessToken = tokenService.generateAccessToken(refreshToken.getRefreshToken());
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add(JwtProperties.HEADER_STRING, "Bearer " + accessToken);
        TokenResponse result = TokenResponse.builder()
                .jwt(new TokenDto(accessToken, refreshToken.getRefreshToken()))
                .exist(false)
                .build();
        Message message = Message.builder().data(result).status(StatusEnum.OK).message(null).build();
        return new ResponseEntity(message, httpHeaders, HttpStatus.OK);
    }

    @PostMapping("/logout")
    public ResponseEntity<Message> logOut(@RequestBody RefreshTokenDto refreshToken) {
        // Todo : 1. accessToken 확인 2. RefreshToken 삭제 (userId 이용) 3. BlackList (유효기간동안 접근시)
        Message message = Message.builder().data("LogoutTest").status(StatusEnum.OK).message(null).build();
        HttpHeaders httpHeaders = new HttpHeaders();
        return new ResponseEntity(message, httpHeaders, HttpStatus.OK);
    }

    @GetMapping("/test/authTest")
    public String authTest(){
        return "AuthTest";
    }
}
