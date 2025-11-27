package channeling.be.domain.log.domain;

import channeling.be.domain.task.domain.TaskStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.time.LocalDateTime;


@Getter
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Entity
@Table(name = "report_log")
public class ReportLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private LocalDateTime loggedAt;

    private Long reportId;
    private Long videoId;

    private String title; // 영상 제목
    private Long view; // 조회수
    private Double viewTopicAvg; // 동일 주제 평균 조회수
    private Double viewChannelAvg; // 체널 평균 조회수
    private Long likeCount; // 좋아요 수
    private Double likeTopicAvg; // 동일 주제 평균 좋아요 수
    private Double likeChannelAvg; // 채널 평균 좋아요 수
    private Long comment; // 댓글 수
    private Double commentTopicAvg; // 동일 주제 평균 댓글 수
    private Double commentChannelAvg; // 채널 평균 좋아요 수
    private Integer concept; //컨셉 일관성
    private Integer seo; // seo 구성
    private Integer revisit; // 재방문률

    @Column(columnDefinition = "TEXT")
    private String summary; // 요약본

    private Long neutralComment; // 중립 댓글 수
    private Long adviceComment; // 조언 댓글 수
    private Long positiveComment; // 긍정 댓글 수
    private Long negativeComment; // 부정 댓글 수

    @Column(columnDefinition = "TEXT")
    private String leaveAnalyze; // 시청자 이탈 분석

    @Column(columnDefinition = "TEXT")
    private String optimization; // 알고리즘 최적화

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // [로그에서만 추가]
    @Enumerated(EnumType.STRING)
    private TaskStatus overviewStatus; // 개요 생성 상태
    @Enumerated(EnumType.STRING)
    private TaskStatus analyzeStatus; // 분석 생성 상태

    @Enumerated(EnumType.STRING)
    private DeleteType deleteType;  // 삭제 타입 확인(사용자 임의, 생성시)

}
