package channeling.be.domain.task.domain;

import channeling.be.domain.common.BaseEntity;
import channeling.be.domain.report.domain.Report;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Task extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "report_id", nullable = false)
    private Report report;

    @Column(nullable = false)
    private TaskStatus overviewStatus;

    @Column(nullable = false)
    private TaskStatus analysisStatus;

    @Column(nullable = false)
    private TaskStatus ideaStatus;
}
