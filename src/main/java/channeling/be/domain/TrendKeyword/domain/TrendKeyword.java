package channeling.be.domain.TrendKeyword.domain;


import channeling.be.domain.channel.domain.Channel;
import channeling.be.domain.common.BaseEntity;
import channeling.be.domain.report.domain.Report;
import channeling.be.domain.video.domain.Video;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class TrendKeyword extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "channel_id")
    private Channel channel;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    TrendKeywordType keywordType; // 트렌드 타입

    @Column(nullable = false)
    String keyword; // 키워드

    @Column(nullable = false)
    Integer score; // 점수


}
