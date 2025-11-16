package channeling.be.domain.report.domain;

import channeling.be.domain.common.BaseEntity;
import channeling.be.domain.video.domain.Video;
import jakarta.persistence.*;
import lombok.*;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Getter
@Builder
@AllArgsConstructor
public class Report extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "video_id", nullable = false)
    private Video video;

    @Column
    private String title; // 영상 제목

    @Column
    private Long view; // 조회수

    @Column
    private Double viewTopicAvg; // 동일 주제 평균 조회수

    @Column
    private Double viewChannelAvg; // 체널 평균 조회수

    @Column
    private Long likeCount; // 좋아요 수

    @Column
    private Double likeTopicAvg; // 동일 주제 평균 좋아요 수

    @Column
    private Double likeChannelAvg; // 채널 평균 좋아요 수

    @Column
    private Long comment; // 댓글 수

    @Column
    private Double commentTopicAvg; // 동일 주제 평균 댓글 수

    @Column
    private Double commentChannelAvg; // 채널 평균 좋아요 수

    @Column
    private Integer concept; //컨셉 일관성

    @Column
    private Integer seo; // seo 구성

    @Column
    private Integer revisit; // 재방문률

    @Column(columnDefinition = "TEXT")
    private String summary; // 요약본

    @Column
    private Long neutralComment; // 중립 댓글 수

    @Column
    private Long adviceComment; // 조언 댓글 수

    @Column
    private Long positiveComment; // 긍정 댓글 수

    @Column
    private Long negativeComment; // 부정 댓글 수

    @Column(columnDefinition = "TEXT")
    private String leaveAnalyze; // 시청자 이탈 분석

    @Column(columnDefinition = "TEXT")
    private String optimization; // 알고리즘 최적화



}
