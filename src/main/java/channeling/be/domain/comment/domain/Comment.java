package channeling.be.domain.comment.domain;

import channeling.be.domain.common.BaseEntity;
import channeling.be.domain.report.domain.Report;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Comment extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "report_id", nullable = false)
    private Report report;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    CommentType commentType; // 댓글 종류

    @Column(nullable = false, columnDefinition = "TEXT")
    String content; // 댓글 내용

}
