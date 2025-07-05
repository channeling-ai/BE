package channeling.be.domain.member.domain;

import channeling.be.domain.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Member extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nickname; // 닉네임

    @Column(nullable = false)
    private String googleEmail; // 구글 이메일

    @Column
    private String profileImage; // 프로필 이미지

    @Column
    private String instagramLink; // 인스타 링크

    @Column
    private String tiktokLink; // 틱톡 링크

    @Column
    private String facebookLink; // 페이스북 링크

    @Column
    private String twitterLink; // 트위터 링크

    @Column
    private String googleId; // 구글 아이디 (로그인 구별을 위한..)
}
