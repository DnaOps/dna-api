package dgu.edu.dnaapi.config.oauth;

import dgu.edu.dnaapi.config.CustomBCryptPasswordEncoder;
import dgu.edu.dnaapi.config.auth.PrincipalDetails;
import dgu.edu.dnaapi.config.oauth.provider.GoogleUserInfo;
import dgu.edu.dnaapi.config.oauth.provider.NaverUserInfo;
import dgu.edu.dnaapi.config.oauth.provider.OAuth2UserInfo;
import dgu.edu.dnaapi.domain.User;
import dgu.edu.dnaapi.domain.UserRole;
import dgu.edu.dnaapi.domain.response.DnaStatusCode;
import dgu.edu.dnaapi.exception.DNACustomException;
import dgu.edu.dnaapi.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PrincipalOauth2UserService extends DefaultOAuth2UserService {

    private final CustomBCryptPasswordEncoder bCryptPasswordEncoder;
    private final UserRepository userRepository;

    // 구글로 부터 받은 userRequest 데이터에 대한 후처리되는 함수
    // 함수 종료시 @AuthenticationPrincipal 어노테이션이 만들어진다.
    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {

//        // 구글 로그인 버튼 클릭 -> 구글 로그인창 -> 로그인을 완료 -> code 를 리턴 (OAuth-Client 라이브러리) -> AccessToken 요청
//        // userRequest 정보 -> 이 정보를 이용해 회원 Profile 정보를 받아야함 (loadUser 이용) -> 구글로 부터 회원 프로필 정보 받음.

        OAuth2User oauth2User = super.loadUser(userRequest);

        System.out.println("oauth2User.getAttributes() = " + oauth2User.getAttributes());

        // String provider = userRequest.getClientRegistration().getRegistrationId(); // Google
        OAuth2UserInfo oAuth2UserInfo = null;
        if (userRequest.getClientRegistration().getRegistrationId().equals("google")) {
            oAuth2UserInfo = new GoogleUserInfo(oauth2User.getAttributes());
        }else if(userRequest.getClientRegistration().getRegistrationId().equals("naver")){
            oAuth2UserInfo = new NaverUserInfo((Map)oauth2User.getAttributes().get("response"));
        }else{
            System.out.println("우리는 구글, 네이버만 지원해요");
            // Todo : 예외처리
            throw new DNACustomException("INVALID OAUTH", DnaStatusCode.INVALID_OAUTH_INFO);
        }

        String provider = oAuth2UserInfo.getProvider();
        String providerId = oAuth2UserInfo.getProviderId();
        String oauthId = provider + "_" + providerId;
        String email = oAuth2UserInfo.getEmail();

        User userEntity = userRepository.findByOauthId(oauthId);
        boolean isExist = userEntity != null;

        Map<String, Object> temp = new HashMap<>(oauth2User.getAttributes());
        temp.put("isExist", isExist);
        temp.put("provider", provider);
        temp.put("providerId", providerId);
        System.out.println("oauth2User = " + oauth2User);
        System.out.println("oauth2User = " + oauth2User.getAttributes());
        System.out.println("oAuth2UserInfo = " + oAuth2UserInfo);


//        oAuth2UserInfo.put("isExist", isExist);
//        oauth2User.getAttributes().put("provider", provider);
//        oauth2User.getAttributes().put("providerId", providerId);

        if (userEntity == null) {
            System.out.println("최초 로그인 입니다.");
//            userEntity = User.builder().userName(userName).password(password)
//                    .email(email)
//                    .role(UserRole.USER_ROLE)
//                    .provider(provider)
//                    .providerId(providerId)
//                    .build();
//            userRepository.save(userEntity);
            userEntity = User.builder()
                    .userName("DNAOAUTHTEST")
                    .build();
        }else{
            System.out.println("이미 로그인을 한적이 있습니다.");
        }

        // User 정보와 Oauth Attributes 정보까지 Authentication 이 가지고 있다.
//        return new PrincipalDetails(userEntity, oauth2User.getAttributes());
        return new PrincipalDetails(userEntity, temp);
    }
}
