package dgu.edu.dnaapi.controller;

import dgu.edu.dnaapi.annotation.JwtRequired;
import dgu.edu.dnaapi.config.jwt.JwtProperties;
import dgu.edu.dnaapi.domain.User;
import dgu.edu.dnaapi.domain.UserDto;
import dgu.edu.dnaapi.domain.UserRole;
import dgu.edu.dnaapi.domain.dto.auth.*;
import dgu.edu.dnaapi.domain.response.*;
import dgu.edu.dnaapi.exception.DNACustomException;
import dgu.edu.dnaapi.repository.refreshtoken.RefreshToken;
import dgu.edu.dnaapi.repository.refreshtoken.RefreshTokenRepository;
import dgu.edu.dnaapi.service.PostCommentService;
import dgu.edu.dnaapi.service.PostService;
import dgu.edu.dnaapi.service.TokenService;
import dgu.edu.dnaapi.service.UserService;
import dgu.edu.dnaapi.util.jwt.TokenProvider;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    private final TokenProvider tokenProvider;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final RefreshTokenRepository refreshTokenRepository;
    private final TokenService tokenService;
    private final PostService postService;
    private final PostCommentService postCommentService;


    @PostMapping("/auth/signUp")
    @ApiOperation(value = "회원가입", notes = "회원가입을 진행한다.")
    public ResponseEntity<Message> signUp(@RequestBody SignUpDto signUpInfo) {
        System.out.println("signUpInfo = " + signUpInfo);
        User user = signUpInfo.toEntity();
        System.out.println("user = " + user);
        Message message = Message.createSuccessMessage(userService.join(user));
        return ResponseEntity.createSuccessResponseMessage(message);
    }

    /**
     * 로그인
     */
    @PostMapping("/auth/authenticate")
    @ApiOperation(value = "로그인", notes = "유저의 로그인을 진행한다.")
    public ResponseEntity<Message> authorize(@RequestBody LoginRequestDto loginRequestDto) {

        User findUser = userService.login(loginRequestDto);
        if (findUser.getRole().equals(UserRole.UNVERIFIED_USER_ROLE))
            throw new DNACustomException(DnaStatusCode.UNVERIFIED_USER);

        String accessToken = tokenProvider.createToken(findUser, JwtProperties.EXPIRATION_TIME);
        String refreshToken = tokenProvider.createToken(findUser, JwtProperties.REFRESH_EXPIRATION_TIME);

        refreshTokenRepository.save(new RefreshToken(refreshToken, findUser.getId()));

        System.out.println("jwt = " + accessToken + refreshToken);

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add(JwtProperties.HEADER_STRING, "Bearer " + accessToken);

        TokenResponse tokenResponse = TokenResponse.builder()
                .jwt(new TokenDto(accessToken, refreshToken))
                .exist(false)
                .build();

        UserDto userResponse = UserDto.builder()
                .username(findUser.getUserName())
                .createdDate(findUser.getCreatedDate())
                .role(findUser.getRole())
                .commentCount(postCommentService.getPostCommentCountByUserId(findUser.getId()))
                .postCount(postService.getPostCountByUserId(findUser.getId()))
                .userId(findUser.getId())
                .build();

        LoginResponseDto result = new LoginResponseDto(tokenResponse, userResponse);
        Message message = Message.createSuccessMessage(result);

        return new ResponseEntity(message, httpHeaders, HttpStatus.OK);
    }

    @PostMapping("/auth/accessToken")
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
        Message message = Message.createSuccessMessage(result);
        return new ResponseEntity(message, httpHeaders, HttpStatus.OK);
    }

    @PostMapping("/auth/logout")
    @ApiOperation(value = "로그아웃", notes = "유저의 로그아웃을 진행한다.")
    public ResponseEntity<Message> logOut(@RequestBody RefreshTokenDto refreshToken) {
        RefreshToken findRefreshToken = refreshTokenRepository.findById(refreshToken.getRefreshToken()).orElseThrow(
                () -> new DNACustomException(DnaStatusCode.TOKEN_INVALID)
        );
        refreshTokenRepository.delete(findRefreshToken);
        return ResponseEntity.createSuccessResponseMessage(Message.createSuccessMessage("success"));
    }

    @GetMapping("/auth/unverifiedUsers")
    @ApiOperation(value = "미 승인 사용자", notes = "미승인 유저들 리스트를 출력한다.")
    public ResponseEntity<Message> getUnverifiedUser() {
        List<UnverifiedUser> unverifiedUserList = userService.getUnverifiedUserList();
        return ResponseEntity.createSuccessResponseMessage(Message.createSuccessMessage(unverifiedUserList));
    }

    @PostMapping("/auth/unverifiedUsers/{userId}")
    @ApiOperation(value = "미 승인 사용자 승인", notes = "미승인 유저를 승인한다.")
    public ResponseEntity<Message> getUnverifiedUser(
            @JwtRequired User requestUser,
            @PathVariable("userId") Long userId
    ) {
        if (!requestUser.getRole().equals(UserRole.ADMIN_ROLE))
            throw new DNACustomException(DnaStatusCode.UNAUTHORIZED_REQUEST);
        long authorizeUserId = userService.authorizeUser(userService.getUserByUserId(userId));
        return ResponseEntity.createSuccessResponseMessage(Message.createSuccessMessage(authorizeUserId));
    }

    @GetMapping("/test/authTest")
    @ApiOperation(value = "권한적용 체크", notes = "권한이 제한이 이루워지고 있는지 확인한다.")
    public String authTest() {
        return "AuthTest";
    }

    @PostMapping("/auth/admin/signUp")
    public ResponseEntity<Message> adminSignUp(@RequestBody SignUpDto signUpInfo) {
        User user = signUpInfo.toEntity();
        user.setRole(UserRole.ADMIN_ROLE);
        Message message = Message.createSuccessMessage(userService.join(user));
        return ResponseEntity.createSuccessResponseMessage(message);
    }

    @PostMapping("/auth/signUp/oauth")
    @ApiOperation(value = "Oauth 회원가입", notes = "Oauth 회원가입 요청.")
    public ResponseEntity<Message> signUpOauth(
            @RequestBody OauthSignUpDto oauthSignUpDto
    ) {
        User user = oauthSignUpDto.toUserEntity();
        return ResponseEntity.createSuccessResponseMessage(Message.createSuccessMessage(userService.join(user)));
    }

    @GetMapping("/auth/duplicate/{email}")
    public ResponseEntity<Message> duplicate(
            @PathVariable("email") String email
    ) {
        userService.validateDuplicateEmail(email);
        return ResponseEntity.createSuccessResponseMessage(Message.createSuccessMessage("사용가능한 이메일입니다."));
    }
}
