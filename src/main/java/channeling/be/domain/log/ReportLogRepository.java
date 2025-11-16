package channeling.be.domain.log;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;

public interface ReportLogRepository extends JpaRepository<ReportLog, Long> {


	@Query(value = """
    SELECT COUNT(r.*)
    FROM report_log r
    JOIN video v       ON r.video_id   = v.id
    JOIN channel c     ON v.channel_id = c.id
    LEFT JOIN task t   ON r.report_id  = t.report_id
    WHERE c.member_id       = :memberId
      AND r.created_at      >= :start
      AND r.overview_status = 'COMPLETED'
      AND r.analyze_status  = 'COMPLETED'
    """, nativeQuery = true)
    long countMonthlyReports(
            @Param("memberId") Long memberId,
            @Param("start") LocalDateTime start
    );
}
