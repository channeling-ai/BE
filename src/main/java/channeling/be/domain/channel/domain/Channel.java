package channeling.be.domain.channel.domain;

import channeling.be.domain.common.BaseEntity;
import channeling.be.domain.member.domain.Member;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Channel extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false, unique = true)
    private Member member;

    @Column(nullable = false)
    private String name; // 채널 이름

    @Column(nullable = false)
    private Long view; // 조회수

    @Column(nullable = false)
    private Long likeCount; // 좋아요 수

    @Column(nullable = false)
    private Long subscribe; // 구독자 수

    @Column(nullable = false)
    private Long share; // 공유 수

    @Column(nullable = false)
    private Long videoCount; // 영상 수

    @Column(nullable = false)
    private Long comment; // 체널 총 댓글 수

    @Column(nullable = false)
    private String link; // 채널 링크

    @Column(nullable = false)
    private LocalDateTime joinDate; // 채널 가입일

    @Column(nullable = false)
    private String target; // 시청자 타겟

    @Column(nullable = false)
    private String concept; // 채널 컨셉

    @Column(nullable = false)
    private String image; // 채널 프로필 이미지

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ChannelHashTag channelHashTag; // 채널 해시태그

    @Column(nullable = false)
    private LocalDateTime channelUpdateAt; // 채널 정보 업데이트 시기
}
