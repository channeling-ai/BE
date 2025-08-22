package channeling.be.domain.video.domain;

import channeling.be.domain.channel.domain.Channel;
import channeling.be.domain.common.BaseEntity;
import channeling.be.domain.idea.domain.Idea;
import channeling.be.domain.report.domain.Report;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.BatchSize;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
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

    @Column(nullable = false, unique = true)
    private String youtubeVideoId; // 영상 ID (유튜브 영상 ID)

    @Enumerated(EnumType.STRING)
    @Column
    private VideoCategory videoCategory; // long, short 여부

    @Column
    private String title; // 영상 제목

    @Column
    private Long view; // 영상 조회수

    @Column
    private Long likeCount; // 영상 좋아요 수

    @Column
    private Long commentCount; // 영상 댓글 수

    @Column
    private String link; // 영상 링크

    @Column
    private LocalDateTime uploadDate; // 업로드 날짜

    @Column
    private String thumbnail; // 썸네일 사진

    @Column
    private String description; // 영상 설명

    @OneToMany(mappedBy = "video",cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @BatchSize(size = 5)
    private List<Idea> ideas;

}
