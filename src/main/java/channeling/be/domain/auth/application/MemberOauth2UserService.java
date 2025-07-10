package channeling.be.domain.auth.application;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.Collections;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class MemberOauth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        // 로그인 진행 시 키값 (sub)
        String userNameAttributeName = userRequest.getClientRegistration()
                .getProviderDetails().getUserInfoEndpoint().getUserNameAttributeName();

        // oauth user 정보
        OAuth2UserService<OAuth2UserRequest, OAuth2User> oAuth2UserService = new DefaultOAuth2UserService();
        OAuth2User oAuth2User = oAuth2UserService.loadUser(userRequest);
        Map<String, Object> memberAttribute = oAuth2User.getAttributes();

        /* TODO 비즈니스 로직
         * 사용자 정보에 포함된 키값 : {sub=115435372144923491689, name=허유진/학생/융합소프트웨어학부 응용소프트웨어전공, given_name=/학생/융합소프트웨어학부 응용소프트웨어전공, family_name=허유진, picture=https://lh3.googleusercontent.com/a/ACg8ocJOVNy8tVtRYO5_m1Ij9eQPkNWBK550DiUNjfs8a_vuUxDU9A=s96-c, email=qetyop9762@mju.ac.kr, email_verified=true, hd=mju.ac.kr}
         * 아래 getGoogleUserInfo() 지워도 되는지 확인 필요
         * OAuth2User에서 가져온 정보로 회원가입 처리 등
         */
        System.out.println(" 엑세스 토큰 " + userRequest.getAccessToken().getTokenValue());
        System.out.println(" 유저 정보 " + memberAttribute);
        System.out.println(" 유저 이름 " + userNameAttributeName);

        // TODO 권한 확인(시큐리티 내 인증키가 sub=115435372144923491689 로 저장되는 구조)
        return new DefaultOAuth2User(
                Collections.singleton(new SimpleGrantedAuthority("ROLE_USER")),
                memberAttribute,
                userNameAttributeName);
    }

    // TODO 구글 사용자 정보 (확인 후 제거)
    private void getGoogleUserInfo(String accessToken) {
        RestTemplate restTemplate = new RestTemplate();

        URI uri = UriComponentsBuilder
                .fromUriString("https://www.googleapis.com")
                .path("/oauth2/v1/userinfo")
                .queryParam("access_token", accessToken)
                .encode()
                .build()
                .toUri();

        Map<String, Object> response = restTemplate.getForObject(uri, Map.class);

        response.forEach((key, value) -> {
            System.out.println("Key: " + key + ", Value: " + value);
        });
    }
}
