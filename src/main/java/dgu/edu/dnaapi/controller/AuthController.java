package dgu.edu.dnaapi.controller;

import dgu.edu.dnaapi.config.jwt.JwtProperties;
import dgu.edu.dnaapi.domain.User;
import dgu.edu.dnaapi.domain.UserDto;
import dgu.edu.dnaapi.domain.UserRole;
import dgu.edu.dnaapi.domain.dto.auth.*;
import dgu.edu.dnaapi.domain.response.*;
import dgu.edu.dnaapi.repository.refreshtoken.RefreshToken;
import dgu.edu.dnaapi.repository.refreshtoken.RefreshTokenRepository;
import dgu.edu.dnaapi.service.TokenService;
import dgu.edu.dnaapi.service.UserService;
import dgu.edu.dnaapi.util.jwt.TokenProvider;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/auth/")
@RestController
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    private final TokenProvider tokenProvider;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final RefreshTokenRepository refreshTokenRepository;
    private final TokenService tokenService;


    @PostMapping("/signUp")
    @ApiOperation(value = "회원가입", notes = "회원가입을 진행한다.")
    public ResponseEntity<Message> signUp(@RequestBody SignUpDto signUpInfo) {
        System.out.println("signUpInfo = " + signUpInfo);
        User user = signUpInfo.toEntity();
        user.setRole(UserRole.USER_ROLE);
        System.out.println("user = " + user);
        Long userId = userService.join(user);
        HttpHeaders httpHeaders = new HttpHeaders();
        Message message = Message.builder().data(userId).apiStatus(new ApiStatus(DnaStatusCode.OK, null)).build();;
        return new ResponseEntity(message, httpHeaders, HttpStatus.OK);
    }

    /**
     * 로그인
     * */
    @PostMapping("/authenticate")
    @ApiOperation(value = "로그인", notes = "유저의 로그인을 진행한다.")
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

        TokenResponse tokenResponse = TokenResponse.builder()
                .jwt(new TokenDto(accessToken, refreshToken))
                .exist(false)
                .build();

        // Todo : 게시글, 댓글 수 가져오기
        UserDto userResponse = UserDto.builder().username(findUser.getUserName())
                .createdDate(findUser.getCreatedDate())
                .role(findUser.getRole())
                .build();

        LoginResponseDto result = new LoginResponseDto(tokenResponse, userResponse);

        Message message = Message.builder()
                                    .data(result)
                                    .apiStatus(new ApiStatus(DnaStatusCode.OK, null))
                                    .build();

        return new ResponseEntity(message, httpHeaders, HttpStatus.OK);
    }

    @PostMapping("/accessToken")
    @ApiOperation(value = "AccessToken 발급", notes = "유저의 Refresh Token 을 이용해 Access Token 재발급한다.")
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
        Message message = Message.builder().data(result).apiStatus(new ApiStatus(DnaStatusCode.OK, null)).build();;
        return new ResponseEntity(message, httpHeaders, HttpStatus.OK);
    }

    @PostMapping("/logout")
    @ApiOperation(value = "로그아웃", notes = "유저의 로그아웃을 진행한다.")
    public ResponseEntity<Message> logOut(@RequestBody RefreshTokenDto refreshToken) {
        // Todo : 1. accessToken 확인 2. RefreshToken 삭제 (userId 이용) 3. BlackList (유효기간동안 접근시)
        Message message = Message.builder().data("LogoutTest").apiStatus(new ApiStatus(DnaStatusCode.OK, null)).build();
        HttpHeaders httpHeaders = new HttpHeaders();
        return new ResponseEntity(message, httpHeaders, HttpStatus.OK);
    }

    @GetMapping("/test/authTest")
    @ApiOperation(value = "권한적용 체크", notes = "권한이 제한이 이루워지고 있는지 확인한다.")
    public String authTest(){
        return "AuthTest";
    }
}
