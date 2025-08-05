package channeling.be.domain.task.domain.repository;

import channeling.be.domain.task.domain.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface TaskRepository extends JpaRepository<Task, Long> {
    @Query("""
    SELECT t
    FROM Task t
    JOIN FETCH t.report r
    JOIN FETCH r.video v
    JOIN FETCH v.channel c
    JOIN FETCH c.member m
    WHERE t.id = :taskId
""")
    Optional<Task> findByIdFetchWithReportAndMember(@Param("taskId") Long taskId);
}
