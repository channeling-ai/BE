package channeling.be.domain.video.domain;

import channeling.be.domain.channel.domain.Channel;
import channeling.be.domain.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Video extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "channel_id", nullable = false)
    private Channel channel;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private VideoCategory videoCategory; // long, short 여부

    @Column(nullable = false)
    private String title; // 영상 제목

    @Column(nullable = false)
    private Long view; // 영상 조회수

    @Column(nullable = false)
    private String link; // 영상 링크

    @Column(nullable = false)
    private LocalDateTime uploadDate; // 업로드 날짜

    @Column(nullable = false)
    private String thumbnail; // 썸네일 사진
}
