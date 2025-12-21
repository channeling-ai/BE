package channeling.be.domain.log.repository;

import channeling.be.domain.log.domain.IdeaLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;

public interface IdeaLogRepository extends JpaRepository<IdeaLog, Long> {

    @Query("""
	SELECT COUNT(i)
	FROM IdeaLog i
	JOIN Channel c ON i.channelId = c.id
	WHERE c.member.id = :memberId
	  AND i.createdAt >= :start
	""")
    long countMonthlyIdeas(
            @Param("memberId") Long memberId,
            @Param("start") LocalDateTime start
    );
}
