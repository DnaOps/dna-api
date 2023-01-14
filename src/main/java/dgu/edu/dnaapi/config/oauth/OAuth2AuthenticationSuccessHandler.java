package dgu.edu.dnaapi.config.oauth;

import com.fasterxml.jackson.databind.ObjectMapper;
import dgu.edu.dnaapi.config.auth.PrincipalDetails;
import dgu.edu.dnaapi.config.jwt.JwtProperties;
import dgu.edu.dnaapi.domain.User;
import dgu.edu.dnaapi.domain.UserRole;
import dgu.edu.dnaapi.domain.dto.TokenDto;
import dgu.edu.dnaapi.domain.dto.TokenResponse;
import dgu.edu.dnaapi.domain.response.Message;
import dgu.edu.dnaapi.domain.response.ResponseEntity;
import dgu.edu.dnaapi.domain.response.StatusEnum;
import dgu.edu.dnaapi.repository.refreshtoken.RefreshToken;
import dgu.edu.dnaapi.repository.refreshtoken.RefreshTokenRepository;
import dgu.edu.dnaapi.util.jwt.TokenProvider;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.util.UriComponentsBuilder;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URI;

@Component
@AllArgsConstructor
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {


    private final TokenProvider tokenProvider;
    private final RefreshTokenRepository refreshTokenRepository;

//    public OAuth2AuthenticationSuccessHandler(TokenProvider tokenProvider, RefreshTokenRepository refreshTokenRepository) {
//        super();
//        this.tokenProvider = tokenProvider;
//        this.refreshTokenRepository = refreshTokenRepository;
//        setRedirectStrategy(new NoRedirectStrategy());
//    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        String targetUrl = determineTargetUrl(request, response, authentication);
        System.out.println("targetUrl = " + targetUrl);

        if (response.isCommitted()) {
            logger.debug("Response has already been committed. Unable to redirect to " + targetUrl);
            return;
        }
        System.out.println("authentication = " + authentication);
        // clearAuthenticationAttributes(request, response);

        // Todo : OAUTH Token 주는 방식 변경 필요 -> 현재 URL Param으로 주는 중

//        PrincipalDetails principalDetails = (PrincipalDetails)authentication.getPrincipal();
//        // Token 생성시 UserName, UserRole 만 필요
//        User oAuthUser = User.builder()
//                        .username(principalDetails.getUser().getUsername())
//                        .role(UserRole.USER_ROLE)
//                        .build();
//
//        String accessToken = tokenProvider.createToken(oAuthUser, JwtProperties.EXPIRATION_TIME);
//        String refreshToken = tokenProvider.createToken(oAuthUser, JwtProperties.REFRESH_EXPIRATION_TIME);
//
//        /**
//         * Todo : 1. Refresh Token 발행, Refresh Token Redis 저장, logOut 시나리오 구현
//         */
//        refreshTokenRepository.save(new RefreshToken(refreshToken, oAuthUser.getId()));
//
//        System.out.println("jwt = " + accessToken + refreshToken);
//
//        HttpHeaders httpHeaders = new HttpHeaders();
//        httpHeaders.add(JwtProperties.HEADER_STRING, JwtProperties.TOKEN_PREFIX + accessToken);
//        httpHeaders.setLocation(URI.create(targetUrl));
//        TokenResponse result = TokenResponse.builder()
//                .jwt(new TokenDto(accessToken, refreshToken))
//                .exist(false)
//                .build();
//        Message message = Message.builder().data(result).status(StatusEnum.OK).message(null).build();
//
//        ObjectMapper om = new ObjectMapper();
//        response.getWriter().write(om.writeValueAsString(new ResponseEntity(message, httpHeaders, HttpStatus.OK)));
//        response.setHeader(JwtProperties.HEADER_STRING, JwtProperties.TOKEN_PREFIX + accessToken);
//        response.setHeader("Location", targetUrl);

        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }

    protected String determineTargetUrl(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {

        // Todo : Change Redirection URL
        String targetUrl = "http://localhost:3000/";
//        System.out.println("targetUrl = " + request.getRequestURL());
        PrincipalDetails principalDetails = (PrincipalDetails)authentication.getPrincipal();
        User oAuthUser = User.builder()
                        .userName(principalDetails.getUser().getUserName())
                        .role(UserRole.USER_ROLE)
                        .build();
        String accessToken = tokenProvider.createToken(authentication);
        String refreshToken = tokenProvider.createToken(oAuthUser, JwtProperties.REFRESH_EXPIRATION_TIME);
        System.out.println("token = " + accessToken);
        return UriComponentsBuilder.fromUriString(targetUrl)
                .queryParam("accessToken", accessToken)
                .queryParam("refreshToken", refreshToken)
                .build().toUriString();
    }

    protected void clearAuthenticationAttributes(HttpServletRequest request, HttpServletResponse response) {
        super.clearAuthenticationAttributes(request);
        // Todo : Redis Token Management
        //httpCookieOAuth2AuthorizationRequestRepository.removeAuthorizationRequestCookies(request, response);
    }

    protected class NoRedirectStrategy implements RedirectStrategy{
        @Override
        public void sendRedirect(HttpServletRequest request, HttpServletResponse response, String url) throws IOException {
            // no direct
        }
    }
}
