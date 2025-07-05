package channeling.be.domain.idea.domain;

import channeling.be.domain.common.BaseEntity;
import channeling.be.domain.video.domain.Video;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Idea extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "video_id", nullable = false)
    private Video video;

    @Column(nullable = false)
    private String title; // 제목

    @Column(nullable = false)
    private String content; // 내용

    @Column(nullable = false)
    private String hashTag; // 해시태그 Json 리스트

    @Column(nullable = false)
    private Boolean isBookMarked; // 북마크 여부


}
