package channeling.be.domain.log;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;

public interface ReportLogRepository extends JpaRepository<ReportLog, Long> {


	@Query("""
	SELECT r
	FROM ReportLog r
	JOIN Video v   ON r.videoId = v.id
	JOIN Channel c ON v.channel.id = c.id
	WHERE c.member.id = :memberId
	  AND r.createdAt >= :start
	""")
    long countMonthlyReports(
            @Param("memberId") Long memberId,
            @Param("start") LocalDateTime start
    );
}
