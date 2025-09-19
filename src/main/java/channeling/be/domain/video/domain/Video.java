package channeling.be.domain.video.domain;

import channeling.be.domain.channel.domain.Channel;
import channeling.be.domain.common.BaseEntity;
import channeling.be.domain.idea.domain.Idea;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.BatchSize;

import java.time.LocalDateTime;
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

    @Column(nullable = false, unique = true, length = 50)
    private String youtubeVideoId; // 영상 ID (유튜브 영상 ID)

    @Enumerated(EnumType.STRING)
    @Column
    private VideoCategory videoCategory; // long, short 여부

    @Column(length = 100)
    private String title; // 영상 제목

    @Column
    private Long view; // 영상 조회수

    @Column
    private Long likeCount; // 영상 좋아요 수

    @Column
    private Long commentCount; // 영상 댓글 수

    @Column(length = 100)
    private String link; // 영상 링크

    @Column
    private LocalDateTime uploadDate; // 업로드 날짜

    @Column(length = 100)
    private String thumbnail; // 썸네일 사진

    @Column(length = 5000)
    private String description; // 영상 설명

    @OneToMany(
            mappedBy = "video",
            fetch = FetchType.LAZY,
            cascade = CascadeType.ALL,       // Video 삭제 시 Idea도 같이 삭제
            orphanRemoval = true             // Video에서 Idea 제거하면 DB에서도 삭제
    )
    @BatchSize(size = 5)
    private List<Idea> ideas;
}
